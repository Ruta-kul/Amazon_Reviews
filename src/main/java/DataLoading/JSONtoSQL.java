package DataLoading;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class that parses JSON file to SQL file
 */
public class JSONtoSQL {

    /**
     * Parses products file to SQL syntax for insertion
     *
     * @param input_file Input json file to be parsed
     * @param table_name Products table to be inserted into
     * @return Array list of string containing queries that can be sent to DB
     */
    public static ArrayList<String> convertProducts(String input_file, String table_name) {
        ArrayList<String> output_query = new ArrayList<>();     // Output result
        BufferedReader inputFileReader = null;                  // File reader
        ArrayList<String> also_viewed = new ArrayList<>();      // Also Viewed table queries
        ArrayList<String> also_bought = new ArrayList<>();     // Also Bought table queries
        ArrayList<String> user_data = new ArrayList<>();        // User List table queries
        ArrayList<String> Categories = new ArrayList<>();       // Categories table queries

        try {
            inputFileReader = new BufferedReader(new FileReader(input_file));
        } catch (FileNotFoundException fe) {
            System.out.println("File not found exception : " + fe);
        }
        String line;
        int count = 0;
        try {
            while ((line = inputFileReader.readLine()) != null) {
                count++;
                JSONObject jsonLine;
                try {
                    jsonLine = (JSONObject) new JSONParser().parse(line);
                } catch (ParseException pe) {
                    System.out.println("parse exception: " + pe + ":" + count);
                    continue;
                }
                String output = "";
                output += "insert into " + table_name;

                String left_half = " (", right_half = " values(";

                for (Iterator iterator = jsonLine.keySet().iterator(); iterator.hasNext(); ) {
                    try {
                        String key = (String) iterator.next();
                        Object current_element = jsonLine.get(key);
                        if (!key.equals("related") && !key.equals("helpful")) {
                            left_half += key + ",";
                        }

                        // Try converting to number if possible
                        if (current_element instanceof String && !key.equals("asin")) {
                            try {
                                current_element = Float.parseFloat((String) (current_element));
                            } catch (NumberFormatException ne) {
                            }
                        }
                        if (current_element instanceof Float) {
                            right_half += current_element.toString() + ",";
                        } else if (current_element instanceof JSONArray) {
                            JSONArray innerValues = (JSONArray) current_element;

                            for (Iterator iterator1 = innerValues.iterator(); iterator1.hasNext(); ) {
                                Object internValue = iterator1.next();
                                Object internValue1 = null;
                                try {
                                    internValue1 = (JSONArray) internValue;
                                } catch (Exception e) {
                                    if (internValue1 instanceof JSONObject) {
                                        internValue1 = (JSONObject) internValue;
                                    } else {
                                        internValue1 = (Object) internValue;
                                    }
                                }
                                right_half += "";
                                if (internValue1 instanceof JSONArray) {
                                    for (Iterator iterator2 = ((JSONArray) internValue1).iterator(); iterator2.hasNext(); ) {
                                        String inside = (String) iterator2.next();
                                        inside = inside.replace("\"", "");
                                        inside = inside.replace(",", " ");
                                        right_half += "\'" + inside + "\'";
                                    }
                                } else if (internValue1 instanceof JSONObject) {
                                    JSONObject temp = (JSONObject) internValue1;
                                    String temp1 = temp.toString().replace(",", "");
                                    right_half += "\'" + temp1 + "\'";
                                }
                            }
                            right_half += ",";
                        } else {
                            if (!key.equals("related") && !key.equals("salesRank") && !key.equals("helpful")
                                    && !key.equals("reviewTime") && !key.equals("asin") && !key.equals("price")
                                    && !key.equals("categories") && !key.equals("overall")) {
                                String temp = current_element.toString().replace(",", " ");
                                temp = temp.replace("\"", " ");
                                temp = temp.replace("}", " ");
                                right_half += "\"" + temp + "\"" + ",";
                            }
                        }
                        if (key.equals("overall")) {
                            String temp = current_element.toString().replace(",", " ");
                            temp = temp.replace("\"", " ");
                            temp = temp.replace("}", " ");
                            Float floatoverall = Float.parseFloat(temp);
                            right_half += floatoverall + ",";
                        } else if (key.equals("helpful")) {
                            left_half += "helpfulness_assign,";
                            left_half += "helpfulness_max,";

                            String temp = current_element.toString();
                            temp = temp.replace("[", "");
                            temp = temp.replace("]", "");
                            int assigned_value = Integer.parseInt(temp.split(",")[0]);
                            int max_value = Integer.parseInt(temp.split(",")[1]);
                            right_half += assigned_value + ",";
                            right_half += max_value + ",";

                        } else if (key.equals("salesRank")) {
                            String temp = current_element.toString().replace(",", " ");
                            temp = temp.replace("\"", " ");
                            if (temp.contains("-")) {
                                temp = temp.replace("-", "0");
                            }
                            temp = temp.split(":")[1];
                            temp = temp.replace("}", " ");
                            right_half += temp + ",";

                        } else if (key.equals("categories")) {

                            // append asin in categories of product
                            String temp = jsonLine.get("asin").toString().replace(",", " ");
                            temp = temp.replace("\"", " ");
                            temp = temp.replace("}", " ");
                            if (temp.contains("-")) {
                                temp = temp.replace("-", "0");
                            }
                            String asin = temp;

                            // append individual category in Categories table
                            for (Object element : (JSONArray) current_element) {
                                temp = element.toString();
                                temp = temp.replace("[", "");
                                temp = temp.replace("]", "");
                                temp = temp.replace(",", "");
                                temp = temp.replace("\"", "");

                                String temp_result = "Insert into Categories values (\"" + temp + "\",\"" + asin + "\");\n";
                                Categories.add(temp_result);
                            }
                        } else if (key.equals("price")) {
                            String temp = current_element.toString().replace(",", " ");
                            temp = temp.replace("\"", " ");
                            if (temp.contains("-")) {
                                temp = temp.replace("-", "0");
                            }
                            right_half += temp + ",";
                        } else if (key.equals("asin")) {
                            String temp = current_element.toString().replace(",", " ");
                            temp = temp.replace("\"", " ");
                            if (temp.contains("-")) {
                                temp = temp.replace("-", "0");
                            }
                            right_half += "\"" + temp + "\"" + ",";
                        } else if (key.equals("related")) {
                            JSONObject innerValues = (JSONObject) current_element;
                            for (Iterator iterator1 = innerValues.keySet().iterator(); iterator1.hasNext(); ) {
                                Object internKey = iterator1.next();
                                JSONArray internValue = (JSONArray) innerValues.get(internKey);
                                if (internKey.toString().equals("buy_after_viewing")) {
                                    for (Iterator iterator2 = internValue.iterator(); iterator2.hasNext(); ) {
                                        String temp = jsonLine.get("asin").toString();
                                        temp = temp.replace(",", "");
                                        temp = temp.replace("-", "0");
                                        String query = "insert into also_bought values (\"" + temp + "\",";
                                        query += "\"" + (String) iterator2.next() + "\",";
                                        query = query.substring(0, query.length() - 2) + "\");\n";
                                        also_bought.add(query);
                                    }
                                } else if (internKey.toString().equals("also_viewed")) {
                                    for (Iterator iterator2 = internValue.iterator(); iterator2.hasNext(); ) {
                                        String temp = jsonLine.get("asin").toString();
                                        temp = temp.replace(",", "");
                                        if (temp.contains("-")) {
                                            temp = temp.replace("-", "0");
                                        }
                                        String query = "insert into also_viewed values (\"" + temp + "\",";
                                        query += "\"" + (String) iterator2.next() + "\",";
                                        query = query.substring(0, query.length() - 2) + "\");\n";
                                        also_viewed.add(query);
                                    }
                                }
                            }
                        } else if (key.equals("reviewerID")) {
                            String name;
                            try {
                                name = jsonLine.get("reviewerName").toString();
                                name = name.replace("\"", " ");
                            } catch (NullPointerException np) {
                                name = "**";
                            }
                            if (name.equals("**")) {
                                name = "N/A";
                            }
                            String query = "insert into UserList values (\"" + jsonLine.get("reviewerID") + "\", \"" + name + "\")" + ";\n";
                            user_data.add(query);
                        } else if (key.equals("helpful")) {
                            String temp = current_element.toString();
                            temp = temp.replace(",", "/");
                            temp = temp.replace("[", "\"");
                            temp = temp.replace("]", "\"");
                            right_half = right_half.substring(0, right_half.length() - 1) + temp + ",";
                        } else if (key.equals("reviewTime")) {
                            String temp = current_element.toString();
                            temp = temp.trim();

                            temp = temp.replace("  ", " ");
                            temp = temp.replace(" ", "/");
                            temp = temp.replace(",", "");
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY");
                            try {
                                java.util.Date utilDate = format.parse(temp);
                                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                                right_half += "\"" + sqlDate + "\" ";
                            } catch (java.text.ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
                left_half = left_half.substring(0, left_half.length() - 1);
                right_half = right_half.substring(0, right_half.length() - 1);
                left_half += ")";
                right_half += ");";
                output = output + left_half + right_half;
                output = output.replace(",,", ",");
                output_query.add(output + "\n");
            }
            inputFileReader.close();
        } catch (IOException ioe) {
            System.out.println("IOException occured: " + ioe);
        }

        output_query.addAll(also_bought);
        output_query.addAll(also_viewed);
        for (String query : user_data) {
            output_query.add(0, query);
        }
//        System.out.println(output_query);
        output_query.addAll(Categories);
        return output_query;
    }


    // Example usage
    public static void main(String[] args) {
        String input_file = "C:\\Users\\Sudhanshu Tiwari\\Documents\\GIT\\AmazonReviews\\src\\main\\java\\DataLoading\\jsontest.txt";
        String table_name = "Product";
        ArrayList<String> results = (JSONtoSQL.convertProducts(input_file, table_name));

        for (String result : results) {
            System.out.print(result);
        }
    }
}
