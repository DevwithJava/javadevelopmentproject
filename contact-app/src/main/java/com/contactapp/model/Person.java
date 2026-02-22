package com.contactapp.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Person entity representing a contact in the database. Aligns exactly with the
 * person table schema.
 */
public class Person {

    private int idperson;
    private String lastname;
    private String firstname;
    private String nickname;
    private String phoneNumber;
    private String address;
    private String emailAddress;
    private LocalDate birthDate;

    /**
     * Default constructor.
     */
    public Person() {
    }

    /**
     * Constructor with all fields.
     */
    public Person(int idperson, String lastname, String firstname, String nickname,
            String phoneNumber, String address, String emailAddress, LocalDate birthDate) {
        this.idperson = idperson;
        this.lastname = lastname;
        this.firstname = firstname;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.emailAddress = emailAddress;
        this.birthDate = birthDate;
    }

    /**
     * Constructor without idperson (for new records).
     */
    public Person(String lastname, String firstname, String nickname,
            String phoneNumber, String address, String emailAddress, LocalDate birthDate) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.emailAddress = emailAddress;
        this.birthDate = birthDate;
    }

    // Getters and Setters
    public int getIdperson() {
        return idperson;
    }

    public void setIdperson(int idperson) {
        this.idperson = idperson;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "Person{"
                + "idperson=" + idperson
                + ", lastname='" + lastname + '\''
                + ", firstname='" + firstname + '\''
                + ", nickname='" + nickname + '\''
                + ", phoneNumber='" + phoneNumber + '\''
                + ", address='" + address + '\''
                + ", emailAddress='" + emailAddress + '\''
                + ", birthDate=" + birthDate
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Person person = (Person) o;
        return idperson == person.idperson
                && Objects.equals(lastname, person.lastname)
                && Objects.equals(firstname, person.firstname)
                && Objects.equals(nickname, person.nickname)
                && Objects.equals(phoneNumber, person.phoneNumber)
                && Objects.equals(address, person.address)
                && Objects.equals(emailAddress, person.emailAddress)
                && Objects.equals(birthDate, person.birthDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idperson, lastname, firstname, nickname, phoneNumber, address, emailAddress, birthDate);
    }
}
