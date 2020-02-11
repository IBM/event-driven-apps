# Fleet manager microservice

This microservice is responsible to manage fleet of container carrier ships. It supports the event, actors, and commands discovered during the event storming workshop and illustrated by the following figure for the "ship actor":

![](https://github.com/ibm-cloud-architecture/refarch-kc/blob/master/analysis/ship-dom-cmd3.png)

The service exposes simple REST API to support getting ships and fleets, and start and stop simulator to emulate ship movements and container metrics events generation. When a ship leaves or enters it will also generates the events as listed in the analysis.

## What you will learn

* Using JAXRS API to define REST resources
* Using microprofile for API documentation
* How to leverage WebSphere Liberty in container to support simple JEE and microprofile services
* Kafka producer code example
* Test Driven Development with JAXRS and Integration test with Kafka

We recommend also reading the [producer design and coding considerations article](https://github.com/ibm-cloud-architecture/refarch-eda/blob/master/docs/kafka/producers.md)

## Pre-Requisites

* [Maven](https://maven.apache.org/install.html) used to compile and package the application.
* Java 8: Any compliant JVM should work.
  * [Java 8 JDK from Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
  * [Java 8 JDK from IBM (AIX, Linux, z/OS, IBM i)](http://www.ibm.com/developerworks/java/jdk/),
    or [Download a Liberty server package](https://developer.ibm.com/assets/wasdev/#filter/assetTypeFilters=PRODUCT)
    that contains the IBM JDK (Windows, Linux)
* We used [Eclipse 2018 edition](https://www.eclipse.org/downloads/) IDE for Java development.
* Clone the parent project to get access to docker compose yml files: `git clone https://github.com/ibm-cloud-architecture/refarch-kc`. Normally you should have access to this repository from the main reference implementation repository using the `clone.sh` script.
* Have docker engine installed on your computer.

## User stories
This service keeps track of each of the container ships available for transporting containers. Each ship has a unique shipID. We limit the scope of a minimum viable product so the following user stories are implemented:

* [x] The information about each ship is kept in a json file for a fleet. Ships are uniquely identified by their name (as shipID).
* [x] The capacity of a ship is represented by a matrix, number of rows x number of columns to make it simpler. Therefore the total number of container is rows*columns.
* [x] Support GPS lat/log position reports, as ship position event, of the position of the ship a different point in time. This is modeled as csv file with one row of (lat,log) pair, a row representing a time stamp. (1h?)
* [ ] Generate ship event when leaving source port and when entering destination port, and when docked.
* [ ] Define query of what happen to a ship from a given time to retrace its past voyages.


For more in depth analysis and concepts explained [see this note](https://github.com/ibm-cloud-architecture/refarch-kc#fleetsships-microservice---concept)

## Run

### Run the fleet service on your laptop

If you are in a hurry and want to see the simulator running quickly on your local machine, you can do start our docker compose for a unique kafka node cluster, start liberty server with the simulator app deployed and use one of the scenario to trigger a simulation. This can be summarized as the following steps
```
# Go to the parent repository refarch-kc/docker folder
$ cd ../refarch-kc/docker

# If not done previously, start kafka and zookeeper locally. See this section for detail: https://github.com/ibm-cloud-architecture/refarch-kc#run-locally.
$ docker-compose -f backbone-compose.yml up

# Go back to this project to build it
$ ./script/buildDocker.sh

# Start the liberty server. Note that this script defines environment variables to access kafka brokers
$ ./scripts/runDocker.sh

# Workaround for NullPointerException when starting Docker :
# run liberty locally (see command above) once.

# Verify the service is up and running by looking at the fleet API in your web browser http://localhost:9081/api/explorer/ and even execute the GET /fleetms/fleets to get the 3 fleets defined.

# Start the kafka consumer to get container metrics so you can see the solution generating events.
$ ./scripts/runContainerMetricConsumer.sh

# Start the "Fire in containers" simulation
$ cd scripts
$ ./startContainerFireSimulation.sh
# .... the simulation can run for a minute if you want to stop use the following command
$ ./stopContainerSimulation.sh

# Another simulation: Start the Container power off simulation
$ ./startContainerPowerOffSimulation.sh
```

If you want to get a clear understanding of the traces see [this note](./docs/SimulatorTracing.md)

### Run the demo locally connected to Event Streams on IBM Cloud

As an alternate you can use our Event Stream backbone we have configured on IBM Cloud and still run the fleet service on you laptop.

If you want to run with our Event Streams backbone deployed on IBM Cloud, ask us for the api key and then do the following:
```
export KAFKA_BROKERS="kafka03-prod02.messagehub.services.us-south.bluemix.net:9093,kafka01-prod02.messagehub.services.us-south.bluemix.net:9093,kafka02-prod02.messagehub.services.us-south.bluemix.net:9093,kafka04-prod02.messagehub.services.us-south.bluemix.net:9093,kafka05-prod02.messagehub.services.us-south.bluemix.net:9093"
export KAFKA_APIKEY="<the super secret key we will give you>"
mvn liberty:run-server
```

## The model

A fleet will have one to many ships. Fleet has id and name. Ship has ID, name, status, position, port and type. Ship carries containers. Container has id, and metrics like amp, temperature. Here is an example of JSON document illustrating this model:
```json
 {
    "id": "f1",
    "name": "KC-NorthAtlantic",
    "ships": [
      {
         "name": "MarieRose",
        "latitude": "37.8044",
        "longitude": "-122.2711",
        "status": "Docked",
        "port": "Oackland",
        "type": "Carrier",
        "maxRow": 3,
        "maxColumn": 7,
         "numberOfContainers" : 17,
         "containers": [
             {"id":"c_2","type":"Reefer","temperature":10,"amp":46,"status":"RUN","row":0,"column":2,"shipId":"MarieRose"}
         ],
      }],
}
```

## Code

The base of the project was created using IBM Microclimate using microprofile / Java EE template deployable in WebSphere Liberty. Once, the project template was generated, we applied a Test Driven Development approach to develop the application logic. But first let define some use stories we want to support in this simulator.

### User stories

We are listing here the basic features to support:

- [x] Support simulate ship movement and refrigerated containers metrics simulation via  REST api to be easily consumable from backend for frontend component using a POST verb.
- [x] Support simulation of container fire, container down and heat wave so container metric events can be analyzed down stream.
- [x] Integrate with IBM Event Streams running on IBM public cloud  using api_key
- [x] Integrate with Kafka running locally.
- [ ] Generate ship position event x seconds, to demonstrate ship movement representing x minutes of real time. Like in a video game.
- [x] Generate, at each position update, the n container metric events for all container carried in the moving ship

### Code organization

The following package structure is used:
* `ibm.labs.kc.model` for the domain specific model.
* `ibm.labs.kc.app.kafka` kafka consumer and producer and config management.
* `ibm.labs.kc.app.rest` set of REST resources with API definitions
* `ibm.labs.kc.dao` data access object for ship and fleet. Use mockup no backend DB yet.
* `ibm.labs.kc.event.model` event definitions for the kafka topic payload
* `ibm.labs.kc.simulator` simulators for the demo as we do not have real ships... yet.

The most important properties are defined in the config.properties file under `src/main/resources`.

### Test Driven Development

Test driven development should be used to develop microservice as it helps to develop by contract and think about how each function should work from a client point of view. [This article](https://cloudcontent.mybluemix.net/cloud/garage/content/code/practice_test_driven_development) introduces the practice.
To apply TDD we want to describe our approach for this project, by starting by the tests.

#### Start simple

As an example of TDD applied to this project, we want to test the "get the list of fleets" feature. As this code is built by iteration, the first iteration is to get the fleet definition and ships definition from files. The `src/main/resources` folder includes a json file to define the fleets. We do not need an external datasource for this mockup solution.

The json is an array of fleet definitions, something like:
```json
[
  {
    "id": "f1",
    "name": "KC-NorthAtlantic",
    "ships": [ ]
  }
]
```

So starting from the test, we implemented in `src/test/java` the `TestReadingFleet` class to test a FleetService. The service will provide the business interface and it will use a data access object to go to the datasource.

The first test may look like the basic code below:

```java
public void testGetAllFleets() {
    FleetDAO dao = new FleetDAOMockup("Fleet.json");
	FleetService serv = new FleetService(dao);
    List<Fleet> f = serv.getFleets();
	Assert.assertNotNull(f);
	Assert.assertTrue(f.size() >= 1);
}
```

After generating class placeholder and java interface, executing the test fails, and we need to implement the DAO and the service operation `getFleets()`. In the FleetService we simply delegate to the DAO.

```java
public List<Fleet> getFleets() {
		return new ArrayList<Fleet>(dao.getFleets());
	}
```

In the future, we may want to filter out the ships or separate fleet from ship in different json files so some logic may be added in this `getFleets()` function. The DAO is defined via an interface, and we add a Factory to build DAO implementation depending on the configuration. The DAO implementation at first is loadding data from file.

To execute all the tests outside of the Eclipse IDE, we use the maven: `mvn test`.

Quickly we can see that the DAO may be more complex than expected so we add unit tests for the DAO too. After 10, 15 minutes we have a service component and a DAO with Factory and Mockup implementation created and tested.

The Fleet service needs to be exposed as REST api, so we add the JAXRS annotations inside the service class to the method we want to expose.

```java
@Path("fleets")
public class FleetService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
	public List<Fleet> getFleets() {}
}
```

So now if we want to test at the API level, we need to do integration tests. This is where **IBM Microclimate** is coming handy as it created a nice example with `HealthEndpointIT` test class to get us started. All integration tests are defined in the `it` java package so we can control the maven life cycle and execute the integration tests when the environment is ready. The `pom.xml` defines configuration using the `maven Failsafe Plugin` which is designed to run integration tests. This Maven plugin has four phases for running integration tests:

* **pre-integration-test** for setting up the integration test environment.
* **integration-test** for running the integration tests.
* **post-integration-test** for tearing down the integration test environment.
* **verify** for checking the results of the integration tests.

The pre-integration-test phase loads IBM Liberty server via another maven plugin: [liberty-maven-app-parent](https://github.com/WASdev/ci.maven/blob/master/docs/parent-pom.md) so that the API can be tested from the app server.

To execute the integration tests do a `mvn verify`.

By using the same code approach as `HealthEndpointIT` we created a `TestFleetAPIsIT` Junit test class.

The environment properties are set in the `pom.xml` file.

```java
    protected String port = System.getProperty("liberty.test.port");
	protected String warContext = System.getProperty("war.context");
    protected String baseUrl = "http://localhost:" + port + "/" + warContext;
    // .... then get a HTTP client and perform a HTTP GET
    Client client = ClientBuilder.newClient();
	Invocation.Builder invoBuild = client.target(url).request();
    Response response = invoBuild.get();
    String fleetsAsString=response.readEntity(String.class);
    //..
```

If you need to debug this test inside Eclipse, you need to start the liberty server as an external process by using `mvn liberty:run-server`.

The second logic we want to TDD is the simulation.

#### Ship Simulator

The simulation of the different container events is done in the class `BadEventSimulator`. But this class is used in a Runner, the `ShipRunner`. The approach is to move the ship to the next position as defined in the separate csv file (named by the ship's name), then to send the new ship position, and the container metrics at that position as events. So the simulator uses two Kafka producers, one for the ship position and one for the container metrics.
The topic names are defined in the `src/main/resource/config.properties` as well as the Kafka parameters. If you did not configure your kafka server, we have a script to create those topics [here](https://github.com/ibm-cloud-architecture/refarch-kc/tree/master/scripts/createLocalTopics.sh)

From a test point of view we want to create a simulation controller instance, call the service simulation operation and verify the impacted container:

```java
@Test
	public void validateContainerDown() {
        serv =  new ShipService();
		ShipSimulationControl ctl = new ShipSimulationControl("JimminyCricket", ShipSimulationControl.REEFER_DOWN);
		ctl.setNumberOfMinutes(1);
		Response res = serv.performSimulation(ctl);
        Ship s = (Ship)res.getEntity();
        Assert.assertTrue(s.getContainers().get(0).get(3).getStatus().equals(Container.STATUS_DOWN));
    }
```
Event after adding the ShipSimulationControl Java Bean and the operation performSimulation into the service... we have a problem... How to unit tests without sending message to Kafka?.

The ShipRunner is a Runnable class and uses the `positionPublisher` and `containerPublisher` which are standard Kafka producers.
Here is a code snippet for the `run()` method of the `ShipRunner`: The ship positions are loaded from a file in the class loader and then for each container in the boat, send metrics.

```java
try  {
    for (Position p : this.positions) {
        // ships publish their position to a queue
        ShipPosition sp = new ShipPosition(this.shipName,p.getLatitude(),p.getLongitude());
        positionPublisher.publishShipPosition(sp);

        // Then publish the state of their containers
        for (List<Container> row :  ship.getContainers()) {
            for (Container c : row) {
                ContainerMetric cm = BadEventSimulator.buildContainerMetric(this.shipName,c,dateFormat.format(currentWorldTime));
                containerPublisher.publishContainerMetric(cm);
            }
        }
        currentWorldTime=modifyTime(currentWorldTime);
        Thread.sleep(Math.round(this.numberOfMinutes*60000/this.positions.size()));
    }
} catch (InterruptedException e) {
```

So to avoid using kafka for unit tests, we can use mockito to mockup the producers. We encourage to read this [Mockito tutorial](https://javacodehouse.com/blog/mockito-tutorial/) and [this one.](http://www.vogella.com/tutorials/Mockito/article.html#testing-with-mock-objects) to have some basic knowledge on how to use mockito. We added the following dependency in the `pom.xml`.

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>2.23.4</version>
    <scope>test</scope>
</dependency>
```

Add a constructor in `ShipRunner` so we can inject the producer. The test can use mockup at the simulator level or at the producer level. Here is an example in the unit test class of injecting for producer:
```java
 @Mock
 static PositionPublisher positionPublisherMock;
 @Mock
 static ContainerPublisher containerPublisherMock;

 @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();


 @Test
 public void validateContainerFire() {
     // use dependency injection via constructor.
    ShipRunner sr = new ShipRunner(positionPublisherMock, containerPublisherMock);
	ShipSimulator s = new ShipSimulator(sr);
    serv =  new ShipService(DAOFactory.buildOrGetShipDAOInstance("Fleet.json"),s);
    // ..
    Response res = serv.performSimulation(ctl);
}
```
Now the tests succeed and do not send any message to Kafka.

### APIs definition

We can define the API using yaml file and generates code from there, but we are using a TDD approach we start by the code: so we need to add API annotations to get the Swagger generated for us. The MicroProfile OpenAPI specification provides a set of Java interfaces and programming models that allow Java developers to natively produce OpenAPI v3 documents from their JAX-RS applications. We added annotations to the resource classes to support API documentation. Here is an example of microprofile openapi annotations.

```java
@Operation(summary = "Get fleet by fleet name",description=" Retrieve a fleet with ships from is unique name")
@APIResponses(
    value = {
        @APIResponse(
            responseCode = "404",
            description = "fleet not found",
            content = @Content(mediaType = "text/plain")),
        @APIResponse(
            responseCode = "200",
            description = "fleet retrieved",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Fleet.class))) })
	public Fleet getFleetByName(
			@Parameter(
		            description = "The fleetname to get ships data",
		            required = true,
		            example = "KC-NorthFleet",
		            schema = @Schema(type = SchemaType.STRING))
			@PathParam("fleetName") String fleetName) {
            }
```

In the Liberty configuration file: `src/main/liberty/server.xml` we added the following features:
```
<feature>jaxrs-2.0</feature>
<feature>openapi-3.0</feature>
<feature>restConnector-2.0</feature>
```
Once the server is restarted, we first go to http://localhost:9080/api/explorer to access the API definitions and even we are able to test it:

![](docs/fleets-api.png)

A summary of the operations defined for this simulator are:

 | API | Description |
 | --- | --- |
 | GET '/fleetms/fleets/' | Get the list of fleet |
 | GET '/fleetms/fleets/:fleetname' | Get the ships of a given fleet |
 | POST '/fleetms/fleets/simulate' | Start to simulate ships movements |
 | POST '/fleetms/ships/simulate' | Start to simulate ship movements and container metrics generation |

![](docs/fleetms-apis.png)

## Running integration tests with Kafka

By adding simulation tests we need to have kafka running now. We have deployed Kafka and Zookeeper to Kubernetes on Docker Edge for Mac and are able to connect to `docker-for-desktop` cluster. We have described this type of deployment [in this note for Kafka](https://github.com/ibm-cloud-architecture/refarch-eda/blob/master/deployments/kafka/README.md) and [this note for zookeeper](https://github.com/ibm-cloud-architecture/refarch-eda/blob/master/deployments/zookeeper/README.md)

As an alternate you can use the docker image from [confluent.io](https://docs.confluent.io/current/installation/docker/docs/installation/single-node-client.html#single-node-basic) and docker-compose to start zookeeper and kafka single broker.

We use environment variables to control the configuration:

  | Variable | Role | Values |
  | --- | --- | --- |
  | KAFKA_BROKERS | IP addresses and port number of the n brokers configured in your environment | |

The pom.xml uses those variables to use the local kafka for the integration tests:

```
<configuration>
        <environmentVariables>
            <KAFKA_BROKERS>gc-kafka-0.gc-kafka-hl-svc.greencompute.svc.cluster.local:32224</KAFKA_BROKERS>
        </environmentVariables>
```

One interesting integration test is defined in the class `it.FireContainerSimulationIT.java` as it starts a Thread running a ContainerConsumer (bullet 1 in figure below) which uses Kafka api to get `Container events` (class `ibm.labs.kc.event.model.ContainerMetric`) from the `bluewaterContainer` topic, and then calls the POST HTTP end point (2): `http://localhost:9080/fleetms/ships/simulate` with a simulator control object (`ibm.labs.kc.dto.model.ShipSimulationControl`). The application is producing ship position events and container metrics events at each time slot (3). The consumer is getting multiple events (4) from the topic showing some containers are burning:

```json
{"id":"c_2","type":"Reefer","temperature":150,"amp":46,"status":"FIRE","row":0,"column":2,"shipId":"JimminyCricket"},
{"id":"c_3","type":"Reefer","temperature":150,"amp":42,"status":"FIRE","row":0,"column":3,"shipId":"JimminyCricket"}
```

![](docs/it-fire-containers.png)

The integration tests are executed with maven:
```
mvn verify
```

## Deployment

### Configuration

The application is configured to provide JAX-RS REST capabilities, JNDI, JSON parsing and Contexts and Dependency Injection (CDI).
These capabilities are provided through dependencies in the `pom.xml` file and Liberty features enabled in the server config file found in `src/main/liberty/config/server.xml`.

### Run Locally

We described the process in the [section above](#run-the-fleet-service-on-your-laptop)

### Run on IBM Cloud with Kubernetes Service

We are deploying the simulator on the kubernetes service. To define your own IBM Kubernetes Service (IKS) [see our explanations here.](https://github.com/ibm-cloud-architecture/refarch-kc/blob/master/docs/prepare-ibm-cloud.md)

We use [Helm](https://helm.sh/) to install the fleetms service. Helm is a package manager to deploy applications and services to Kubernetes cluster. Package definitions are charts, which are yaml files, to be shareable between teams.

The first time you need to build a chart for any microservice, select a chart name (e.g. `fleetms`) and then use the command like:
```
$ mkdir chart && cd chart
$ helm init fleetms
```

This creates yaml files and a simple set of folders. Those files play a role to define the deployment configuration for kubernetes. Under the templates folder the yaml files use parameters coming from the `values.yaml` and `chart.yaml` files.

* *Chart.yaml*: This is a global parameter file. Set the version and name attributes, as they will be used in deployment.yaml. Each time you deploy a new version of your app you can just change the version number. The values in the chart.yaml are used in the templates.

The following modifications were done to the deployment configuration file to leverage environment variables and docker registry and kafka api key secrets.
* In `values.yml` file:
 ```yml
 env:
  kafka:
    brokers: kafka03....
 image:
     repository: ibmcase/kcontainer-fleet-ms
     tag: latest
     pullPolicy: Always
 ```
* In `deployment.yml` template file
 ```yml
   spec:
      imagePullSecrets:
          - name: {{ .Values.image.pullSecret }}
      containers: ...
    env:
        - name: KAFKA_BROKERS
            value: "{{ .Values.env.kafka.brokers }}"
        - name: KAFKA_APIKEY
            valueFrom:
              secretKeyRef:
                name: es-secret
                key: apikey
 ```


The commands for the deploymnet are described below:

```
# be sure to be connected to your kubernetes cluster:
$ ibmcloud login -a https://api.us-east.bluemix.net

# Target the IBM Cloud Container Service region in which you want to work
$ ibmcloud cs region-set us-east

$ ibmcloud cs cluster-config <cluster-name>
$ export KUBECONFIG=/Users/jeromeboyer/.bluemix/plugins/container-service/clusters/<cluster-name>/<kube-config-<cluster-name>.yml>

# Verify you reach the cluster and get the nodes
$ kubectl get nodes
$ helm init
$ helm version
$ cd chart
$ helm install fleetms/ --name kc-fleetms --namespace browncompute

# if you have an issue and wants to uninstall do
$ helm del --purge kc-fleetms

# If you need to upgrade an existing deployed release use the command:
$ helm upgrade kc-fleetms fleetms/ --namespace browncompute
```

To get the IP address and port number of the `kc-fleetms` API use the commands:

```
$ kubectl get pods --namespace browncompute
NAME                                  READY     STATUS    RESTARTS   AGE
fleetms-deployment-85ccf47475-nj54q   1/1       Running   0          1h


$ kubectl describe pod fleetms-deployment-85ccf47475-nj54q -n browncompute

# Get port number for the exposed nodeport
$ kubectl get service -n browncompute
NAME              TYPE       CLUSTER-IP       EXTERNAL-IP   PORT(S)                         AGE
fleetms-service   NodePort   172.21.151.255   <none>        9080:30951/TCP,9443:30931/TCP   1h

# Get public address of the worker nodes:
$ ibmcloud ks workers <cluster_name>
```

Test the deployed app is running using the URL: `http://<public-IP-address>:<port>/fleetms/fleets`

#### Potential issue

Here is a [troubleshooting note](https://github.com/ibm-cloud-architecture/refarch-integration/blob/master/docs/icp/troubleshooting.md) that can always be helpful to search solution for common problem.

While deploying a liberty on ubuntu docker image to IKS, we got a vulnerability issue. This is reported in the image registry, in the image name. The resolution was "Upgrade apt to >= 1.2.29ubuntu0.1", which means modifying the Dockerfile to get the last apt. (Adding the following `RUN apt-get update -q -y && apt-get dist-upgrade -q -y` in the docker file).

### Run on IBM Cloud Private

<TBD>

### DevOps

To deploy this application to IBM Cloud using a DevOps toolchain click the **Create Toolchain** button below.
[![Create Toolchain](https://console.ng.bluemix.net/devops/graphics/create_toolchain_button.png)](https://console.ng.bluemix.net/devops/setup/deploy/)


## fleet-quarkus-ms project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

### Packaging and running the application

The application is packageable using `./mvnw package`.
It produces the executable `fleet-quarkus-ms-1.0-SNAPSHOT-runner.jar` file in `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/fleet-quarkus-ms-1.0-SNAPSHOT-runner.jar`.

### Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or you can use Docker to build the native executable using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your binary: `./target/fleet-quarkus-ms-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image-guide.