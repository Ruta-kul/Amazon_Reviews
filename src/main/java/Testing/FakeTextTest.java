package Testing;

import FakeText.FakeTextFeatures;
import FakeText.FakeTextUtil;

/**
 * Testing class for testing methods in the fake text module
 */
public class FakeTextTest {


    public static void main(String[] args) {
        testSimilarity();

    }

    private static void testSimilarity() {
        String string1 = "I like chocolate !";
        String string2 = "I like chocolate !";
//        String string2 = "But I prefer chocolate cakes more";
        String string3 = "I like chocolate cakes";


        FakeTextUtil ftu = new FakeTextUtil();
        FakeTextFeatures ftf = new FakeTextFeatures();
        System.out.println(ftu.getCosineSimilarity(string1, string2));
        System.out.println(ftu.getCosineSimilarity(string3, string2));
        System.out.println(ftu.getCosineSimilarity(string1, string3));
    }

}
