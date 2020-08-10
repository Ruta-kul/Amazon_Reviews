package DataLoading;

import DatabaseConnection.DBUtilities;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * This class is the main bulkloading class responsible for controlling the bulkload process
 * The commented section can be used to provide a simple GUI based access if necessary or to change endpoints
 * for local data loading
 *
 * Main details that need to be updated:
 * 1) bulkload_path : Directory from which bulk load files need to be loaded from
 * 2) bulkload_type : File type , review or product file
 * 3) query log file : Optional file for reviewing queries.
 * 4) endpoint : Database oonnection URL
 * 5) username : Database username
 * 6) password : Database password
 * 7) batch size: If using the bulkload method from db utilities
 */
public class BulkLoader {

    public static void main(String[] args) {

        String bulkload_path = "Bulkload";  // Bulkload directory path here
        String bulkload_type = "Product"; // either Product or review


//        bulkload_type = args[0] ;
        System.out.println("bulkload type is:" + bulkload_type);


//
//        String bulkload_path = JOptionPane.showInputDialog("Please enter the bulkload directory!");
//        String bulkload_type = JOptionPane.showInputDialog("Please enter filetype: Product or review");
        String query_log_file = bulkload_path +"generatedsql.sql";

        // End points , keep the session variable to ensure queries continue after errors and
        // to allow text truncation in case of overflow

        // AWS end point
//        String end_point = "jdbc:mysql://amazon.c9yalx65oods.us-east-1.rds.amazonaws.com/Amazon" +
//                "Reviews?sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false";
//        String username = "admin";
//        String pword  = "admin100";

//        String end_point =  "jdbc:mysql://" +JOptionPane.showInputDialog("Please enter the" +
//                " connection endpoint of db followed by / and then the database name(eg: amazon.c9yalx65oods.us-east-1.rds.amazonaws.com/AmazonReviews)")+
//                "?sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false";
//        String username =  JOptionPane.showInputDialog("Please enter the username for db login");
//        String pword = JOptionPane.showInputDialog("Please enter the password for db login");

//         Local end point
        String end_point = "jdbc:mysql://localhost:3306/practice" +
                "?sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false";
        String username = "root";
        String pword  = "root";
        int batch_size  = 10000;

        DBUtilities dbu = new DBUtilities();
        File folder = new File(bulkload_path);
        File[] list_of_files = folder.listFiles();

        Connection conn = dbu.establishConnection(end_point, username, pword);
        BufferedWriter sql_file_generated;

        try {
            sql_file_generated = new BufferedWriter(new FileWriter(query_log_file));
            for (File file: list_of_files) {

                System.out.println("Currently parsing file:" + file.getName());
                String current_file = file.getAbsolutePath();
                String table_name;
                if (bulkload_type.equals( "Product")) {
                    table_name = "Product";
                } else {
                    table_name = "Reviews";
                }
                System.out.println("Retrieving queries");
                ArrayList<String> queries = (JSONtoSQL.convertProducts(current_file, table_name));
                System.out.println("Loading data into database");
//                System.out.println("Failing queries will appear below ...");

                int count = 0;

                // Uncomment this to use the bulkloading module. Relatively same speed
                // Comment the for loop section below then in exchange
//                dbu.insertBulkQuery(conn,queries, batch_size);

                for (String query : queries) {
                    sql_file_generated.write(query);
                    count = dbu.insertQuery(conn, query);

                    // if error display query for reference
                    if (count < 1) {
//                        System.out.println(query);
                    }
                }
            }
            sql_file_generated.close();
            dbu.closeConnection(conn);
            System.out.println(" Loading Completed!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
