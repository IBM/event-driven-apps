package ibm.labs.kc.dao;

import java.util.Collection;

import ibm.labs.kc.model.Ship;

public interface ShipDAO {

	public Ship getShipByName(String shipName);

	public Ship save(Ship s);
	
	public Collection<Ship> getAllShips();

	public Ship loadContainersForTheShip(Ship s);

}
