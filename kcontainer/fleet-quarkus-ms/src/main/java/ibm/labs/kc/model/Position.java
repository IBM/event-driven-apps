package ibm.labs.kc.model;

public class Position {
	protected String latitude;
	protected String longitude;
	
	public Position(String la, String lo) {
		this.latitude = la;
		this.longitude = lo;
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

	@Override
	public boolean equals(Object o) {
		Position p = (Position)o;
		return (this.getLatitude().equals(p.getLatitude()) && this.getLongitude().equals(p.getLongitude()));
		
	}
	
}
