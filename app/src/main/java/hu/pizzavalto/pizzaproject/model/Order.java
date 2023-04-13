package hu.pizzavalto.pizzaproject.model;

import java.util.List;

public class Order {
    private Long id;
    private Long userId;
    private String location;
    private String order_date;
    private int price;
    private String phone_number;
    private boolean ready;
    private List<PizzaDto> orderPizzas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public List<PizzaDto> getOrderPizzas() {
        return orderPizzas;
    }

    public void setOrderPizzas(List<PizzaDto> orderPizzas) {
        this.orderPizzas = orderPizzas;
    }
}