package ibm.labs.kc.containermgr.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import ibm.labs.kc.containermgr.ContainerProducer;
import ibm.labs.kc.model.events.ContainerEvent;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import com.google.gson.Gson;

@Component
@Conditional(OnKafkaDisabledCondition.class)
public class ContainerProducerImpl implements ContainerProducer {
	private static final Logger LOG = Logger.getLogger(ContainerProducerImpl.class.toString());
	private WebTarget target;
	List<ContainerEvent> eventsSent;

	public ContainerProducerImpl() {
		LOG.info("Using REST implementation.");
		Client client = ClientBuilder.newClient();
		if (System.getenv("CONTAINER_ENDPOINT")!= null ){
			target = client.target(System.getenv("CONTAINER_ENDPOINT"));
		}
		eventsSent=new ArrayList<ContainerEvent>();
	}

	@Override
	public void emit(ContainerEvent co) {
		String value = new Gson().toJson(co);
		String eventDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());
		String eventId = UUID.randomUUID().toString();

		try {
			String response = target.request()
				.header("X-B3-Flags","1")
				.header("CE-SpecVersion","1.0")
				.header("CE-Type", "ContainerEvent")
				.header("CE-Time", eventDate)
				.header("CE-ID", eventId)
				.header("CE-Source", "dev.knative.containerproducerimpl")
				.post(Entity.entity(value, MediaType.APPLICATION_JSON)
								, String.class);
			LOG.info(response);
			eventsSent.add(co);
		} catch (Exception e){
			LOG.info("Error in JAX-RS: " + e);
			throw e;
		}		
	}

	@Override
	public List<ContainerEvent> getEventsSent() {
		return eventsSent;
	}

}
