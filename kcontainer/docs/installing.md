# Installing kContainer

## Setting up your Kubernetes cluster

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


