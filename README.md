# PizzaVáltó Projekt Android App
Ez az alkalmazás egy pizza rendelő alkalmazás, amely lehetővé teszi a felhasználók számára a pizza kiválasztását, hozzáadását a kosárhoz, rendelés feladását, az előző rendelések megtekintését és a felhasználói információk frissítését.

## Futtatás

A projekt az Android Studio-ban Java nyelven lett írva. A futtatáshoz szükséges:
- Backend: [PizzaProject Backend](https://github.com/SBalint2002/PizzaProject-spring.git)
- Fejlesztői környezet: [Android Studio](https://developer.android.com/studio)
- Adatbázis kezelő: [MariaDB](https://mariadb.org/)
- Csomagkezelő: [Gradle](https://gradle.org/)

### Telepítés
Ha nincs telepítve az alkalmazás, akkor a következő lépéseket kell végrehajtani:

- Klónozza le a projektet a Gitből a következő paranccsal:
```bash
git clone https://github.com/SBalint2002/PizzaProject-android.git
```
Nyissa meg a projektet az Android Studio-ban, majd lépjen be a build.gradle fájlba és nyomja meg a Sync gombot.

Futtassa az alkalmazást az Android Studio-ban az Emulatorban vagy egy külső eszközön.

### Tesztelés
Ha a felhasználó külső eszközt (például mobiltelefont) szeretne használni az alkalmazás teszteléséhez, akkor az alábbi lépéseket kell követnie:

Módosítsa a [NetworkService.java](https://github.com/SBalint2002/PizzaProject-android/blob/main/app/src/main/java/hu/pizzavalto/pizzaproject/retrofit/NetworkService.java) fájlban a BASE_URL-t a saját eszköze belső hálózati címére, mint például a _http://192.168.0.101:8080_.

## Funkciók
A felhasználó számára a következő funkciók érhetők el:

- Bejelentkezhet vagy regisztrálhat az alkalmazásba.
- Láthatja az étlapot (pizzákat) és hozzáadhatják a kiválasztott pizzákat a kosárhoz.
- A kosárba helyezett termékeket ellenőrizhetik és megrendelhetik azokat, megadva a szállítási címet és a telefonszámot.
- Az előző rendeléseket meg lehet tekinteni az alkalmazásban. A készen álló rendelések szürkék, az új rendelések pirosak.
- Frissíthetik a saját információikat. Ha egy mező nem változott az nem frissül az adatbázisban.

## Dokumentáció
A fejlesztői dokumentáció a docs mappában található az index.html megnyitásával!

Felhasználói dokumentáció az __Android felhasználói doksi.docx__ fájlban található.