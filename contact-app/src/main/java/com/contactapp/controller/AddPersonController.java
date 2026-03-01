package com.contactapp.controller;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.contactapp.dao.PersonDAO;
import com.contactapp.model.Person;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controller for add_person.fxml — the Add Person dialog.
 * TASK 3 — updated with Categories, Favourites and Photo features.
 */
public class AddPersonController implements Initializable {

    // ── Form fields ───────────────────────────────────────────────────────────

    @FXML private TextField        firstnameField;
    @FXML private TextField        lastnameField;
    @FXML private TextField        nicknameField;
    @FXML private TextField        phoneField;
    @FXML private TextField        emailField;
    @FXML private TextField        addressField;
    @FXML private DatePicker       birthDatePicker;
    @FXML private ComboBox<String> categoryComboBox;  // dropdown: Friend/Family/Colleague/Other
    @FXML private CheckBox         favoriteCheckBox;  // marks contact as favourite
    @FXML private ImageView        photoPreview;      // shows selected photo preview
          // shows selected file name
    @FXML private Label            errorLabel;        // shows validation errors

    // ── State ─────────────────────────────────────────────────────────────────

    private final PersonDAO personDAO = new PersonDAO();
    private boolean saved = false;
    private String selectedPhotoPath = null;  // stores the path of the chosen photo

    // ── Initialisation ────────────────────────────────────────────────────────

    /**
     * Called automatically by JavaFX after the FXML is loaded.
     * Sets up the category dropdown and makes the photo preview circular.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errorLabel.setVisible(false);

        // Populate category dropdown
        categoryComboBox.getItems().addAll(
                Person.CATEGORY_FRIEND,
                Person.CATEGORY_FAMILY,
                Person.CATEGORY_COLLEAGUE,
                Person.CATEGORY_OTHER
        );
        categoryComboBox.setValue(Person.CATEGORY_OTHER);

// Make photo preview circular
Circle clip = new Circle(40);
photoPreview.setClip(clip);
photoPreview.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);");
    }

    // ── Public API 

    /**
     * Returns true if the user successfully saved a new contact.
     */
    public boolean isSaved() {
        return saved;
    }

    // ── Button handlers 

    /**
     * Opens a file chooser so the user can pick a profile photo.
     */
    @FXML
    private void onChoosePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Photo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(firstnameField.getScene().getWindow());
        if (file != null) {
            selectedPhotoPath = file.getAbsolutePath();
            photoPreview.setImage(new Image(file.toURI().toString()));
            photoPreview.setVisible(true);
        
        }
    }

    /**
     * Validates the form and saves the new contact to the database.
     */
    @FXML
    private void onSave() {
        if (!validateInputs()) return;
        Person person = buildPersonFromForm();
        int generatedId = personDAO.add(person);
        if (generatedId > 0) {
            saved = true;
            closeDialog();
        } else {
            showError("Failed to save contact. Please try again.");
        }
    }

    /**
     * Closes the dialog without saving.
     */
    @FXML
    private void onCancel() {
        closeDialog();
    }

    // ── Validation

    /**
     * Validates all required fields and format rules.
     */
    private boolean validateInputs() {
        if (firstnameField.getText().isBlank()) {
            showError("First name is required.");
            firstnameField.requestFocus();
            return false;
        }
        if (lastnameField.getText().isBlank()) {
            showError("Last name is required.");
            lastnameField.requestFocus();
            return false;
        }
        if (nicknameField.getText().isBlank()) {
            showError("Nickname is required.");
            nicknameField.requestFocus();
            return false;
        }
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            showError("Please enter a valid email address.");
            emailField.requestFocus();
            return false;
        }
        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && !phone.matches("^[+\\d\\s\\-]{1,15}$")) {
            showError("Phone must be max 15 characters (digits, +, -).");
            phoneField.requestFocus();
            return false;
        }
        LocalDate birthDate = birthDatePicker.getValue();
        if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
            showError("Birth date cannot be in the future.");
            return false;
        }
        errorLabel.setVisible(false);
        return true;
    }

    // ── Helpers 

    /**
     * Builds a Person object from the current form values.
     */
    private Person buildPersonFromForm() {
        return new Person(
                lastnameField.getText().trim(),
                firstnameField.getText().trim(),
                nicknameField.getText().trim(),
                phoneField.getText().trim().isEmpty()   ? null : phoneField.getText().trim(),
                addressField.getText().trim().isEmpty() ? null : addressField.getText().trim(),
                emailField.getText().trim().isEmpty()   ? null : emailField.getText().trim(),
                birthDatePicker.getValue(),
                categoryComboBox.getValue(),
                favoriteCheckBox.isSelected(),
                selectedPhotoPath
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