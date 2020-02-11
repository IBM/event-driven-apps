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
import com.google.gson.Gson;

import ibm.gse.orderms.domain.model.order.ShippingOrder;
import ibm.gse.orderms.infrastructure.AppRegistry;
import ibm.gse.orderms.infrastructure.events.EventListener;
import ibm.gse.orderms.infrastructure.events.OrderCancellationPayload;
import ibm.gse.orderms.infrastructure.events.OrderCancelledEvent;
import ibm.gse.orderms.infrastructure.events.OrderEvent;
import ibm.gse.orderms.infrastructure.events.OrderEventBase;
import ibm.gse.orderms.infrastructure.events.OrderRejectEvent;
import ibm.gse.orderms.infrastructure.events.OrderSpoiltEvent;
import ibm.gse.orderms.infrastructure.events.OrderSpoiltPayload;
import ibm.gse.orderms.infrastructure.events.reefer.ReeferAssignedEvent;
import ibm.gse.orderms.infrastructure.events.reefer.ReeferAssignmentPayload;
import ibm.gse.orderms.infrastructure.events.reefer.ReeferNotFoundEvent;
import ibm.gse.orderms.infrastructure.events.reefer.ReeferNotFoundPayload;
import ibm.gse.orderms.infrastructure.events.voyage.VoyageAssignedEvent;
import ibm.gse.orderms.infrastructure.events.voyage.VoyageAssignmentPayload;
import ibm.gse.orderms.infrastructure.events.voyage.VoyageNotFoundEvent;
import ibm.gse.orderms.infrastructure.events.voyage.VoyageNotFoundPayload;
import ibm.gse.orderms.infrastructure.repository.ShippingOrderRepository;

