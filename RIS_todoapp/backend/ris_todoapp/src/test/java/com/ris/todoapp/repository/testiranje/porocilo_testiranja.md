# Poročilo o testiranju

## Opis testov
- **GeocodingServiceTest:** Vsebuje dva testa:
    - Test uspešnega geokodiranja: Preverja, ali metoda `geocode` pravilno vrne zemljepisne koordinate za veljaven naslov.
    - Test neuspešnega geokodiranja: Simulira neuspeh pri geokodiranju in preverja, ali metoda vrne prazen rezultat.
    - **Pomen:** Zagotavlja, da geokodiranje deluje pravilno in je pripravljeno na napake.

- **TaskControllerTest:** Vsebuje dva testa:
    - Test kreiranja naloge z veljavnimi podatki: Preverja, ali naloga pravilno ustvari zapis v bazi.
    - Test kreiranja naloge z neveljavnim uporabnikom: Preverja, ali aplikacija pravilno zavrne zahtevek za neveljavnega uporabnika.
    - **Pomen:** Zagotavlja, da je kontroler `TaskController` robusten pri obravnavi veljavnih in neveljavnih podatkov.

- **TaskTypeRepositoryTest:** Vsebuje en test:
    - Test pridobivanja tipov nalog: Preverja, ali `TaskTypeRepository` pravilno pridobi tipe nalog iz baze.
    - **Pomen:** Zagotavlja pravilno delovanje JPA-repozitorija.

## Člani skupine in odgovornost
- **Aleš Močnik:**
    - GeocodingServiceTest (oba testa).
    - TaskControllerTest (oba testa).

## Analiza rezultatov testiranja
- **Skupno število testov:** 5
- **Število uspešnih testov:** 5
- **Napake:** Med testiranjem niso bile odkrite nobene napake.
- **Uspešnost:** Vsi testi so bili uspešno izvedeni, kar potrjuje, da so osnovne funkcionalnosti pravilno implementirane.
- **Popravki:** Med razvojem so bile popravljene manjše napake, kot so nepravilni podatki za geokodiranje in napake pri konstruktorjih.