package unit.resources;

import dao.TheftDao;
import model.Theft;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TheftsResourceTest {

    String uri;
    Client client;
    WebTarget target;
    String testStore = "1023417";
    String testArticle = "16890558127297";
    String testArticleType = "519";
    String testEPC = "3602C13058A1000066F99C";
    String testStartDate = "01-01-21";
    String testEndDate = "01-02-21";

    @BeforeEach
    void setUp() {
        client = ClientBuilder.newClient();
        uri = "http://localhost:8080/GTA_VI/rest/";
        target = client.target(uri);
    }

    @Test
    void getTheftsWithNoParams() {
        Response response = target.path("thefts").request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
        ArrayList<Theft> thefts = response.readEntity(ArrayList.class);
        // We get all the thefts
        assertEquals(TheftDao.instance.getTheftMap().size(), thefts.size());
    }

    @Test
    void getTheftsWithArticleParam() {
        Response response = target.path("thefts").queryParam("article", testArticleType).request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
        ArrayList<Theft> thefts = response.readEntity(ArrayList.class);
        long expectedSize = TheftDao.instance.getTheftMap().values()
                .stream()
                .filter(a -> a.getArticleType().equals(testArticleType)).count();
        assertEquals(expectedSize, thefts.size());
    }

    @Test
    void getTheftsWithStoreParam() {
        Response response = target.path("thefts").queryParam("store", testStore).request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
        ArrayList<Theft> thefts = response.readEntity(ArrayList.class);
        long expectedSize = TheftDao.instance.getTheftMap().values()
                .stream()
                .filter(a -> a.getLocation().equals(testStore)).count();
        assertEquals(expectedSize, thefts.size());
    }

    @Test
    void getTheftsWithBothParams() {
        Response response = target.path("thefts").queryParam("store", testStore).queryParam("article", testArticleType).
                request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
        ArrayList<Theft> thefts = response.readEntity(ArrayList.class);
        long expectedSize =  TheftDao.instance.getTheftMap().values()
                .stream()
                .filter(a -> a.getLocation().equals(testStore) && a.getArticleType().equals(testArticleType)).count();
        assertEquals(expectedSize, thefts.size());
    }

    @Test
    void getArticle() {
        Response response = target.path("thefts").path(testEPC).request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
        Theft expected = TheftDao.instance.getTheftMap().get(testEPC);
        Theft actual = response.readEntity(Theft.class);
        assertEquals(expected.getArticle(), actual.getArticle());
    }

    @Test
    void getAmountThefts() {
        Response response = target.path("thefts").path("amount").
                queryParam("start",testStartDate).queryParam("end",testEndDate).queryParam("store", testStore).
                request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
    }

    @Test
    void getSumThefts() {
        Response response = target.path("thefts").path("amount").
                queryParam("start",testStartDate).queryParam("end",testEndDate).
                request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
    }

    @Test
    void getValueTheftsWithoutStoreParam() {
        Response response = target.path("thefts").path("value").
                queryParam("start",testStartDate).queryParam("end",testEndDate).
                request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
    }

    @Test
    void getValueTheftsWithStoreParam() {
        Response response = target.path("thefts").path("value").
                queryParam("start",testStartDate).queryParam("store", testStore).queryParam("end",testEndDate).
                request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
    }

    @Test
    void getCategories() {
        Response response = target.path("thefts").path("categories").
                queryParam("store",testStore).
                request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
    }

    @Test
    void getDay() {
        Response response = target.path("thefts").path("day").
                queryParam("store",testStore).
                request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
    }

    @Test
    void getWeek() {
        Response response = target.path("thefts").path("week").
                queryParam("store", testStore).
                request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
    }

    @Test
    void getMonth() {
        Response response = target.path("thefts").path("month").
                queryParam("store",testStore).
                request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
    }

    @Test
    void getDailyTotal() {
        Response response = target.path("thefts").path("dailytotal").
                queryParam("dat",testEndDate).
                request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
    }

    @Test
    void getWeeklyTotal() {
        Response response = target.path("thefts").path("weeklytotal")
                .queryParam("date",testStartDate)
                .request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
    }

    @Test
    void getMonthlyTotal() {
        Response response = target.path("thefts").path("monthlytotal").
                queryParam("date",testStartDate).
                request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
    }

    @Test
    void getYearlyTotal() {
        Response response = target.path("thefts").path("yearlytotal").
                queryParam("date",testStartDate).
                request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
    }

    @Test
    void getHotHour() {
        Response response = target.path("thefts").path("hothour").
                request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
    }

    @Test
    void getHotArticle() {
        Response response = target.path("thefts").path("hotarticle").
                request(MediaType.APPLICATION_JSON).get();
        assertEquals(200,response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
    }
}