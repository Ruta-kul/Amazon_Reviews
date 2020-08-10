<!--
This file contains the main review page application that is used for analyzing the amazon reviews dataset. It can be broadly split into 3 parts:
Section 1 : JS dependencies and fetching data from the database.
Section 2 : Display appropriate sections and connect to section 1 for content update.
Section 3 : Modals that are displayed or hidden based on requirements.
-->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="DatabaseConnection.DBUtilities" %>
<%@ page import="MiningModule.SentimentAnalysis" %>

<html>
<head>
    <title>
        Amazon review analyzer
    </title>
    <!-- -------------------------------------------------------------------------------------------------------------------------- -->
    <!--Dependencies-->
    <script src="js/jquery-3.3.1.js"></script>  <!-- JQuery script -->
    <link href="css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css"/> <!-- Bootstrap scripts -->
    <script src="js/bootstrap.min.js"></script>
    <link href="css/open-iconic-bootstrap.css" rel="stylesheet"/>
    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <!--Google charts scripts -->

    <!--to wait for loading of google charts library -->
    <script>function dummy() {
    } </script>
    <script>
        google.charts.load('current', {'packages': ['corechart']});
        google.charts.setOnLoadCallback(dummy());
    </script>

    <!-- -------------------------------------------------------------------------------------------------------------------------- -->
    <!-- Section 1 : Database access and update element JS scripts -->

    <script>
        /**
         * Function that displays the also bought results for the current product
         */
        function displayAlsoBought() {
            var newData = "<table class='table'><tr><th>Item</th><th>Name</th><th>Category</th></tr>";
            for (var i = 0; i < arguments.length; i++) {
                var data = arguments[i].split("#");
                newData = newData + "<tr><td><img style='width: 150px;height: 150px' src='" + data[2] + "'/></td><td>" + data[0] + "</td><td>" + data[1] + "</td></tr>";
            }
            newData = newData + "</table>";

            document.getElementById("alsoBoughtData").innerHTML = newData;
            $("#also_bought").modal()
        }
    </script>

    <Script>
        /**
         * Function that displays helpful reviews for the current product and connects to Sentiment analysis and fake review modules
         */
        function displayHelpfulReviews() {
            var newData = "<table class='table'><thead class='thead-dark'><tr><th>Reviewer:</th><th>Comments:</th><th>Helpfulness:</th><th>Overall Rating</th><th>Sentiment Score</th><th>Check fake review</th></tr>";

            for (var i = 0; i < arguments.length; i++) {
                var data = arguments[i].split("#");
                newData = newData + "<tr><td>" + data[0] + "</td><td>" + data[1] + "</td><td>" + ((data[3] / data[4]) * 100).toFixed(2) + "%</td><td>" + data[2] + "</td><td>" + data[5] + "</td><td><button class='btn btn-primary' onclick='checkFake(\"" + data[6] + "\",\"" + data[1] + "\",\"" + data[7] + "\",\"" + data[2] + "\")'>Check Fake review</button></td></tr>";
            }

            newData = newData + " </thead></table>";

            document.getElementById("helpfulReviewsData").innerHTML = newData;

            $("#helpfulReviews").modal();

        }
    </Script>

    <script>
        /**
         * Displays review ratings and reviews analysis results, generates graphs using google charts
         */
        function displayReviewAnalysis() {
            // setup for graph
            var graph_data = new google.visualization.DataTable();
            graph_data.addColumn('number', 'Overall Rating');
            graph_data.addColumn('number', 'Reviews');

            //setup for table
            var newData = "<table class='table'><thead class='thead-dark'><tr><th>Overall Rating</th><th>Reviews Count:</th></tr>";
            for (var i = 0; i < arguments.length; i++) {
                var data = arguments[i].split("#");
                newData = newData + "<tr><td>" + data[0] + "</td><td>" + data[1] + "</td></tr>";
                graph_data.addRow([Number(data[0]), Number(data[1])]);
            }

            newData = newData + " </thead></table>";

            // Chart options
            var options = {
                title: 'Overall Ratings vs number of reviews',
                'width': 500,
                'height': 700,
                hAxis: {
                    title: 'Overall Ratings',
                    format: 'Rating:'
                },
                vAxis: {
                    title: 'Number of Reviews'
                }
            };

            // Draw table
            document.getElementById("reviewAnalysisData").innerHTML = newData;

            // Add graph to an element based on table data.
            var chart = new google.visualization.ColumnChart(document.getElementById('reviewAnalysisDataGraph'));
            chart.draw(graph_data, options)

            $("#rating_analytics").modal();
        }
    </script>

    <script>
        /**
         * Displays popularity analysis data and graph generated using google charts
         */
        function displayPopularityAnalysis() {
            // setup for graph
            var graph_data = new google.visualization.DataTable();
            graph_data.addColumn('number', 'Review Year');
            graph_data.addColumn('number', 'Avg Rating');

            //setup for table
            var newData = "<table class='table'><thead class='thead-dark'><tr><th>Year</th><th>Average Rating</th><th>Number of Reviews</th></tr>";
            for (var i = 0; i < arguments.length; i++) {
                var data = arguments[i].split("#");


                newData = newData + "<tr><td>" + data[0] + "</td><td>" + data[1] + "</td>" + "<td>" + data[2] + "</td>" + "</tr>";
                graph_data.addRow([Number(data[0]), Number(data[1])]);
            }

            newData = newData + " </thead></table>";

            // Chart options
            var options = {
                title: 'Average rating over the years',
                'width': 500,
                'height': 700,
                hAxis: {
                    title: 'Year'
                },
                vAxis: {
                    title: 'Average Rating',
                    viewWindow: {
                        min: 1,
                        max: 5
                    },
                    ticks: [1, 2, 3, 4, 5, 6]
                }
            };

            // Draw table
            document.getElementById("popularityAnalysis").innerHTML = newData;

            // Add graph to an element based on table data.
            var chart = new google.visualization.LineChart(document.getElementById('popularityAnalysisChart'));
            chart.draw(graph_data, options)

            // Display the modal
            $("#popularAnalytics").modal();

        }

    </script>

    <script>
        /**
         * Checks if a given review is fake
         *
         * @param reviewId  reviewID
         * @param reviewText    the reviewText
         * @param reviewerID   reviewer Id
         * @param overall   Overall rating of the review text
         */
        function checkFake(reviewId, reviewText, reviewerID, overall) {

            var xhttp = new XMLHttpRequest();
            xhttp.onreadystatechange = function () {
                if (this.readyState == 4 && this.status == 200) {
                    alert(this.responseText);
                }
            };

            xhttp.open("GET", "checkFakeReview.jsp?asin=" + reviewId + "&reviewText=" + reviewText + "&reviewerID=" + reviewerID + "&overall=" + overall, true);
            xhttp.send();
        }
    </script>

