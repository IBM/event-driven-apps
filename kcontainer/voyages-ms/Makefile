# Image URL to use all building/pushing image targets
IMG ?= eventingapps/voyage-ms
TAG ?= 1.0.0

# Run tests
test: 
	mvn compile
	echo "to be done"

# Build docker image
build-docker:
	docker build -f Dockerfile -t ${IMG}:${TAG} .

# Push docker image
push-docker:
	docker push ${IMG}:${TAG}

# Run locally
run:
	npm start

# Run inner loop with Skaffold
dev:
	skaffold dev

deploy-on-knative:
	kubectl apply -k k8s/overlays/knative-eventing

deploy-on-es:
	kubectl apply -k k8s/overlays/es		

