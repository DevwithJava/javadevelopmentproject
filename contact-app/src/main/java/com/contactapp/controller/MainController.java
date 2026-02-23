package com.contactapp.controller;

import com.contactapp.dao.PersonDAO;
import com.contactapp.model.Person;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller for main.fxml - the main window of the application.
 * TASK 2
 */
public class MainController {

    @FXML
    private TableView<Person> personTable;
    @FXML
    private TableColumn<Person, Number> idColumn;
    @FXML
    private TableColumn<Person, String> lastnameColumn;
    @FXML
    private TableColumn<Person, String> firstnameColumn;
    @FXML
    private TableColumn<Person, String> nicknameColumn;
    @FXML
    private TableColumn<Person, String> phoneColumn;
    @FXML
    private TableColumn<Person, String> addressColumn;
    @FXML
    private TableColumn<Person, String> emailColumn;
    @FXML
    private TableColumn<Person, String> birthDateColumn;

    private final PersonDAO personDAO = new PersonDAO();
    private final ObservableList<Person> persons = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        configureColumns();
        personTable.setItems(persons);
    }

    public void setInitialPersons(List<Person> initialPersons) {
        if (initialPersons == null) {
            loadPersons();
            return;
        }
        persons.setAll(initialPersons);
    }

    @FXML
    private void onRefresh() {
        loadPersons();
    }

    @FXML
    private void onDeleteContact() {
        Person selected = personTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No Selection", "Please select a contact to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete selected contact?");
        confirm.setContentText(selected.getFirstname() + " " + selected.getLastname());

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        boolean deleted = personDAO.delete(selected.getIdperson());
        if (deleted) {
            loadPersons();
        } else {
            showInfo("Delete Failed", "Contact could not be deleted.");
        }
    }

    @FXML
    private void onAddContact() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/contactapp/view/add_person.fxml"));

            Stage dialog = new Stage();
            dialog.setTitle("Add New Contact");
            dialog.setScene(new Scene(loader.load()));
            dialog.initModality(Modality.APPLICATION_MODAL);
            if (personTable.getScene() != null && personTable.getScene().getWindow() != null) {
                dialog.initOwner(personTable.getScene().getWindow());
            }
            dialog.setResizable(false);

            AddPersonController controller = loader.getController();
            dialog.showAndWait();

            if (controller.isSaved()) {
                loadPersons();
            }
        } catch (IOException e) {
            showError("Unable to open Add Contact dialog.", e);
        }
    }

    private void loadPersons() {
        persons.setAll(personDAO.getAll());
    }

    private void configureColumns() {
        idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdperson()));
        lastnameColumn.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getLastname())));
        firstnameColumn.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getFirstname())));
        nicknameColumn.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getNickname())));
        phoneColumn.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getPhoneNumber())));
        addressColumn.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getAddress())));
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getEmailAddress())));
        birthDateColumn.setCellValueFactory(data -> new SimpleStringProperty(formatDate(data.getValue().getBirthDate())));
    }

    private String formatDate(LocalDate date) {
        return date == null ? "" : date.toString();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}
