package hu.pizzavalto.pizzaproject.model;

import androidx.annotation.NonNull;

public class User {
    private Long id;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String role;

    public User() {

    }

    public User(Long id, String first_name, String last_name, String email, String password, String role) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" + "id=" + id + ", first_name='" + first_name + '\'' + ", last_name='" + last_name + '\'' + ", email='" + email + '\'' + ", password='" + password + '\'' + ", role='" + role + '\'' + '}';
    }
}