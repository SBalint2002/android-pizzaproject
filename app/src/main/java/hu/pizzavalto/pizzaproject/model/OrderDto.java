package hu.pizzavalto.pizzaproject.model;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Az OrderDto osztály egy rendelés adatait tartalmazza, amelyeket a felhasználó küld a szervernek.
 */
public class OrderDto {
    /**
     * A rendelés helyszínét tartalmazó String.
     */
    private final String location;
    /**
     * A rendelő telefonszámát tartalmazó String.
     */
    private final String phoneNumber;
    /**
     * Azon pizzá-k azonosítóit tartalmazó Long lista, amelyeket a felhasználó rendelni kíván.
     */
    private final List<Long> pizzaIds;

    /**
     * Az OrderDto osztály konstruktora.
     *
     * @param location    a rendelés helyszínét tartalmazó String
     * @param phoneNumber a rendelő telefonszámát tartalmazó String
     * @param pizzaIds    azon pizza-k azonosítóit tartalmazó Long lista, amelyeket a felhasználó rendelni kíván
     */
    public OrderDto(String location, String phoneNumber, List<Long> pizzaIds) {
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.pizzaIds = pizzaIds;
    }

    /**
     * Az OrderDto osztály toString metódusa, amely visszaadja az osztály összes adatát String formátumban.
     */
    @NonNull
    @Override
    public String toString() {
        return "OrderDto{" + "location='" + location + '\'' + ", phoneNumber='" + phoneNumber + '\'' + ", pizzaIds=" + pizzaIds + '}';
    }
}
