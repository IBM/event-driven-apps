package ibm.gse.orderms.infrastructure.rest;


import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ibm.gse.orderms.infrastructure.command.events.OrderCommandEvent;
import ibm.gse.orderms.infrastructure.events.EventEmitter;
import ibm.gse.orderms.infrastructure.events.OrderEventBase;

public class OrderCommandEventSource implements EventEmitter  {
	private static final Logger logger = LoggerFactory.getLogger(OrderCommandEventSource.class);
	
	private WebTarget target;
    
    public OrderCommandEventSource() {
    	initProducer();
    }
    
    private void initProducer() {
		Client client = ClientBuilder.newClient();
		if (System.getenv("ORDER_COMMAND_ENDPOINT") != null) {
			target = client.target(System.getenv("ORDER_COMMAND_ENDPOINT"));
		}	
    }
    
	/**
	 *  Emit an event to an http endpoint (e.g. knative channel or broker)
	 * 
	 */
	@Override
	public void emit(OrderEventBase event) throws Exception {
		if (target == null) initProducer();
		OrderCommandEvent orderCommandEvent = (OrderCommandEvent)event;
		String value = new Gson().toJson(orderCommandEvent);
		String eventDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());
		String eventId = UUID.randomUUID().toString();

		try {
			String response = target.request()
				.header("X-B3-Flags","1")
				.header("CE-SpecVersion","1.0")
				.header("CE-Type", "OrderCommandEvent")
				.header("CE-Time", eventDate)
				.header("CE-ID", eventId)
				.header("CE-Source", "dev.knative.ordercommandeventsource")
				.post(Entity.entity(value, MediaType.APPLICATION_JSON)
								, String.class);
			logger.info(response);
		} catch (Exception e){
			logger.error("Error in JAX-RS", e);
			throw e;
		}		
	}

	@Override
	public void safeClose() {		
	}

}
