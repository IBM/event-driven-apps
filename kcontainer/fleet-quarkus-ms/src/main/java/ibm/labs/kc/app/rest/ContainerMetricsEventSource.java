package ibm.labs.kc.app.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibm.labs.kc.app.kafka.EventEmitter;
import ibm.labs.kc.event.model.BlueWaterEvent;
import ibm.labs.kc.event.model.ContainerMetric;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

public class ContainerMetricsEventSource implements EventEmitter {
	
	private static ContainerMetricsEventSource instance;
	private static final Logger logger = LoggerFactory.getLogger(ShipPositionEventSource.class);
	private WebTarget target;
	protected  Gson parser;

	
	private ContainerMetricsEventSource() {
		Client client = ClientBuilder.newClient();
		if (System.getenv("BLUEWATER_CONTAINER_ENDPOINT") != null){
			target = client.target(System.getenv("BLUEWATER_CONTAINER_ENDPOINT"));
		}	
		parser = new Gson();
	}
	
	public static ContainerMetricsEventSource getInstance() {
		if (instance == null) instance = new ContainerMetricsEventSource();
		return instance;
	}
	
	
	@Override
	public void emit(BlueWaterEvent event) throws Exception {
		ContainerMetric c = (ContainerMetric)event;
		String eventAsJson = parser.toJson(c);
		String eventDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());
		String eventId = UUID.randomUUID().toString();

		try {
			String response = target.request()
				.header("X-B3-Flags","1")
				.header("CE-SpecVersion","1.0")
				.header("CE-Type", "BlueWaterEvent")
				.header("CE-Time", eventDate)
				.header("CE-ID", eventId)
				.header("CE-Source", "dev.knative.containermetricseventsource")
				.post(Entity.entity(eventAsJson, MediaType.APPLICATION_JSON)
								, String.class);
			logger.info(response);
		} catch (Exception e){
			logger.error("Error in JAX-RS", e);
			throw e;
		}		
	}
	
}
