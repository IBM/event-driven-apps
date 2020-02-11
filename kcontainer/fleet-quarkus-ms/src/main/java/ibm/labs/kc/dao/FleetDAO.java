package ibm.labs.kc.dao;

import java.util.Collection;

import ibm.labs.kc.model.Fleet;

public interface FleetDAO {

	public Collection<Fleet> getFleets();

	public Fleet getFleetByName(String fleetName);
}
