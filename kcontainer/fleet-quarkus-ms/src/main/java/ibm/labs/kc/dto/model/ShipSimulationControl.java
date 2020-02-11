package ibm.labs.kc.dto.model;

/**
 * Exchange control information for ship simulation. This is a DTO for controlling the 
 * simulation behavior.
 * @author jerome boyer
 *
 */
public class ShipSimulationControl {
	public static final String CONTAINER_FIRE = "CONTAINER_FIRE";
	public static final String HEAT_WAVE = "HEAT_WAVE";
	public static final String REEFER_DOWN = "REEFER_DOWN";
	public static final String STOP = "STOP";
	public static final String RUN_VESSELS = "RUN_VESSELS ";
	protected String shipName;
	protected String command;
	private int numberOfContainers = 1;
	public double numberOfMinutes = 1;

	public ShipSimulationControl() {}
	
	public  ShipSimulationControl(String name, String command) {
		this.shipName = name;
		this.command = command;
	}
	public  ShipSimulationControl(String name, String command,double mnt) {
		this.shipName = name;
		this.command = command;
		this.numberOfMinutes = mnt;
	}
	
	public String getShipName() {
		return shipName;
	}

	public String getCommand() {
		return command;
	}

	public int getNumberOfContainers() {
		return numberOfContainers;
	}

	public void setNumberOfContainers(int numberOfContainers) {
		this.numberOfContainers = numberOfContainers;
	}

	public double getNumberOfMinutes() {
		return numberOfMinutes;
	}

	public void setNumberOfMinutes(double numberOfMinutes) {
		this.numberOfMinutes = numberOfMinutes;
	}

	public void setShipName(String shipName) {
		this.shipName = shipName;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
}
