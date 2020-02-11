package ibm.labs.kc.serv.ut;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import ibm.labs.kc.app.rest.FleetService;
import ibm.labs.kc.dao.FleetDAO;
import ibm.labs.kc.dao.FleetDAOMockup;
import ibm.labs.kc.model.Fleet;
import ibm.labs.kc.model.Ship;
import ibm.labs.kc.simulator.FleetSimulator;

/**
 * Starting with TDD we want to test the following operations
 * - read from file the fleet definition
 * 
 * @author jeromeboyer
 *
 */
public class TestReadingFleet {
	 @Mock
	 static FleetSimulator simulator;

	 
	 @Rule public MockitoRule mockitoRule = MockitoJUnit.rule(); 

	public static FleetService serv ;
	public static FleetDAO dao;
	
	@BeforeClass
	public static void init() {
		dao = new FleetDAOMockup("Fleet.json");
		serv = new FleetService(dao,simulator);
	}
	
	
	@Test
	public void testGetAllFleets() {
		List<Fleet> f = serv.getFleets();
		Assert.assertNotNull(f);
		Assert.assertTrue(f.size() >= 1);
	}
	
	@Test
	public void testGetFleetByName() {
		Fleet f = serv.getFleetByName("KC-NorthAtlantic");
		Assert.assertNotNull(f);
		Assert.assertTrue("KC-NorthAtlantic".equals(f.getName()));
		for (Ship s : f.getShips()) {
			System.out.println("\t"+s.toString());
		}
		
		f = serv.getFleetByName("wrongname");
		Assert.assertNull(f);
		
	}

}
