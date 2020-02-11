package ibm.labs.kc.simulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ibm.labs.kc.model.Fleet;
import ibm.labs.kc.model.Position;
import ibm.labs.kc.model.Ship;

/**
 * Top class for the KS simulation. Offers APIs to load ship position for a fleet, or for a ship giving its name.
 * 
 * Ship's positions are in a csv file in the class path with the name of the ship as file name.
 * 
 * @author jerome boyer
 *
 */
public class KCSimulator {
	/**
	 * load for each ship of the fleet their positions for their journey from csv file
	 * @param f
	 * @return
	 */
	public HashMap<String, List<Position>> readShipsPositions(Fleet f) {
		HashMap<String,List<Position>> shipsPositions  = new HashMap<String,List<Position>>();
		for (Ship s : f.getShips()) {
			List<Position> LP = readShipPositions(s.getName());
			shipsPositions.put(s.getName(),LP);
		}
		return shipsPositions;
	}
	
	public List<Position> readShipPositions(String shipname) {
		List<Position> LP = new ArrayList<Position>();
		InputStream fin = null;
		try {
			fin= getClass().getClassLoader().getResourceAsStream(shipname+".csv");
			BufferedReader br = new BufferedReader(new InputStreamReader(fin));
	        String line = "";
	        String cvsSplitBy = ",";	        
			while((line = br.readLine()) != null) {
				String[] positions = line.split(cvsSplitBy);
			    Position p = new Position(positions[0],positions[1]);
			    LP.add(p);
			}
		} catch (IOException e) {
			System.err.println(shipname);
			e.printStackTrace();
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return LP;
	}
}
