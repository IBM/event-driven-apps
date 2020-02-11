package ibm.labs.kc.app.kafka;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import ibm.labs.kc.event.model.BlueWaterEvent;
import ibm.labs.kc.event.model.ContainerMetric;

public class ContainerMetricsProducer extends BaseProducer implements EventEmitter {
	
	private static ContainerMetricsProducer instance;
	private final KafkaProducer<String, String> kafkaProducer;
	
	private ContainerMetricsProducer() {
	     System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
	     System.setProperty(org.slf4j.impl.SimpleLogger.LOG_FILE_KEY,"System.out");
		 Properties p = ApplicationConfig.buildProducerProperties("ContainerMetric-Client");
		 kafkaProducer = new KafkaProducer<String, String>(p);
	     topic = ApplicationConfig.getProperties().getProperty(ApplicationConfig.KAFKA_CONTAINER_TOPIC_NAME);
	     System.out.println("Producing to " + topic);
	}
	
	public static ContainerMetricsProducer getInstance() {
		if (instance == null) instance = new ContainerMetricsProducer();
		return instance;
	}
	
	
	public void close() {
		 kafkaProducer.close(5000, TimeUnit.MILLISECONDS);
	}

	@Override
	public void emit(BlueWaterEvent event) throws Exception {
		 try {
			 ContainerMetric c = (ContainerMetric)event;
			 String eventAsJson = parser.toJson(c);
			 ProducerRecord<String, String> record = new ProducerRecord<String, String>(
               topic,null,eventAsJson);
       
			 // Send record asynchronously
			 Future<RecordMetadata> future = kafkaProducer.send(record);
			 RecordMetadata recordMetadata = future.get(5000, TimeUnit.MILLISECONDS);
			 System.out.println("@@@ Container Event " + eventAsJson + " sent -> offset:" + recordMetadata.offset() + " on partition:" + recordMetadata.partition() );
    
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		} 

		
	}
	
}
