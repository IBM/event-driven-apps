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

	
