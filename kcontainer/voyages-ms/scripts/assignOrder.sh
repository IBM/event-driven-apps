#!/bin/bash

if [ $# -eq 2 ]
then
  voyageID=$1
  hostn=$2
else
  if [ $# -eq 1 ]
  then
    voyageID=$1   
  else
    echo "Usage $0 voyageID [hostname]"
    exit 1
  fi
  hostn="localhost:3000"
fi
url="http://$hostn/voyage/$voyageID/assign"
echo $url
curl -v -X POST -H "Content-Type: application/json" -d "@./orderAssign.json" $url

