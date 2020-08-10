package FakeText;

import DatabaseConnection.DBUtilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Class containing features that determine whether a review is fake
 */
public class FakeTextFeatures {

    /**
     * Reflects that the review is on the brand rather than the product itself.
     *
     * @param reviewText Review text to be parsed
     * @param brand_name Brand name of the product
     * @return Whether branc name is present and score based on it
     */
    public int Feature1_Brand(String reviewText, String brand_name) {

        if (reviewText.toLowerCase().contains(brand_name.toLowerCase())) {
            return 1;
        }
        return 0;
    }

    /**
     * Marks reviews that contain the seller name- in this case amazon
     *
     * @param reviewText Review text to be parsed
     * @return 1 in case seller is present, 0 otherwise
     */
    public int Feature3_seller_website(String reviewText) {

        if (reviewText.toLowerCase().contains("amazon"))
            return 1;
        else
            return 0;
    }

    /**
     * Checks if the given review rating is an outlier
     *
     * @param overall Overall rating of the product
     * @param asin    Product asin
     * @param conn    Connection established with database
     * @return 2 if an outlier, 0 otherwise
     */
    public int Feature6_outlier_review(Double overall, String asin, Connection conn) {
        DBUtilities dbu = new DBUtilities();
        String average_reviews_query = "select avg(overall) from Reviews where asin='" + asin + "'";
        ResultSet rs = dbu.selectQuery(conn, average_reviews_query);
        double avg = 0.0;
        try {
            while (rs.next()) {
                avg = rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (Math.abs(overall - avg) > 2) {
            return 2;
        } else {
            return 0;
        }
    }

    /**
     * Checks for similar reviews present. This checks the originality and the genuineness of a review.
     *
     * @param reviewText Review Text to be checked
     * @param reviewerID Reviewer ID of the review
     * @param asin       asin of the product which is being reviewed
     * @param conn       Connection established to the database
     * @return Similarity value after comparison
     * @throws SQLException Needs to be caught for handling query errors
     */
    public double Feature7_8_similar_reviews(String reviewText, String reviewerID, String asin, Connection conn) throws SQLException {
        double similar = 0.0;
        DBUtilities dbu = new DBUtilities();

        String query = "select reviewText from Reviews where asin ='" + asin + "' and reviewerID!='" + reviewerID + "'";

        ResultSet rs = dbu.selectQuery(conn, query);
        FakeTextUtil fakeUtil = new FakeTextUtil();
        while (rs.next()) {
            double currentMatch = fakeUtil.getCosineSimilarity(reviewText, rs.getString("reviewText"));
            if (currentMatch > similar) {
                similar = currentMatch;
            }
        }

        return similar;

    }

}
