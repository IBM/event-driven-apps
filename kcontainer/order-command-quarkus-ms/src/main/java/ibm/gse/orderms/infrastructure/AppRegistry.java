package ibm.gse.orderms.infrastructure;

import javax.enterprise.context.ApplicationScoped;

import ibm.gse.orderms.app.ShippingOrderResource;
import ibm.gse.orderms.infrastructure.events.EventEmitter;
import ibm.gse.orderms.infrastructure.kafka.ErrorEventProducer;
import ibm.gse.orderms.infrastructure.kafka.OrderCommandProducer;
import ibm.gse.orderms.infrastructure.kafka.OrderEventProducer;
import ibm.gse.orderms.infrastructure.repository.ShippingOrderRepository;
import ibm.gse.orderms.infrastructure.repository.ShippingOrderRepositoryMock;
import ibm.gse.orderms.infrastructure.rest.ErrorEventSource;
import ibm.gse.orderms.infrastructure.rest.OrderCommandEventSource;
import ibm.gse.orderms.infrastructure.rest.OrderEventSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * App Registry is a one place to get access to resources of the application.
 * @author jeromeboyer
 *
 */
@ApplicationScoped
public class AppRegistry {

	private ShippingOrderResource orderResource = null;
	
	private static AppRegistry instance = new AppRegistry();

	private static ShippingOrderRepository orderRepository;
	private static EventEmitter orderCommandProducer;
	private static EventEmitter orderEventProducer;
	private static EventEmitter errorEventProducer;
	private static final Logger logger = LoggerFactory.getLogger(AppRegistry.class);
	
	public static AppRegistry getInstance() {
		return instance;
		
	}
	
	
	public  ShippingOrderResource orderResource() {
		synchronized(this) {
			if (orderResource == null ) {
				orderResource = new ShippingOrderResource();
			}
		}
		return orderResource;
	}


	
    public ShippingOrderRepository shippingOrderRepository() {
    	synchronized(this) {
	        if (orderRepository == null) {
	        	orderRepository = new ShippingOrderRepositoryMock();
	        }
    	}
        return orderRepository;
    }


	public EventEmitter orderCommandProducer() {
	    	synchronized(this) {
	    		if (orderCommandProducer == null) {
					if (System.getenv("KAFKA_BROKERS") != null) {
						logger.info("KAFKA_BROKERS provided, initializing Kafka orderCommandProducer");
						orderCommandProducer = new OrderCommandProducer();
					} else {
						logger.info("KAFKA_BROKERS not provided, initializing REST orderCommandEventSource");
						orderCommandProducer = new OrderCommandEventSource();
					}
	    		}
	    	}
	        return orderCommandProducer;
	}
	
	public EventEmitter orderEventProducer() {
    	synchronized(this) {
    		if (orderEventProducer == null) {
				if (System.getenv("KAFKA_BROKERS") != null) {
					logger.info("KAFKA_BROKERS provided, initializing Kafka orderEventProducer");
					orderEventProducer = new OrderEventProducer();
				} else {
					logger.info("KAFKA_BROKERS not provided, initializing REST orderEventSource");
					orderEventProducer = new OrderEventSource();
				}
    		}
    	}
        return orderEventProducer;
}


	public EventEmitter errorEventProducer() {
		synchronized(this) {
    		if (errorEventProducer == null) {
				if (System.getenv("KAFKA_BROKERS") != null) {
					logger.info("KAFKA_BROKERS provided, initializing Kafka errorEventProducer");
					errorEventProducer = new ErrorEventProducer();
				} else {
					logger.info("KAFKA_BROKERS not provided, initializing REST orderEventSource");
					errorEventProducer = new ErrorEventSource();
				}
    		}
    	}
        return errorEventProducer;
	}

}
