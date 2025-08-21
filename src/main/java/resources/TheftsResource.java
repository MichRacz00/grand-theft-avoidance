package resources;

import access.DatabaseAccesser;
import dao.TheftDao;
import model.Theft;
import model.TheftGroup;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("thefts")
public class TheftsResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Theft> getThefts(@QueryParam("article") String article, @QueryParam("store") String storeID) {
        if(article == null && storeID == null)
            return new ArrayList<>(TheftDao.instance.getTheftMap().values());
        else if(article != null && storeID == null)
            return TheftDao.instance.getTheftMap().values()
                    .stream()
                    .filter(a -> a.getArticleType().equals(article)).collect(Collectors.toList());
        else if (article == null)
            return TheftDao.instance.getTheftMap().values()
                    .stream()
                    .filter(a -> a.getLocation().equals(storeID)).collect(Collectors.toList());
        else return TheftDao.instance.getTheftMap().values()
                    .stream()
                    .filter(a -> a.getLocation().equals(storeID) && a.getArticleType().equals(article)).collect(Collectors.toList());
    }

    @GET
    @Path("{epc}")
    @Produces(MediaType.APPLICATION_JSON)
    public Theft getArticle(@PathParam("epc") String epc) {
        Theft theft = TheftDao.instance.getTheftMap().get(epc);
        if (theft == null) throw new RuntimeException("Theft with " + epc + " not found");
        return theft;
    }

    @GET
    @Path("amount")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TheftGroup> getAmountThefts(@QueryParam("start") String start,
                                            @QueryParam("end") String end,
                                            @QueryParam("store") int store) {

        String q = "SELECT DATE(datetime), COUNT(*) AS number FROM \"gta-vi\".alarm WHERE " +
                "datetime >= '" + start + "'::date AND " +
                "datetime <= '" + end + "'::date " +
                "AND store_id_ut = " + store + " " +
                "GROUP BY DATE(datetime) " +
                "ORDER BY DATE(datetime) ASC";

        return getTheftGroups(q);
    }


    @GET
    @Path("sum")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TheftGroup> getSumThefts(@QueryParam("start") String start,
                                            @QueryParam("end") String end) {

        String q = "SELECT DATE(datetime), COUNT(*) AS number FROM \"gta-vi\".alarm WHERE " +
                "datetime >= '" + start + "'::date AND " +
                "datetime <= '" + end + "'::date " +
                "GROUP BY DATE(datetime) " +
                "ORDER BY DATE(datetime) ASC";

        return getTheftGroups(q);
    }

    @GET
    @Path("value")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TheftGroup> getValueThefts(@QueryParam("start") String start,
                                           @QueryParam("end") String end,
                                           @DefaultValue("-1") @QueryParam("store") int store) {

        String q = "SELECT date, SUM(a.price_eur) AS value " +
                   "FROM \"gta-vi\".alarm t, \"gta-vi\".articles a " +
                   "WHERE t.article_id = a.article_id " +
                   "AND   datetime >= '" + start + "'::date " +
                   "AND   datetime <= '" + end + "'::date " +
                   "AND store_id_ut = " + store + " " +
                   "GROUP BY date " +
                   "ORDER BY date ASC;";

        if (store == -1) {
            q = "SELECT date, SUM(a.price_eur) AS value " +
                    "FROM \"gta-vi\".alarm t, \"gta-vi\".articles a " +
                    "WHERE t.article_id = a.article_id " +
                    "AND   datetime >= '" + start + "'::date " +
                    "AND   datetime <= '" + end + "'::date " +
                    "GROUP BY date " +
                    "ORDER BY date ASC;";
        }

        return getTheftGroups(q);
    }

    @GET
    @Path("categories")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TheftGroup> getCategories(@QueryParam("store") int store,
                                          @DefaultValue("10") @QueryParam("limit") int limit) {

        String q = "SELECT category, COUNT(*) " +
                "FROM \"gta-vi\".alarm t, \"gta-vi\".articles a " +
                "WHERE a.article_id = t.article_id " +
                "AND   t.store_id_ut = " + store + " " +
                "GROUP BY category " +
                "ORDER BY COUNT(*) DESC " +
                "LIMIT " + limit + ";";

        DatabaseAccesser da = new DatabaseAccesser();
        ResultSet rs = da.queryRS(q);
        ArrayList<TheftGroup> timeTheft = new ArrayList<>();

        try {
            while (rs.next()) {
                TheftGroup tg = new TheftGroup(store, rs.getInt(1), rs.getInt(2));
                timeTheft.add(tg);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return timeTheft;
    }

    @GET
    @Path("day")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TheftGroup> getDay(@QueryParam("store") int store) {

        String q = "SELECT DATE_PART('hour', t.datetime), COUNT(*) " +
                   "FROM \"gta-vi\".alarm t, \"gta-vi\".articles a " +
                   "WHERE t.store_id_ut = " + store + " " +
                   "AND t.article_id = a.article_id " +
                   "GROUP BY DATE_PART('hour', t.datetime) " +
                   "ORDER BY DATE_PART('hour', t.datetime);";

        DatabaseAccesser da = new DatabaseAccesser();
        ResultSet rs = da.queryRS(q);
        ArrayList<TheftGroup> timeTheft = new ArrayList<>();

        try {
            while (rs.next()) {
                TheftGroup tg = new TheftGroup(rs.getString(1), rs.getInt(2));
                timeTheft.add(tg);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return timeTheft;
    }

    @GET
    @Path("week")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TheftGroup> getWeek(@QueryParam("store") int store) {
        String q = "SELECT DATE_PART('isodow', datetime), COUNT(*) " +
                "FROM \"gta-vi\".alarm " +
                "WHERE store_id_ut = '" + store + "' " +
                "GROUP BY DATE_PART('isodow', datetime) " +
                "ORDER BY DATE_PART('isodow', datetime) ASC;";

        DatabaseAccesser da = new DatabaseAccesser();
        ResultSet rs = da.queryRS(q);
        ArrayList<TheftGroup> timeTheft = new ArrayList<>();

        try {
            while (rs.next()) {
                TheftGroup tg = new TheftGroup(rs.getString(1), rs.getInt(2));
                timeTheft.add(tg);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return timeTheft;
    }

    @GET
    @Path("month")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TheftGroup> getMonth(@QueryParam("store") int store) {
        String q = "SELECT DATE_PART('day', datetime), COUNT(*) " +
                "FROM \"gta-vi\".alarm " +
                "WHERE store_id_ut = '" + store + "' " +
                "GROUP BY DATE_PART('day', datetime) " +
                "ORDER BY DATE_PART('day', datetime) ASC;";

        DatabaseAccesser da = new DatabaseAccesser();
        ResultSet rs = da.queryRS(q);
        ArrayList<TheftGroup> timeTheft = new ArrayList<>();

        try {
            while (rs.next()) {
                TheftGroup tg = new TheftGroup(rs.getString(1), rs.getInt(2));
                timeTheft.add(tg);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return timeTheft;
    }

    @GET
    @Path("dailytotal")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TheftGroup> getDailyTotal(@QueryParam("date") String date) {
        String q = "SELECT date, COUNT(*) " +
                   "FROM \"gta-vi\".alarm " +
                   "WHERE date = '" + date + "' " +
                   "GROUP BY date;";

        DatabaseAccesser da = new DatabaseAccesser();
        ResultSet rs = da.queryRS(q);
        ArrayList<TheftGroup> timeTheft = new ArrayList<>();

        try {
            while (rs.next()) {
                TheftGroup tg = new TheftGroup(rs.getString(1), rs.getInt(2));
                timeTheft.add(tg);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return timeTheft;
    }

    @GET
    @Path("weeklytotal")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TheftGroup> getWeeklyTotal(@QueryParam("date") String date) {
        String q = "SELECT DATE_PART('week', datetime), COUNT(*)\n" +
                   "FROM \"gta-vi\".alarm\n" +
                   "WHERE DATE_PART('week', datetime) = DATE_PART('week', '" + date + "'::date)\n" +
                   "GROUP BY DATE_PART('week', datetime);";

        DatabaseAccesser da = new DatabaseAccesser();
        ResultSet rs = da.queryRS(q);
        ArrayList<TheftGroup> timeTheft = new ArrayList<>();

        try {
            while (rs.next()) {
                TheftGroup tg = new TheftGroup(date, rs.getInt(2));
                timeTheft.add(tg);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return timeTheft;
    }

    @GET
    @Path("monthlytotal")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TheftGroup> getMonthlyTotal(@QueryParam("date") String date) {
        String q = "SELECT DATE_PART('month', datetime), COUNT(*)\n" +
                "FROM \"gta-vi\".alarm\n" +
                "WHERE DATE_PART('month', datetime) = DATE_PART('month', '" + date + "'::date)\n" +
                "GROUP BY DATE_PART('month', datetime);";

        DatabaseAccesser da = new DatabaseAccesser();
        ResultSet rs = da.queryRS(q);
        ArrayList<TheftGroup> timeTheft = new ArrayList<>();

        try {
            while (rs.next()) {
                TheftGroup tg = new TheftGroup(date, rs.getInt(2));
                timeTheft.add(tg);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return timeTheft;
    }

    @GET
    @Path("yearlytotal")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TheftGroup> getYearlyTotal(@QueryParam("date") String date) {
        String q = "SELECT DATE_PART ('month', datetime), COUNT(*)\n" +
                   "FROM \"gta-vi\".alarm\n" +
                   "WHERE DATE_PART ('year', datetime) = DATE_PART ('year', '" + date + "'::date)\n" +
                   "GROUP BY DATE_PART ('month', datetime)\n" +
                   "ORDER BY DATE_PART ('month', datetime) ASC;";

        DatabaseAccesser da = new DatabaseAccesser();
        ResultSet rs = da.queryRS(q);
        ArrayList<TheftGroup> timeTheft = new ArrayList<>();

        try {
            while (rs.next()) {
                TheftGroup tg = new TheftGroup(rs.getString(1), rs.getInt(2));
                timeTheft.add(tg);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return timeTheft;
    }


    @GET
    @Path("hothour")
    @Produces(MediaType.APPLICATION_JSON)
    public TheftGroup getHotHour() {
        String q = "SELECT DATE_PART('hour', datetime), COUNT(*)\n" +
                "FROM \"gta-vi\".alarm\n" +
                "GROUP BY DATE_PART('hour', datetime)\n" +
                "ORDER BY COUNT(*) DESC\n" +
                "LIMIT 1;";

        DatabaseAccesser da = new DatabaseAccesser();
        ResultSet rs = da.queryRS(q);
        TheftGroup tg = new TheftGroup(null, 0);

        try {
            while (rs.next()) {
                tg = new TheftGroup(rs.getInt(1) + "", rs.getInt(2));
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return tg;
    }

    @GET
    @Path("hotarticle")
    @Produces(MediaType.APPLICATION_JSON)
    public TheftGroup getHotArticle() {
        String q = "SELECT a.article_id, COUNT(*)\n" +
                "FROM \"gta-vi\".alarm t, \"gta-vi\".articles a\n" +
                "WHERE t.article_id = a.article_id\n" +
                "GROUP BY a.article_id\n" +
                "ORDER BY COUNT(*) DESC\n" +
                "LIMIT 1;";

        DatabaseAccesser da = new DatabaseAccesser();
        ResultSet rs = da.queryRS(q);
        TheftGroup tg = new TheftGroup(null, 0);
        try {
            while (rs.next()) {
                tg = new TheftGroup(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return tg;
    }

    private List<TheftGroup> getTheftGroups(String q) {
        DatabaseAccesser da = new DatabaseAccesser();
        ResultSet rs = da.queryRS(q);
        ArrayList<TheftGroup> timeTheft = new ArrayList<>();

        try {
            while (rs.next()) {
                TheftGroup tg = new TheftGroup(rs.getString(1), rs.getInt(2));

                timeTheft.add(tg);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return timeTheft;
    }

}
