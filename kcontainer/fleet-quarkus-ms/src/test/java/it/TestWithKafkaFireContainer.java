package it;

import java.util.List;

import javax.ws.rs.core.Response;

import ibm.labs.kc.app.rest.ShipService;
import ibm.labs.kc.dto.model.ShipSimulationControl;
import ibm.labs.kc.event.model.ContainerMetric;
import ibm.labs.kc.model.Container;
import ibm.labs.kc.model.Ship;

public class TestWithKafkaFireContainer {

	public static void main(String[] args) {
		ShipService serv = new ShipService("Fleet.json");
		System.out.println("Validate  containers fire");
		ShipSimulationControl ctl = new ShipSimulationControl("JimminyCricket", ShipSimulationControl.CONTAINER_FIRE);
		ctl.setNumberOfContainers(4);
		ctl.setNumberOfMinutes(1);
		Response res = serv.performSimulation(ctl);
		Ship s = (Ship)res.getEntity();
		for (List<Container> row : s.getContainers()) {
			for (Container c : row) {
				System.out.print(c.toString() + " --- ");
			}
			System.out.println("\n---------------------");
		}
		ContainerEventConsumer consumer = new ContainerEventConsumer();
		for (ContainerMetric cm: consumer.consume()) {
			System.out.println(cm.toString());
		}
	}

}
