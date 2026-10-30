package com.web.sevlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.util.DbConnection;

public class CreateTrackServlet extends HttpServlet {
    private static Connection connection = DbConnection.getConnection();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
        Product product = new Product(
                Integer.parseInt(req.getParameter("productId")),
                req.getParameter("productName"),
                Double.parseDouble(req.getParameter("productPrice")));
        String insertQuery = "INSERT INTO PRODUCTS VALUES (?, ?, ?);";

        try {
            Connection connection = getConnection();
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

            insertStatement.setInt(1, product.getProductId());
            insertStatement.setString(2, product.getProductName());
            insertStatement.setDouble(3, product.getProductPrice());

            insertStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.sendRedirect(req.getContextPath() + "/getProductList?purpose=getProductList");*/
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        ArrayList<Product> listOfProducts = new ArrayList<Product>();
//        String selectQuery = "SELECT * FROM PRODUCTS;";
//
//        try {
//            Connection connection = getConnection();
//            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
//            ResultSet resultSet = selectStatement.executeQuery();
//            while(resultSet.next()) {
//                listOfProducts.add(new Product(
//                        resultSet.getInt(1),
//                        resultSet.getString(2),
//                        resultSet.getDouble(3)));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        PrintWriter writer = resp.getWriter();
//        writer.print(listOfProducts.toString());
    }
}
