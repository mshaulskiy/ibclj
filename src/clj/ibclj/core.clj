(ns ibclj.core
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout go-loop]])
  [:import com.ib.controller.ApiController
           (com.ib.controller ApiController$TopMktDataAdapter)
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

(defn create-contract [symbol]
  (doto (com.ib.controller.NewContract.)
    (.symbol symbol)
    (.secType com.ib.controller.Types$SecType/STK)
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


(defn create-row [c]
  (reify
    com.ib.controller.ApiController$ITopMktDataHandler
    (tickPrice [this tick-type p auto-execute?]
      ;(println (.name tick-type) "price: " p)
      (>!! c [(.name tick-type) p])
      )
    (tickSize [this tick-type size]
      ;(println (.name tick-type) "size: " size)
      (>!! c [(.name tick-type) size])
      )
    (tickString [this tick-type val]
      ;(println (.name tick-type) "LastTime: " val)
      (>!! c [(.name tick-type) val])
      )
    (marketDataType [this data-type]
      (println "Frozen? " (= data-type com.ib.controller.Types$MktDataType/Frozen)))
    (tickSnapshotEnd [this] (println "tick snapshot end"))
    ))


(defn start []
  (let [api (api-ctrl)
        contract (create-contract "VXX")
        c (chan)
        row (create-row c)]
    (.connect api "localhost" 7497 5)
    (.reqTopMktData api contract "" false row)

    (go-loop []
             (println (<! c))
             (recur))

    (Thread/sleep 100000)
    (.cancelTopMktData api row)
    (.disconnect api)))


(defn -main
  "the main function"
  []
  (start))
