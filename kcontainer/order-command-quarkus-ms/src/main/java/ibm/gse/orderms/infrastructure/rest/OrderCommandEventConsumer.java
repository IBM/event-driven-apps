package ibm.gse.orderms.infrastructure.rest;

import java.util.Date;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibm.gse.orderms.domain.model.order.ShippingOrder;
import ibm.gse.orderms.infrastructure.AppRegistry;
import ibm.gse.orderms.infrastructure.command.events.OrderCommandEvent;
import ibm.gse.orderms.infrastructure.events.OrderEvent;
import ibm.gse.orderms.infrastructure.kafka.ErrorEvent;
import ibm.gse.orderms.infrastructure.repository.OrderCreationException;
import ibm.gse.orderms.infrastructure.repository.OrderUpdateException;
import ibm.gse.orderms.infrastructure.repository.ShippingOrderRepository;

@Path("/ordercommandevents")
public class OrderCommandEventConsumer {
    static final Logger logger = LoggerFactory.getLogger(OrderCommandEventConsumer.class);
    private static ShippingOrderRepository orderRepository = AppRegistry.getInstance().shippingOrderRepository();
    private String schemaVersion = "1";
    private static OrderEventSource orderEventSource = new OrderEventSource();
    private static ErrorEventSource errorEventSource = new ErrorEventSource();


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response consumeEvent(String orderCommandEventStr) {
		logger.info("@@@@@ OrderCommandEventConsumer @@@@@@@@@ ************");
		OrderCommandEvent orderCommandEvent = OrderCommandEvent.deserialize(orderCommandEventStr);
        logger.info("handle command event : " + orderCommandEvent.getType());
		
		switch (orderCommandEvent.getType()) {
        case OrderCommandEvent.TYPE_CREATE_ORDER:
        	processOrderCreation(orderCommandEvent);
            break;
        case OrderCommandEvent.TYPE_UPDATE_ORDER:
        	processOrderUpdate(orderCommandEvent);
            break;
		}
		
	    return Response.ok().entity("ok").build();
    }
    
    /**
	 * Handle create order command: persist order into repository and emit event 'order created'
	 * for others to consume. The order is in pending mode until others services responded
	 * with a Voyage and a Reefer assignment events. When the 'order created` event is generated, the
	 * read from command topic can be committed. It commits the offset only 
	 * when both save to the repository and send order created events succeed. 
	 * 
	 * If it is not able to persist, it does not emit event on orders topic, 
	 * but emits on errors topic. The error topics is managed downstream by other component like a CLI 
	 * or an automatic recovery process. The data are not lost as they are still in order command topic.  
	 * 
	 * If publishing to any topic, even after retries, fails then the approach is 
	 * to die and let the scheduler recreate the app, and connection to kafka may be 
	 * recovered. 
	 * 
	 * The order is still in pending and the offset is not committed. So it is possible
	 * to restart the app and it will reload from the last committed offset. So the data
	 * may be reprocessed, therefore the repository should do nothing in this duplicate info
	 * if it found the order already created.
	 *  
	 * @param commandEvent
	 */
	private void processOrderCreation(OrderCommandEvent commandEvent ) {
		logger.info("@@@@ processOrderCreation()!!");
		ShippingOrder shippingOrder = (ShippingOrder) commandEvent.getPayload();
		shippingOrder.setStatus(ShippingOrder.PENDING_STATUS);
		try {
    		orderRepository.addOrUpdateNewShippingOrder(shippingOrder);	
		} catch (OrderCreationException e) {
			// need other components to fix this save operation: CLI / human or automatic process
			generateErrorEvent(shippingOrder);
    		return ; 
    	}
        OrderEvent orderCreatedEvent = new OrderEvent(new Date().getTime(),
                		OrderEvent.TYPE_ORDER_CREATED,
                		schemaVersion,
                		shippingOrder.toShippingOrderPayload());
        try {
        	orderEventSource.emit(orderCreatedEvent);
		} catch (Exception e) {
			// the order is in the repository but the app could not send to event backbone
			// consider communication with backbone as major issue
			e.printStackTrace();
		}   
	}
	
	/**
	 * For the order update 
	 * @param commandEvent
	 */
	private void processOrderUpdate(OrderCommandEvent commandEvent) {
	    ShippingOrder shippingOrder = (ShippingOrder) commandEvent.getPayload();
        String orderID = shippingOrder.getOrderID();
        try {
        	 Optional<ShippingOrder> oco = orderRepository.getOrderByOrderID(orderID);
        	 if (oco.isPresent()) {
        		 orderRepository.updateShippingOrder(shippingOrder);
        		 OrderEvent orderUpdateEvent = new OrderEvent(new Date().getTime(),
         			  	OrderEvent.TYPE_ORDER_UPDATED,
         			    schemaVersion,
         			  	shippingOrder.toShippingOrderPayload());
        		 try {       
	        		 orderEventSource.emit(orderUpdateEvent);
        		 } catch (Exception e) {
        			e.printStackTrace();
        		 }
        	 } else {
        		logger.error("Cannot update order - Unknown order Id " + orderID);
        		generateErrorEvent(shippingOrder);
        	 }
        } catch (OrderUpdateException oue) {
        	generateErrorEvent(shippingOrder);
        }
	}

	
	private void generateErrorEvent(ShippingOrder shippingOrder) {
		ErrorEvent errorEvent = new ErrorEvent(new Date().getTime(), 
				schemaVersion, 
				shippingOrder.toShippingOrderPayload(),
				"Repository access issue");
		try {
			errorEventSource.emit(errorEvent);
		} catch (Exception e1) {
			e1.printStackTrace();
			logger.error("Error event production error for order: " + shippingOrder.getOrderID());
		}
	}

}
