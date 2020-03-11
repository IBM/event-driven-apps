package ibm.labs.kc.app.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("/bluewatercontainerevents")
public class ContainerMetricsEventConsumer {
    static final Logger logger = LoggerFactory.getLogger(ContainerMetricsEventConsumer.class);
   
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response consumeEvent(String containerEvent) {
        logger.info("@@@@@ Hello ContainerMetricsEventConsumer @@@@@@@@@ ************ \n" + containerEvent);
        
	    //return Response.ok().entity(order.getOrderID()).build();
	    //API contract expects a JSON Object and not just a plaintext string
	    return Response.ok().entity("ok").build();
    }
    
   

}
