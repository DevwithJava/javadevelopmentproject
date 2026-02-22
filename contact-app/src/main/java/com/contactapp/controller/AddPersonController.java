package com.contactapp.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.contactapp.dao.PersonDAO;
import com.contactapp.model.Person;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for add_person.fxml — the Add Person dialog.
 * TASK 3
 */
public class AddPersonController implements Initializable {

    //  Form fields

    @FXML private TextField  firstnameField;
    @FXML private TextField  lastnameField;
    @FXML private TextField  nicknameField;
    @FXML private TextField  phoneField;
    @FXML private TextField  emailField;
    @FXML private TextField  addressField;
    @FXML private DatePicker birthDatePicker;
    @FXML private Label      errorLabel;

    //  State 

    private final PersonDAO personDAO = new PersonDAO();

    /**
     * Set to true when the user saves successfully.
     * MainController checks this to decide whether to refresh the table.
     */
    private boolean saved = false;

    //  Initialisation 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errorLabel.setVisible(false);
    }

    //  Public API 

    /**
     * Returns true if the user saved a new contact successfully.
     */
    public boolean isSaved() {
        return saved;
    }

    //  Button handlers 

    @FXML
    private void onSave() {
        if (!validateInputs()) {
            return;
        }

        Person person = buildPersonFromForm();
        int generatedId = personDAO.add(person);   // uses Task 1's add() method

        if (generatedId > 0) {
            saved = true;
            closeDialog();
        } else {
            showError("Failed to save contact. Please try again.");
        }
    }

    @FXML
    private void onCancel() {
        closeDialog();
    }

    //  Validation 

    /**
     * Validates all required fields and basic format rules.
     * Returns true only if everything is valid.
     */
    private boolean validateInputs() {

        // Required: firstname
        if (firstnameField.getText().isBlank()) {
            showError("First name is required.");
            firstnameField.requestFocus();
            return false;
        }

        // Required: lastname
        if (lastnameField.getText().isBlank()) {
            showError("Last name is required.");
            lastnameField.requestFocus();
            return false;
        }

        // Required: nickname
        if (nicknameField.getText().isBlank()) {
            showError("Nickname is required.");
            nicknameField.requestFocus();
            return false;
        }

        // Optional: email format check
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            showError("Please enter a valid email address.");
            emailField.requestFocus();
            return false;
        }

        // Optional: phone length check (max 15 chars, digits/+/-)
        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && !phone.matches("^[+\\d\\s\\-]{1,15}$")) {
            showError("Phone must be max 15 characters (digits, +, -).");
            phoneField.requestFocus();
            return false;
        }

        // Optional: birth date must not be in the future
        LocalDate birthDate = birthDatePicker.getValue();
        if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
            showError("Birth date cannot be in the future.");
            return false;
        }

        errorLabel.setVisible(false);
        return true;
    }

    
    private Person buildPersonFromForm() {
        return new Person(
                lastnameField.getText().trim(),
                firstnameField.getText().trim(),
                nicknameField.getText().trim(),
                phoneField.getText().trim().isEmpty()   ? null : phoneField.getText().trim(),
                addressField.getText().trim().isEmpty() ? null : addressField.getText().trim(),
                emailField.getText().trim().isEmpty()   ? null : emailField.getText().trim(),
                birthDatePicker.getValue()
        );
    }

    private void showError(String message) {
        errorLabel.setText("⚠  " + message);
        errorLabel.setVisible(true);
    }

    private void closeDialog() {
        Stage stage = (Stage) firstnameField.getScene().getWindow();
        stage.close();
    }
}