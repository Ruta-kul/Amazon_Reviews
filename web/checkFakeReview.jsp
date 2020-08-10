
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="FakeText.FakeTextFeatures" %>
<%@ page import="java.sql.*"%>

<%
    Connection conn=null;
   try{
       String asin=request.getParameter("asin");
       String reviewText=request.getParameter("reviewText").replaceAll("[(),]", "").replaceAll("'","\\\\'").replaceAll("\"\"","");
       String reviewerID=request.getParameter("reviewerID");
       Double overall=Double.parseDouble(request.getParameter("overall"));
       if(asin=="" || asin==null)
       {
           out.print("Incorrect input");
       }
       String connectionURL = "jdbc:mysql://amazon.c9yalx65oods.us-east-1.rds.amazonaws.com/AmazonReviews";
//        String connectionURL = "jdbc:mysql://sudhanshu1994-41072.portmap.io:41072/practice";
       Class.forName("com.mysql.jdbc.Driver").newInstance();
       conn = DriverManager.getConnection(connectionURL, "tempuser", "temp");
       FakeTextFeatures ft=new FakeTextFeatures();

       double featureSimilar=ft.Feature7_8_similar_reviews(reviewText,reviewerID,asin,conn);
       out.print("Similiar = "+featureSimilar);
       int amazonName=ft.Feature3_seller_website(reviewText);
       out.print("Contains amazon name = "+amazonName);
       int outlier=ft.Feature6_outlier_review(overall,reviewText,conn);
       out.print("Outlier "+outlier);
   }
   catch (Exception e)
   {
       e.printStackTrace();
   }
   finally {

   }


%>
