package ibm.labs.kc.dao;

import java.util.Collection;
import java.util.HashMap;

import ibm.labs.kc.model.Container;
import ibm.labs.kc.model.Ship;

public class ShipDAOMockup implements ShipDAO {

	private static HashMap<String,Ship> ships = new HashMap<String,Ship>();
	
	public ShipDAOMockup() {
	}
	
	@Override
	public Ship getShipByName(String shipName) {
		return ships.get(shipName);
	}

	@Override
	public Ship save(Ship s) {
		ships.put(s.getName(), s);
		return s;
	}

	@Override
	public Collection<Ship> getAllShips() {
		return ships.values();
	}

	@Override
	public Ship loadContainersForTheShip(Ship s) {
		int row = 0 , column = 0;
		s.setContainers(null);
		for (int i = 0; i <= s.getNumberOfContainers(); i++) {
			Container c = new Container();
			c.setId("c_" + i);
			c.setTemperature(Math.round(Math.random()*60+15));
			c.setAmp(Math.round(Math.random()*40 + 10));
			c.setCo2(Math.round(Math.random()*100));
			c.setHumidity(Math.round(Math.random()*100));
			c.setShipId(s.getName());
			c.setType("Reefer");
			c.setRow(row);
			c.setColumn(column);
			s.getContainers().get(row).add(c);
			if ( column < s.getMaxColumn() - 1) {
				column ++;	
			} else {
				row++;
				column = 0;
			}	
		}
		for (int r=s.getMaxRow()-1;r>=0;r--) {
			if (s.getContainers().get(r).size() == 0) {
				s.getContainers().remove(r);
			}
		}
		return s;
	}

}
