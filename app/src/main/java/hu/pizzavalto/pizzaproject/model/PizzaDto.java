package hu.pizzavalto.pizzaproject.model;

public class PizzaDto {
    private Long id;
    private Pizza pizza;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pizza getPizza() {
        return pizza;
    }
}
