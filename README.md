# ibclj

An Clojure application using the Java Interactive Brokers API.

## Usage

Optional:
* download latest.edemo.mac.jnlp from https://www.interactivebrokers.com/demo   (launch TWS Latest 954.2 at the time (avoid beta version))
* download latest jar file containing the API from https://www.interactivebrokers.com 

Necessary:
* run lib/latest.edemo.mac.jnlp   (it's the IB Webstart component previously download already commited to this repo)
* open ActiveX port: Settings -> API -> enable ActiveX  (you need to open a local port for this project to work)
* git clone git@github.com:matlux/ibclj.git
* cd ibclj
* mvn install:install-file -DgroupId=com.quantechlab -DartifactId=ibclient -Dversion=9.71.01 -Dpackaging=jar -Dfile=lib/ibclient-9.71.01.jar
* lein run

You should see some ticks printed on the console.


## Disclaimer

This project is not affiliated or supported by with https://www.interactivebrokers.com

The following files were downloaded from https://www.interactivebrokers.com for the convenience of bootstrapping this project quickly. You should download the latest version and agree with the T&C from https://www.interactivebrokers.com when using this application.

lib/ibclient-9.71.01.jar
lib/latest.edemo.mac.jnlp

## License

Copyright © 2015 Matlux Ltd

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
