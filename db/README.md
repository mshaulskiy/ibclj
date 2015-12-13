MAC OS Installation
we need to install InfluxDB 0.8 as it's the only one Capacitor supports atm, run following command

> brew install homebrew/versions/influxdb08

Running Influx 
start db using config file provided with this repo, the default one seems to fail 

>  influxdb -config=IBCLJ_ROOT_FOLDER/db/influxdb.conf 


Access Web GUI

Admin Panel http://localhost:8083 
Default login: root/root
