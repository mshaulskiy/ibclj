## MAC OS Installation
we need to install InfluxDB 0.8 as it's the only one Capacitor supports atm, run following command

    > brew install homebrew/versions/influxdb08

## Running Influx 
start db using config file provided with this repo, the default one seems to fail. 

    >  influxdb -config=IBCLJ_ROOT_FOLDER/db/influxdb.conf 

Config file is prepared for this command to be run in project's root directory. if you start db from other destination you might want to consider changing logs and data storage paths.


## Access Web GUI

Admin Panel http://localhost:8083 
Default login: root/root


## Create initial Database
Make sure you create your database before trying to capture ticks, you can do this in InfluxDb admin panel or using this command in clojure repl 

    > (def db-client (influx/make-client {:db "ib_ticks"}))

    > (influx/create-db db-client)


## Export data into a file 

with one easy CURL cmd into their Web API

    curl "http://localhost:8086/db/ib_ticks/series?u=root&p=root&q=select%20*%20from%20VXX_ticks%3B" > db/VXX_ticks.json

## Import json data from file

    curl -XPOST -d @db/VXX_ticks.json "http://hostb:8086/db/ib_ticks/series?u=root&p=root"
