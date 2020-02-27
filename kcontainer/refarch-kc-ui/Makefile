# Image URL to use all building/pushing image targets
IMG ?= eventingapps/kcontainer-ui
TAG ?= 1.0.0

# Run tests
test: 
	mvn compile
	echo "to be done"

# Build locally using image with angular/node tools
build-local:
	docker run -v ${PWD}:/home -ti trion/ng-cli bash -c "cd /home/ui  && npm install && ng build && cd ../server && npm install && npm run build"

# Build docker image
build-docker:
	echo "${DOCKER_PASSWORD}" | docker login -u "${DOCKER_USERNAME}" --password-stdin
	docker build -t ${IMG}:${TAG} .

# Push docker image
push-docker:
	docker push ${IMG}:${TAG}

# Run locally
run:
	cd server && npm start

# Run inner loop with Skaffold
dev:
	skaffold dev

deploy-on-knative:
	kubectl apply -k k8s/overlays/knative-eventing

deploy-on-es:
	kubectl apply -k k8s/overlays/es

	