</head>

<body>

<%
    // Establish connection
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    try {

        String connectionURL = "jdbc:mysql://amazon.c9yalx65oods.us-east-1.rds.amazonaws.com/AmazonReviews"; // AWS connection string
//        String connectionURL = "jdbc:mysql://sudhanshu1994-41072.portmap.io:41072/practice";  // Alternate connection string
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        connection = DriverManager.getConnection(connectionURL, "tempuser", "temp");
//        connection = DriverManager.getConnection(connectionURL, "remote", "remote");

    } catch (Exception ex) {
        out.println("Unable to connect to database" + ex);
    }

    String title = request.getParameter("title");
%>
<!-- -------------------------------------------------------------------------------------------------------------------------->
<br><br>


<!------------------------Section 2 : Displayed content on the page-------------------------------------------------------------- -->
<center>
    <div class="container">
        <h4>Welcome to Amazon Product Review Analyzer</h4>
        <!--Search Bar-->
        <form action="Review.jsp">
            <div class="input-group mb-3">
                <input type="text" name="title" class="form-control" placeholder="Product Name"
                       aria-label="Product Name/ID"
                       aria-describedby="Search">
                <div class="input-group-append">
                    <span class="input-group-text" id="Search"><button type="submit" name="submit"><span
                            class="oi oi-magnifying-glass"></span></button></span>
                </div>
            </div>
        </form>
        <!--Product details-->
        <table>


            <%
                if (title != null) {

                    try {
                        statement = connection.createStatement();
                        String sql = "select pdt.asin,pdt.title,pdt.categories,pdt.imurl,avg(overall) as average_overall_rating,avg(helpfulness_assign/helpfulness_max*100) as average_helpfulness from Product pdt,Reviews rw where pdt.asin=rw.asin and pdt.title like'" + title + "%' group by pdt.asin  ";
                        resultSet = statement.executeQuery(sql);
                        resultSet.next();
                        if (resultSet.getString("asin") == null) {
                            out.println("No data found from database");
                            System.out.println("No Data");
                        } else {
                            resultSet.previous();
                            while (resultSet.next()) {
                                String asin = resultSet.getString("asin");

            %>
            <tr>
                <td>
                    <img style="width:300px;height:300px" src="<%=resultSet.getString("imurl")%>"/></td>
                <td>
                        <span>
                            <b>Product name:</b> <%=resultSet.getString("title")%><br>
                            <b>Category:</b> <%=resultSet.getString("categories")%><br>
                            <span><b>Helpfulness:</b></span><span>
                                <div class="progress">


                                    <%
                                        String also_bought_sql = "select * from Product where asin in (select abt.also_bought from also_bought abt,Product pdt where abt.asin=pdt.asin and pdt.asin='" + asin + "')";
                                        Statement st_also_bought = connection.createStatement();
                                        ResultSet resultSet2 = st_also_bought.executeQuery(also_bought_sql);
                                        String also_bought = "";
                                        while (resultSet2.next()) {
                                            also_bought = also_bought + "'" + resultSet2.getString("title") + "#" + resultSet2.getString("categories") + "#" + resultSet2.getString("imurl") + "',";
                                        }
                                        also_bought = also_bought.replaceAll("(,)*$", "");
                                        String helpful_reviews = "";
                                        String helpful_reviews_sql = "select * from Reviews where asin='" + asin + "' and helpfulness_assign/helpfulness_max>0.6 order by (helpfulness_assign/helpfulness_max) desc limit 5";
                                        Statement helpful_st = connection.createStatement();
                                        ResultSet resultSet3 = helpful_st.executeQuery(helpful_reviews_sql);

                                        SentimentAnalysis sa = new SentimentAnalysis();
                                        while (resultSet3.next()) {
                                            String reviewText = resultSet3.getString("reviewText").replaceAll("[(),]", "").replaceAll("'", "\\\\'").replaceAll("\"\"", "");
                                            int sentiment = sa.getSentimentValue(reviewText);
                                            String reviewerID = resultSet3.getString("reviewerID");
                                            /*
                                             * "Very negative" = 0 "Negative" = 1 "Neutral" = 2 "Positive" = 3
                                             * "Very positive" = 4
                                             */
                                            String sentiment_result = "";
                                            switch (sentiment) {
                                                case 0:
                                                    sentiment_result = "Very negative (0)";
                                                    break;
                                                case 1:
                                                    sentiment_result = "Negative (1)";
                                                    break;
                                                case 2:
                                                    sentiment_result = "Neutral (2)";
                                                    break;
                                                case 3:
                                                    sentiment_result = "Positive (3)";
                                                    break;
                                                case 4:
                                                    sentiment_result = "Very Positive (4)";
                                                    break;
                                            }

                                            helpful_reviews = helpful_reviews + "'" + resultSet3.getString("reviewerName") + "#" + reviewText + "#" + resultSet3.getString("overall") + "#"
                                                    + resultSet3.getInt("helpfulness_assign") + "#" + resultSet3.getInt("helpfulness_max") + "#" + sentiment_result + "#" + asin + "#" + reviewerID + "',";

                                        }

                                        helpful_reviews = helpful_reviews.replaceAll("(,)*$", "");

                                        //--------------------------------------------------------------

                                        String rating_analysis_sql = "select overall,count(overall) as count from Reviews where asin ='" + asin + "' group by overall";
                                        DBUtilities dbu = new DBUtilities();
                                        ResultSet resultSetRatingAnalysis = dbu.selectQuery(connection, rating_analysis_sql);
                                        String ratingTextAnalysis = "";

                                        while (resultSetRatingAnalysis.next()) {
                                            String ratingText = "'" + resultSetRatingAnalysis.getString("overall") + "#" + resultSetRatingAnalysis.getString("count") + "',";
                                            ratingTextAnalysis += ratingText;
                                        }
                                        ratingTextAnalysis = ratingTextAnalysis.substring(0, ratingTextAnalysis.length() - 1);

                                        //--------------------------------------------------------------x
                                        String popularity_text_query = "select year(reviewTime) as review_year, avg(overall) as average, count(overall) as counts from Reviews where asin = '" + asin + "'" +
                                                "group by year(reviewTime)";
                                        ResultSet popularitySetAnalysis = dbu.selectQuery(connection, popularity_text_query);
                                        String popularityTextAnalysis = "";
                                        while (popularitySetAnalysis.next()) {
                                            String popularityText = "'" + popularitySetAnalysis.getString("review_year") + "#" + popularitySetAnalysis.getString("average") + "#" + popularitySetAnalysis.getString("counts") + "',";
                                            popularityTextAnalysis += popularityText;
                                        }
                                        popularityTextAnalysis = popularityTextAnalysis.substring(0, popularityTextAnalysis.length() - 1);


                                        //--------------------------------------------------------------
                                    %>
                                    <%
                                        DecimalFormat df = new DecimalFormat();
                                        df.setMaximumFractionDigits(2);
                                        String helpfulness = df.format(resultSet.getFloat("average_helpfulness"));
                                    %>
                                        <div class="progress-bar bg-danger" role="progressbar"
                                             style="width: <%=helpfulness%>%"
                                             aria-valuenow="100" aria-valuemin="0"
                                             aria-valuemax="100"><%=helpfulness%>%</div>
                                </div>
                            </span>


                            <span><b>Rating:</b></span><span>
                                <div class="progress">
                                    <%
                                        float overall_value = resultSet.getFloat("average_overall_rating");
                                        float overall_percent = (overall_value / 5) * 100;
                                        String overall_value_html = df.format(overall_value);
                                        String overall_value_percent_html = df.format(overall_percent);
                                    %>
                                    <div class="progress-bar bg-warning" role="progressbar"
                                         style="width: <%=overall_value_percent_html%>%"
                                         aria-valuenow="100" aria-valuemin="0"
                                         aria-valuemax="100"><%=overall_value_html%>/5</div>
                                </div>
                            </span>
                        </span>
                    <br>
                    <span>

                            <a href="#!" class="btn btn-primary" onclick="displayAlsoBought(<%=also_bought%>)">Also
                                Bought</a> &nbsp;&nbsp;
                            <a href="#!" class="btn btn-primary"
                               onclick="displayPopularityAnalysis(<%=popularityTextAnalysis%>)">
                                Popularity Analysis</a>&nbsp;
                            <a href="#!" class="btn btn-primary"
                               onclick="displayReviewAnalysis(<%=ratingTextAnalysis%>)">
                                Review rating analysis</a>&nbsp;
                            <a href="#!" class="btn btn-primary" onclick="displayHelpfulReviews(<%=helpful_reviews%>)">
                                Top helpful reviews</a>&nbsp;
                            <br>

                        </span>
                </td>


            </tr>
            <%

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        connection.close();
                    }
                }

            %>
        </table>
    </div>
</center>


<!-- ---------------------------------------------------------------------------------------------------------------------------->

<!-- Section 3:  Modals section  -->

<div class="modal fade" id="rating_analytics" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="width:100%;max-width: 100% !important;" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalTitle">Review Rating Analysis</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="container" id="reviewAnalysisData">

                </div>
                <div class="container" id="reviewAnalysisDataGraph">

                </div>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>

            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="also_bought" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="width:100%;max-width: 100% !important;" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="also_bought_title">Also bought with this item</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body" id="alsoBoughtData">

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>

            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="popularAnalytics" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="fakeReviewTitle">Popularity Analysis</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="container" id="popularityAnalysis">
                </div>
                <div class="container" id="popularityAnalysisChart">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>

            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="helpfulReviews" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="helpfulReviewsTitle">Top Helpful Reviews</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="container" id="helpfulReviewsData">

                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>

            </div>
        </div>
    </div>
</div>


</body>

</html>