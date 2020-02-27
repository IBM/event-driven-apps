# Image URL to use all building/pushing image targets
IMG ?= eventingapps/spring-container-ms
TAG ?= 1.0.0

# Run tests
test: 
	mvn compile
	echo "to be done"

# Build docker image
build-docker:
	#mvn package -DskipTests
	docker build -f Dockerfile -t ${IMG}:${TAG} .

# Push docker image
push-docker:
	echo "${DOCKER_PASSWORD}" | docker login -u "${DOCKER_USERNAME}" --password-stdin
	docker push ${IMG}:${TAG}

build-docker-tools:
	docker build -t eventingapps/container-cli -f scripts/containers-cli/Dockerfile scripts/containers-cli

push-docker-tools:
	echo "${DOCKER_PASSWORD}" | docker login -u "${DOCKER_USERNAME}" --password-stdin
	docker push eventingapps/container-cli

# Run locally
run:
	mvn compile exec:java -Dexec.mainClass="ibm.labs.kc.containermgr.SBApplication"

# Run inner loop with Skaffold
dev:
	skaffold dev

deploy-on-knative:
	kubectl apply -k k8s/overlays/knative-eventing

deploy-on-es:
	kubectl apply -k k8s/overlays/es		

