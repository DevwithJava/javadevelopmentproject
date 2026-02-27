package com.contactapp.controller;

import com.contactapp.dao.PersonDAO;
import com.contactapp.model.Person;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for edit_person.fxml
 * Handles pre-filling the form with an existing person's data and saving updates.
 *
 * Task 4 
 */
public class EditPersonController {

    // ── DAO ───────────────────────────────────────────────────────────────────
    private final PersonDAO personDAO = new PersonDAO();

    // ── State ─────────────────────────────────────────────────────────────────
    private Person currentPerson;
    private Runnable onSavedCallback;

    // ── FXML Fields ───────────────────────────────────────────────────────────
    @FXML private TextField lastnameField;
    @FXML private TextField firstnameField;
    @FXML private TextField nicknameField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private TextField emailField;
    @FXML private DatePicker birthDatePicker;
    @FXML private Label errorLabel;

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Pre-fills the form with the selected person's current data.
     * Must be called by MainController before showing the dialog.
     *
     * @param person the person to edit
     */
    public void setPerson(Person person) {
        this.currentPerson = person;
        lastnameField.setText(person.getLastname());
        firstnameField.setText(person.getFirstname());
        nicknameField.setText(person.getNickname());
        phoneField.setText(person.getPhoneNumber() != null ? person.getPhoneNumber() : "");
        addressField.setText(person.getAddress() != null ? person.getAddress() : "");
        emailField.setText(person.getEmailAddress() != null ? person.getEmailAddress() : "");
        birthDatePicker.setValue(person.getBirthDate());
    }

    /**
     * Registers a callback that fires after a successful save.
     * Used by MainController to refresh the table.
     *
     * @param callback a Runnable that refreshes the main TableView
     */
    public void setOnSavedCallback(Runnable callback) {
        this.onSavedCallback = callback;
    }

    // ── Handlers ──────────────────────────────────────────────────────────────

    /**
     * Validates the form, updates the person in the DB, and closes the dialog.
     */
    @FXML
    private void handleSave() {
        if (!isValid()) return;

        currentPerson.setLastname(lastnameField.getText().trim());
        currentPerson.setFirstname(firstnameField.getText().trim());
        currentPerson.setNickname(nicknameField.getText().trim());
        currentPerson.setPhoneNumber(phoneField.getText().trim());
        currentPerson.setAddress(addressField.getText().trim());
        currentPerson.setEmailAddress(emailField.getText().trim());
        currentPerson.setBirthDate(birthDatePicker.getValue());

        personDAO.update(currentPerson);

        if (onSavedCallback != null) {
            onSavedCallback.run();
        }

        closeDialog();
    }

    /**
     * Closes the dialog without saving any changes.
     */
    @FXML
    private void handleCancel() {
        closeDialog();
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    /**
     * Validates that all required fields are filled in.
     * Shows an error message if validation fails.
     *
     * @return true if the form is valid, false otherwise
     */
    private boolean isValid() {
        if (lastnameField.getText().trim().isEmpty()
                || firstnameField.getText().trim().isEmpty()
                || nicknameField.getText().trim().isEmpty()) {
            errorLabel.setText("Last name, first name and nickname are required.");
            errorLabel.setVisible(true);
            return false;
        }
        errorLabel.setVisible(false);
        return true;
    }

    /**
     * Closes the dialog window.
     */
    private void closeDialog() {
        Stage stage = (Stage) lastnameField.getScene().getWindow();
        stage.close();
    }

}