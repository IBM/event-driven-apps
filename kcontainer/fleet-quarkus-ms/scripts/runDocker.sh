export KAFKA_BROKERS="localhost:9092"
export KAFKA_ENV="LOCAL"
docker rm kcontainer-fleet-ms
docker run --name kcontainer-fleet-ms -e KAFKA_BROKERS -e KAFKA_ENV -p 9080:9080 -p 9444:9443 ibmcase/kcontainer-fleet-ms 
