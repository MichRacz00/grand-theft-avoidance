package resources;

import access.DatabaseAccesser;
import dao.LocationsDao;
import dao.UsersDao;
import model.Hashing;
import model.Location;
import model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Path("/stores")
public class LocationsResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Location> getStores() {
        List<Location> stores = new ArrayList<>(LocationsDao.instance.getLocationMap().values());
        return stores;
    }

    @GET
    @Path("{storeID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Location getStore(@PathParam("storeID") String id) {
        Location location = LocationsDao.instance.getLocationMap().get(id);
        if (location == null) throw new RuntimeException("Store with " + id + " not found");
        return location;
    }

    @GET
    @Path("amount")
    @Produces(MediaType.TEXT_HTML)
    public String getAmount() {
        String output = (LocationsDao.instance.getLocationMap().size()) + "";
        return output;
    }

    @GET
    @Path("mapfilter")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Location> getStoresByThefts(@QueryParam("min") String min,
                                              @QueryParam("max") String max) {

        String q = "SELECT store_id, COUNT(*),l.latitude, l.longitude\n" +
                "FROM \"gta-vi\".store l, \"gta-vi\".alarm t\n" +
                "WHERE l.store_id = t.store_id_ut\n" +
                "GROUP BY store_id, l.latitude, l.longitude\n" +
                "HAVING COUNT(*) >" +min+ " AND COUNT(*) < " + max+ "";

        DatabaseAccesser da = new DatabaseAccesser();
        ResultSet rs = da.queryRS(q);
        ArrayList<Location> locations = new ArrayList<>();

        try {
            while (rs.next()) {
                Location l = new Location(rs.getInt(1), rs.getInt(3), rs.getInt(4),  rs.getInt(2));

                locations.add(l);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return locations;
    }

    @POST
    @Path("addstore")
    public void addStore(@QueryParam("id") int id,
                         @QueryParam("latitude") int lat,
                         @QueryParam("longitude") int lon) {
        DatabaseAccesser da = new DatabaseAccesser();

        try {
            String q = "INSERT INTO \"gta-vi\".store " +
                       "(store_id, latitude, longitude) " +
                       "VALUES (?, ?, ?);";

            Connection connection = DriverManager.getConnection(da.url, da.username, da.password);
            PreparedStatement ps = connection.prepareStatement(q);
            ps.setInt(1, id);
            ps.setInt(2, lat);
            ps.setInt(3, lon);
            ps.executeUpdate();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
