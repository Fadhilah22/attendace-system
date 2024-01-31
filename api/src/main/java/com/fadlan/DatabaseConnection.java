package com.fadlan;

// java sql
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

// JDBC
import org.postgresql.util.*;

public class DatabaseConnection {
    private String URL;
    private String USER;
    private String PASSWORD;

    public DatabaseConnection(String URL, String USER, String PASSWORD){
        this.URL = URL;
        this.USER = USER;
        this.PASSWORD = PASSWORD;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(this.URL, this.USER, this.PASSWORD);
    }

    public static void main(String[] args) {

    }
}
