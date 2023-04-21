package hu.pizzavalto.pizzaproject.model;

import java.util.List;

/**
 * Az Order osztály reprezentál egy rendelést.
 */
public class Order {
    /**
     * A rendelés azonosítója.
     */
    private Long id;
    /**
     * A rendelés helye.
     */
    private final String location;
    /**
     * A rendelés dátuma.
     */
    private final String order_date;
    /**
     * A rendelés ára.
     */
    private final int price;
    /**
     * A rendelő telefonszáma.
     */
    private final String phone_number;
    /**
     * A rendelés állapota, kész vagy sem.
     */
    private final boolean ready;
    /**
     * A rendelt pizzák listája.
     */
    private final List<PizzaDto> orderPizzas;

    /**
     * Konstruktor egy Order objektum létrehozásához.
     *
     * @param id           A rendelés azonosítója.
     * @param location     A rendelés helye.
     * @param order_date   A rendelés dátuma.
     * @param price        A rendelés ára.
     * @param phone_number A rendelő telefonszáma.
     * @param ready        Az állapota, kész vagy sem.
     * @param orderPizzas  A rendelt pizzák listája.
     */
    public Order(Long id, String location, String order_date, int price, String phone_number, boolean ready, List<PizzaDto> orderPizzas) {
        this.id = id;
        this.location = location;
        this.order_date = order_date;
        this.price = price;
        this.phone_number = phone_number;
        this.ready = ready;
        this.orderPizzas = orderPizzas;
    }

    /**
     * Visszaadja a rendelés azonosítóját.
     *
     * @return A rendelés azonosítója.
     */
    public Long getId() {
        return id;
    }

    /**
     * Beállítja a rendelés azonosítóját.
     *
     * @param id A rendelés azonosítója.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Visszaadja a rendelés helyét.
     *
     * @return A rendelés helye.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Visszaadja a rendelés dátumát.
     *
     * @return A rendelés dátuma.
     */
    public String getOrder_date() {
        return order_date;
    }

    /**
     * Visszaadja a rendelés árát.
     *
     * @return A rendelés ára.
     */
    public int getPrice() {
        return price;
    }

    /**
     * Visszaadja a rendelő telefonszámát.
     *
     * @return A rendelő telefonszáma.
     */
    public String getPhone_number() {
        return phone_number;
    }

    /**
     * Visszaadja a rendelés állapotát.
     *
     * @return A rendelés állapota, kész vagy sem.
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * Visszaadja a rendelt pizzák listáját.
     *
     * @return A rendelt pizzák listája.
     */
    public List<PizzaDto> getOrderPizzas() {
        return orderPizzas;
    }
}