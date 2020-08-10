package DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;


/**
 * This class provides utility methods that can be used for establishing connection to the database server
 * as well as retrieving query results
 */
public class DBUtilities {

    // For future usage as necessary
    private ArrayList<String> End_points = new ArrayList<>();
    private ArrayList<String> UName = new ArrayList<>();
    private ArrayList<String> PassW = new ArrayList<>();

    /**
     * Class constructor
     */
    public DBUtilities() {
        updateDataAccessPoints();
    }

    /**
     * Establishes connection with a given URL
     *
     * @param URL      URL to connect to
     * @param username Username of DB account
     * @param password Password of DB account
     * @return Returns connection object if established successfully
     */
    public Connection establishConnection(String URL, String username, String password) {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, username, password);
            return con;
        } catch (ClassNotFoundException cfe) {
            System.out.println("Class not found exception :" + cfe);
        } catch (SQLException sqe) {
            System.out.println("SQL Exception: " + sqe);
        }
        System.out.println("Connection established with : " + URL + " !");
        return con;
    }

    /**
     * Closes connection with the provided connection object
     *
     * @param con Connection to be closed
     */
    public void closeConnection(Connection con) {
        try {
            con.close();
        } catch (SQLException e) {
            System.out.println("Sql Exception:" + e);
        }
    }

    /**
     * Executes the select query on a connected connection
     *
     * @param con   Connection on which query is to be done
     * @param query The query
     * @return Returns result set acquired after execution of query
     */
    public ResultSet selectQuery(Connection con, String query) {
        ResultSet rs = null;
        try {
            Statement stmt = con.createStatement();
            rs = stmt.executeQuery(query);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * Utility method to display all entries in a result set
     *
     * @param rs Result set to be displayed
     */
    public void displayResultSet(ResultSet rs) {
        try {
            int columns = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                for (int i = 1; i < columns + 1; i++) {
                    String columnValue = rs.getString(i);
                    System.out.print(columnValue + ",");
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a given result set into an array list of strings
     *
     * @param rs Result set to be converted
     * @return Array list of strings containing result set elements
     */
    public ArrayList<String> resultSettoArrayList(ResultSet rs) {

        ArrayList<String> result = new ArrayList<>();

        try {
            int columns = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                for (int i = 1; i < columns + 1; i++) {
                    String columnValue = rs.getString(i);
                    result.add(columnValue);
                }
//                System.out.println();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return result;
    }

    /**
     * Inserts data through a connection based on the query
     *
     * @param con   Connection to send query to
     * @param query Query
     * @return Number of rows updated
     */
    public int insertQuery(Connection con, String query) {
        try {
            PreparedStatement statement = con.prepareStatement(query);
            return statement.executeUpdate();
        } catch (Exception e) {
//            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Alternative bulk insert query method
     *
     * @param con        Connection to send query to
     * @param queries    Array list of string containing queries
     * @param batch_size Number of queries in a batch
     */
    public void insertBulkQuery(Connection con, ArrayList<String> queries, int batch_size) {
        try {
            Statement stmt = con.createStatement();
            System.out.println("inserting queries");
            con.setAutoCommit(false);
            int count = 0;

            for (String query : queries) {
                count++;
                // current batch ended
                if (count % batch_size == 0) {

                    con.commit();
                    // Reinintialize for next batch
                    stmt = con.createStatement();
                    System.out.println("parsed " + count + " queries");
                }
                stmt.addBatch(query);
            }


            con.commit();
            System.out.println("parsed " + count + " queries totsl");

            con.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs updates or deletes on the database connection established based on the query
     *
     * @param con   Connection to send query to
     * @param query Query
     */
    public void updateOrDelQuery(Connection con, String query) {
        Statement stmt;
        try {
            stmt = con.createStatement();
            stmt.executeLargeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Utility method to add data access points. Can be used to provide ease in access of multiple data points if necessary
     */
    private void updateDataAccessPoints() {
        End_points.add("jdbc:mysql://amazon.c9yalx65oods.us-east-1.rds.amazonaws.com//AmazonReviews:3306");
        UName.add("sudhanshu");
        PassW.add("sudhanshu");
    }
}
