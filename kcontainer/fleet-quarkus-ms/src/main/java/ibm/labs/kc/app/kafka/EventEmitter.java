package ibm.labs.kc.app.kafka;

import ibm.labs.kc.event.model.BlueWaterEvent;

public interface EventEmitter {
	  public void emit(BlueWaterEvent event) throws Exception;
}
