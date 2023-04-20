package hu.pizzavalto.pizzaproject.model;

import androidx.annotation.NonNull;

public class Pizza {
    private Long id;
    private String name;
    private final int price;
    private final String description;
    private final String picture;
    private final boolean available;

    public Pizza(Long id, String name, int price, String description, String picture, boolean available) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.picture = picture;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getPicture() {
        return picture;
    }

    public boolean isAvailable() {
        return available;
    }

    @NonNull
    @Override
    public String toString() {
        return "Pizza{" + "id=" + id + ", name='" + name + '\'' + ", price=" + price + ", description='" + description + '\'' + ", picture='" + picture + '\'' + ", available=" + available + '}';
    }
}
