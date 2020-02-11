package ibm.gse.orderms.app;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ibm.gse.orderms.infrastructure.kafka.OrderCommandAgent;
import ibm.gse.orderms.infrastructure.kafka.OrderEventAgent;


@Path("health")
public class StarterLivenessCheck  {

	@Inject
	OrderCommandAgent orderCommandsAgent;
	
	@Inject
	OrderEventAgent orderEventAgent;
	
	public StarterLivenessCheck(OrderCommandAgent orderCommandsAgent,
			OrderEventAgent orderEventAgent) {
		this.orderCommandsAgent = orderCommandsAgent;
		this.orderEventAgent = orderEventAgent;
	}
	
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response healthcheck() {
		long now = System.currentTimeMillis();
		boolean up = isAlive();
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
    public boolean isAlive() {
    	boolean status = orderCommandsAgent.isRunning() && orderEventAgent.isRunning();
		return status;
    }
}
