(ns ibclj.core


  )


(comment

  ;(:import [com.ib.controller.ApiController] )
  ;(import 'com.ib.controller.ApiController)

  [:import ;;com.ib.controller.ApiConnection
   com.ib.controller.ApiController]

  )

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

(defn api-ctrl []
  (com.ib.controller.ApiController. (create-controller)
                                    (reify com.ib.controller.ApiConnection$ILogger
                                      (log [this s] (print s)))
                                    (reify com.ib.controller.ApiConnection$ILogger
                                      (log [this s] (print s)))))


(defn start []
  (let [api (api-ctrl)]
    (.connect api "localhost" 7497 5)
    (Thread/sleep 1000)
    (.disconnect api)))


(defn -main
  "the main function"
  []
 (start)


      )
