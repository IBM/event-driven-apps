package it;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.junit.Before;

public class BaseIntegrationTest {
	 protected String port = System.getProperty("liberty.test.port");
	 protected String warContext = System.getProperty("war.context");
	 protected String baseUrl = "http://localhost:" + port + "/" + warContext;
	 
	  @Before
	    public void verify() {
	    	if (this.port == null) {
	    		this.port = "9080";
	    	}
	    	if (this.warContext == null) {
	    		this.warContext = "fleetms";
	    	}
	    	if (this.baseUrl.contains("null")) {
	    		baseUrl = "http://localhost:" + port + "/" + warContext;
	    	}
	    }
	  
	    public String getBaseUrl() {
	    	if (! this.baseUrl.contains("fleetms")) {
	    		baseUrl = "http://localhost:9080/fleetms";
	    	}
	    	return this.baseUrl;
	    }
	    
	   protected int makeGetRequest(String url) {
	      Client client = ClientBuilder.newClient();
	      Invocation.Builder invoBuild = client.target(url).request();
	      Response response = invoBuild.get();
	      int responseCode = response.getStatus();
	      response.close();
	      return responseCode;
	   }
	   
	   protected Response makePostRequest(String url, String json) {
		   Client client = ClientBuilder.newClient();
		   Invocation.Builder invoBuild = client.target(url).request();
		   Response response = invoBuild.post(Entity.json(json));
		   return response; 
	   }
}
