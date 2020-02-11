#!/bin/bash
root_folder=$(cd $(dirname $0); cd ..; pwd)
if [ $# -eq 1 ]
then
  hostn=$1
else
  hostn="localhost:8080"
fi
url="http://$hostn/ships/simulate"
cd scripts
curl -v -H "accept: */*" -H "Content-Type: application/json" -d @./shipCtlPowerOff.json $url
