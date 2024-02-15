package com.admin;


// Http Server
import static spark.Spark.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

// import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Database connection
import com.database.DatabaseConnection;

// // Browser
// import com.cors.CorsConfig;

// util
import com.google.gson.Gson;

import io.github.cdimascio.dotenv.Dotenv;

public class App {
    static Logger logger = LoggerFactory.getLogger(App.class);
    private static Gson gson = new Gson();

    private static double countTimeElapsed(Timestamp time1,  Timestamp time2){
        // coverts time1 into double
        double dtime1 = (double) time1.getHours() + ((double)time1.getMinutes() / 100);

        // Extract hour and minutes from time2
        double dtime2 = (double)time2.getHours() + ((double)time2.getMinutes() / 100);
        
        System.out.println("time 1 >> " + dtime1);
        System.out.println("time 2 >> " + dtime2);
        
        return dtime2 - dtime1;
    }

    public static void main(String[] args) {
        port(8081);

        // BasicConfigurator.configure();

        Dotenv db_data =  Dotenv.configure()
                                .directory("assets")
                                .filename(".env") // instead of '.env', use 'env'
                                .load();

        DatabaseConnection db = new DatabaseConnection(db_data.get("DB_URL"),
                                                        db_data.get("DB_USER"),
                                                        db_data.get("DB_PASSWORD"));

        Connection con;
        try {
            con = db.connect();
            System.out.println("Connected");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // get lists of all attendee currently clocking in
        get("users/clock-in", (request, response) -> {
            StringBuilder userListsFin = new StringBuilder();
            try {
                PreparedStatement userQuery = con.prepareStatement(
                    "SELECT userid, username, datein, NOW() AS datecurrent FROM userstub"
                );
                ResultSet userLists = userQuery.executeQuery();

                while(userLists.next()) {
                    userListsFin.append(userLists.getString("userid"));
                    userListsFin.append(" ");
                    userListsFin.append(userLists.getString("username"));
                    userListsFin.append(" ");
                    userListsFin.append(userLists.getString("datein"));
                    userListsFin.append(" ");
                    userListsFin.append(userLists.getString("datecurrent"));
                    userListsFin.append("\n");


                    Timestamp datein = userLists.getTimestamp("datein");

                    Timestamp datecurrent = userLists.getTimestamp
                    ("datecurrent");

                    System.out.println("time elapsed >> " + countTimeElapsed(datein, datecurrent));
                }

                userQuery.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return userListsFin;
        });
    }
}
