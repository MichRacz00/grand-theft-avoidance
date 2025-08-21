package dao;

import access.DatabaseAccesser;
import model.Article;
import model.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public enum LocationsDao {
    instance;

    private Map<String, Location> locationMap = new HashMap<>();

    private LocationsDao() {
        String q = "SELECT l.store_id, l.latitude, l.longitude, COUNT(*) " +
                   "FROM \"gta-vi\".store l, \"gta-vi\".alarm t " +
                   "WHERE l.store_id = t.store_id_ut " +
                   "GROUP BY l.latitude, l.store_id, l.longitude";

        DatabaseAccesser da = new DatabaseAccesser();
        ResultSet rs = da.queryRS(q);

        try {
            while (rs.next()) {
                Location l = new Location(rs.getInt(1), rs.getInt(2),
                                            rs.getInt(3), rs.getInt(4));

                locationMap.put(l.getId() + "", l);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public Map<String, Location> getLocationMap() {
        return locationMap;
    }

}
