package ibm.labs.kc.serv.ut;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import ibm.labs.kc.dao.ShipDAO;
import ibm.labs.kc.dao.ShipDAOMockup;
import ibm.labs.kc.model.Ship;

public class TestShipDAO {

	@Test
	public void validateThereIsNoShipYet() {
		ShipDAO dao = new ShipDAOMockup();
		Collection<Ship> f = dao.getAllShips();
		Assert.assertNotNull(f);
		Assert.assertTrue(f.size() == 0);
	}

	@Test
	public void validateOneShipExist() {
		ShipDAO dao = new ShipDAOMockup();
		Ship s = new Ship("Silver Chamrock");
		dao.save(s);
		Collection<Ship> f = dao.getAllShips();
		Assert.assertNotNull(f);
		Assert.assertTrue(f.size() == 1);
	}
	
	@Test
	public void validateGettingShipByName() {
		ShipDAO dao = new ShipDAOMockup();
		Ship s = new Ship("Sunk Quickly");
		dao.save(s);
		Ship f = dao.getShipByName(s.getName());
		Assert.assertNotNull(f);
		Assert.assertTrue(f.getName().equals(s.getName()));
	}
}
