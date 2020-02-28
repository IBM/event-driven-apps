fleet-changed := $(shell utils/check-source-changed.sh fleet-quarkus-ms)
orderc-changed := $(shell utils/check-source-changed.sh order-command-quarkus-ms)
orderq-changed := $(shell utils/check-source-changed.sh order-query-quarkus-ms)
ui-changed := $(shell utils/check-source-changed.sh refarch-kc-ui)
container-changed := $(shell utils/check-source-changed.sh spring-container-ms)
voyages-changed := $(shell utils/check-source-changed.sh voyages-ms)

deploy-on-knative:
	kubectl apply -k order-command-quarkus-ms/k8s/overlays/knative-eventing
	kubectl apply -k order-query-quarkus-ms/k8s/overlays/knative-eventing
	kubectl apply -k spring-container-ms/k8s/overlays/knative-eventing
	kubectl apply -k voyages-ms/k8s/overlays/knative-eventing
	kubectl apply -k fleet-quarkus-ms/k8s/overlays/knative-eventing
	kubectl apply -k refarch-kc-ui/k8s/overlays/knative-eventing

deploy-on-es:
	kubectl apply -k order-command-quarkus-ms/k8s/overlays/es
	kubectl apply -k order-query-quarkus-ms/k8s/overlays/es
	kubectl apply -k spring-container-ms/k8s/overlays/es
	kubectl apply -k voyages-ms/k8s/overlays/es
	kubectl apply -k fleet-quarkus-ms/k8s/overlays/es
	kubectl apply -k refarch-kc-ui/k8s/overlays/es

# builds only projects that have changed based on last commit
build-changed:
	@if [ "${fleet-changed}" = "changed" ]; then\
        cd fleet-quarkus-ms; make build-docker; cd ..;\
    fi
	@if [ "${orderc-changed}" = "changed" ]; then\
        cd order-command-ms; make build-docker; cd ..;\
    fi
	@if [ "${orderq-changed}" = "changed" ]; then\
        cd order-query-ms; make build-docker; cd ..;\
    fi	
	@if [ "${ui-changed}" = "changed" ]; then\
        cd refarch-kc-ui; make build-docker; cd ..;\
    fi
	@if [ "${container-changed}" = "changed" ]; then\
        cd spring-container-ms; make build-docker; cd ..;\
    fi	
	@if [ "${voyages-changed}" = "changed" ]; then\
        cd voyages-ms; make build-docker; cd ..;\
    fi	

# push only images based on build-changed
push-changed:
	@if [ "${fleet-changed}" = "changed" ]; then\
        cd fleet-quarkus-ms; make push-docker; cd ..;\
    fi
	@if [ "${orderc-changed}" = "changed" ]; then\
        cd order-command-ms; make push-docker; cd ..;\
    fi
	@if [ "${orderq-changed}" = "changed" ]; then\
        cd order-query-ms; make push-docker; cd ..;\
    fi	
	@if [ "${ui-changed}" = "changed" ]; then\
        cd refarch-kc-ui; make push-docker; cd ..;\
    fi
	@if [ "${container-changed}" = "changed" ]; then\
        cd spring-container-ms; make push-docker; cd ..;\
    fi	
	@if [ "${voyages-changed}" = "changed" ]; then\
        cd voyages-ms; make push-docker; cd ..;\
    fi

build-docker-tools:
	docker build -t eventingapps/cli -f utils/cli/Dockerfile utils/cli

push-docker-tools:
	echo "${DOCKER_PASSWORD}" | docker login -u "${DOCKER_USERNAME}" --password-stdin
	docker push eventingapps/cli