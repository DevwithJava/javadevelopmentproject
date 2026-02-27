package com.contactapp;

import com.contactapp.util.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Entry point of the Contact App JavaFX application.
 */
public class MainApp extends Application {

    @Override
    public void init() throws Exception {
        String createTable = "CREATE TABLE IF NOT EXISTS person (" +
                "idperson INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "lastname VARCHAR(45) NOT NULL, " +
                "firstname VARCHAR(45) NOT NULL, " +
                "nickname VARCHAR(45) NOT NULL, " +
                "phone_number VARCHAR(15) NULL, " +
                "address VARCHAR(200) NULL, " +
                "email_address VARCHAR(150) NULL, " +
                "birth_date DATE NULL)";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTable);
            System.out.println("[MainApp] Database initialised successfully.");
        } catch (SQLException e) {
            System.err.println("[MainApp] Failed to initialise database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/contactapp/view/main.fxml")
        );
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(
                getClass().getResource("/com/contactapp/css/style.css").toExternalForm()
        );
        primaryStage.setTitle("Contact App");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        Connection conn = DBConnection.getConnection();
        if (conn != null && !conn.isClosed()) {
            conn.close();
            System.out.println("[MainApp] Database connection closed.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}