package it;

import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

/**
 * The liberty server need to run before this test can execute in eclipse. 
 * This test will run successfully in the integration.test step in maven
 * @author jeromeboyer
 *
 */
public class HealthEndpointIT extends BaseIntegrationTest {

   
    private String endpoint = "/health";
    private String url = baseUrl + endpoint;
    
    @Test
    public void testEndpoint() throws Exception {
        System.out.println("Testing endpoint " + url);
        int maxCount = 30;
        int responseCode = makeGetRequest(url);
        for(int i = 0; (responseCode != 200) && (i < maxCount); i++) {
          System.out.println("Response code : " + responseCode + ", retrying ... (" + i + " of " + maxCount + ")");
          Thread.sleep(5000);
          responseCode = makeGetRequest(url);
        }
        assertTrue("Incorrect response code: " + responseCode, responseCode == 200);
    }

 
}
