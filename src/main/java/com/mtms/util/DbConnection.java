package com.mtms.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbConnection {
    public static Connection getConnection() {
        try{
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/MTMS?currentSchema=public",
                    "postgres",
                    "admin");
        }catch(Exception ex){
            System.out.println("Exception occured while establishing DB Connection");
            ex.printStackTrace();
            return null;
        }
    }
}