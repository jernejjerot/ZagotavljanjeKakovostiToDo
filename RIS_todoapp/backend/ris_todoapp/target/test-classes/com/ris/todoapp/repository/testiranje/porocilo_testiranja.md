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

- **TaskTypeControllerTest:**
  - testCreateTaskType: Preverja uspešno ustvarjanje novega tipa naloge prek HTTP POST zahtevka.
  - **Pomen:** Zagotavlja, da aplikacija omogoča dodajanje novih tipov nalog in pravilno zapisovanje v bazo podatkov.
  - testDeleteTaskType: Preverja uspešno brisanje obstoječega tipa naloge prek HTTP DELETE zahtevka.
  - **Pomen:** Preverja, ali sistem pravilno odstrani zapise tipov nalog iz baze.

- **TaskTypeControllerTest:**
  - testGetUserById_Success: Preverja uspešen HTTP GET zahtevek za pridobitev uporabnika po ID-ju.
  - **Pomen:** Zagotavlja, da se uporabniški podatki pravilno pridobivajo glede na unikatni ID.

- **AdminControllerTest:** Vsebuje šest testov:
    - **Test brisanja uporabnika kot admin:** Preverja, ali admin lahko izbriše uporabnika.
    - **Test neuspešnega brisanja uporabnika kot ne-admin:** Preverja, ali ne-admin ne more brisati uporabnikov.
    - **Test pridobivanja vseh uporabnikov kot admin:** Preverja, ali admin lahko pridobi seznam vseh uporabnikov.
    - **Test neuspešnega pridobivanja vseh uporabnikov kot ne-admin:** Preverja, ali ne-admin ne more pridobiti seznam vseh uporabnikov.
    - **Test brisanja naloge kot admin:** Preverja, ali admin lahko izbriše nalogo.
    - **Test neuspešnega brisanja naloge kot ne-admin:** Preverja, ali ne-admin ne more brisati nalog.
        - **Pomen:** Ti testi zagotavljajo pravilno delovanje funkcionalnosti za administrativne uporabnike in validacijo dovoljenj.

---

## Člani skupine in odgovornost
- **Aleš Močnik:**
    - GeocodingServiceTest (oba testa).
    - TaskControllerTest (oba testa).
    - AdminControllerTest (en test).
- **Jernej Jerot:**
  - TaskTypeControllerTest.
  - UserControllerTest.
  - AdminControllerTest (dva testa).

- **Jaka Cvikl:**
    - UserRepositoryTest (vsi trije testi).
    - AdminControllerTest (trije testi).

## Analiza rezultatov testiranja
- **Skupno število testov:** 17
- **Število uspešnih testov:** 17
- **Napake:** Med testiranjem niso bile odkrite nobene napake.
- **Uspešnost:** Vsi testi so bili uspešno izvedeni, kar potrjuje, da so osnovne funkcionalnosti pravilno implementirane.
- **Popravki:** Med razvojem so bile popravljene manjše napake, kot so nepravilni podatki za geokodiranje in napake pri konstruktorjih.
- Optimizirano je bilo brisanje entitet v testih za zagotavljanje ustreznega čiščenja podatkov po vsakem scenariju.
-Dodaniso bili  manjkajoči podatki za `taskType` v testih in posodobljena je bila validacija dovoljenj v kontrolerju za administrativne funkcije.