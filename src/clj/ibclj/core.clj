(ns ibclj.core
  [:import  com.ib.controller.ApiController])


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
                                      (log [this s] (print s)))
                                    (reify com.ib.controller.ApiConnection$ILogger
                                      (log [this s] (print s)))))

(defn create-row []
  (reify
    com.ib.controller.ApiController$ITopMktDataHandler
    (tickPrice [this tick-type p auto-execute?]
      (println (format "tick: %s price: %s" tick-type  p)))
    (tickSize [this tick-type size]
      (println (format "type: %s size: %s" tick-type size)))
    (tickString [this tick-type val]
      (println (format  "Last time, type: %s value: %d" tick-type val) ))
    (marketDataType [this data-type]
      (println "Frozen? " (= data-type com.ib.controller.Types$MktDataType/Frozen)))
    (tickSnapshotEnd [this] (println "tick snapshot end"))
    ))


(defn start []
  (let [api (api-ctrl)
        c (create-contract "VXX")
        row (create-row)]
    (.connect api "localhost" 7497 5)
    (.reqTopMktData api c "" false row)

    (Thread/sleep 100000)
    (.cancelTopMktData api row)
    (.disconnect api)))


(defn -main
  "the main function"
  []
  (start))
