package ibm.labs.kc.event.model;

import com.google.gson.annotations.SerializedName;

public class ContainerMetric implements BlueWaterEvent {
	// used when the event does not match the Java Bean used.
	@SerializedName("containerId")
	protected String id;
	@SerializedName("tempC")
	protected long temperature;
	protected long amp;
	protected float cumulativePowerConsumption;
	protected int contentType;
	protected float humidity;
	protected float co2;
 	protected long	Tproduce;
	protected String shipID;
	private long timestampMillis;
	private String type;
	private String version;
	
	public ContainerMetric(String shipId,String id, long t, long a, long ts) {
		this.id = id;
		this.shipID = shipId;
		this.temperature = t;
		this.amp = a;
		this.timestampMillis = ts;
	}

	public String toString() {
		return getShipID()+ " " + getId() + " T= " + getTemperature() + " A= " + getAmp() + " ts:" + getTimestampMillis();	
	}
	
	public String getId() {
		return id;
	}

	public long getTemperature() {
		return temperature;
	}

	public long getAmp() {
		return amp;
	}

	public String getShipID() {
		return shipID;
	}

	public void setShipID(String shipId) {
		this.shipID = shipId;
	}

	public float getCumulativePowerConsumption() {
		return cumulativePowerConsumption;
	}

	public void setCumulativePowerConsumption(float cumulativePowerConsumption) {
		this.cumulativePowerConsumption = cumulativePowerConsumption;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public long getTproduce() {
		return Tproduce;
	}

	public void setTproduce(long tproduce) {
		Tproduce = tproduce;
	}

	public void setTemperature(long temperature) {
		this.temperature = temperature;
	}

	public void setAmp(long amp) {
		this.amp = amp;
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
