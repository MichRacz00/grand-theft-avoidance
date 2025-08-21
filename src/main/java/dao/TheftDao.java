package dao;

import access.DatabaseAccesser;
import model.Article;
import model.Location;
import model.Theft;

import java.awt.*;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public enum TheftDao {
    instance;

    private Map<String, Theft> theftMap = new HashMap<>();

    private TheftDao() {
        String q = "SELECT a.store_id_ut, a.date, a.timestamp, a.epc, a.article_id, a.datetime, ar.article FROM \"gta-vi\".alarm a, \"gta-vi\".articles ar " +
                "WHERE ar.article_id = a.article_id";

        DatabaseAccesser da = new DatabaseAccesser();
        ResultSet rs = da.queryRS(q);


        try {
            while (rs.next()) {
                Theft t = new Theft(rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(1),rs.getString(7));

                theftMap.put(t.getEpc(), t);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public Map<String, Theft> getTheftMap() {
        return theftMap;
    }
}
