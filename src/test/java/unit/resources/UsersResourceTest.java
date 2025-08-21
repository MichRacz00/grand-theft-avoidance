package unit.resources;

import dao.UsersDao;
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

class UsersResourceTest {

    String uri;
    Client client;
    WebTarget target;
    String testUser = "Store";


    @BeforeEach
    void setUp() {
        client = ClientBuilder.newClient();
        uri = "http://localhost:8080/GTA_VI/rest/";
        target = client.target(uri);
    }

    @Test
    void getUsers() {
        Response response = target.path("users").request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
        ArrayList<User> users = response.readEntity(ArrayList.class);
        long expectedSize = UsersDao.instance.getUserMap().size();
        assertEquals(expectedSize, users.size());
    }

    @Test
    void getUser() {
        Response response = target.path("users").path(testUser).request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
        User user = response.readEntity(User.class);
        User expectedUser = UsersDao.instance.getUserMap().get(testUser);
        assertEquals(expectedUser.getPermission(), user.getPermission());
        assertEquals(expectedUser.getUsername(), user.getUsername());
    }
}