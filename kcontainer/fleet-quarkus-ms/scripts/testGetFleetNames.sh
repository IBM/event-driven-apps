#!/bin/bash
if [ $# -eq 2 ]
then
  hostn=$1
  fleetname=$2
else
  if [[ $# -eq 1 ]]
  then
   hostn=$1
  else 
    hostn="localhost:9080"
  fi 
  fleetname="KC-NorthPacific"
fi

url="http://$hostn/fleets/$fleetname"

echo $(curl  $url)
