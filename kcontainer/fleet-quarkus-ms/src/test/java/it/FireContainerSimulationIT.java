package it;

import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.google.gson.Gson;

import ibm.labs.kc.dto.model.ShipSimulationControl;

/**
 * Start moving ship and simulate a fire on container.
 * This is an integration test so we need access to Kafka brokers
 * @author jerome boyer
 *
 */
public class FireContainerSimulationIT extends BaseIntegrationTest {

	private String endpoint = "/ships/simulate";
    private String url = getBaseUrl() + endpoint;

    @Test
    public void testFireFourContainers() throws Exception {
    	
    	System.out.println("Start Kafka container event Consumer ");
    	ContainerEventConsumer consumer = new ContainerEventConsumer();
    	Thread t = new Thread(consumer);
    	t.start();

        System.out.println("Testing endpoint " + url);
        // create the simulation control parameters
        ShipSimulationControl ctl = new ShipSimulationControl("JimminyCricket", ShipSimulationControl.CONTAINER_FIRE);
        int maxCount = 5;
        int responseCode = 0;
        Response response = null;
        for(int i = 0; (responseCode != 200) && (i <= maxCount); i++) {
          System.out.println("Response code : " + responseCode + ", retrying ... (" + i + " of " + maxCount + ")");
          Thread.sleep(5000);
		  response = makePostRequest(url,new Gson().toJson(ctl));
		  responseCode = response.getStatus();
		  if (response.hasEntity()) {
			  System.out.print(response.readEntity(String.class));
		  }
		  response.close(); 
        }
        assertTrue("Incorrect response code: " + responseCode, responseCode == 200);
    }


}
