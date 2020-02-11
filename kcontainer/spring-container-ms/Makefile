# Image URL to use all building/pushing image targets
IMG ?= cloudoperators/ibmcloud-operator

# Run tests
test: 
	mvn compile
	echo "to be done"

# Run locally
run:
	mvn compile exec:java -Dexec.mainClass="ibm.labs.kc.containermgr.SBApplication"
