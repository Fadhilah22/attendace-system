package com.fadlan;

// Http Server
import static spark.Spark.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// db
import com.fadlan.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

// util
import java.util.UUID;
import io.github.cdimascio.dotenv.Dotenv;

public class App {
    static Logger logger = LoggerFactory.getLogger(App.class);
    private static Gson gson = new Gson();

    public static void main(String[] args) {
        port(8080);
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
                return "UserNotCreated";
            }
            String id = UUID.randomUUID().toString();
            userStub.setId(id);

            PreparedStatement statement = con.prepareStatement(
              "INSERT INTO userstub (userid, username, useremail, datein) " +
              "VALUES (?, ?, ?, NOW())");
            statement.setString(1, userStub.getId());
            statement.setString(2, userStub.getName());
            statement.setString(3, userStub.getEmail());

            int rowsAffected = statement.executeUpdate();

            response.status(200);
            return "User created with ID" + id + ", rows affected " + rowsAffected;
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
                statement.close();

                statement = con.prepareStatement("SELECT dateout FROM userlog WHERE userid LIKE ?");
                statement.setString(1, id);

                ResultSet resultDateout = statement.executeQuery();
                resultDateout.next();
                Timestamp dateout = resultDateout.getTimestamp("dateout");
                System.out.println("Dateout: " + dateout);

            } else {
                // Handle the case where no rows are returned for the given id
                return "No data found for user with id: " + id;
            }

            // delete user from userstub
            statement = con.prepareStatement("DELETE FROM userstub WHERE userid LIKE ?");
            statement.setString(1, id);


            int rowsAffectedUpdate2 = statement.executeUpdate();
            statement.close();

            return "User with id " + id + " deleted";

          } catch (SQLException e) {
              e.printStackTrace();
          }

          return "0";
        });
    }
}
