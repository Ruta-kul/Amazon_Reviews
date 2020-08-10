package DataLoading;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;

/**
 * Class used to split JSon file into separate columns if necessary
 */
public class SplitJSON {

    /**
     * Splits file based on the elements to keep list
     *
     * @param input_file       Input file name
     * @param output_file      Output file name
     * @param elements_to_keep Array List containing names of elemenets to be retained
     */
    public void splitFile(String input_file, String output_file, ArrayList<String> elements_to_keep) {
        BufferedReader input_file_reader = null;
        BufferedWriter output_file_writer = null;
        try {
            input_file_reader = new BufferedReader(new FileReader(input_file));
            output_file_writer = new BufferedWriter(new FileWriter(output_file));
        } catch (FileNotFoundException fe) {
            System.out.println("File not found :" + fe);
        } catch (IOException ie) {
            System.out.println("IOException: " + ie);
        }
        String line;
        JSONObject jo = new JSONObject();
        try {
            while ((line = input_file_reader.readLine()) != null) {
                JSONObject jsonLine = (JSONObject) new JSONParser().parse(line);
                jo = new JSONObject();
                for (String element : elements_to_keep) {
                    jo.put(element, jsonLine.get(element));
                    System.out.println(jo.get(element));
                }
                System.out.println(jo.toString());
                output_file_writer.write(jo.toString() + "\n");

            }
            output_file_writer.close();
            input_file_reader.close();
        } catch (IOException ioe) {
            System.out.println("IOError:" + ioe);
        } catch (ParseException pe) {
            System.out.println("Error while parsing file : " + pe);
        }
    }


    // Sample usage
    public static void main(String args[]) {
        String input_file = "C:\\Users\\Sudhanshu Tiwari\\Documents\\GIT\\AmazonReviews\\src\\main\\java\\DataLoading\\jsontest.txt";
        String output_file = "C:\\Users\\Sudhanshu Tiwari\\Documents\\GIT\\AmazonReviews\\src\\main\\java\\DataLoading\\output.txt";

        ArrayList<String> elements_to_keep = new ArrayList<>();
        elements_to_keep.add("asin");
        elements_to_keep.add("imUrl");
        elements_to_keep.add("categories");

        SplitJSON splitj = new SplitJSON();
        splitj.splitFile(input_file, output_file, elements_to_keep);


    }

}
