package hu.pizzavalto.pizzaproject.model;

import androidx.annotation.NonNull;

import java.util.List;

public class OrderDto {
    private final String location;
    private final String phoneNumber;
    private final List<Long> pizzaIds;

    public OrderDto(String location, String phoneNumber, List<Long> pizzaIds) {
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.pizzaIds = pizzaIds;
    }

    @NonNull
    @Override
    public String toString() {
        return "OrderDto{" + "location='" + location + '\'' + ", phoneNumber='" + phoneNumber + '\'' + ", pizzaIds=" + pizzaIds + '}';
    }
}
