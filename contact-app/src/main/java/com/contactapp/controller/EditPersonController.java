package com.contactapp.controller;

import java.io.File;

import com.contactapp.dao.PersonDAO;
import com.contactapp.model.Person;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controller for edit_person.fxml.
 * Handles pre-filling the form with existing data and saving updates.
 * Task 4 — updated with Categories, Favourites and Photo features.
 */
public class EditPersonController {

    // ── DAO ───────────────────────────────────────────────────────────────────

    private final PersonDAO personDAO = new PersonDAO();

    // ── State ─────────────────────────────────────────────────────────────────

    private Person currentPerson;
    private Runnable onSavedCallback;
    private String selectedPhotoPath = null; // stores chosen photo path

    // ── FXML Fields ───────────────────────────────────────────────────────────

    @FXML private TextField        lastnameField;
    @FXML private TextField        firstnameField;
    @FXML private TextField        nicknameField;
    @FXML private TextField        phoneField;
    @FXML private TextField        addressField;
    @FXML private TextField        emailField;
    @FXML private DatePicker       birthDatePicker;
    @FXML private ComboBox<String> categoryComboBox;  // category dropdown
    @FXML private CheckBox         favoriteCheckBox;  // favourite flag
    @FXML private ImageView        photoPreview;      // photo thumbnail
      // shows file name
    @FXML private Label            errorLabel;

    // ── Initialisation ────────────────────────────────────────────────────────

    /**
     * Called automatically by JavaFX after FXML loads.
     * Populates the category dropdown.
     */
    @FXML
   private void initialize() {
    categoryComboBox.getItems().addAll(
            Person.CATEGORY_FRIEND,
            Person.CATEGORY_FAMILY,
            Person.CATEGORY_COLLEAGUE,
            Person.CATEGORY_OTHER
    );

    // Make photo preview circular
    javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(40, 40, 40);
    photoPreview.setClip(clip);
}

    // ── Public API 

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
        categoryComboBox.setValue(
                person.getCategory() != null ? person.getCategory() : Person.CATEGORY_OTHER);
        favoriteCheckBox.setSelected(person.isFavorite());

        // Load existing photo if available
        if (person.getPhotoPath() != null && !person.getPhotoPath().isEmpty()) {
            selectedPhotoPath = person.getPhotoPath();
            File photoFile = new File(selectedPhotoPath);
            if (photoFile.exists()) {
                photoPreview.setImage(new Image(photoFile.toURI().toString()));
                photoPreview.setVisible(true);
                
            }
        }
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

    // ── Button handlers ───────────────────────────────────────────────────────

    /**
     * Opens a file chooser so the user can pick a new profile photo.
     */
    @FXML
    private void onChoosePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Photo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(lastnameField.getScene().getWindow());
        if (file != null) {
            selectedPhotoPath = file.getAbsolutePath();
            photoPreview.setImage(new Image(file.toURI().toString()));
            photoPreview.setVisible(true);
        
        }
    }

    /**
     * Validates the form, updates the person in the DB and closes the dialog.
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
        currentPerson.setCategory(categoryComboBox.getValue());
        currentPerson.setFavorite(favoriteCheckBox.isSelected());
        currentPerson.setPhotoPath(selectedPhotoPath); // save photo path

        personDAO.update(currentPerson);

        if (onSavedCallback != null) onSavedCallback.run();
        closeDialog();
    }

    /**
     * Closes the dialog without saving.
     */
    @FXML
    private void handleCancel() {
        closeDialog();
    }

    // ── Validation ────────────────────────────────────────────────────────────

    /**
     * Validates that all required fields are filled in.
     *
     * @return true if valid, false otherwise
     */
    private boolean isValid() {
        if (lastnameField.getText().trim().isEmpty()
                || firstnameField.getText().trim().isEmpty()
                || nicknameField.getText().trim().isEmpty()) {
            errorLabel.setText("Last name, first name and nickname are required.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            return false;
        }
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        return true;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Closes the dialog window.
     */
    private void closeDialog() {
        Stage stage = (Stage) lastnameField.getScene().getWindow();
        stage.close();
    }
}