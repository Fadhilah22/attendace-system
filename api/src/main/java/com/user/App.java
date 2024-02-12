package com.user;

// Http Server
import static spark.Spark.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cors.CorsConfig;
import com.database.DatabaseConnection;
import com.google.gson.Gson;

// util
import io.github.cdimascio.dotenv.Dotenv;

public class App {
    static Logger logger = LoggerFactory.getLogger(App.class);
    private static Gson gson = new Gson();

    public static void main(String[] args) {

        port(8080);

        BasicConfigurator.configure();

        // Serve static files from the specified directory
        staticFiles.externalLocation("../web/");

        // enable cors
        CorsConfig.enableCORS("*", "*", "*");

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

        // Create -> http POST
        post("/users", (request, response) -> {

            String json_user = request.body();
            UserStub userStub = gson.fromJson(json_user, UserStub.class);

            if(userStub == null){
                response.status(400);
                return "User Not Created";
            }

            // String id = UUID.randomUUID().toString();
            // userStub.setId(id);

            // find if id already exist
            PreparedStatement findQuery = con.prepareStatement(
                "SELECT userid FROM userstub WHERE userid LIKE ?;"
            );
            findQuery.setString(1, userStub.getId());
            ResultSet findResult = findQuery.executeQuery();
            if(findResult.next()){
                // means the query did resturn a value (supposed to be the value of similiar id)
                response.status(409);
                return "data exists";
            }

            // insert data into server
            PreparedStatement statement = con.prepareStatement(
              "INSERT INTO userstub (userid, username, useremail, datein) " +
              "VALUES (?, ?, ?, NOW())");
            statement.setString(1, userStub.getId());
            statement.setString(2, userStub.getName());
            statement.setString(3, userStub.getEmail());

            int rowsAffected = statement.executeUpdate();

            response.status(200);
            return "User created with ID" + userStub.getId() + ", rows affected " + rowsAffected;
        });

        // Read -> http GET
        get("/users", (request, response) -> {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM userstub");
            ResultSet result = statement.executeQuery();

            StringBuilder res = new StringBuilder();
            while(result.next()) {
                res.append(result.getString("userid"))
                    .append(" ")
                    .append(result.getString("username"))
                    .append(" ")
                    .append(result.getString("datein"))
                    .append("\n");
            }
            return res.toString();
        });

        // Update -> http PUT

        // Delete -> http DELETE
        delete("/users", (request, response) -> {
            try {
            // mapping recieved data into userstub class
            String json_user = request.body();
            UserStub userStub = gson.fromJson(json_user, UserStub.class);

            if(userStub == null){
                response.status(400);
                return "Failed to recieve user data";
            }

            String id = userStub.getId();

            // get datein from database given recieved id
            PreparedStatement statement = con.prepareStatement("SELECT datein FROM userstub WHERE userid LIKE ?");
            statement.setString(1, id);
            ResultSet result = statement.executeQuery();

            Timestamp datein;
            if (result.next()) {
                datein = result.getTimestamp("datein");

                System.out.println("ID : " + id);
                System.out.println("Datein: " + datein);

                statement.close();
                result.close();

                // add to userlog
                statement = con.prepareStatement("INSERT INTO userlog (userid, datein, dateout) " +
                                                 "VALUES (?, ?, NOW())");
                statement.setString(1, id);
                statement.setTimestamp(2, datein);

                int rowsAffectedUpdate1 = statement.executeUpdate();
                System.out.println("" + rowsAffectedUpdate1);
                statement.close();

                statement = con.prepareStatement("SELECT dateout FROM userlog WHERE userid LIKE ?");
                statement.setString(1, id);

                ResultSet resultDateout = statement.executeQuery();
                resultDateout.next();
                Timestamp dateout = resultDateout.getTimestamp("dateout");
                System.out.println("Dateout: " + dateout);

            } else {
                // Handle the case where no rows are returned for the given id
                response.status(404);
                return "No data found for user with id: " + id;
            }

            // delete user from userstub
            statement = con.prepareStatement("DELETE FROM userstub WHERE userid LIKE ?");
            statement.setString(1, id);


            int rowsAffectedUpdate2 = statement.executeUpdate();
            System.out.println("" + rowsAffectedUpdate2);
            statement.close();

            response.status(200);

            return "User with id " + id + " deleted";

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return "0";
        });

    }
}
