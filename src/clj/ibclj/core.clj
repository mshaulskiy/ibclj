(ns ibclj.core
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout go-loop]])
  [:import com.ib.controller.ApiController
           (com.ib.controller ApiController$TopMktDataAdapter NewContract)
           ])


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
      (>!! c [(.symbol contract) (.name tick-type) p])
      )
    (tickSize [this tick-type size]
      ;(println (.name tick-type) "size: " size)
      (>!! c [(.symbol contract) (.name tick-type) size])
      )
    (tickString [this tick-type val]
      ;(println (.name tick-type) "LastTime: " val)
      (>!! c [(.symbol contract) (.name tick-type) val])
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

(defn start []
  (let [api (api-ctrl)
        c (chan)
        vxx-ctx (contract-ctx c "VXX" com.ib.controller.Types$SecType/STK)
        spy-ctx (contract-ctx c "SPY" com.ib.controller.Types$SecType/STK)
]
    (.connect api "localhost" 7497 5)

    (subscribe! api vxx-ctx c)
    (subscribe! api spy-ctx c)

    (go-loop []
             (println (<! c))
             (recur))

    (Thread/sleep 100000)

    (unsubscribe! api vxx-ctx)
    (unsubscribe! api spy-ctx)

    (.disconnect api)))


(defn -main
  "the main function"
  []
  (start))


(start)