@Path("/orderevents")
public class OrderEventConsumer implements EventListener{
    static final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);
    private static ShippingOrderRepository orderRepository = AppRegistry.getInstance().shippingOrderRepository();
    private String schemaVersion = "1";
    private static OrderEventSource orderEventSource = new OrderEventSource();
	private static final Gson gson = new Gson();


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response consumeEvent(String orderEvent) {
        logger.info("@@@@@ OrderEventConsumer @@@@@@@@@ ************ \n"+orderEvent);       
        
		OrderEventBase event = deserialize(orderEvent);

        handle(event);
		
	    //return Response.ok().entity(order.getOrderID()).build();
	    //API contract expects a JSON Object and not just a plaintext string
	    return Response.ok().entity("ok").build();
	}
	
	public OrderEventBase deserialize(String eventAsString) {
    	OrderEventBase orderEvent = gson.fromJson(eventAsString, OrderEventBase.class);
        switch (orderEvent.getType()) {
            case OrderEventBase.TYPE_ORDER_CREATED:
			case OrderEventBase.TYPE_ORDER_UPDATED:
				return gson.fromJson(eventAsString, OrderEvent.class);
			case OrderEventBase.TYPE_ORDER_REJECTED:
				return gson.fromJson(eventAsString, OrderRejectEvent.class);
            case OrderEventBase.TYPE_VOYAGE_ASSIGNED:
				return gson.fromJson(eventAsString, VoyageAssignedEvent.class);
			case OrderEventBase.TYPE_CONTAINER_NOT_FOUND:
				return gson.fromJson(eventAsString, ReeferNotFoundEvent.class);
			case OrderEventBase.TYPE_VOYAGE_NOT_FOUND:
                return gson.fromJson(eventAsString, VoyageNotFoundEvent.class);
            case OrderEventBase.TYPE_ORDER_CANCELLED:
                return gson.fromJson(eventAsString, OrderCancelledEvent.class);
            case OrderEventBase.TYPE_CONTAINER_ALLOCATED:
                return gson.fromJson(eventAsString, ReeferAssignedEvent.class);
            case OrderEventBase.TYPE_ORDER_SPOILT:
           	    return gson.fromJson(eventAsString, OrderSpoiltEvent.class);
            default:
                logger.warn("Not supported event: " + eventAsString);
                return null;
        }
    }
    
	@Override
	public void handle(OrderEventBase orderEvent) {
		 try {
	            if (orderEvent == null) return;
	            switch (orderEvent.getType()) {
	            case OrderEventBase.TYPE_VOYAGE_ASSIGNED:
	                synchronized (orderRepository) {
	                	VoyageAssignmentPayload voyageAssignment = (VoyageAssignmentPayload)((VoyageAssignedEvent) orderEvent).getPayload();
	                    String orderID = voyageAssignment.getOrderID();
	                    Optional<ShippingOrder> oco = orderRepository.getOrderByOrderID(orderID);
	                    if (oco.isPresent()) {
	                        ShippingOrder shippingOrder = oco.get();
	                        shippingOrder.assign(voyageAssignment);
	                        orderRepository.updateShippingOrder(shippingOrder);
	                    } else {
	                        throw new IllegalStateException("Cannot update - Unknown order Id " + orderID);
	                    }
	                }
	                break;
	            case OrderEventBase.TYPE_ORDER_CANCELLED:
	                synchronized (orderRepository) {
	                    OrderCancellationPayload cancellation = ((OrderCancelledEvent) orderEvent).getPayload();
	                    String orderID = cancellation.getOrderID();
	                    Optional<ShippingOrder> oco = orderRepository.getOrderByOrderID(orderID);
	                    if (oco.isPresent()) {
	                        ShippingOrder shippingOrder = oco.get();
	                        shippingOrder.cancel(cancellation);
	                        orderRepository.updateShippingOrder(shippingOrder);
	                    } else {
	                        throw new IllegalStateException("Cannot update - Unknown order Id " + orderID);
	                    }
	                }
	                break;
	            case OrderEventBase.TYPE_CONTAINER_ALLOCATED:
	            	synchronized (orderRepository) {
	            		ReeferAssignmentPayload ca = ((ReeferAssignedEvent) orderEvent).getPayload();
		            	String orderID = ca.getOrderID();
		            	Optional<ShippingOrder> oco = orderRepository.getOrderByOrderID(orderID);
		            	if (oco.isPresent()) {
		                     ShippingOrder shippingOrder = oco.get();
		                     shippingOrder.assignContainer(ca);
		                     orderRepository.updateShippingOrder(shippingOrder);
		                } else {
		                    throw new IllegalStateException("Cannot update - Unknown order Id " + orderID);
		                }
	            	}
                    break;
                case OrderEventBase.TYPE_ORDER_SPOILT:
	            	synchronized (orderRepository) {
	            		OrderSpoiltPayload os = ((OrderSpoiltEvent) orderEvent).getPayload();
		            	String orderID = os.getOrderID();
		            	Optional<ShippingOrder> oco = orderRepository.getOrderByOrderID(orderID);
		            	if (oco.isPresent()) {
		                     ShippingOrder shippingOrder = oco.get();
		                     shippingOrder.spoilOrder();
		                     orderRepository.updateShippingOrder(shippingOrder);
		                } else {
		                    throw new IllegalStateException("Cannot update - Unknown order Id " + orderID);
		                }
	            	}
					break;
				case OrderEventBase.TYPE_CONTAINER_NOT_FOUND:
	            	synchronized (orderRepository) {
	            		ReeferNotFoundPayload payload = ((ReeferNotFoundEvent) orderEvent).getPayload();
		            	String orderID = payload.getOrderID();
		            	Optional<ShippingOrder> oco = orderRepository.getOrderByOrderID(orderID);
		            	if (oco.isPresent()) {
		                     ShippingOrder shippingOrder = oco.get();
		                     shippingOrder.rejectOrder();
							 orderRepository.updateShippingOrder(shippingOrder);
							 sendOrderRejectEvent(shippingOrder, payload.getReason());
		                } else {
		                    throw new IllegalStateException("Cannot update - Unknown order Id " + orderID);
		                }
	            	}
	            	break;
				case OrderEventBase.TYPE_VOYAGE_NOT_FOUND:
	            	synchronized (orderRepository) {
	            		VoyageNotFoundPayload payload = ((VoyageNotFoundEvent) orderEvent).getPayload();
		            	String orderID = payload.getOrderID();
		            	Optional<ShippingOrder> oco = orderRepository.getOrderByOrderID(orderID);
		            	if (oco.isPresent()) {
		                     ShippingOrder shippingOrder = oco.get();
		                     shippingOrder.rejectOrder();
							 orderRepository.updateShippingOrder(shippingOrder);
							 sendOrderRejectEvent(shippingOrder, payload.getReason());
		                } else {
		                    throw new IllegalStateException("Cannot update - Unknown order Id " + orderID);
		                }
	            	}
	            	break;
	            case OrderEventBase.TYPE_ORDER_CREATED:
				case OrderEventBase.TYPE_ORDER_UPDATED:
				case OrderEventBase.TYPE_ORDER_REJECTED:
	            	break;
	            default:
	                logger.warn("Not yet implemented event type: " + orderEvent.getType());
	            }
	        } catch (Exception e) {
	            logger.error((new Date()).toString() + " " + e.getMessage(), e);
	        }
		
	}

	public void sendOrderRejectEvent (ShippingOrder shippingOrder, String reason){
		OrderRejectEvent orderRejectedEvent = new OrderRejectEvent(new Date().getTime(), schemaVersion, shippingOrder.toOrderRejectPayload(reason));
		try {
			orderEventSource.emit(orderRejectedEvent);
		} catch (Exception e) {
			// the order is in the repository but the app could not send to event backbone
			// consider communication with backbone as major issue
			e.printStackTrace();
		}
	}

}
