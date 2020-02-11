package ibm.labs.kc.app.kafka;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import ibm.labs.kc.event.model.BlueWaterEvent;
import ibm.labs.kc.event.model.ShipPosition;

public class ShipPositionProducer extends BaseProducer implements EventEmitter {
	
	 private static  KafkaProducer<String, String> kafkaProducer;
	 private static ShipPositionProducer instance;
	 
	 private ShipPositionProducer() {
		 Properties p = ApplicationConfig.buildProducerProperties(ShipPositionProducer.class.getName());
		 kafkaProducer = new KafkaProducer<String, String>(p);
	     topic = ApplicationConfig.getProperties().getProperty(ApplicationConfig.KAFKA_SHIP_TOPIC_NAME);
	     System.out.println("ShipPositionProducer produces to " + topic);
	 }
	 
	public synchronized static EventEmitter getInstance() {
		if (instance == null) instance = new ShipPositionProducer();
		return instance;
	}
	
	public void close() {
		kafkaProducer.close(ApplicationConfig.PRODUCER_TIMEOUT_SECS, TimeUnit.SECONDS);
	}

	@Override
	public void emit(BlueWaterEvent event) throws InterruptedException, ExecutionException, TimeoutException {
		ShipPosition sp = (ShipPosition) event;
		String eventAsJson = parser.toJson(sp);
		String key = sp.getShipID();
		ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, eventAsJson);

		Future<RecordMetadata> future = kafkaProducer.send(record, new Callback() {
            public void onCompletion(RecordMetadata metadata, Exception e) {
                if(e != null) {
                   e.printStackTrace();
                } else {
                   System.out.println("@@@@ Ship Position event: " + eventAsJson + " " + topic + " -> the offset: " + metadata.offset() + " on partition:" + metadata.partition() );
                }
            }
        });
	    future.get(ApplicationConfig.PRODUCER_TIMEOUT_SECS, TimeUnit.SECONDS);
	}


}
