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
    (accountList [this list] (println "AccountList" list))
    (message [this id error-code error-msg] (println id error-code error-msg))
    (error [this e] (println e))
    (show [this msg] (println msg))
    (connected [this] (println "We are connected!"))
    (disconnected [this] (println "We are diconnected!"))

    ))

(defn api-ctrl []
  (com.ib.controller.ApiController. (create-controller)
                                    (reify com.ib.controller.ApiConnection$ILogger
                                      (log [this s] (println s)))
                                    (reify com.ib.controller.ApiConnection$ILogger
                                      (log [this s] (println s)))))


(defn start []
  (let [api (api-ctrl)]
    (.connect api "localhost" 7497 5 "")
    (Thread/sleep 1000)
    (.disconnect api)))


(defn -main
  "the main function"
  []
 (start)


      )
