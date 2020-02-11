package ibm.labs.kc.app.rest;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ibm.labs.kc.dao.DAOFactory;
import ibm.labs.kc.dao.ShipDAO;
import ibm.labs.kc.dto.model.ShipSimulationControl;
import ibm.labs.kc.model.Container;
import ibm.labs.kc.model.Ship;
import ibm.labs.kc.simulator.ShipSimulator;

/**
 * REST resource for ships
 * @author jeromeboyer
 *
 */

@Path("ships")
public class ShipService {
	
	protected ShipDAO dao;
	
	protected ShipSimulator simulator;
	
	public ShipService() {
	  dao = DAOFactory.buildOrGetShipDAOInstance();	
	  simulator = new ShipSimulator();
	}
	
	public ShipService(ShipDAO d,ShipSimulator s) {
		simulator = s;
		dao = d;	
	}
	
	public ShipService(String fileName) {
	   dao = DAOFactory.buildOrGetShipDAOInstance(fileName);
	   simulator = new ShipSimulator();
	}
	
	/**
	 * Given the ship name perform moving the ship and create the base condition for
	 * each container impacted by the simulation 
	 * @param simulation controller
	 * @return the ship
	 */
	@POST
	@Path(value="/simulate")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response performSimulation(ShipSimulationControl ctl) {
		Ship s = dao.getShipByName(ctl.getShipName());
		if (s == null) {
			return Response.status(404).build();
		}
		if (ShipSimulationControl.STOP.equals(ctl.getCommand())) {
			simulator.stop(s);
			s.setContainers(new ArrayList<ArrayList<Container>>());
			return Response.ok().entity(s).build();
		}
		s=dao.loadContainersForTheShip(s);
		simulator.addAndStart(s,ctl);
		return Response.ok().entity(s).build();
	}
	
	/**
	 * The docker loads container on boat, so the operation is modeled here 
	 */
	public void loadContainerOnShip(String containerID, String shipID) {
		
	}
	
}
