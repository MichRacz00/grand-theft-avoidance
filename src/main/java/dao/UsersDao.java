package dao;

import access.DatabaseAccesser;
import model.User;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public enum UsersDao {
    instance;

    private Map<String, User> userMap = new HashMap<>();

    private UsersDao() {
        String q = "SELECT * FROM login;";
        DatabaseAccesser da = new DatabaseAccesser();
        ResultSet rs = da.queryRS(q);

        try {
            while (rs.next()) {
                User u = new User(rs.getString(1), rs.getInt(3), rs.getInt(4));
                userMap.put(u.getUsername(), u);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public Map<String, User> getUserMap() {
        return userMap;
    }
}
