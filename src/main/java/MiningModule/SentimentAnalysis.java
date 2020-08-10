package MiningModule;

import DatabaseConnection.DBUtilities;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

public class SentimentAnalysis {

    // aws
    static String db_url = "jdbc:mysql://amazon.c9yalx65oods.us-east-1.rds.amazonaws.com/AmazonReviews";
    static String uname = "tempuser";
    static String pw = "temp";

    // local
//    static String db_url = "jdbc:mysql://localhost:3306/practice";
//    static String uname = "root";
//    static String pw = "root";

    // Refer for more details : https://stanfordnlp.github.io/CoreNLP/api.html
    // Demo here for playing around : http://nlp.stanford.edu:8080/sentiment/rntnDemo.html

    public int getSentimentValue(String content) {

        /*
         * "Very negative" = 0 "Negative" = 1 "Neutral" = 2 "Positive" = 3
         * "Very positive" = 4
         */

        int calculatedSentiment = 0;

        // creates a StanfordCoreNLP object, with POS tagging, NER, parsing, and sentiment

        Properties properties = new Properties();
        properties.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

        if (content != null && content.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(content);

            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {

                // Parse tree of content
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);

                // Obtain the sentiment score
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);

                String partText = sentence.toString();

                if (partText.length() > longest) {
                    calculatedSentiment = sentiment;
                    longest = partText.length();
                }
            }
        }

        // if sentiment values go out of bounds
        if (calculatedSentiment > 4)
            return 4;
        else if (calculatedSentiment < 0)
            return 0;
        return calculatedSentiment;
    }


    public static ArrayList<String> retrieveReviews(String category, String asin, Connection conn) {

        // for general category query, asin = -1

        ArrayList<String> Reviews;
        DBUtilities dbu = new DBUtilities();

        if (null == conn)
            conn = dbu.establishConnection(db_url, uname, pw);

        System.out.println("connection established");

        if (asin.equals("-1")) {
            // category query
            String query = "select reviewText from Reviews where asin in ( select asin from product" +
                    "where categories=\"" + category + "\") limit 5000";
            System.out.println("current query + " + query);

            Reviews = dbu.resultSettoArrayList(dbu.selectQuery(conn, query));
        } else {
            String query = "select reviewText from Reviews where asin =" + "\"" + asin + "\"";
            ResultSet rs = dbu.selectQuery(conn, query);
            Reviews = dbu.resultSettoArrayList(rs);
        }

        return Reviews;
    }
}
