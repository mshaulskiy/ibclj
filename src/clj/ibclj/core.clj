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
    (connected [this] (println "We are connected!"))
    (disconnected [this] (println "We are diconnected!"))

    ))


(defn -main
  "the main function"
  []
  (println "Hello, World!")


      )
