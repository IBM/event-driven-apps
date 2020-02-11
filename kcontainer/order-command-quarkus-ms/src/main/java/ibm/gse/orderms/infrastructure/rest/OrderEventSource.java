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
import ibm.gse.orderms.infrastructure.events.OrderEvent;
import ibm.gse.orderms.infrastructure.events.OrderEventBase;
import ibm.gse.orderms.infrastructure.events.OrderRejectEvent;
import ibm.gse.orderms.infrastructure.events.OrderRejectPayload;
import ibm.gse.orderms.infrastructure.events.ShippingOrderPayload;

public class OrderEventSource implements EventEmitter  {
	private static final Logger logger = LoggerFactory.getLogger(OrderEventSource.class);
	
	private WebTarget target;
    
    public OrderEventSource() {
    	initEventSource();
    }
    
    private void initEventSource() {
		Client client = ClientBuilder.newClient();
		if (System.getenv("ORDER_ENDPOINT") != null) {
			target = client.target(System.getenv("ORDER_ENDPOINT"));
		}
    }
    
	/**
	 *  Emit an event to an http endpoint (e.g. knative channel or broker)
	 * 
	 */
	@Override
	public void emit(OrderEventBase event) throws Exception {
		if (target == null) initEventSource();
		
		String key;
        String value;
        switch (event.getType()) {
        case OrderEvent.TYPE_ORDER_CREATED:
        case OrderEvent.TYPE_ORDER_UPDATED:
            OrderEvent orderEvent = (OrderEvent)event;
            key = ((ShippingOrderPayload)orderEvent.getPayload()).getOrderID();
            value = new Gson().toJson(orderEvent);
            break;
        case OrderEvent.TYPE_ORDER_REJECTED:
            OrderRejectEvent orderRejected = (OrderRejectEvent) event;
            key = ((OrderRejectPayload)orderRejected.getPayload()).getOrderID();
            value = new Gson().toJson(orderRejected);
            break;
        default:
            key = null;
            value = null;
        }

		String eventDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());
		String eventId = UUID.randomUUID().toString();

		try {
			String response = target.request()
				.header("X-B3-Flags","1")
				.header("CE-SpecVersion","1.0")
				.header("CE-Type", "OrderEvent")
				.header("CE-Time", eventDate)
				.header("CE-ID", eventId)
				.header("CE-Source", "dev.knative.ordereventsource")
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
