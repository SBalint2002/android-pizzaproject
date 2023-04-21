package hu.pizzavalto.pizzaproject.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.pizzavalto.pizzaproject.R;

/**
 * Egy {@link Fragment} alosztály amely megjeleníti a projektről szóló Fragmentet ami tartalmaz egy képet és egy mottót.
 */
public class AboutUsFragment extends Fragment {
    /**
     * @param inflater           A LayoutInflater objektum, amely segítségével a fragmentben található nézeteket "felfújja"(inflate).
     * @param container          Ha nem null, ez a szülő nézet, amelyhez a fragment UI-ját csatolni kell.
     *                           A fragment ne adja hozzá magát a nézethez, de az elrendezés LayoutParams-ek generálására használható.
     * @param savedInstanceState Ha az Activity újra inicializálódik
     *                           az előzőleg elmentett állapot visszaállításához szükséges adatokat tartalmazza.
     *                           <b><i>Megjegyzés: Ellenkező esetben null.</i></b>
     * @return A "felfújt"(inflate) fragment UI-ját reprezentáló View objektum.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }
}