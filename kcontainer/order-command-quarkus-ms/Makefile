# Image URL to use all building/pushing image targets
IMG ?= cloudoperators/ibmcloud-operator

# Run tests
test: 
	mvn compile
	echo "to be done"

# Build docker image
build-docker:
	mvn package -DskipTests
	docker build -f src/main/docker/Dockerfile.jvm -t us.icr.io/order-command-quarkus-ms-jvm .

# Run locally
run:
	./mvnw compile quarkus:dev: -Ddebug=5005
