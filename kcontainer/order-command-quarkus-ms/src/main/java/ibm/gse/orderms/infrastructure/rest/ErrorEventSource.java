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

import ibm.gse.orderms.infrastructure.events.EventEmitter;
import ibm.gse.orderms.infrastructure.events.OrderEventBase;
import ibm.gse.orderms.infrastructure.kafka.ErrorEvent;

public class ErrorEventSource implements EventEmitter  {
	private static final Logger logger = LoggerFactory.getLogger(ErrorEventSource.class);
	
	private WebTarget target;
    
    public ErrorEventSource() {
    	initEventSource();
    }
    
    private void initEventSource() {
		Client client = ClientBuilder.newClient();
		if (System.getenv("ERROR_ENDPOINT") != null){
			target = client.target(System.getenv("ERROR_ENDPOINT"));
		}
    }
    
	/**
	 *  Emit an event to an http endpoint (e.g. knative channel or broker)
	 * 
	 */
	@Override
	public void emit(OrderEventBase event) throws Exception {
		if (target == null) initEventSource();
		ErrorEvent errorEvent = (ErrorEvent) event;
        String value = new Gson().toJson(errorEvent);
		String eventDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").format(new Date());
		String eventId = UUID.randomUUID().toString();

		try {
			String response = target.request()
				.header("X-B3-Flags","1")
				.header("CE-SpecVersion","0.2")
				.header("CE-Type", "ErrorEvent")
				.header("CE-Time", eventDate)
				.header("CE-ID", eventId)
				.header("CE-Source", "dev.knative.erroreventsource")
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
