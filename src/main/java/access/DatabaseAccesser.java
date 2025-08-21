package access;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * Class that accesses the database and creates data objects.
 */
public class DatabaseAccesser {
    public static String url;
    public static String username;
    public static String password;

    public DatabaseAccesser() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String host = "bronto.ewi.utwente.nl";
        String dbName = "dab_di20212b_113";

        username = "dab_di20212b_113";
            password = "b0X0v8FBOi4wTG/w";

        url = "jdbc:postgresql://" + host + ":5432/" + dbName + "?currentSchema=gta-vi";
    }

    /**
     * Queries the database.
     *
     * @param q query
     * @return result of the query
     */
    public static ArrayList<String[]> query(String q) {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(q);
            ArrayList<String[]> output = new ArrayList<>();

            while (rs.next()) {
                String[] tuple = new String[rs.getMetaData().getColumnCount()];
                for (int i = 1; i != rs.getMetaData().getColumnCount() + 1; i ++) {
                    tuple[i - 1] = rs.getString(i);
                }
                output.add(tuple);
            }

            connection.close();
            return output;

        } catch (SQLException e) {
            System.out.println("Error connecting: " + e);
        }

        return null;
    }

    public static ResultSet queryRS(String q) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(q);
            connection.close();
            return rs;

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    /**
     * Returns names for columns of the table.
     *
     * @param table
     * @author Michaels
     */
    public static String[] getColumnNames(String table) {
        String q = "SELECT COLUMN_NAME\n" +
                   "FROM INFORMATION_SCHEMA.COLUMNS\n" +
                   "WHERE TABLE_NAME = " + table;

        return query(q).get(0);
    }
}
