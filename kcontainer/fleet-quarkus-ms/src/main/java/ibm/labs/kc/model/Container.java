package ibm.labs.kc.model;

public class Container {
	public static final String STATUS_RUN = "RUN";
	public static final String STATUS_DOWN = "DOWN";
	public static final String STATUS_HEAT = "HEAT";
	public static final String STATUS_FIRE = "FIRE";
	
	protected String id;
	protected String type;
	protected long temperature;
	protected float humidity;
	protected float co2;
	protected long amp;
	protected String status = STATUS_RUN;
    // this is the position of the container in the boat
	protected long row;
	protected long column;
	protected String shipId; // in case the container fails in water we knew where it comes from ;-))

	public Container() {
		
	}
	
	public String toString() {
		return getId() + " " + getType() + " " + getRow() + ":" + getColumn() + " T:" + getTemperature() + " A:" + getAmp() + " " + getStatus();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getTemperature() {
		return temperature;
	}

	public void setTemperature(long temperature) {
		this.temperature = temperature;
	}

	public long getAmp() {
		return amp;
	}

	public void setAmp(long amp) {
		this.amp = amp;
	}

	public long getRow() {
		return row;
	}

	public void setRow(long row) {
		this.row = row;
	}

	public long getColumn() {
		return column;
	}

	public void setColumn(long column) {
		this.column = column;
	}

	public String getShipId() {
		return shipId;
	}

	public void setShipId(String shipId) {
		this.shipId = shipId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public float getHumidity() {
		return humidity;
	}

	public void setHumidity(float humidity) {
		this.humidity = humidity;
	}

	public float getCo2() {
		return co2;
	}

	public void setCo2(float co2) {
		this.co2 = co2;
	}
}
