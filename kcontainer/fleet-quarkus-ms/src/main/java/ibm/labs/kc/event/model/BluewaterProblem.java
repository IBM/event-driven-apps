package ibm.labs.kc.event.model;

import com.google.gson.annotations.SerializedName;

public class BluewaterProblem {
	protected String issue;
	protected double weatherC;
	protected String containerId;
	protected String status;
	@SerializedName("tempC")
	protected double temperature;
	protected double amp;
	protected String shipId;
	protected String latitude;
	protected String longitude;
	protected String tag;
	protected String severity;
	@SerializedName("ts")
	protected String timeStamp;
	
   public BluewaterProblem() {
   }
   
   public String toString() {
	   return "Shipid:" + getShipId() + " " + getContainerId() + " " + getStatus() + " " + getIssue() 
	   + " " + getSeverity() + " lat:" + getLatitude() + " long:" + getLongitude() + " " + getTimeStamp();
   }

public String getIssue() {
	return issue;
}

public void setIssue(String issue) {
	this.issue = issue;
}

public double getWeatherC() {
	return weatherC;
}

public void setWeatherC(double weatherC) {
	this.weatherC = weatherC;
}

public String getContainerId() {
	return containerId;
}

public void setContainerId(String containerId) {
	this.containerId = containerId;
}

public String getStatus() {
	return status;
}

public void setStatus(String status) {
	this.status = status;
}

public double getTemperature() {
	return temperature;
}

public void setTemperature(double temperature) {
	this.temperature = temperature;
}

public double getAmp() {
	return amp;
}

public void setAmp(double amp) {
	this.amp = amp;
}

public String getShipId() {
	return shipId;
}

public void setShipId(String shipId) {
	this.shipId = shipId;
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

public String getTag() {
	return tag;
}

public void setTag(String tag) {
	this.tag = tag;
}

public String getSeverity() {
	return severity;
}

public void setSeverity(String severity) {
	this.severity = severity;
}

public String getTimeStamp() {
	return timeStamp;
}

public void setTimeStamp(String timeStamp) {
	this.timeStamp = timeStamp;
}
   
   
}
