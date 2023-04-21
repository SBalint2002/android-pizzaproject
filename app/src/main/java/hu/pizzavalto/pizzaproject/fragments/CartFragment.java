package hu.pizzavalto.pizzaproject.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import hu.pizzavalto.pizzaproject.R;
import hu.pizzavalto.pizzaproject.components.OrderActivity;
import hu.pizzavalto.pizzaproject.model.Pizza;
import hu.pizzavalto.pizzaproject.model.PizzaViewModel;

/**
 * Egy {@link Fragment} alosztály, ami megjeleníti a kosárhoz adott pizzákat soronként.
 * Innen irányít át a megrendelés oldalra. Mikor átirányít nem zárja be ezt az Osztályt
 * mivel az ott megnyomott vissza gomb esetén ne kelljen újraindítani az egészet mert
 * különben törlődne a kosár tartalma.
 */
public class CartFragment extends Fragment {
    /**
     * Kosárban elmentett pizzákat tartalmazó osztály.
     */
    private PizzaViewModel pizzaViewModel;

    /**
     * Pizzák listájának megjelenítésére szolgáló konténer.
     */
    private LinearLayout itemContainer;

    /**
     * Rendelés gomb létrehozása.
     */
    private Button orderButton;

    /**
     * Fragment szövegmezőinek létrehozása. Összérték és üres kosárt jelző szöveg.
     */
    private TextView sumAllPrice, emptyCart;

    /**
     * A kosár alapértelmezett értéke.
     */
    private int price = 0;

    /**
     * Ar objektum publikus Getter metódusa
     *
     * @return visszaadja az ár objektumot
     */
    public int getPrice() {
        return price;
    }

