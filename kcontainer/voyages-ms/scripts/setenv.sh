#!/bin/bash
export LDFLAGS=-L/usr/local/opt/openssl/lib
export CPPFLAGS=-I/usr/local/opt/openssl/include
export msname="voyages"
export chart=$(ls ./chart/| grep $msname)
export kname="kc-"$chart
export ns="browncompute"
