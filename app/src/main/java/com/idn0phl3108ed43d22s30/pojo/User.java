package com.idn0phl3108ed43d22s30.pojo;

import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jimish on 29/6/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private static final String TAG = "User";

    @JsonProperty(ApiKeys.ID_LABEL)
    private String userId;
    @JsonProperty(ApiKeys.USER_FIRSTNAME)
    private String firstname;
    @JsonProperty(ApiKeys.USER_LASTNAME)
    private String lastname;
    @JsonProperty(ApiKeys.USER_EMAIL)
    private String email;
    @JsonProperty(ApiKeys.PASSWORD_LABEL)
    private String password;
    @JsonProperty(ApiKeys.USER_CONTACT)
    private String contact;
    @JsonProperty(ApiKeys.USER_USERNAME)
    private String username;

    public User(){}

    public User(String userId, String firstname, String lastname, String email, String password, String contact, String username) {
        this.userId = userId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.contact = contact;
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
