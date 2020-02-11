#!/bin/bash

export msname="fleetms"
export chart=$(ls ./chart/| grep $msname)
export kname="kc-"$chart
export ns="browncompute"

