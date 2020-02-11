package ibm.labs.kc.simulator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import ibm.labs.kc.app.kafka.EventEmitter;
import ibm.labs.kc.dto.model.ShipSimulationControl;
import ibm.labs.kc.event.model.ContainerMetric;
import ibm.labs.kc.event.model.ShipPosition;
import ibm.labs.kc.model.Container;
import ibm.labs.kc.model.Position;
import ibm.labs.kc.model.Ship;

/**
 * This is the Ship Simulator. For each ship it sends position and its containers states to
 * remote topic every x seconds.
 * 
 * It uses thread to execute the ship movement in parallel. THIS is not mandatory it could have been done
 * sequentially. may be we need to revisit that.
 * 
 * The 'game time' is managed with x second = 45 minutes of real world and produce new position
 * @author jerome boyer
 *
 */
public class ShipRunner implements Runnable {
	public Logger logger = Logger.getLogger(ShipRunner.class.getName());
	public static final long WORLD_TIME_STEP = 45; // in minutes
	
	protected EventEmitter positionPublisher;
	protected EventEmitter containerPublisher;
	protected static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	protected String shipName;
	protected Ship ship;
	protected List<Position> positions;
	protected double numberOfMinutes;
	

	public ShipRunner(EventEmitter pb,EventEmitter cb) {
		this.positionPublisher = pb;
		this.containerPublisher = cb;
	}
	
	public void init(Ship s, List<Position> positions, ShipSimulationControl ctl) {
		if (ctl == null) {
			this.init(s, positions, 1);
			
		} else {
			this.init(s, positions, ctl.getNumberOfMinutes());
			// prepare some containers from the selected simulator
			if (ShipSimulationControl.CONTAINER_FIRE.equals(ctl.getCommand())) {
				BadEventSimulator.fireContainers(s, ctl.getNumberOfContainers());
			}
			if (ShipSimulationControl.HEAT_WAVE.equals(ctl.getCommand())) {
				BadEventSimulator.heatWave(s);
			}
			if (ShipSimulationControl.REEFER_DOWN.equals(ctl.getCommand())) {
				BadEventSimulator.reeferDown(s);
			}
		}
		
	}
	
	// Run the simulation for n minutes
	public void init(Ship s, List<Position> positions,double numberOfMinutes) {
		this.shipName = s.getName();
		this.positions = positions;
		this.ship = s;
		this.numberOfMinutes = numberOfMinutes;
	}

	
	@Override
	public void run() {		
		System.out.println("@@@@ Start simulation for the ship:" + shipName );
		Date currentWorldTime = new Date();
		try  { 
			Position previous = new Position(this.getShip().getLatitude(),this.getShip().getLongitude());
			for (Position p : this.positions) {
				// ship publishes its position to the ship topic 
				ShipPosition sp = new ShipPosition(this.shipName,p.getLatitude(),p.getLongitude(),currentWorldTime.getTime());
				if (p.equals(previous)) {
					sp.setStatus(Ship.AT_PORT);
					this.getShip().setStatus(Ship.AT_PORT);
				} else {
					sp.setStatus(Ship.AT_SEA);
					this.getShip().setStatus(Ship.AT_SEA);
					sp.setSpeed(15);
					sp.setAmbiantTemperature(22);
					sp.setCompass(310);
				}
				previous = p;
				logger.info(sp.toString());
				this.getShip().setLatitude(p.getLatitude());
				this.getShip().setLongitude(p.getLongitude());
				positionPublisher.emit(sp);
				
				// Then publishes the state of their containers
				for (List<Container> row :  ship.getContainers()) {
					for (Container c : row) {
						ContainerMetric cm = BadEventSimulator.buildContainerMetric(this.getShip(),c,currentWorldTime.getTime());
						containerPublisher.emit(cm);
					}
				}
				currentWorldTime=modifyTime(currentWorldTime);
	            // The simulation needs to run in x minutes, so play all the position in this time
				Thread.currentThread().sleep(Math.round(this.numberOfMinutes*60000/this.positions.size()));	
			}
        } catch (Exception e) { 
        	e.printStackTrace();
            System.out.println ("ShipRunner stopped"); 
            Thread.currentThread().interrupt();
        } finally {
        	/** do not want to close as producers are thread safe and reusable.
        	if (containerPublisher != null)
        		containerPublisher.close();
        	if (positionPublisher != null)
        		positionPublisher.close();
        	*/
        }
	}
	
	
	private Date modifyTime(Date currentWorldTime) {
		Date d = new Date(currentWorldTime.getTime() + WORLD_TIME_STEP * 60000);
		return d;
	}


	public String getShipName() {
		return shipName;
	}

	public Ship getShip() {
		return ship;
	}

	public List<Position> getPositions() {
		return positions;
	}

	public double getNumberOfMinutes() {
		return numberOfMinutes;
	}
	
	
	
} // class ShipRunner

