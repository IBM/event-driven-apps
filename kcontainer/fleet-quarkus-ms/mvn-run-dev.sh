#!/bin/bash
set -ex

mvn compile quarkus:dev -Dquarkus.http.host=0.0.0.0 -Dvertx.cacheDirBase=/tmp/vertx-cache