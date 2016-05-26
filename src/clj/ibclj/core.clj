(ns ibclj.core
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout go-loop]]
            [capacitor.core :as influx])
  [:import com.ib.controller.ApiController
            (com.ib.controller ApiController$TopMktDataAdapter NewContract)
   ]
  (:gen-class))

(def db-client (influx/make-client {:db "ib_ticks"}))

;First time to create db
;(influx/create-db db-client)

(comment
  ;Added Simple Moving Average calculations
  ;5-day SMA: (11 + 12 + 13 + 14 + 15) / 5 = 13
  ;can I use partition on dynamic list?


  (defn average [lst] (/ (reduce + lst) (count lst)))
  (defn moving-average [window lst] (map average (partition window 1 lst)))

  )

()


(defn create-controller []
  (reify
    com.ib.controller.ApiController$IConnectionHandler
    (accountList [this list] (print "AccountList" list))
    (message [this id error-code error-msg] (print id error-code error-msg))
    (error [this e] (print e))
    (show [this msg] (print msg))
    (connected [this] (print "We are connected!"))
    (disconnected [this] (print "We are diconnected!"))

    ))

(defn create-contract [symbol type]
  (doto (com.ib.controller.NewContract.)
    (.symbol symbol)
    (.secType type)
    (.expiry "")
    (.strike 0.0)
    (.right com.ib.controller.Types$Right/None)
    (.multiplier "")
    (.exchange "SMART")
    (.currency "USD")
    (.localSymbol "")
    (.tradingClass "")))

(defn api-ctrl []
  (com.ib.controller.ApiController. (create-controller)
                                    (reify com.ib.controller.ApiConnection$ILogger
                                      (log [this s]         ;(print "loggerin:" s)
                                        ))
                                    (reify com.ib.controller.ApiConnection$ILogger
                                      (log [this s]         ;(print "loggerout:" s)
                                        ))))


(defn create-row [c ^NewContract contract]
  (reify
    com.ib.controller.ApiController$ITopMktDataHandler
    (tickPrice [this tick-type p auto-execute?]
      ;(println (.name tick-type) "price: " p)
      (>!! c {:sym (.symbol contract) :type (.name tick-type) :value p})
      )
    (tickSize [this tick-type size]
      ;(println (.name tick-type) "size: " size)
      (>!! c {:sym (.symbol contract) :type (.name tick-type) :value size})
      )
    (tickString [this tick-type val]
      ;(println (.name tick-type) "LastTime: " val)
      (>!! c {:sym (.symbol contract) :type (.name tick-type) :value val})
      )
    (marketDataType [this data-type]
      (println "Frozen? " (= data-type com.ib.controller.Types$MktDataType/Frozen)))
    (tickSnapshotEnd [this] (println "tick snapshot end"))
    ))

(defn contract-ctx [c sym type]
  (let [contract (create-contract sym type)
        row (create-row c contract)]
    {:contract contract :sym sym :type type :row row}
    ))

(defn subscribe! [api ctx c]
  (.reqTopMktData api (:contract ctx) "" false (:row ctx)))

(defn unsubscribe! [api ctx]
  (.cancelTopMktData api (:row ctx)))

(defn series-name [symbol]
  (str symbol "_ticks"))


(defn update-moving-avg [old-value-atom type new-price]
  (if (= type "LAST")
    (swap! old-value-atom (fn [{:keys [sum n]}]
                            (let [new-n (inc n)
                                  new-sum (+ sum new-price)
                                  new-avg (/ new-sum new-n)]
                              {:sum new-sum :n new-n :avg new-avg})))))



(defn update-ticker [symbol row column value]
  (if (= column "LAST_TIMESTAMP")
              (do
                (influx/write-point db-client [ (series-name symbol) {"symbol" symbol} @row])
                (reset! row {:type column }))
              (swap! row assoc column value)))

(def tickers ["VXX" "SPY"])   ;; "AAPL" "GOOG"

(defn start []
  (let [api (api-ctrl)
        c (chan)
        ctxs (map #(contract-ctx c % com.ib.controller.Types$SecType/STK) tickers)
;        vxx-ctx (contract-ctx c "VXX" com.ib.controller.Types$SecType/STK)
 ;       spy-ctx (contract-ctx c "SPY" com.ib.controller.Types$SecType/STK)
]
    (.connect api "localhost" 7497 5)

    (doseq [ctx ctxs]
      (subscribe! api ctx c))


    (let [tick-data (->> tickers
                         (map (fn [sym] [sym (atom {})]))
                         (into {}))
          moving-avg (->> tickers
                          (map (fn [sym] [sym (atom {:sum 0 :n 0})]))
                          (into {}))]
      (go-loop []
        (let [{:keys [sym type value]} (<! c)]
          (println sym type value)
          (update-ticker sym (tick-data sym) type value)
          (update-moving-avg (get moving-avg sym) type value)
          (println sym @(get moving-avg sym) )
          (recur))))

    (Thread/sleep (* 16 60 60 1000))

    (doseq [ctx ctxs]
      (unsubscribe! api ctx))

    (.disconnect api)))


(defn -main
  "the main function"
  []
  (start))


;(start)
