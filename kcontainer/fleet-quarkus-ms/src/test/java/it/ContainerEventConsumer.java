package it;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import ibm.labs.kc.app.kafka.ApplicationConfig;
import ibm.labs.kc.app.kafka.BaseKafkaConsumer;
import ibm.labs.kc.event.model.ContainerMetric;

public class ContainerEventConsumer extends BaseKafkaConsumer implements Runnable {
    
    public ContainerEventConsumer() {
    	 super();
    	 prepareConsumer(ApplicationConfig.getProperties().getProperty(ApplicationConfig.KAFKA_CONTAINER_TOPIC_NAME),
    			 ApplicationConfig.getProperties().getProperty(ApplicationConfig.KAFKA_CONSUMER_CLIENTID)+"_container");
	}

    
    public List<ContainerMetric>  consume() {
    	List<ContainerMetric> buffer = new ArrayList<ContainerMetric>();
    	ConsumerRecords<String, String> records = kafkaConsumer.poll(
    			Long.parseLong(ApplicationConfig.getProperties().getProperty(ApplicationConfig.KAFKA_POLL_DURATION)));
    	
	    for (ConsumerRecord<String, String> record : records) {
	    	ContainerMetric a = gson.fromJson(record.value(), ContainerMetric.class);
	    	buffer.add(a);
	    }
    	return buffer;
    }


	@Override
	public void run() {
		while (true) {
			for (ContainerMetric cm: consume()) {
				System.out.println(cm.toString());
			}
		}
		
	}
    
}
