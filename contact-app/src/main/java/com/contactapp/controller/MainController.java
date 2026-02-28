package com.contactapp.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.contactapp.dao.PersonDAO;
import com.contactapp.model.Person;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for main.fxml - the main window of the application.
 * Updated with Categories filter, Favourites toggle and CSV Export.
 */
public class MainController {
    
    @FXML private TableView<Person>           personTable;
    @FXML private TableColumn<Person, Number> idColumn;
    @FXML private TableColumn<Person, String> lastnameColumn;
    @FXML private TableColumn<Person, String> firstnameColumn;
    @FXML private TableColumn<Person, String> nicknameColumn;
    @FXML private TableColumn<Person, String> phoneColumn;
    @FXML private TableColumn<Person, String> addressColumn;
    @FXML private TableColumn<Person, String> emailColumn;
    @FXML private TableColumn<Person, String> birthDateColumn;
    @FXML private TableColumn<Person, String> categoryColumn;
    @FXML private TableColumn<Person, String> favoriteColumn;

    @FXML private TextField        searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private Button darkModeButton;
    @FXML private TableColumn<Person, String> photoColumn;

    private boolean darkMode = false;
    private final PersonDAO personDAO = new PersonDAO();
    private final ObservableList<Person> persons = FXCollections.observableArrayList();
    private FilteredList<Person> filteredPersons;
    private boolean showingFavouritesOnly = false;

    @FXML
    private void initialize() {
        configureColumns();
        filteredPersons = new FilteredList<>(persons, p -> true);
        personTable.setItems(filteredPersons);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        categoryFilter.getItems().add("All");
        categoryFilter.getItems().addAll(
                Person.CATEGORY_FRIEND,
                Person.CATEGORY_FAMILY,
                Person.CATEGORY_COLLEAGUE,
                Person.CATEGORY_OTHER
        );
        categoryFilter.setValue("All");
        categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        loadPersons();
    }

    @FXML
    private void onRefresh() {
        loadPersons();
    }

    @FXML
    private void onSearch() {
        applyFilters();
    }

    @FXML
    private void onToggleFavourites() {
        showingFavouritesOnly = !showingFavouritesOnly;
        applyFilters();
    }

    @FXML
    private void onAddContact() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/contactapp/view/add_person.fxml"));
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
            if (controller.isSaved()) loadPersons();
        } catch (IOException e) {
            showError("Unable to open Add Contact dialog.", e);
        }
    }

    @FXML
    private void onEditContact() {
        Person selected = personTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No Selection", "Please select a contact to edit.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/contactapp/view/edit_person.fxml"));
            Stage dialog = new Stage();
            dialog.setTitle("Edit Contact");
            dialog.setScene(new Scene(loader.load()));
            dialog.initModality(Modality.APPLICATION_MODAL);
            if (personTable.getScene() != null && personTable.getScene().getWindow() != null) {
                dialog.initOwner(personTable.getScene().getWindow());
            }
            dialog.setResizable(false);
            EditPersonController controller = loader.getController();
            controller.setPerson(selected);
            controller.setOnSavedCallback(this::loadPersons);
            dialog.showAndWait();
        } catch (IOException e) {
            showError("Unable to open Edit Contact dialog.", e);
        }
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
        if (result.isEmpty() || result.get() != ButtonType.OK) return;
        boolean deleted = personDAO.delete(selected.getIdperson());
        if (deleted) {
            loadPersons();
        } else {
            showInfo("Delete Failed", "Contact could not be deleted.");
        }
    }

    @FXML
    private void onExportCSV() {
        File file = new File("contacts_export.csv");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Header row
            writer.println("ID,Last Name,First Name,Nickname,Category,Phone,Email,Address,DOB,Favourite");
            // Data rows
            for (Person p : persons) {
                writer.println(
                    p.getIdperson() + "," +
                    safe(p.getLastname()) + "," +
                    safe(p.getFirstname()) + "," +
                    safe(p.getNickname()) + "," +
                    safe(p.getCategory()) + "," +
                    safe(p.getPhoneNumber()) + "," +
                    safe(p.getEmailAddress()) + "," +
                    safe(p.getAddress()) + "," +
                    formatDate(p.getBirthDate()) + "," +
                    (p.isFavorite() ? "Yes" : "No")
                );
            }
            showInfo("Export Successful", "Contacts exported to:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            showError("Export failed.", e);
        }
    }


    @FXML
