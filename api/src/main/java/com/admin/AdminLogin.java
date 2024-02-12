package com.admin;

import static spark.Spark.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cors.CorsConfig;
import com.database.DatabaseConnection;
import com.encryption.PasswordEncrypt;
import com.google.gson.Gson;

// util
import io.github.cdimascio.dotenv.Dotenv;

/**
 * AdminApp
 * to handle admin login requests
 * requests that will be requested in this web is:
 * - GET REQUEST -> sign in with existing admin account credentials.
 * - POST REQUEST -> sign up a new admin account credential.
 */

public class AdminLogin {
    static Logger logger = LoggerFactory.getLogger(AdminLogin.class);
    private static Gson gson = new Gson();
    private static PasswordEncrypt passwordEncrypt = new PasswordEncrypt();

    public static void main(String[] args) {

        port(8081);

        // BasicConfigurator.configure();

        CorsConfig.enableCORS("*", "*", "*");

        Dotenv db_data = Dotenv.configure()
                                .directory("assets")
                                .filename(".env")
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

        get("/users", (request, response) -> {
            StringBuilder finid = new StringBuilder();
            try {
                // mapping recieved data into adminstub class
                String json_admin = request.body();
                AdminStub adminStub = gson.fromJson(json_admin, AdminStub.class);

                if(adminStub == null){
                    response.status(400);
                    return "Failed to recieve admin data";
                }

                // LOGGING
                System.out.println(adminStub.getName());
                System.out.println(adminStub.getPassword());

                String name = adminStub.getName();
                String password = passwordEncrypt.encrypt(adminStub.getPassword());

                System.out.println("name : " + name);
                System.out.println("password : " + password);

                // finds admin name
                PreparedStatement adminQuery = con.prepareStatement("SELECT adminid, adminpassword FROM adminstub " +
                "WHERE adminname LIKE ?");
                adminQuery.setString(1, name);
                ResultSet nameQueryResult = adminQuery.executeQuery();
                // if query does returns a row
                if(nameQueryResult.next()){
                    // validate password refered by the adminid
                    if(password.equals(nameQueryResult.getString("adminpassword"))){
                        // if password is right
                        response.status(200);
                        return "log in";
                    } else {
                        // if password is wrong
                        response.status(401);
                        return "invalid credential";
                    }
                } else {
                    // if name does not exist in server
                    response.status(400);
                    return "user not found";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "This >> " + finid.toString();
        });
    }
}