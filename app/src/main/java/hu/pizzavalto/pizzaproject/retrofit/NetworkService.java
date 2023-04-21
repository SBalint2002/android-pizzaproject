package hu.pizzavalto.pizzaproject.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    /**
     * Az alap URL, amire a hálózati kérés lesz elküldve.
     * Ez az eszköz belső hálózati címe backend porttal kiegészítve.
     * Külső eszköz esetén a számítógép címét kell megadni.
     * pl: 192.168.10.156:8080
     */
    private static final String BASE_URL = "http://10.0.2.2:8080";

    // Retrofit példány
    private Retrofit retrofit;

    /**
     * Ez a konstruktor meghívja a initializeRetrofit() metódust, hogy inicializálja a Retrofit példányt.
     */
    public NetworkService() {
        initializeRetrofit();
    }

    /**
     * Metódus, amely inicializálja a Retrofit példányt.
     * Ez a metódus beállítja a Retrofit-et a BASE_URL segítségével, majd hozzáad egy HTTP naplózási Interceptor-t, amely részletesen naplózza az összes HTTP kérést és választ.
     * Ezenkívül beállítja a GSON konvertert a Retrofit példányon belül, amely lehetővé teszi a JSON adatok automatikus átalakítását Java objektumokká.
     */
    private void initializeRetrofit() {
        //HttpLoggingInterceptor hozzáadása a Retrofit-hez
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // OkHttpClient inicializálása és az httpLoggingInterceptor hozzáadása
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(loggingInterceptor);

        // GSON inicializálása
        Gson gson = new GsonBuilder().setLenient().create();

        // Retrofit inicializálása a BASE_URL és az előzőleg konfigurált OkHttpClient és Gson objektumok segítségével
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(clientBuilder.build()).addConverterFactory(GsonConverterFactory.create(gson)).build();
    }

    /**
     * Getter metódus, amely visszaadja a Retrofit példányt.
     * Azok a metódusok amelyek hálózati kéréseket hajtanak végre azok használják ezt a getter-t a Retrofit példány eléréséhez.
     *
     * @return Visszaküldi a példányt.
     */
    public Retrofit getRetrofit() {
        return retrofit;
    }
}