private void onToggleDarkMode() {
    darkMode = !darkMode;
    Scene scene = personTable.getScene();
    String darkCSS = getClass().getResource("/com/contactapp/css/dark-mode.css").toExternalForm();

    if (darkMode) {
        scene.getStylesheets().add(darkCSS);
        darkModeButton.setText("☀ Light Mode");
    } else {
        scene.getStylesheets().remove(darkCSS);
        darkModeButton.setText("🌙 Dark Mode");
    }
}
    public void setInitialPersons(List<Person> initialPersons) {
        if (initialPersons == null) {
            loadPersons();
            return;
        }
        persons.setAll(initialPersons);
        applyFilters();
    }

    private void loadPersons() {
        persons.setAll(personDAO.getAll());
        applyFilters();
    }

    private void applyFilters() {
        String query    = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String category = categoryFilter.getValue();
        filteredPersons.setPredicate(person -> {
            if (showingFavouritesOnly && !person.isFavorite()) return false;
            if (category != null && !category.equals("All")
                    && !category.equals(person.getCategory())) return false;
            if (!query.isEmpty()) {
                boolean nameMatch =
                        person.getFirstname().toLowerCase().contains(query) ||
                        person.getLastname().toLowerCase().contains(query);
                if (!nameMatch) return false;
            }
            return true;
        });
    }

    private void configureColumns() {
    idColumn.setCellValueFactory(
            data -> new SimpleIntegerProperty(data.getValue().getIdperson()));
    lastnameColumn.setCellValueFactory(
            data -> new SimpleStringProperty(safe(data.getValue().getLastname())));
    firstnameColumn.setCellValueFactory(
            data -> new SimpleStringProperty(safe(data.getValue().getFirstname())));
    nicknameColumn.setCellValueFactory(
            data -> new SimpleStringProperty(safe(data.getValue().getNickname())));
    phoneColumn.setCellValueFactory(
            data -> new SimpleStringProperty(safe(data.getValue().getPhoneNumber())));
    addressColumn.setCellValueFactory(
            data -> new SimpleStringProperty(safe(data.getValue().getAddress())));
    emailColumn.setCellValueFactory(
            data -> new SimpleStringProperty(safe(data.getValue().getEmailAddress())));
    birthDateColumn.setCellValueFactory(
            data -> new SimpleStringProperty(formatDate(data.getValue().getBirthDate())));
    categoryColumn.setCellValueFactory(
            data -> new SimpleStringProperty(safe(data.getValue().getCategory())));
    favoriteColumn.setCellValueFactory(
            data -> new SimpleStringProperty(data.getValue().isFavorite() ? "⭐" : ""));

    // Photo column — shows circular thumbnail
  // Photo column — shows circular thumbnail with default avatar
photoColumn.setCellFactory(col -> new javafx.scene.control.TableCell<Person, String>() {
    private final javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
    private final javafx.scene.layout.StackPane container = new javafx.scene.layout.StackPane();
    {
        imageView.setFitWidth(36);
        imageView.setFitHeight(36);
        imageView.setPreserveRatio(false);

        // Circular clip
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(18, 18, 18);
        imageView.setClip(clip);

        // Circular border
        javafx.scene.shape.Circle border = new javafx.scene.shape.Circle(19);
        border.setFill(javafx.scene.paint.Color.TRANSPARENT);
        border.setStroke(javafx.scene.paint.Color.web("#5a8cd6"));
        border.setStrokeWidth(1.5);

        container.getChildren().addAll(imageView, border);
        container.setAlignment(javafx.geometry.Pos.CENTER);
        setAlignment(javafx.geometry.Pos.CENTER);
    }

    @Override
    protected void updateItem(String path, boolean empty) {
        super.updateItem(path, empty);
        if (empty) {
            setGraphic(null);
            return;
        }
        // Try to load photo or show default avatar
        if (path != null && !path.isEmpty()) {
            java.io.File f = new java.io.File(path);
            if (f.exists()) {
                imageView.setImage(new javafx.scene.image.Image(
                    f.toURI().toString(), 36, 36, false, true));
                setGraphic(container);
                return;
            }
        }
        // Default avatar — initials circle
        javafx.scene.layout.StackPane avatar = new javafx.scene.layout.StackPane();
        javafx.scene.shape.Circle bg = new javafx.scene.shape.Circle(18);
        bg.setFill(javafx.scene.paint.Color.web("#5a8cd6"));
        Person person = getTableView().getItems().get(getIndex());
        String initials = "";
        if (person.getFirstname() != null && !person.getFirstname().isEmpty())
            initials += person.getFirstname().charAt(0);
        if (person.getLastname() != null && !person.getLastname().isEmpty())
            initials += person.getLastname().charAt(0);
        javafx.scene.control.Label initialsLabel = new javafx.scene.control.Label(initials.toUpperCase());
        initialsLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        avatar.getChildren().addAll(bg, initialsLabel);
        avatar.setAlignment(javafx.geometry.Pos.CENTER);
        setAlignment(javafx.geometry.Pos.CENTER);
        setGraphic(avatar);
    }
});
photoColumn.setCellValueFactory(
        data -> new SimpleStringProperty(safe(data.getValue().getPhotoPath())));
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
        alert.initOwner(personTable.getScene().getWindow());
        alert.showAndWait();
    }

    private void showError(String message, Exception e) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(message);
    alert.setContentText(e.getClass().getName() + ": " + e.getMessage() 
        + (e.getCause() != null ? "\nCaused by: " + e.getCause().getMessage() : ""));
    alert.initOwner(personTable.getScene().getWindow());
    alert.setResizable(true);
    alert.showAndWait();
}
}