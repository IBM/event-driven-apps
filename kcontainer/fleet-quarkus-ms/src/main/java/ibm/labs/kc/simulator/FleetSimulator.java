package ibm.labs.kc.simulator;

import ibm.labs.kc.app.kafka.ContainerMetricsProducer;
import ibm.labs.kc.app.kafka.ShipPositionProducer;
import ibm.labs.kc.dto.model.ShipSimulationControl;
import ibm.labs.kc.model.Fleet;
import ibm.labs.kc.model.Ship;

/**
 * Simulate all the ships of a fleet. This means it keeps a map of ship, thread
 * 
 * @author jerome boyer
 *
 */
public class FleetSimulator extends KCSimulator {
	
	public ShipSimulator shipSimulator = new ShipSimulator();
	
	public FleetSimulator() {
		this.shipSimulator = new ShipSimulator();
	}
	
	public FleetSimulator(ShipPositionProducer positionPublisher,
			ContainerMetricsProducer containerPublisher) {
		this.shipSimulator = new ShipSimulator(positionPublisher,containerPublisher);
	}
	
	/**
	 * Simulate the ship movement for n minutes. Each 5 s represents 2 hours of boat running
	 * @param fleet
	 * @param d
	 */
	public void start(Fleet f, double d) {
		
		for (Ship s : f.getShips()) {			
			ShipSimulationControl ctl = new ShipSimulationControl(s.getName(), ShipSimulationControl.RUN_VESSELS, d);
			shipSimulator.addAndStart(s, ctl);
		}
	}

	
	public void stop(Fleet f) {
		for (Ship s : f.getShips()) {
			shipSimulator.stop(s);
		}
	}
}
