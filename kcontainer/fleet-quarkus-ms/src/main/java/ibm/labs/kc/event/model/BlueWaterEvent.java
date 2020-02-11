package ibm.labs.kc.event.model;

public interface BlueWaterEvent {
	 public long getTimestampMillis();

	 public void setTimestampMillis(long timestampMillis);

	 public String getType();

	 public void setType(String type);

	 public void setVersion(String version);

	 public String getVersion();
}
