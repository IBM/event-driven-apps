#!/bin/bash
if [ $# -eq 1 ]
then
  hostn=$1
else
  hostn="localhost:3100"
fi
url="http://$hostn/voyage"
echo "GET $url"
curl  $url
