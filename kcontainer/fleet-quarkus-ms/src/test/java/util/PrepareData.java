package util;

import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ibm.labs.kc.dao.FleetDAO;
import ibm.labs.kc.dao.FleetDAOMockup;
import ibm.labs.kc.model.Fleet;

public class PrepareData {
	static Gson parser = new GsonBuilder().setPrettyPrinting().create();
	
	public static void main(String[] args) {
		
		FleetDAO dao = new FleetDAOMockup();
		Collection<Fleet> f = dao.getFleets();
		String s=parser.toJson(f);
		System.out.println(s);
	}

}
