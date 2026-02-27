# Contact App

A JavaFX contact management application backed by a SQLite database.

## Group Members
- (Task 1) Elisee Watson — Project Setup & Database Layer
- (Task 2) Ballo Steve — List & Delete Features
- (Task 3) Naomi Nketsiah — Add Person Form
- (Task 4) Delassie Brempong — Update Feature & UI Polish



# Run the application
mvn javafx:run

# Run tests
mvn test
```

## Project Structure

```
src/
├── main/
│   ├── java/com/contactapp/
│   │   ├── model/          — Person data class
│   │   ├── dao/            — Database access (PersonDAO, SQLiteConnection)
│   │   ├── controller/     — JavaFX controllers
│   │   └── MainApp.java    — Application entry point
│   └── resources/com/contactapp/view/
│       ├── main.fxml
│       ├── add_person.fxml
│       └── edit_person.fxml
└── test/
    └── java/com/contactapp/dao/
        └── PersonDAOTest.java
```


Schema:
```sql
CREATE TABLE IF NOT EXISTS person (
    idperson     INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    lastname     VARCHAR(45)  NOT NULL,
    firstname    VARCHAR(45)  NOT NULL,
    nickname     VARCHAR(45)  NOT NULL,
    phone_number VARCHAR(15)  NULL,
    address      VARCHAR(200) NULL,
    email_address VARCHAR(150) NULL,
    birth_date   DATE         NULL
);
```
