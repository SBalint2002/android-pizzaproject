package hu.pizzavalto.pizzaproject.model;

import androidx.annotation.NonNull;

/**
 * A felhasználó modellje.s
 */
public class User {
    /**
     * Az azonosító.
     */
    private Long id;
    /**
     * A felhasználó keresztneve.
     */
    private String first_name;
    /**
     * A felhasználó vezetékneve.
     */
    private String last_name;
    /**
     * A felhasználó e-mail címe.
     */
    private String email;
    /**
     * A felhasználó jelszava.
     */
    private String password;
    /**
     * A felhasználó szerepköre.
     */
    private String role;

    /**
     * Üres konstruktor.
     */
    public User() {

    }

    /**
     * Konstruktor az összes mező inicializálásához.
     *
     * @param id         Az azonosító.
     * @param first_name A felhasználó keresztneve.
     * @param last_name  A felhasználó vezetékneve.
     * @param email      A felhasználó e-mail címe.
     * @param password   A felhasználó jelszava.
     * @param role       A felhasználó szerepköre.
     */
    public User(Long id, String first_name, String last_name, String email, String password, String role) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * Az azonosító lekérdezése.
     *
     * @return Az azonosító.
     */
    public Long getId() {
        return id;
    }

    /**
     * Az azonosító beállítása.
     *
     * @param id Az azonosító.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * A felhasználó keresztnevének lekérdezése.
     *
     * @return A felhasználó keresztneve.
     */
    public String getFirst_name() {
        return first_name;
    }

    /**
     * A felhasználó keresztnevének beállítása.
     *
     * @param first_name A felhasználó keresztneve.
     */
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    /**
     * A felhasználó vezetéknevának lekérdezése.
     *
     * @return A felhasználó vezetékneve.
     */
    public String getLast_name() {
        return last_name;
    }

    /**
     * A felhasználó vezetéknevának beállítása.
     *
     * @param last_name A felhasználó vezetékneve.
     */
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    /**
     * A felhasználó e-mail címének lekérdezése.
     *
     * @return A felhasználó e-mail címe.
     */
    public String getEmail() {
        return email;
    }

    /**
     * A felhasználó e-mail címének beállítása.
     *
     * @param email A felhasználó e-mail címe.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * A felhasználó jelszavának beállítása.
     *
     * @param password A felhasználó jelszava.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * A felhasználó szerepkörének lekérdezése.
     *
     * @return A felhasználó szerepköre.
     */
    public String getRole() {
        return role;
    }

    /**
     * Az osztály toString metódusa.
     *
     * @return Az objektum toString metódusa.
     */
    @NonNull
    @Override
    public String toString() {
        return "User{" + "id=" + id + ", first_name='" + first_name + '\'' + ", last_name='" + last_name + '\'' + ", email='" + email + '\'' + ", password='" + password + '\'' + ", role='" + role + '\'' + '}';
    }
}