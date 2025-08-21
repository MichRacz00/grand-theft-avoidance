package unit.resources;

import dao.LocationsDao;
import model.Location;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LocationsResourceTest {

    String uri;
    Client client;
    WebTarget target;
    String testStore = "1023417";

    @BeforeEach
    void setUp() {
        client = ClientBuilder.newClient();
        uri = "http://localhost:8080/GTA_VI/rest/";
        target = client.target(uri);
    }

    @Test
    void getStores() {
        Response response = target.path("stores").request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
        ArrayList<Location> stores = response.readEntity(ArrayList.class);
        long expectedSize = LocationsDao.instance.getLocationMap().size();
        assertEquals(expectedSize, stores.size());
    }

    @Test
    void getStore() {
        Response response = target.path("stores/" + testStore).request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
        Location store = response.readEntity(Location.class);
        assertEquals(Integer.parseInt(testStore), store.getId());
    }

    @Test
    void getAmount() {
        Response response = target.path("stores/amount").request(MediaType.APPLICATION_JSON).get();
        // 406: Not Acceptable "rest/stores/amount" does not produce JSON
        assertEquals(406,response.getStatus());
        assertNotEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
        response = target.path("stores/amount").request(MediaType.TEXT_HTML).get();
        assertEquals(LocationsDao.instance.getLocationMap().size() + "", response.readEntity(String.class));
    }

    @Test
    void getStoresByThefts() {
        Response response = target.path("stores").path("mapfilter").request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
    }
}