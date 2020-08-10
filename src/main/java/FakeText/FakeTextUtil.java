package FakeText;

import info.debatty.java.stringsimilarity.Cosine;

/**
 * Utility class containing methods that assist with determining feature values
 */
public class FakeTextUtil {

    /**
     * Uses java based string comparison methods library by tedebatty
     * Refer https://github.com/tdebatty/java-string-similarity for the actual details of implementation.
     * Cosine similarity is useful as it works regardless of the document size. It works better with
     * larger number of sparse features which a sentence should have.
     * A pretrained model such as GloVe and then cosine similarity would increase the accuracy of this comparison but for our purposes
     * cosine should suffice to provide a relative close comparison.
     *
     * @param String1 String 1 to be compared
     * @param String2 String 2 to be compared
     * @return Result of Cosine similarity comparison
     */
    public double getCosineSimilarity(String String1, String String2) {

        Cosine cosine = new Cosine();
        return cosine.similarity(String1, String2);
    }

}
