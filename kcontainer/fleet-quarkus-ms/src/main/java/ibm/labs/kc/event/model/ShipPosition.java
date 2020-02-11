package ibm.labs.kc.event.model;

import com.google.gson.annotations.SerializedName;

/**
 * This is an ship position event with a light adaptation of the following structure
 * https://www.navcen.uscg.gov/?pageName=AISMessage27
 * 
 * @author jerome boyer
 *
 */
public class ShipPosition implements BlueWaterEvent {
	//@SerializedName("shipId")
	protected String shipID;
	protected String latitude;
	protected String longitude;
	protected int speed;
	protected float ambiantTemperature;
	protected int compass;
	protected String status;
	@SerializedName("ts")
	protected String timeStamp;
	private long timestampMillis;
	private String type;
	private String version;

	
	public ShipPosition() {
		
	}
	
	public ShipPosition(String shipName, String latitude, String longitude, long ts) {
		this.shipID = shipName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.timestampMillis = ts;
	}

	public String toString() {
		return getShipID() + " " +getStatus() + " La:" + getLatitude() + " Lo:" + getLongitude() + " @ " + getTimestampMillis();
	}

	public String getShipID() {
		return shipID;
	}

	public void setShipID(String shipID) {
		this.shipID = shipID;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}


	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public float getAmbiantTemperature() {
		return ambiantTemperature;
	}

	public void setAmbiantTemperature(float ambiantTemperature) {
		this.ambiantTemperature = ambiantTemperature;
	}

	public int getCompass() {
		return compass;
	}

	public void setCompass(int compass) {
		this.compass = compass;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getTimestampMillis() {
        return timestampMillis;
    }

    public void setTimestampMillis(long timestampMillis) {
        this.timestampMillis = timestampMillis;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
	
}
