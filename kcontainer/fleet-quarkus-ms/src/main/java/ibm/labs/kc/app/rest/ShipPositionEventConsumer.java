package ibm.labs.kc.app.rest;

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


@Path("/bluewatershipevents")
public class ShipPositionEventConsumer {
    static final Logger logger = LoggerFactory.getLogger(ShipPositionEventConsumer.class);
   
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response consumeEvent(String shipEvent) {
        logger.info("@@@@@ ShipPositionEventConsumer @@@@@@@@@ ************ \n" + shipEvent);
        
	    //return Response.ok().entity(order.getOrderID()).build();
	    //API contract expects a JSON Object and not just a plaintext string
	    return Response.ok().entity("ok").build();
    }
    
   

}
