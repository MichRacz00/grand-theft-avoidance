package unit.resources;

import dao.ArticlesDao;
import model.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArticlesResourceTest {

    String uri;
    Client client;
    WebTarget target;
    String testArticle = "16890558127297";

    @BeforeEach
    void setUp() {
        client = ClientBuilder.newClient();
        uri = "http://localhost:8080/GTA_VI/rest/";
        target = client.target(uri);
    }


    @Test
    void getArticles() {
        Response response = target.path("articles").request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, response.getStatus());
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
        ArrayList<Article> articles = response.readEntity(ArrayList.class);
        long expectedSize = ArticlesDao.instance.getArticleMap().size();
        assertEquals(expectedSize, articles.size());
    }

    @Test
    void getArticle() throws IOException {
        Response response = target.path("articles/" + testArticle).request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, response.getStatus()); // Service working 200 OK = Pass
        assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON); // We do indeed receive back JSON
        Article responseArticle = response.readEntity(Article.class);
        assertEquals(Long.parseLong(testArticle), responseArticle.getId()); // We get the article with the correct ID
    }
}