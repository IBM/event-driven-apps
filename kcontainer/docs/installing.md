# Installing kContainer

## Prereqs

Install [kubectx and kubens](https://github.com/ahmetb/kubectx#installation). For macOS you can just install with:

```shell
brew install kubectx
```

## Setting up your Kubernetes cluster

Create the namespace for the app:

```shell
kubectl create ns kcontainer
```

### Installing on an Upstream Kubernetes 

Please note that the minimum version required: is v1.15.10.

Note also that you will need enough CPU capacity in your cluster, thus you should use a paid IKS cluster
or a minikube with enough CPU/mem allocated. The free IKS cluster ends up running out of CPU for scheduling the
pods.

Install Operator Lifecycle Manager (OLM), so that we can install all the community operators from OperatorHub:

```shell
curl -sL https://github.com/operator-framework/operator-lifecycle-manager/releases/download/0.14.1/install.sh | bash -s 0.14.1
```

Install all the required operators via OperatorHub and OLM:

```shell
kubectl create -f https://operatorhub.io/install/ibmcloud-operator.yaml
kubectl create -f https://operatorhub.io/install/event-streams-topic.yaml
kubectl create -f https://operatorhub.io/install/composable-operator.yaml
kubectl create -f https://operatorhub.io/install/knative-eventing-operator.yaml
kubectl create -f https://operatorhub.io/install/strimzi-kafka-operator.yaml
```

configure the IBM Cloud Operator as explained in [OperatorHub.io](https://operatorhub.io/operator/ibmcloud-operator) in
the `Requirements` section.

Install the knative eventing custom resource:

```shell
cat <<-EOF | kubectl apply -f -
apiVersion: v1
kind: Namespace
metadata:
 name: knative-eventing
---
apiVersion: operator.knative.dev/v1alpha1
kind: KnativeEventing
metadata:
  name: knative-eventing
  namespace: knative-eventing
EOF
```

#### Create a Kafka cluster:

```shell
kubectl create namespace kafka
```

Create a [small ephemeral Apache Kafka Cluster](https://strimzi.io/quickstarts/minikube/) with one node for each, Apache Zookeeper and Apache Kafka:

```shell
kubectl apply -f https://raw.githubusercontent.com/strimzi/strimzi-kafka-operator/0.16.2/examples/kafka/kafka-ephemeral-single.yaml -n kafka 
```
Verify Kafka works by running a producer in one terminal:

```shell
kubectl -n kafka run kafka-producer -ti --image=strimzi/kafka:0.16.2-kafka-2.4.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --broker-list my-cluster-kafka-bootstrap:9092 --topic my-topic
```

and a consumer in another terminal:

```
kubectl -n kafka run kafka-consumer -ti --image=strimzi/kafka:0.16.2-kafka-2.4.0 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic my-topic --from-beginning
```
#### Instal the Knative Kafka Source

Install the knative kafka source operator as follows:

```shell
curl -sL "https://raw.githubusercontent.com/openshift-knative/knative-kafka-operator/master/deploy/crds/eventing_v1alpha1_knativeeventingkafka_crd.yaml" | kubectl apply --filename -
curl -sL "https://raw.githubusercontent.com/openshift-knative/knative-kafka-operator/master/deploy/operator.yaml" | kubectl apply -n operators --filename -
curl -sL "https://raw.githubusercontent.com/openshift-knative/knative-kafka-operator/master/deploy/role.yaml" | kubectl apply -n operators --filename -
curl -sL "https://raw.githubusercontent.com/openshift-knative/knative-kafka-operator/master/deploy/role_binding.yaml" | sed 's/default/operators/' | kubectl apply -n operators --filename -
curl -sL "https://raw.githubusercontent.com/openshift-knative/knative-kafka-operator/master/deploy/service_account.yaml" | kubectl apply -n operators --filename -
```

Create an instance of Knative eventing Kafka with the following CR:

```shell
cat <<-EOF | kubectl apply -f -
apiVersion: eventing.knative.dev/v1alpha1
kind: KnativeEventingKafka
metadata:
  name: knative-eventing-kafka
  namespace: knative-eventing
spec:
  bootstrapServers: my-cluster-kafka-bootstrap.kafka:9092
  setAsDefaultChannelProvisioner: yes
EOF
```

#### Setup kafka as default channel

```shell
cat <<-EOF | kubectl apply -f -
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: default-ch-webhook
  namespace: knative-eventing
data:
  default-ch-config: |
    clusterDefault:
      apiVersion: messaging.knative.dev/v1alpha1
      kind: KafkaChannel
EOF
```

Due to issues with the latest knative eventing operator, it is reccomended to [apply the RBAC fix](#rbac-fix)
at this point.

Test that a channel backed by Kafka is created correctly:

```shell
cat <<-EOF | kubectl apply -f -
---
apiVersion: messaging.knative.dev/v1alpha1
kind: Channel
metadata:
  name: testchannel-two
EOF
```

then:

```shell 
kubectl get channels
```

and 

```shell
kubectl get kafkachannels
```

should return the same result, specifically you should see a valid URL for:

```shell
k get channels
NAME              READY   REASON   URL                                                              AGE
testchannel-one   True             http://testchannel-one-kn-channel.kcontainer.svc.cluster.local   3d3h

k get kafkachannels
NAME              READY   REASON   URL                                                              AGE
testchannel-one   True             http://testchannel-one-kn-channel.kcontainer.svc.cluster.local   3d3h
```

Note: if kafka-channels do not get created, repeat the step for [Setup kafka as default channel](#setup-kafka-as-default-channel)

### OpenShift 4.3+

Make sure you have installed the latest [OC CLI](https://mirror.openshift.com/pub/openshift-v4/clients/ocp/latest/)

Open the OpenShift Web Console and make sure you are in the `Administrator` perspective, then:
- go to `Home->Projects->Create Project` and create a new project with name `kcontainer`
- go to `Operators->OperatorHub` and install the following operators, keeping all default options:
    - IBM Cloud Operator
    - Event Streams Topic
    - Composable
    - Strimzi
    - Knative Eventing Operator
    - Knative Apache Kafka Operator
- configure the IBM Cloud Operator as explained in [OperatorHub.io](https://operatorhub.io/operator/ibmcloud-operator) in
the `Requirements` section.

Make sure you are in the kcontainer project, go to `Operators`->`Installed Operators`, and:
- Select the Strimzi Operator
- In Provided APIs, select the Kafka API and click on `create instance`
- Use the default YAML template, and click `Create`

It might take few minutes for the kafka cluster to become online. You can check the status 
going to `Operators`->`Installed Operators`->`Strimzi` and checking that the `Kafka` instance
has `Condition` with status `Ready`=`True`. You may also check that all pods for kafka are started. 

Next, select `Operators`->`Installed Operators`->`Knative Apache Kafka Operator` and under `Provided APIs`
select `Knative components for Apache Kafka` and click `Create instance`. Edit the default yaml
template to update the field for `bootstrapServers` to `my-cluster-kafka-bootstrap.kcontainer:9092`
(instead than `my-cluster-kafka-bootstrap.kafka:9092`). Wait until the `Knative components for Apache Kafka Overview` CR
shows a `Condition` `Ready`=`True`. You may also check that the pods for `kafka-ch-controller` and `kafka-ch-dispatcher`
are up and running.

## Installing kContainer microservices

You may install kContainer directly on Event Streams (Kafka) or on Knative Eventing, using channels
backed by a Kafka cluster managed by the Strimzi operator.

You may also install individually each micoservice and service dependencies, or you may install
all microservices and dependencies at once.

To install individually a microservice, change directory to the microservice directory and then run

```shell
make deploy-on-knative
```

to install on knative eventing or:

```shell
make deploy-on-es
```

to deploy on event stream.

To install all microservices, just stay at the top level directory and run `make deploy-on-knative` or 
`make deploy-on-es`.

Please note that currently one microservice (spring-container-ms) depends on the IBM Cloud Postgresql service,
which currently may take ~ 30 minutes to fully provision, therefore the springcontainerms will take long time
to get to running state. To check when the service becomes active, use the command:

```shell
kubectl get bindings kcontainer-postgresql
```

service will be fully active when the status becomes `Online`.


# Troubleshooting

## RBAC Fix

there are RBAC issues with the current cluster roles for `kafka-webhook` and `eventing-controller`.
You will need to patch the clusterroles as following:

```shell
kubectl edit clusterrole kafka-webhook
```

find the section for `secrets` resources and ensure that all the following `verbs` are present:

```yaml
 verbs:
  - get
  - create
  - list
  - update
  - patch
```

then find the apiGroup `admissionregistration.k8s.io` and ensure you have the following resources and verbs:

```yaml
resources:
  - mutatingwebhookconfigurations
  - validatingwebhookconfigurations
  verbs:
  - get
  - list
  - create
  - update
  - delete
  - patch
  - watch
 ``` 

make sure you save (e.g. [ESC]:wq in vim) then, edit the following cluster role:

```shell
kubectl edit clusterrole knative-eventing-controller
```

find the apiGroup `messaging.knative.dev` and ensure you have the following resources and verbs:

```yaml
resources:
  - mutatingwebhookconfigurations
  - validatingwebhookconfigurations
  verbs:
  - get
  - list
  - create
  - update
  - delete
  - patch
  - watch
 ``` 

Then apply the following patch:

```bash
cat <<-EOF | kubectl apply -f -
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: knative-eventing-controller-patch
rules:
- apiGroups:
  - messaging.knative.dev
  resources:
  - kafkachannels
  - kafkachannels/status
  verbs:
  - get
  - list
  - create
  - update
  - delete
  - patch
  - watch
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: eventing-controller-patch
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: knative-eventing-controller-patch
subjects:
- kind: ServiceAccount
  name: eventing-controller
  namespace: knative-eventing
EOF
```  