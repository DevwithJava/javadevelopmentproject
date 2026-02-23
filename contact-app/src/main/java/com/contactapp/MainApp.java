package com.contactapp;

import com.contactapp.controller.MainController;
import com.contactapp.dao.PersonDAO;
import com.contactapp.model.Person;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry point of the Contact App JavaFX application.
 *
 * TASK 2
 */
public class MainApp extends Application {

    private final PersonDAO personDAO = new PersonDAO();
    private List<Person> initialPersons = new ArrayList<>();

    @Override
    public void init() {
        // Hint requirement: preload table data during application initialization.
        initialPersons = personDAO.getAll();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/contactapp/view/main.fxml"));
            Scene scene = new Scene(loader.load());
            MainController mainController = loader.getController();
            mainController.setInitialPersons(initialPersons);

            primaryStage.setTitle("Contact App");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load main.fxml.", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
