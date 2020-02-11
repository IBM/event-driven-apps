package ibm.labs.kc.app.kafka;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.google.gson.Gson;

/**
 * Common to all consumers
 * @author jeromeboyer
 *
 */
public class BaseKafkaConsumer {
	protected static KafkaConsumer<String, String> kafkaConsumer;
    
	protected Gson gson = new Gson();
    
	public BaseKafkaConsumer() {
	}
	
	protected void prepareConsumer(String topicName,String clientID) {
		Properties properties = ApplicationConfig.buildConsumerProperties(clientID);
        kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(Arrays.asList(topicName));
	}
    
    public void commitOffset() {
    	kafkaConsumer.commitSync();
    }
    
    public void close() {
    	kafkaConsumer.close();
    }

	public static KafkaConsumer<String, String> getKafkaConsumer() {
		return kafkaConsumer;
	}

	public Gson getGson() {
		return gson;
	}

}
