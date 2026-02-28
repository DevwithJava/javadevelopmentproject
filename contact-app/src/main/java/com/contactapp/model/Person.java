package com.contactapp.model;

import java.time.LocalDate;
import java.util.Objects;

public class Person {

    public static final String CATEGORY_FRIEND    = "Friend";
    public static final String CATEGORY_FAMILY    = "Family";
    public static final String CATEGORY_COLLEAGUE = "Colleague";
    public static final String CATEGORY_OTHER     = "Other";

    private int idperson;
    private String lastname;
    private String firstname;
    private String nickname;
    private String phoneNumber;
    private String address;
    private String emailAddress;
    private LocalDate birthDate;
    private String  category;
    private boolean favorite;
    private String  photoPath;   

    public Person() {}

    public Person(int idperson, String lastname, String firstname, String nickname,
            String phoneNumber, String address, String emailAddress,
            LocalDate birthDate, String category, boolean favorite, String photoPath) {
        this.idperson = idperson; this.lastname = lastname; this.firstname = firstname;
        this.nickname = nickname; this.phoneNumber = phoneNumber; this.address = address;
        this.emailAddress = emailAddress; this.birthDate = birthDate;
        this.category = category; this.favorite = favorite; this.photoPath = photoPath;
    }

    public Person(String lastname, String firstname, String nickname,
            String phoneNumber, String address, String emailAddress,
            LocalDate birthDate, String category, boolean favorite, String photoPath) {
        this.lastname = lastname; this.firstname = firstname; this.nickname = nickname;
        this.phoneNumber = phoneNumber; this.address = address;
        this.emailAddress = emailAddress; this.birthDate = birthDate;
        this.category = category; this.favorite = favorite; this.photoPath = photoPath;
    }

    public Person(String lastname, String firstname, String nickname,
            String phoneNumber, String address, String emailAddress,
            LocalDate birthDate, String category, boolean favorite) {
        this(lastname, firstname, nickname, phoneNumber, address, emailAddress,
             birthDate, category, favorite, null);
    }

    public Person(String lastname, String firstname, String nickname,
            String phoneNumber, String address, String emailAddress, LocalDate birthDate) {
        this(lastname, firstname, nickname, phoneNumber, address, emailAddress,
             birthDate, CATEGORY_OTHER, false, null);
    }

    public Person(int idperson, String lastname, String firstname, String nickname,
            String phoneNumber, String address, String emailAddress, LocalDate birthDate) {
        this(idperson, lastname, firstname, nickname, phoneNumber, address, emailAddress,
             birthDate, CATEGORY_OTHER, false, null);
    }

    public int getIdperson()                         { return idperson; }
    public void setIdperson(int idperson)            { this.idperson = idperson; }
    public String getLastname()                      { return lastname; }
    public void setLastname(String lastname)         { this.lastname = lastname; }
    public String getFirstname()                     { return firstname; }
    public void setFirstname(String firstname)       { this.firstname = firstname; }
    public String getNickname()                      { return nickname; }
    public void setNickname(String nickname)         { this.nickname = nickname; }
    public String getPhoneNumber()                   { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber)   { this.phoneNumber = phoneNumber; }
    public String getAddress()                       { return address; }
    public void setAddress(String address)           { this.address = address; }
    public String getEmailAddress()                  { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
    public LocalDate getBirthDate()                  { return birthDate; }
    public void setBirthDate(LocalDate birthDate)    { this.birthDate = birthDate; }
    public String getCategory()                      { return category; }
    public void setCategory(String category)         { this.category = category; }
    public boolean isFavorite()                      { return favorite; }
    public void setFavorite(boolean favorite)        { this.favorite = favorite; }
    public String getPhotoPath()                     { return photoPath; }
    public void setPhotoPath(String photoPath)       { this.photoPath = photoPath; }

    @Override
    public String toString() {
        return "Person{idperson=" + idperson + ", lastname='" + lastname + '\''
                + ", firstname='" + firstname + '\'' + ", category='" + category + '\''
                + ", favorite=" + favorite + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person p = (Person) o;
        return idperson == p.idperson && favorite == p.favorite
                && Objects.equals(lastname, p.lastname) && Objects.equals(firstname, p.firstname)
                && Objects.equals(nickname, p.nickname) && Objects.equals(phoneNumber, p.phoneNumber)
                && Objects.equals(address, p.address) && Objects.equals(emailAddress, p.emailAddress)
                && Objects.equals(birthDate, p.birthDate) && Objects.equals(category, p.category)
                && Objects.equals(photoPath, p.photoPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idperson, lastname, firstname, nickname,
                phoneNumber, address, emailAddress, birthDate, category, favorite, photoPath);
    }
}