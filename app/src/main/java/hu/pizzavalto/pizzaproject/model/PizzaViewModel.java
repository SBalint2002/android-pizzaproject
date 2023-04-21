package hu.pizzavalto.pizzaproject.model;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A PizzaViewModel egy ViewModel osztály, ami a Pizza objektumokkal és azok darabszámaival
 * foglalkozik. A pizzaIds egy hash map, ami az adott pizza azonosítóját és darabszámát tartalmazza,
 * a pizzas pedig a kosárba helyezett pizzák listáját tárolja. Az osztály metódusai lehetővé teszik,
 * hogy új pizzákat adjunk hozzá, módosítsuk a pizzák darabszámát, és töröljük az összes pizzát.
 */
public class PizzaViewModel extends ViewModel {
    /**
     * Egy hash map, ami az adott pizza azonosítóját és darabszámát tartalmazza,
     */
    private HashMap<Long, Integer> pizzaIds = new HashMap<>();

    /**
     * A kosárba rakott pizzák listáját tárolja
     */
    private List<Pizza> pizzas = new ArrayList<>();

    /**
     * Lekérdezi a pizza azonosítóit tartalmazó hashmap-et.
     *
     * @return pizza azonosítók.
     */
    public HashMap<Long, Integer> getPizzaIds() {
        return pizzaIds;
    }

    /**
     * Pizza hashmap módosítása. Ezzel lehet módosítani a pizza darabszámát.
     *
     * @param pizzaIds pizza azonosítók.
     */
    public void setPizzaIds(HashMap<Long, Integer> pizzaIds) {
        this.pizzaIds = pizzaIds;
    }

    /**
     * Pizza lista lekérése.
     *
     * @return pizza lista.
     */
    public List<Pizza> getPizzas() {
        return pizzas;
    }

    /**
     * Pizza lista módosítása.
     *
     * @param pizzas pizza lista.
     */
    public void setPizzas(List<Pizza> pizzas) {
        this.pizzas = pizzas;
    }

    /**
     * Ez a metódus hozzáad egy új pizzát a kosárhoz. Ha a pizza már szerepel a kosárban, akkor csak
     * a darabszámot növeli.
     *
     * @param pizza a kosárhoz adott pizza
     */
    public void addPizza(Pizza pizza) {
        if (pizzaIds.containsKey(pizza.getId())) {
            updatePizzaQuantity(pizza, pizzaIds.get(pizza.getId()) + 1);
        } else {
            pizzaIds.put(pizza.getId(), 1);
            pizzas.add(pizza);
        }
    }

    /**
     * Ez a metódus módosítja a kosárban szereplő egy adott pizza darabszámát.
     *
     * @param pizza       a módosítandó pizza
     * @param newQuantity az új darabszám
     */
    public void updatePizzaQuantity(Pizza pizza, int newQuantity) {
        if (pizzaIds.containsKey(pizza.getId())) {
            pizzaIds.put(pizza.getId(), newQuantity);
        }
    }

    /**
     * Ez a metódus kiüríti a kosár tartalmát.
     */
    public void clear() {
        pizzaIds = new HashMap<>();
        pizzas = new ArrayList<>();
    }
}
