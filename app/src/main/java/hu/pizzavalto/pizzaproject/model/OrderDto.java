package hu.pizzavalto.pizzaproject.model;

import java.util.List;

public class OrderDto {
    private String location;
    private String phoneNumber;
    private List<Long> pizzaIds;

    public OrderDto(String location, String phoneNumber, List<Long> pizzaIds) {
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.pizzaIds = pizzaIds;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Long> getPizzaIds() {
        return pizzaIds;
    }

    public void setPizzaIds(List<Long> pizzaIds) {
        this.pizzaIds = pizzaIds;
    }

    @Override
    public String toString() {
        return "OrderDto{" +
                "location='" + location + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", pizzaIds=" + pizzaIds +
                '}';
    }
}
