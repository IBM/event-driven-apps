package ibm.labs.kc.containermgr.rest;

import java.util.List;
import java.util.logging.Logger;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import ibm.labs.kc.containermgr.ContainerService;
import ibm.labs.kc.containermgr.OrderProducer;
import ibm.labs.kc.containermgr.dao.OrderDAO;
import ibm.labs.kc.model.container.ContainerOrder;
import ibm.labs.kc.model.events.ContainerNotFoundEvent;
import ibm.labs.kc.model.events.OrderCreationEvent;
import ibm.labs.kc.model.events.OrderEvent;
import ibm.labs.kc.model.events.OrderRejectedEvent;
import ibm.labs.kc.order.model.Order;
/*
 * Consume events from 'orders' topic. Started when the spring application context
 * is initialized.
 */
@RestController
public class OrderEventConsumer {
	private static final Logger LOG = Logger.getLogger(OrderEventConsumer.class.toString());
	private Gson parser = new Gson();

	@Autowired
	private ContainerService containerService;

	@Autowired
	private OrderDAO orderDAO;

	@Autowired
	private OrderProducer orderProducer;


	@PostMapping("/orderevents")
    // Request Container so that if new latitude, longitude or capacity changes due to the maintenance, the container gets updated
    public ResponseEntity<String> onEvent(@Valid @RequestBody String orderEvent) {
		LOG.info("Received order event:" + orderEvent);
		if (orderEvent.contains(OrderEvent.TYPE_CREATED)) {
			OrderCreationEvent oe = parser.fromJson(orderEvent, OrderCreationEvent.class);
			Order order = oe.getPayload();
			List<ContainerOrder> listOfContainers = containerService.assignContainerToOrder(order);
			if (listOfContainers.size()>0){
				String containers="";
				for (ContainerOrder co : listOfContainers){
					orderDAO.save(co);
					containers = containers + co.getContainerID() + " ";
				} 
				LOG.info("These are the containers assigned for the order " + order.getOrderID() + ": " + containers);
			}
			else {
				LOG.info("There is no container available for orderID: " + order.getOrderID() + ". The order will be rejected.");
				orderProducer.emit(new ContainerNotFoundEvent(order.getOrderID(), "A container could not be found for this order"));
			}
		}
		if (orderEvent.contains(OrderEvent.TYPE_REJECTED)) {
			OrderRejectedEvent orderRejected = parser.fromJson(orderEvent, OrderRejectedEvent.class);
			Order order = orderRejected.getPayload();
			// Only unassign container from order when a container has previously been assigned.
			// Otherwise, this OrderReject event comes from this very microservice where a container had not been assigned.
			if (order.getContainerID() != null && order.getContainerID() != "" && !order.getContainerID().isEmpty()){
				if (!containerService.unAssignContainerToOrder(order))
					LOG.severe("[ERROR] - An error occurred unassigning container: " + order.getContainerID() + " from order: " + order.getOrderID());
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body("ok");
	}



}
