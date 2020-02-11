package ibm.gse.orderms.app;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ibm.gse.orderms.infrastructure.kafka.OrderCommandAgent;
import ibm.gse.orderms.infrastructure.kafka.OrderEventAgent;


@Path("ready")
public class StarterReadinessCheck {

	@Inject
	OrderCommandAgent orderCommandsAgent;
	
	@Inject
	OrderEventAgent orderEventAgent;
	
	public StarterReadinessCheck(OrderCommandAgent commandAgent, OrderEventAgent eventAgent) {
		this.orderCommandsAgent = commandAgent;
		this.orderEventAgent = eventAgent;
	}

	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response healthcheck() {
		long now = System.currentTimeMillis();
		boolean up = isReady();
		if(up){
			return Response.ok("{\"status\":\"UP\", \"when\":\"" + now + "\"}").build();
		} else {
			return Response.status(400).build();
		}
    }
	
	/**
	 * Verify each agent is alive
	 * @return
	 */
    public boolean isReady() {
    	boolean status = orderCommandsAgent.isRunning() && orderEventAgent.isRunning();
		return status;
    }
    
}
