package it;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import ibm.labs.kc.model.Fleet;

public class TestFleetAPIsIT extends BaseIntegrationTest {

	 private String endpoint = "/fleets";
	 private String url = getBaseUrl() + endpoint;
	 private Gson parser = new Gson();
	@Test
	public void testGettingFleetsFromREST() throws InterruptedException{ 
		 System.out.println("Testing endpoint " + url);
	        int maxCount = 5;
	        int responseCode = 0;
	        for(int i = 0; (responseCode != 200) && (i < maxCount); i++) {
	          System.out.println("Response code : " + responseCode + ", retrying ... (" + i + " of " + maxCount + ")");

	          Client client = ClientBuilder.newClient();
		      Invocation.Builder invoBuild = client.target(url).request();
		      Response response = invoBuild.get();
		      if (response.hasEntity()) {
			      String fleetsAsString=response.readEntity(String.class);
			     
			      try{
				      Fleet[] fa = parser.fromJson(fleetsAsString,Fleet[].class);
				      assertNotNull(fleetsAsString);
				      for (Fleet f : fa) {
				    	  System.out.println(f.toString());
				      }
			      }catch(IllegalStateException | JsonSyntaxException exception){
			    	  System.out.println(exception);
			      }
		      }
		      
		      responseCode = response.getStatus();
	          Thread.sleep(5000);
	        }
	        assertTrue("Incorrect response code: " + responseCode, responseCode == 200);
	}

}
