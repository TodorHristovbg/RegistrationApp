package com.todor.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public  final class DataBase{

    //private  static  final String DB_URL = requireEnv("DB_URL");
    //private  static  final String DB_USER = requireEnv("DB_USER");
    //private  static  final String DB_PASSWORD = requireEnv("DB_PASSWORD");

    private  static  final String DB_URL = "jdbc:postgresql://localhost:5432/registrationapp_db";
    private  static  final String DB_USER = "registrationappadmin";
    private  static  final String DB_PASSWORD = "registrationapp";

    private DataBase(){

    }
    private static String requireEnv(String name){

        String value = System.getenv(name);
        if(value==null || value.isBlank()){
            throw new IllegalStateException("Required environment value is missing: "+name);
        }

        return  value;
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
    }
}