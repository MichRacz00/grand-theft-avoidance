package resources;

import access.DatabaseAccesser;
import dao.ArticlesDao;
import dao.UsersDao;
import model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Path("users")
public class UsersResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getUsers() {
        List<User> users = new ArrayList<>(UsersDao.instance.getUserMap().values());
        return users;
    }

    @GET
    @Path("{username}")
    public User getUser(@PathParam("username") String username) {
        User user = UsersDao.instance.getUserMap().get(username);
        if (user == null) throw new RuntimeException("User with " + username + " not found");
        return user;
    }

    @DELETE
    @Path("deleteuser")
    public void deleteUser(@QueryParam("username") String username){

        String q = "DELETE FROM \"gta-vi\".login l WHERE l.login = ?";

        DatabaseAccesser da = new DatabaseAccesser();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(da.url, da.username, da.password);
            PreparedStatement ps = connection.prepareStatement(q);
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

}
