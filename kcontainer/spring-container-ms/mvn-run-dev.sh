#!/bin/bash
set -ex

nodemon -e java -w src -x 'mvn compile exec:java -Dexec.mainClass="ibm.labs.kc.containermgr.SBApplication"'