package ibm.labs.kc.containermgr.rest;

import java.util.List;
import java.util.logging.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import ibm.labs.kc.containermgr.OrderProducer;
import ibm.labs.kc.model.events.OrderEvent;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import com.google.gson.Gson;

@Component
@Conditional(OnKafkaDisabledCondition.class)
public class OrderProducerImpl implements OrderProducer {
	private static final Logger LOG = Logger.getLogger(OrderProducerImpl.class.toString());
	private WebTarget target;
	List<OrderEvent> eventsSent;

	public OrderProducerImpl() {
		LOG.info("Using REST implementation:");
		Client client = ClientBuilder.newClient();
		if (System.getenv("ORDER_ENDPOINT")!=null){
			target = client.target(System.getenv("ORDER_ENDPOINT"));
		}
		eventsSent=new ArrayList<OrderEvent>();
	}

	@Override
	public void emit(OrderEvent co) {
		String value = new Gson().toJson(co);
		String eventDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());
		String eventId = UUID.randomUUID().toString();

		try {
			String response = target.request()
				.header("X-B3-Flags","1")
				.header("CE-SpecVersion","1.0")
				.header("CE-Type", "OrderEvent")
				.header("CE-Time", eventDate)
				.header("CE-ID", eventId)
				.header("CE-Source", "dev.knative.orderproducerimpl")
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
	public List<OrderEvent> getEventsSent() {
		return eventsSent;
	}
}
