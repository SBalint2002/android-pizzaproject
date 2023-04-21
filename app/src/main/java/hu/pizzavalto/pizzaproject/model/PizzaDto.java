package hu.pizzavalto.pizzaproject.model;

/**
 * Az PizzaDto osztály egy rendeléshez tartózó pizza adatait tartalmazza.
 * Ezt teszi lehetővé hogy egy rendeléshez több pizzát lehessen megadni.
 */
public class PizzaDto {
    /**
     * Azonosító.
     */
    private Long id;
    /**
     * A Pizza objektuma.
     */
    private Pizza pizza;

    /**
     * Azonosító lekérdezése.
     *
     * @return azonosító.
     */
    public Long getId() {
        return id;
    }

    /**
     * Azonosító módosítása.
     *
     * @param id azonosító.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Pizza objektum lekérdezése.
     *
     * @return Pizza objektum.
     */
    public Pizza getPizza() {
        return pizza;
    }
}
