package resources;

import dao.ArticlesDao;
import model.Article;
import model.Theft;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("articles")
public class ArticlesResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Article> getArticles() {
        List<Article> articles = new ArrayList<>(ArticlesDao.instance.getArticleMap().values());
        return articles;
    }

    /* To get details about a specific article
     */
    @GET
    @Path("{articleID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Article getArticle(@PathParam("articleID") String id) {
        Article article = ArticlesDao.instance.getArticleMap().get(id);
        if (article == null) throw new RuntimeException("Article with " + id + " not found");
        return article;
    }
}
