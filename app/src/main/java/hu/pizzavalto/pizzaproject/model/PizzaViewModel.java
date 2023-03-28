package hu.pizzavalto.pizzaproject.model;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PizzaViewModel extends ViewModel {
    private HashMap<Long, Integer> pizzaIds = new HashMap<>();
    private List<Pizza> pizzas = new ArrayList<>();

    public HashMap<Long, Integer> getPizzaIds() {
        return pizzaIds;
    }

    public void setPizzaIds(HashMap<Long, Integer> pizzaIds) {
        this.pizzaIds = pizzaIds;
    }

    public List<Pizza> getPizzas() {
        return pizzas;
    }

    public void setPizzas(List<Pizza> pizzas) {
        this.pizzas = pizzas;
    }

    public void addPizza(Pizza pizza) {
        if (pizzaIds.containsKey(pizza.getId())) {
            updatePizzaQuantity(pizza, pizzaIds.get(pizza.getId()) + 1);
        } else {
            pizzaIds.put(pizza.getId(), 1);
            pizzas.add(pizza);
        }
    }

    public void updatePizzaQuantity(Pizza pizza, int newQuantity) {
        if (pizzaIds.containsKey(pizza.getId())) {
            pizzaIds.put(pizza.getId(), newQuantity);
        }
    }

    public void clear() {
        pizzaIds = new HashMap<>();
        pizzas = new ArrayList<>();
    }
}
