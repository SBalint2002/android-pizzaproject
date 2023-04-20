package hu.pizzavalto.pizzaproject.model;

import java.util.List;

public class Order {
    private Long id;
    private final String location;
    private final String order_date;
    private final int price;
    private final String phone_number;
    private final boolean ready;
    private final List<PizzaDto> orderPizzas;

    public Order(Long id, String location, String order_date, int price, String phone_number, boolean ready, List<PizzaDto> orderPizzas) {
        this.id = id;
        this.location = location;
        this.order_date = order_date;
        this.price = price;
        this.phone_number = phone_number;
        this.ready = ready;
        this.orderPizzas = orderPizzas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public String getOrder_date() {
        return order_date;
    }

    public int getPrice() {
        return price;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public boolean isReady() {
        return ready;
    }

    public List<PizzaDto> getOrderPizzas() {
        return orderPizzas;
    }
}