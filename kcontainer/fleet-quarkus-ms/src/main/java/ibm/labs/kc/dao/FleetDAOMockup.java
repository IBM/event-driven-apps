package ibm.labs.kc.dao;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;

import com.google.gson.Gson;

import ibm.labs.kc.model.Fleet;

public class FleetDAOMockup implements FleetDAO {
	
	private static HashMap<String,Fleet> fleet = new HashMap<String,Fleet>();
	
	public FleetDAOMockup() {
		init("Fleet.json");
	}
	// only one of this factory method to be used
	
	
	/**
	 * When using the fleet definition from json file 
	 * @param fleetFileName
	 */
	public FleetDAOMockup(String fleetFileName) {
		init(fleetFileName);
	}
	
	private void init(String fleetFileName) {
		InputStream fin= getClass().getClassLoader().getResourceAsStream(fleetFileName);
		Reader json = new InputStreamReader(fin);
		Fleet [] fleets = new Gson().fromJson(json, Fleet[].class);
		for (Fleet f : fleets) {
			fleet.put(f.getName(),f);
		}
	}
	
	@Override
	public Collection<Fleet> getFleets() {
		return fleet.values();
	}

	@Override
	public Fleet getFleetByName(String fleetName) {	
		return fleet.get(fleetName);
	}

}
