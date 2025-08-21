package access;

import dao.UsersDao;
import model.Hashing;
import model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;

/**
 * Login screen.
 *
 * @author Michael, Antoine
 */

@Path("login")
public class LoginHandler {

    public static final int TIMEOUT = 900;

    public LoginHandler() {

    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public String login(@QueryParam("login") String login,
                        @QueryParam("password") String password) {

        DatabaseAccesser da = new DatabaseAccesser();

        try {
            Connection connection = DriverManager.getConnection(da.url, da.username, da.password);

            String q = "SELECT password, permission, salt " +
                       "FROM \"gta-vi\".login " +
                       "WHERE login = ?";

            PreparedStatement ps = connection.prepareStatement(q);
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String saltedPass = password + rs.getString(3);
                saltedPass = Hashing.SHA512_encoder(saltedPass);
                String permission = rs.getString(2);

                if (saltedPass.equals(rs.getString(1))) {
                    SecureRandom sr = new SecureRandom();
                    byte bytes[] = new byte[20];
                    sr.nextBytes(bytes);
                    int token = ByteBuffer.wrap(bytes).getInt();

                    q = "UPDATE \"gta-vi\".login\n" +
                            "SET token = ?, timestamp = NOW()\n" +
                            "WHERE login = ?";

                    ps = connection.prepareStatement(q);
                    ps.setInt(1, token);
                    ps.setString(2, login);
                    ps.executeUpdate();

                    connection.close();

                    String output = "{\"token\": " + token + ", " +
                                     "\"permission\": " + permission + "}";

                    return output;
                } else {
                    connection.close();
                }
            }

            connection.close();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return "{\"token\": false, \"permission\": false}";
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    public String newUser(@QueryParam("login") String login,
                          @QueryParam("password") String password,
                          @QueryParam("storeid") int storeid,
                          @QueryParam("permission") int permission) {

        DatabaseAccesser da = new DatabaseAccesser();

        try {
            String q = "SELECT EXISTS(SELECT login\n" +
                       "FROM \"gta-vi\".login WHERE login = ?)";

            Connection connection = DriverManager.getConnection(da.url, da.username, da.password);
            PreparedStatement ps = connection.prepareStatement(q);
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();

            boolean userInDB = true;
            while (rs.next()) {
                userInDB = rs.getBoolean(1);
            }

            if (!userInDB) {

                SecureRandom sr = new SecureRandom();
                byte bytes[] = new byte[16];
                sr.nextBytes(bytes);
                int salt = ByteBuffer.wrap(bytes).getInt();

                password = Hashing.SHA512_encoder(password + salt);

                q = "INSERT INTO \"gta-vi\".login " +
                        "(login, password, store, permission, salt) " +
                        "VALUES (?, ?, ?, ?, ?);";

                ps = connection.prepareStatement(q);
                ps.setString(1, login);
                ps.setString(2, password);
                ps.setInt(3, storeid);
                ps.setInt(4, permission);
                ps.setInt(5, salt);

                ps.executeUpdate();
                connection.close();

                User newUser = new User(login,permission);
                UsersDao.instance.getUserMap().put(login,newUser);

                return "true";
            } else {
                return "false";
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return "false";
    }

    @GET
    @Path("getId")
    @Produces(MediaType.APPLICATION_JSON)
    public String getStoreId(@QueryParam("token") int token) {
        String q = "SELECT store, permission\n" +
                   "FROM \"gta-vi\".login\n" +
                   "WHERE token = ?;";

        DatabaseAccesser da = new DatabaseAccesser();

        int storeid = 0;
        int permission = 0;
        try {
            Connection connection = DriverManager.getConnection(da.url, da.username, da.password);
            PreparedStatement ps = connection.prepareStatement(q);
            ps.setInt(1, token);
            ResultSet rs = ps.executeQuery();
            connection.close();

            while (rs.next()) {
                storeid = rs.getInt(1);
                permission = rs.getInt(2);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        if (!checkTimestamp(token)) {
            return "false";
        }

        updateTimestamp(token);
        String output = "{\"store\": " + storeid + ", \"permission\": " + permission + "}";
        return output;
    }

    public boolean checkTimestamp(int token) {
        String q = "SELECT CEIL ((DATE_PART('hour', NOW() - login.timestamp) * 60 +\n" +
                "        DATE_PART('minute', NOW() - login.timestamp)) * 60 +\n" +
                "        DATE_PART('second', NOW() - login.timestamp))\n" +
                "FROM \"gta-vi\".login\n" +
                "WHERE token = " + token + ";";

        DatabaseAccesser da = new DatabaseAccesser();
        ArrayList<String[]> result = da.query(q);
        if (result.size() == 0) {
            return false;
        } else {
            return Integer.parseInt(result.get(0)[0]) < TIMEOUT;
        }
    }

    public void updateTimestamp(int token) {
        String q = "UPDATE \"gta-vi\".login SET\n" +
                "                timestamp = NOW()\n" +
                "        WHERE token = " + token + ";";

        DatabaseAccesser da = new DatabaseAccesser();
        ArrayList<String[]> result = da.query(q);
    }

    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_HTML)
    public String userCount() {
        return UsersDao.instance.getUserMap().size() + "";
    }

    @DELETE
    public void deleteToken(@QueryParam("token") int token) {
        String q = "UPDATE \"gta-vi\".login\n" +
                   "SET token = null\n" +
                   "WHERE token = ?;";

        DatabaseAccesser da = new DatabaseAccesser();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(da.url, da.username, da.password);
            PreparedStatement ps = connection.prepareStatement(q);
            ps.setInt(1, token);
            ps.executeUpdate();
            connection.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @PUT
    @Path("modifyStoreId")
    public void modifyStoreId(@QueryParam("tempstore") int store,
                              @QueryParam("token") int token) {

        String q = "UPDATE \"gta-vi\".login\n" +
                   "SET store = ?\n" +
                   "WHERE token = ?;";

        DatabaseAccesser da = new DatabaseAccesser();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(da.url, da.username, da.password);
            PreparedStatement ps = connection.prepareStatement(q);
            ps.setInt(1, store);
            ps.setInt(2, token);
            ps.executeUpdate();
            connection.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}