    /**
     * Az Activity létrehozásakor meghívódó metódus, amely inicializálja az Activity-t.
     *
     * @param savedInstanceState Ha az Activity újra inicializálódik
     *                           az előzőleg elmentett állapot visszaállításához szükséges adatokat tartalmazza.
     *                           <b><i>Megjegyzés: Ellenkező esetben null.</i></b>
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pizzaViewModel = new ViewModelProvider(requireActivity()).get(PizzaViewModel.class);
    }

    /**
     * Megjeleníti fragment_cart layout-ot, beállítja a szükséges változókat, majd visszatér a View-vel.
     *
     * @param inflater           A LayoutInflater objektum, amely segítségével a fragmentben található nézeteket "felfújja"(inflate).
     * @param container          Ha nem null, ez a szülő nézet, amelyhez a fragment UI-ját csatolni kell.
     *                           A fragment ne adja hozzá magát a nézethez, de az elrendezés LayoutParams-ek generálására használható.
     * @param savedInstanceState Ha nem null, akkor a fragment korábbi mentett állapotából újra létrehozza.
     * @return A létrehozott View.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Meghívja a UI elemek inicializálásért felelős metódust
        init(view);

        // Kosár tartalmát kéri le
        getCartContent();

        // Rendelés gombra kattintáskor tovább adja az OrderActivity-nek a kosár tartalmát majd
        // átiráányít oda anélkül hogy bezárná az aktív osztályt.
        // Ha a kosár üres azt egy Toast-ban közli és nem irányít át.
        orderButton.setOnClickListener(order -> {
            HashMap<Long, Integer> pizzaIds = pizzaViewModel.getPizzaIds();
            if (pizzaIds != null && !pizzaIds.isEmpty()) {
                Intent intent = new Intent(this.getActivity(), OrderActivity.class);
                intent.putExtra("pizzaIds", pizzaIds);
                intent.putExtra("price", getPrice());
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "A kosár üres!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    /**
     * Alapértelmezetten egy üres kosár jelzéssel ellátott TextView jelenik meg.
     * Viszont ha van Pizza elrakva a kosárba, ezt eltűnteti és helyette
     * Soronként jeleníti meg a különböző pizzák nevét és mennyiségét, gombokkal
     * Lehet törölni a kosárból, csökkenteni illetvet növelni a mennyiséget.
     */
    private void getCartContent() {
        // Beállítja hogy hol jelenítse meg a pizzákat
        MenuFragment menuFragment = new MenuFragment();
        menuFragment.setTargetFragment(CartFragment.this, 0);

        HashMap<Long, Integer> pizzaIds = pizzaViewModel.getPizzaIds();

        // Ha üres akkor látszódik az ezt jelző szöveg, különben meg eltűnik
        if (pizzaIds.isEmpty()) {
            emptyCart.setVisibility(VISIBLE);
        } else {
            emptyCart.setVisibility(GONE);
        }

        /**
         * Létrehozza a pizza típusokat. Minden pizza azonosítóból csak 1 sort jelenít meg mennyiséggel együtt
         * A cartItem típús áll a Pizza nevéből, mennyiségéből, egy plusz/minusz és egy bezárás gombból.
         * Plusz gombbal hozzáadunk egyet a mennyiséghez.
         * Minusz gombbal elveszünk a mennyiségből.
         * Az X/bezárás gombbal meg kitöröljük az összes hasonló azonosítójú pizzát a kosárból
         */
        for (Long pizzaId : pizzaIds.keySet()) {
            Optional<Integer> pizzaIdValue = Optional.ofNullable(pizzaIds.get(pizzaId));
            if (pizzaIdValue.isPresent()) {
                final int[] count = {pizzaIdValue.orElse(0)};

                // Azonosító alapján visszaadja a hozzátartozó pizza adatait
                Pizza pizza = pizzaViewModel.getPizzas().stream().filter((x) -> Objects.equals(x.getId(), pizzaId)).findFirst().get();

                // Létrehozza a cartItem xml fájlját amibe az adatok kerülnek
                View cartItem = LayoutInflater.from(getActivity()).inflate(R.layout.cart_item, (ViewGroup) getView(), false);

                // Pizza nevét elhelyezi az xml-ben
                TextView pizzaName = cartItem.findViewById(R.id.pizzaName);
                pizzaName.setText(pizza.getName());

                // A mennyiségét elhelyezi az annak kijelőlt részen.
                TextView itemQuantity = cartItem.findViewById(R.id.itemQuantity);
                if (count[0] < 2) {
                    itemQuantity.setText("");
                }
                itemQuantity.setText(String.valueOf(count[0]));
                itemContainer.addView(cartItem);

                // Össz ár megjelenítése
                TextView sumPrice = cartItem.findViewById(R.id.sumPrice);
                sumPrice.setText(setPriceText(count[0], pizza.getPrice()));
                updatePrice(price += count[0] * pizza.getPrice());

                // Gombok létrehozása
                Button minusButton = cartItem.findViewById(R.id.button_minus);
                Button plusButton = cartItem.findViewById(R.id.button_plus);
                Button deleteButton = cartItem.findViewById(R.id.deleteItem);

                // Minusz gombbal elveszünk a mennyiségből.
                minusButton.setOnClickListener(decrease -> {
                    if (count[0] > 1) {
                        count[0]--;
                        pizzaIds.put(pizzaId, count[0]);

                        itemQuantity.setText(String.valueOf(count[0]));
                        sumPrice.setText(setPriceText(count[0], pizza.getPrice()));
                        updatePrice(price - pizza.getPrice());
                    }
                });

                // Plusz gombbal hozzáadunk egyet a mennyiséghez.
                plusButton.setOnClickListener(increase -> {
                    count[0]++;
                    pizzaIds.put(pizzaId, count[0]);

                    itemQuantity.setText(String.valueOf(count[0]));
                    sumPrice.setText(setPriceText(count[0], pizza.getPrice()));
                    updatePrice(price + pizza.getPrice());
                });

                // Az X/bezárás gombbal meg kitöröljük az összes hasonló azonosítójú pizzát a kosárból
                deleteButton.setOnClickListener(increase -> {
                    itemContainer.removeView(cartItem);
                    pizzaIds.remove(pizza.getId());
                    pizzaViewModel.setPizzaIds(pizzaIds);
                    pizzaViewModel.getPizzas().remove(pizza);
                    updatePrice(price - count[0] * pizza.getPrice());
                    if (pizzaIds.isEmpty()) {
                        emptyCart.setVisibility(VISIBLE);
                    }
                });
            } else {
                System.out.println("Pizza ezzel az id-val nem létezik!");
            }
        }
    }

    /**
     * Összesített ár beállítása az aktuális értékre.
     *
     * @param count megkapott pizza mennyisége.
     * @param price megkapott pizza darab ára.
     * @return a megkapott pizzáknak az összesített értékét küldi válaszként.
     */
    public String setPriceText(int count, int price) {
        return String.valueOf(count * price);
    }

    /**
     * Frissíti az összesített ár szövegét az aktuális értékre.
     *
     * @param newPrice új ár értéke.
     */
    private void updatePrice(int newPrice) {
        sumAllPrice.setText("Fizetendő összeg: " + newPrice + " Ft");
    }

    /**
     * A szövegek, gomb, konténer inicializálása
     * Ezek a megjelenítési elemek szolgálnak a kosár tartalmának megjelenítésére és módosítására.
     *
     * @param view A Fragment felületéhez tartozó nézetek hierarchiáját tartalmazó View objektum.
     */
    private void init(View view) {
        orderButton = view.findViewById(R.id.orderButton);
        sumAllPrice = view.findViewById(R.id.sumAllPrice);
        emptyCart = view.findViewById(R.id.emptyCart);
        itemContainer = view.findViewById(R.id.itemContainer);
    }
}