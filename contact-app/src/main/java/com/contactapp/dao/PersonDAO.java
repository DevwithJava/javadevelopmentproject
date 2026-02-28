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

public class PersonDAO {

    public List<Person> getAll() {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT * FROM person ORDER BY lastname, firstname";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) persons.add(mapResultSetToPerson(rs));
            System.out.println("[DAO] Retrieved " + persons.size() + " persons.");
        } catch (SQLException e) {
            System.err.println("[DAO] Error retrieving all persons: " + e.getMessage());
        }
        return persons;
    }

    public List<Person> getFavorites() {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT * FROM person WHERE favorite = 1 ORDER BY lastname, firstname";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) persons.add(mapResultSetToPerson(rs));
        } catch (SQLException e) {
            System.err.println("[DAO] Error retrieving favourites: " + e.getMessage());
        }
        return persons;
    }

    public List<Person> getByCategory(String category) {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT * FROM person WHERE category = ? ORDER BY lastname, firstname";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) persons.add(mapResultSetToPerson(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error retrieving by category: " + e.getMessage());
        }
        return persons;
    }

    public Person getById(int id) {
        String sql = "SELECT * FROM person WHERE idperson = ?";
        Person person = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) person = mapResultSetToPerson(rs);
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error retrieving person by ID: " + e.getMessage());
        }
        return person;
    }

    public int add(Person person) {
        if (person == null) return -1;
        String sql = "INSERT INTO person(lastname, firstname, nickname, phone_number, address, "
                + "email_address, birth_date, category, favorite, photo_path) VALUES(?,?,?,?,?,?,?,?,?,?)";
        int generatedId = -1;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setPreparedStatementParameters(stmt, person);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedId = keys.getInt(1);
                        System.out.println("[DAO] Added person with ID: " + generatedId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error adding person: " + e.getMessage());
        }
        return generatedId;
    }

    public boolean update(Person person) {
        if (person == null || person.getIdperson() <= 0) return false;
        String sql = "UPDATE person SET lastname=?, firstname=?, nickname=?, phone_number=?, "
                + "address=?, email_address=?, birth_date=?, category=?, favorite=?, photo_path=? WHERE idperson=?";
        boolean success = false;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setPreparedStatementParameters(stmt, person);
            stmt.setInt(11, person.getIdperson());
            success = stmt.executeUpdate() > 0;
            if (success) System.out.println("[DAO] Updated person ID: " + person.getIdperson());
        } catch (SQLException e) {
            System.err.println("[DAO] Error updating person: " + e.getMessage());
        }
        return success;
    }

    public boolean toggleFavorite(int id) {
        String sql = "UPDATE person SET favorite = CASE WHEN favorite=1 THEN 0 ELSE 1 END WHERE idperson=?";
        boolean success = false;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            success = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO] Error toggling favourite: " + e.getMessage());
        }
        return success;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM person WHERE idperson=?";
        boolean success = false;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            success = stmt.executeUpdate() > 0;
            if (success) System.out.println("[DAO] Deleted person ID: " + id);
        } catch (SQLException e) {
            System.err.println("[DAO] Error deleting person: " + e.getMessage());
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
                rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null,
                rs.getString("category") != null ? rs.getString("category") : Person.CATEGORY_OTHER,
                rs.getInt("favorite") == 1,
                rs.getString("photo_path")
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
        stmt.setString(8, person.getCategory() != null ? person.getCategory() : Person.CATEGORY_OTHER);
        stmt.setInt(9, person.isFavorite() ? 1 : 0);
        stmt.setString(10, person.getPhotoPath());
    }
}