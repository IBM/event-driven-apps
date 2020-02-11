package ibm.labs.kc.app.rest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import ibm.labs.kc.app.kafka.EventEmitter;
import ibm.labs.kc.event.model.BlueWaterEvent;
import ibm.labs.kc.event.model.ShipPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

public class ShipPositionEventSource implements EventEmitter {
	 private static final Logger logger = LoggerFactory.getLogger(ShipPositionEventSource.class);
	 private static ShipPositionEventSource instance;
	 private WebTarget target;
	 protected  Gson parser;
	 
	 private ShipPositionEventSource() {
		Client client = ClientBuilder.newClient();
		if (System.getenv("BLUEWATER_SHIP_ENDPOINT") != null) {
			target = client.target(System.getenv("BLUEWATER_SHIP_ENDPOINT"));
		}
		parser = new Gson();
	 }
	 
	public synchronized static EventEmitter getInstance() {
		if (instance == null) instance = new ShipPositionEventSource();
		return instance;
	}
	
	@Override
	public void emit(BlueWaterEvent event) throws InterruptedException, ExecutionException, TimeoutException {
		ShipPosition sp = (ShipPosition) event;
		String eventAsJson = parser.toJson(sp);
		String key = sp.getShipID();

		String eventDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());
		String eventId = UUID.randomUUID().toString();

		try {
			String response = target.request()
				.header("X-B3-Flags","1")
				.header("CE-SpecVersion","1.0")
				.header("CE-Type", "BlueWaterEvent")
				.header("CE-Time", eventDate)
				.header("CE-ID", eventId)
				.header("CE-Source", "dev.knative.shippositioneventsource")
				.post(Entity.entity(eventAsJson, MediaType.APPLICATION_JSON)
								, String.class);
			logger.info(response);
		} catch (Exception e){
			logger.error("Error in JAX-RS", e);
			throw e;
		}		
	}
}
