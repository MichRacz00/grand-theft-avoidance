package dao;

import access.DatabaseAccesser;
import model.Article;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public enum ArticlesDao {
    instance;

    private Map<String, Article> articleMap = new HashMap<>();

    private ArticlesDao() {
        String q = "SELECT r.article_id, r.category, r.article, r.color, r.size, r.price_eur, COUNT(a.*) " +
                   "FROM  \"gta-vi\".articles r, \"gta-vi\".alarm a " +
                    "WHERE r.article_id = a.article_id " +
                    "GROUP BY r.article_id, r.category, r.article, r.color, r.size, r.price_eur; ";

        DatabaseAccesser da = new DatabaseAccesser();
        ResultSet rs = da.queryRS(q);

        try {
            while (rs.next()) {
                Article a = new Article(rs.getLong(1), rs.getInt(2),
                    rs.getInt(3), rs.getString(4), rs.getString(5),
                        rs.getDouble(6),rs.getInt(7));

                articleMap.put(a.getId() + "", a);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public Map<String, Article> getArticleMap() {
        return articleMap;
    }

}
