package ibm.labs.kc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Group the ship per fleet, so it is easier to get dashboard view based on fleet.
 * Also fleet represents a business ownership boundary
 * @author jerome boyer
 *
 */
public class Fleet {

	protected String id;
	protected String name;
	protected String color;
	protected List<Ship> ships = new ArrayList<Ship>();
	
	public Fleet(String name) {
		this.name = name;
	}
	
	public String toString() {
		return getId() + " " + getName();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Ship> getShips() {
		return ships;
	}

	public void setShips(List<Ship> ships) {
		this.ships = ships;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
