package ibm.labs.kc.serv.ut;

import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import ibm.labs.kc.app.kafka.ContainerMetricsProducer;
import ibm.labs.kc.app.kafka.ShipPositionProducer;
import ibm.labs.kc.app.rest.ShipService;
import ibm.labs.kc.dao.DAOFactory;
import ibm.labs.kc.dto.model.ShipSimulationControl;
import ibm.labs.kc.model.Container;
import ibm.labs.kc.model.Ship;
import ibm.labs.kc.simulator.ShipSimulator;

/**
 * simulate the ship movement and the container
 * @author jeromeboyer
 *
 */
public class TestShipSimulation  {
   
	 @Mock
	 static ShipPositionProducer positionProducerMock;
	 @Mock
	 static ContainerMetricsProducer containerProducerMock;
	 
	 @Rule public MockitoRule mockitoRule = MockitoJUnit.rule(); 
	 
	 public static ShipService serv;
	
	
	@Before
	public   void init() {
		 ShipSimulator s = new ShipSimulator(positionProducerMock,containerProducerMock);		 
		 serv =  new ShipService(DAOFactory.buildOrGetShipDAOInstance("Fleet.json"),s);
	}
	
	public void printShip(Ship s) {
		for (List<Container> row : s.getContainers()) {
			for (Container c : row) {
				System.out.println(c.toString() + " --- ");
			}
			System.out.println("\n---------------------");
		}
	}
	
	
	@Test
	public void validateContainerFire() throws InterruptedException {
		System.out.println("Validate containers fire");
		ShipSimulationControl ctl = new ShipSimulationControl("BlackBear", ShipSimulationControl.CONTAINER_FIRE);
		ctl.setNumberOfContainers(4);
		ctl.setNumberOfMinutes(.25);
		Response res = serv.performSimulation(ctl);
		Ship s = (Ship)res.getEntity();	
		Assert.assertTrue(s.getContainers().get(0).get(2).getStatus().equals(Container.STATUS_FIRE));
		//verify(positionPublisherMock).publishShipPosition(null);
		Thread.sleep(30000);
		ctl = new ShipSimulationControl("BlackBear", ShipSimulationControl.STOP);
		res = serv.performSimulation(ctl);
	}

	@Test
	public void validateContainerDown() throws InterruptedException {
		System.out.println("Validate containers down");
		ShipSimulationControl ctl = new ShipSimulationControl("JimminyCricket", ShipSimulationControl.REEFER_DOWN);
		ctl.setNumberOfMinutes(.25);
		Response res = serv.performSimulation(ctl);
		Thread.sleep(10000);
		Ship s = (Ship)res.getEntity();
		printShip(s);
	}
	
}
