package ibm.labs.kc.simulator;

import ibm.labs.kc.event.model.ContainerMetric;
import ibm.labs.kc.model.Container;
import ibm.labs.kc.model.Ship;

public class BadEventSimulator {	
	
	public static void fireContainers(Ship s, int numberOfContainers) {
	    if (numberOfContainers >= 4) {
	    	Container c = s.getContainers().get(0).get(2);
			c.setStatus(Container.STATUS_FIRE);
			c.setTemperature(150);
			c = s.getContainers().get(0).get(3);
			c.setStatus(Container.STATUS_FIRE);
			c.setTemperature(150);
			c = s.getContainers().get(0).get(4);
			c.setStatus(Container.STATUS_FIRE);
			c.setTemperature(150);
			c = s.getContainers().get(1).get(2);
			c.setStatus(Container.STATUS_FIRE);
			c.setTemperature(150);
	    } else {
	    	Container c = s.getContainers().get(0).get(2);
	    	c.setStatus(Container.STATUS_FIRE);
	    	c.setTemperature(150);
			c = s.getContainers().get(0).get(3);
			c.setStatus(Container.STATUS_FIRE);
			c.setTemperature(150);
	    }
	}
	    
	
	public static ContainerMetric buildContainerMetric(Ship ship,Container c, long currentWorldTime) {
		if (ship.getStatus() == Ship.AT_SEA) {
			switch(c.getStatus()) {
			case Container.STATUS_FIRE:
				c.setTemperature(c.getTemperature() + 50);
				c.setHumidity(0);
				c.setCo2(100);
				break;
			case Container.STATUS_HEAT:
				c.setTemperature(c.getTemperature() + 2);
				break;
			case Container.STATUS_DOWN:
				c.setAmp(0);
				break;
			}
		}
		ContainerMetric cm = new ContainerMetric(ship.getName(),c.getId(),c.getTemperature(),c.getAmp(),currentWorldTime);
		return cm;
	}


	public static void reeferDown(Ship s) {
		Container c = s.getContainers().get(0).get(3);
    	c.setStatus(Container.STATUS_DOWN);
    	c.setAmp(0);
	}


	public static void heatWave(Ship s) {
		int topContainerCount = 0;
		int topRowAllocated = s.getContainers().size() - 1;
		long lastIndex = 0;
		for (Container c : s.getContainers().get(topRowAllocated)) {
			c.setStatus(Container.STATUS_HEAT);
			c.setTemperature(50);
			topContainerCount++;
			lastIndex = c.getColumn();
		}
		// the top row may not be completed in this case the row under will have containers exposed to heat
		if (topContainerCount < s.getMaxColumn()) {
			for (Container c : s.getContainers().get(topRowAllocated-1)) {
				if (c.getColumn() >= lastIndex) {
					c.setStatus(Container.STATUS_HEAT);
					c.setTemperature(50);
					topContainerCount++;
				}
			}
		}
		
	}
}
