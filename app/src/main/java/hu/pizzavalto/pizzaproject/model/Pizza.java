package hu.pizzavalto.pizzaproject.model;

import androidx.annotation.NonNull;

/**
 * A Pizza osztály leírása
 */
public class Pizza {
    /**
     * A Pizza azonosítója
     */
    private Long id;
    /**
     * A Pizza neve
     */
    private String name;
    /**
     * A Pizza ára
     */
    private final int price;
    /**
     * A Pizza leírása
     */
    private final String description;
    /**
     * A Pizza képe
     */
    private final String picture;
    /**
     * Meghatározza, hogy az adott Pizza elérhető-e
     */
    private final boolean available;

    /**
     * Pizza objektum inicializálása
     *
     * @param id          A Pizza azonosítója
     * @param name        A Pizza neve
     * @param price       A Pizza ára
     * @param description A Pizza leírása
     * @param picture     A Pizza képe
     * @param available   Meghatározza, hogy az adott Pizza elérhető-e
     */
    public Pizza(Long id, String name, int price, String description, String picture, boolean available) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.picture = picture;
        this.available = available;
    }

    /**
     * Azonosító lekérése
     *
     * @return A Pizza azonosítója
     */
    public Long getId() {
        return id;
    }

    /**
     * Azonosító beállítása
     *
     * @param id A Pizza azonosítója
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Név lekérése
     *
     * @return A Pizza neve
     */
    public String getName() {
        return name;
    }

    /**
     * Név beállítása
     *
     * @param name A Pizza neve
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Ar lekérése
     *
     * @return A Pizza ára
     */
    public int getPrice() {
        return price;
    }

    /**
     * Leírás lekérése
     *
     * @return A Pizza leírása
     */
    public String getDescription() {
        return description;
    }

    /**
     * Kép lekérése
     *
     * @return A Pizza képe
     */
    public String getPicture() {
        return picture;
    }

    /**
     * Elérhetőség lekérése
     *
     * @return true, ha az adott Pizza elérhető, false egyébként
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Az osztály toString metódusa.
     *
     * @return Az objektum toString metódusa.
     */
    @NonNull
    @Override
    public String toString() {
        return "Pizza{" + "id=" + id + ", name='" + name + '\'' + ", price=" + price + ", description='" + description + '\'' + ", picture='" + picture + '\'' + ", available=" + available + '}';
    }
}
