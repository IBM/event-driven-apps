package ibm.labs.kc.serv.ut;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import ibm.labs.kc.dao.DAOFactory;
import ibm.labs.kc.dao.FleetDAO;
import ibm.labs.kc.dao.FleetDAOMockup;
import ibm.labs.kc.model.Fleet;

public class TestFleetDAO {

	@Test
	public void testDAOReadFleets() {
		FleetDAO dao = new FleetDAOMockup("Fleet.json");
		Collection<Fleet> f = dao.getFleets();
		Assert.assertNotNull(f);
		Assert.assertTrue(f.size() >= 1);
	}
	
	@Test
	public void testDAOReadFleetsUsingFactory() {
		FleetDAO dao = DAOFactory.buildOrGetFleetDAO("Fleet.json");	
		Collection<Fleet> f = dao.getFleets();
		Assert.assertNotNull(f);
		Assert.assertTrue(f.size() >= 1);
	}
	

}
