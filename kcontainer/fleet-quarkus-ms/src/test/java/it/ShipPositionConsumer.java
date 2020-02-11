package it;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import ibm.labs.kc.app.kafka.ApplicationConfig;
import ibm.labs.kc.app.kafka.BaseKafkaConsumer;
import ibm.labs.kc.app.kafka.EventEmitter;
import ibm.labs.kc.app.kafka.ShipPositionProducer;
import ibm.labs.kc.event.model.ShipPosition;



public class ShipPositionConsumer extends BaseKafkaConsumer {

    
    public ShipPositionConsumer() {
    	 super();
    	 prepareConsumer(ApplicationConfig.getProperties().getProperty(ApplicationConfig.KAFKA_SHIP_TOPIC_NAME),
    			 "ship-consumer");
	}
    
    public List<ShipPosition>  consume() {
    	List<ShipPosition> buffer = new ArrayList<ShipPosition>();
    	ConsumerRecords<String, String> records = kafkaConsumer.poll(
    			Long.parseLong(ApplicationConfig.getProperties().getProperty(ApplicationConfig.KAFKA_POLL_DURATION)));
    	
	    for (ConsumerRecord<String, String> record : records) {
	    	System.out.println(record.offset() + " " + record.partition());
	    	ShipPosition a = gson.fromJson(record.value(), ShipPosition.class);
	    	buffer.add(a);
	    }
    	return buffer;
    }
    
    
	public static void main(String[] args) throws Exception {
		/*
		ShipPosition sp = new ShipPosition("MyBoat","45.0900","-122.15050");
		EventEmitter positionPublisher = ShipPositionProducer.getInstance();
		positionPublisher.emit(sp);
		*/
		ShipPositionConsumer consumer = new ShipPositionConsumer();
		for ( ShipPosition p : consumer.consume()) {
			System.out.println(p.toString());
		};
	}

}
