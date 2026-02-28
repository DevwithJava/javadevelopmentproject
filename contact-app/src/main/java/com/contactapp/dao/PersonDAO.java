package com.contactapp.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.contactapp.model.Person;
import com.contactapp.util.DBConnection;

/**
 * Data Access Object for Person entity. Handles all database operations for the
 * person table.
 */
public class PersonDAO {

    /**
     * Retrieves all persons from the database.
     */
    public List<Person> getAll() {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT * FROM person ORDER BY lastname, firstname";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Person person = mapResultSetToPerson(rs);
                persons.add(person);
            }

            System.out.println("[DAO] Retrieved " + persons.size() + " persons from database");

        } catch (SQLException e) {
            System.err.println("[DAO] Error retrieving all persons: " + e.getMessage());
        }

        return persons;
    }

    /**
     * Retrieves a person by ID.
     */
    public Person getById(int id) {
        String sql = "SELECT * FROM person WHERE idperson = ?";
        Person person = null;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    person = mapResultSetToPerson(rs);
                    System.out.println("[DAO] Retrieved person with ID: " + id);
                }
            }

        } catch (SQLException e) {
            System.err.println("[DAO] Error retrieving person by ID: " + id + " - " + e.getMessage());
        }

        return person;
    }

    /**
     * Adds a new person to the database.
     */
    public int add(Person person) {
        if (person == null) {
            System.out.println("[DAO] Warning: Attempt to add null person");
            return -1;
        }

        String sql = "INSERT INTO person(lastname, firstname, nickname, phone_number, address, email_address, birth_date) "
                + "VALUES(?,?,?,?,?,?,?)";
        int generatedId = -1;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setPreparedStatementParameters(stmt, person);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                        System.out.println("[DAO] Added new person with ID: " + generatedId);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("[DAO] Error adding person: " + e.getMessage());
        }

        return generatedId;
    }

    /**
     * Updates an existing person in the database.
     */
    public boolean update(Person person) {
        if (person == null || person.getIdperson() <= 0) {
            System.out.println("[DAO] Warning: Attempt to update invalid person");
            return false;
        }

        String sql = "UPDATE person SET lastname=?, firstname=?, nickname=?, phone_number=?, "
                + "address=?, email_address=?, birth_date=? WHERE idperson=?";
        boolean success = false;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            setPreparedStatementParameters(stmt, person);
            stmt.setInt(8, person.getIdperson());

            int affectedRows = stmt.executeUpdate();
            success = affectedRows > 0;

            if (success) {
                System.out.println("[DAO] Updated person with ID: " + person.getIdperson());
            } else {
                System.out.println("[DAO] Warning: No person found with ID: " + person.getIdperson());
            }

        } catch (SQLException e) {
            System.err.println("[DAO] Error updating person with ID: " + person.getIdperson() + " - " + e.getMessage());
        }

        return success;
    }

    /**
     * Deletes a person from the database.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM person WHERE idperson=?";
        boolean success = false;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            success = affectedRows > 0;

            if (success) {
                System.out.println("[DAO] Deleted person with ID: " + id);
            } else {
                System.out.println("[DAO] Warning: No person found with ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("[DAO] Error deleting person with ID: " + id + " - " + e.getMessage());
        }

        return success;
    }

    private Person mapResultSetToPerson(ResultSet rs) throws SQLException {
        return new Person(
                rs.getInt("idperson"),
                rs.getString("lastname"),
                rs.getString("firstname"),
                rs.getString("nickname"),
                rs.getString("phone_number"),
                rs.getString("address"),
                rs.getString("email_address"),
                rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null
        );
    }

    private void setPreparedStatementParameters(PreparedStatement stmt, Person person) throws SQLException {
        stmt.setString(1, person.getLastname());
        stmt.setString(2, person.getFirstname());
        stmt.setString(3, person.getNickname());
        stmt.setString(4, person.getPhoneNumber());
        stmt.setString(5, person.getAddress());
        stmt.setString(6, person.getEmailAddress());
        stmt.setDate(7, person.getBirthDate() != null ? Date.valueOf(person.getBirthDate()) : null);
    }
}
