#!/bin/bash
if [ $# -eq 1 ]
then
  hostn=$1
else
  hostn="localhost:8080"
fi
url="http://$hostn/ships/simulate"

curl -v -H "accept: */*" -H "Content-Type: application/json" -d @./shipCtlHeatWave.json $url
