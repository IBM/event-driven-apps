package ibm.labs.kc.app.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class FleetServiceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/fleets")
          .then()
             .statusCode(200)
             .body(is("hello"));
    }

}