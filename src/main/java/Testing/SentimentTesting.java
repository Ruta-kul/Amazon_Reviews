package Testing;

import MiningModule.SentimentAnalysis;

import java.util.ArrayList;

/**
 * Testing class for testing methods in the Mining Module for sentiment analysis
 */
public class SentimentTesting {

    int number_of_queries;

    public static void main(String[] args) {
        displaySentiments();
    }

    public static ArrayList<String> displaySentiments() {
        ArrayList<String> result = new ArrayList<>();

        // can provide exiting conn
        SentimentAnalysis sa = new SentimentAnalysis();
        ArrayList<String> reviews = SentimentAnalysis.retrieveReviews("", "0004488938", null);

        for (String review : reviews) {
            int sentiment = sa.getSentimentValue(review);
            result.add(sentiment + "," + review);
            System.out.println(sentiment + "," + review);
        }
        return result;
    }

}
