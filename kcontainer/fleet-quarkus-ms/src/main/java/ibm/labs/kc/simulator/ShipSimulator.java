package ibm.labs.kc.simulator;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import ibm.labs.kc.app.kafka.ContainerMetricsProducer;
import ibm.labs.kc.app.kafka.EventEmitter;
import ibm.labs.kc.app.kafka.ShipPositionProducer;
import ibm.labs.kc.app.rest.ContainerMetricsEventSource;
import ibm.labs.kc.app.rest.ShipPositionEventSource;
import ibm.labs.kc.dto.model.ShipSimulationControl;
import ibm.labs.kc.model.Position;
import ibm.labs.kc.model.Ship;

/**
 * Focus on one ship and move it and play with its containers
 * @author jerome boyer
 *
 */
public class ShipSimulator extends KCSimulator {
	private EventEmitter positionPublisher;
	private EventEmitter containerPublisher;

	static ConcurrentHashMap<String, Thread> runners = new ConcurrentHashMap<String, Thread>();
	
	public ShipSimulator() {
		if (System.getenv("KAFKA_BROKERS") != null) {
		 this.positionPublisher = ShipPositionProducer.getInstance();
		 this.containerPublisher = ContainerMetricsProducer.getInstance();
		} else {
		 this.positionPublisher = ShipPositionEventSource.getInstance();
		 this.containerPublisher = ContainerMetricsEventSource.getInstance();
		} 
	}
	
	public ShipSimulator(ShipPositionProducer positionPublisher,
			ContainerMetricsProducer containerPublisher) {
		this.positionPublisher = positionPublisher;
		this.containerPublisher = containerPublisher;
	}


	/**
	 * Simulate the ship movement for n minutes. Each 5 s represents 2 hours of boat running, an one step in the 
	 * csv file for the position
	 * @param ship
	 * @param simulation controller
	 */
	public void addAndStart(Ship s, ShipSimulationControl ctl) {
		List<Position> shipPositions = readShipPositions(s.getName());
		ShipRunner shipRunner = new ShipRunner(this.positionPublisher,this.containerPublisher);
		shipRunner.init(s,shipPositions,ctl);
		Thread t = new Thread(shipRunner,s.getName());
		runners.put(s.getName(), t);
		t.start();
	}

	
	public void stop(Ship s) {
		Thread t = runners.get(s.getName());
		if (t != null) {
			t.interrupt();
		}
	}



}
