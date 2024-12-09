# Scrum Poročilo - Dodajanje Prilog v To-Do Aplikacijo

## Sprint Pregled
**Uporabniška zgodba:**
> Kot uporabnik želim možnost dodajanja prilog (slik ali dokumentov) k nalogam, da imam vse pomembne informacije na enem mestu.

**Sprint Cilj:**
Implementirati funkcionalnost za nalaganje, shranjevanje in prikaz prilog v To-Do aplikaciji, ki vključuje backend, frontend, in testiranje.

---

## Sprint Razdelitev
### Naloge (Backlog):
1. **Backend - Prilagoditev podatkovne baze:**
   - Dodaj 'pictures' v tabelo 'Tasks'.
   - Dodaj testne podatke v INSERT INTO Tasks in posodobi skripto.

2. **Backend - Posodobitev entitet in repozitorijev:**
   - Posodobi entiteto Tasks z pictures.
   - Posodobi `TaskController` za upravljanje s slikamo.

3. **Frontend - Posodobitev uporabniškega vmesnika:**
   - V obrazec za dodajanje/urejanje nalog dodaj možnost nalaganja slik.
   - Omogoči ogled prenesenih slik.

4. **Frontend - Ustvarjanje API povezav:**
   - Posodobi `server.js`, da omogoča prenos in upravljanje slik prek API-ja.

5. **Shranjevanje datotek na strežniku:**
   - Ustvari mapo `/uploads` za shranjevanje datotek na strežniku.
   - Dodaj logiko za upravljanje datotek z uporabo knjižnice Multer. --lahko kaj drugega kot Multer ker je to tudi za datoteke

6. **Testiranje:**
   - Testiraj nalaganje slik in prejšnjih funkcionalnosti.

---

## Sprint Časovne Ocene (Planning Poker)

| Naloga                                    |      Ocena (Ure)      |
|-------------------------------------------|-----------------------|
| Prilagoditev podatkovne baze              | 0.5                   |
| Posodobitev entitet in repozitorijev      | 2                     |
| Posodobitev `TaskController`              | 1                     |
| Posodobitev uporabniškega vmesnika        | 3                     |
| Posodobitev API povezav (`server.js`)     | 2                     |
| Upravljanje slik na strežniku             | 2                     |
| Testiranje funkcionalnosti                | 1                     |

---

## Sprint Napredek

| Naloga                                     | Status | Opombe                       |
|-------------------------------------------|--------|------------------------------|
| Prilagoditev podatkovne baze              | Done   | //////////////////////////// |
| Posodobitev entitet in repozitorijev      | Done   | //////////////////////////// |
| Posodobitev `TaskController`              | Done   | //////////////////////////// |
| Posodobitev uporabniškega vmesnika        | Done   | //////////////////////////// |
| Posodobitev API povezav (`server.js`)     | Done   | //////////////////////////// |
| Upravljanje slik na strežniku             | Doing  |                              |
| Testiranje funkcionalnosti                | Done   | //////////////////////////// |

---

## Ovire in Izzivi
- Prikaz default slike
- potreben refresh page za prikaz task-a
---

## Sprint Skupni Čas
**Zaključene naloge:**
Prilagoditev podatkovne baze **0.5 ur**
- **Primeri testnih podatkov dodani v mysql**:
  - Vstavljeni primeri nalog z različnimi slikami.
- Posodobitev entitet in repozitorijev: **0.5 ure**
   -**Getterji in Setterji v Task entiteti**
     -Dodana getPicture in setPicture v Task entiteti
- Posodobitev `TaskController`: **1 ura**
  -**Dodan default.jpg v TaskController**
   -v primeru če ni zbrana nobena slika
- Posodobitev uporabniškega vmesnika: **4 ure**
    - Dodan obrazec za nalaganje slik.
    - Prikaz slik pri ustvarjenih nalogah.
- Posodobitev API povezav (`server.js`): **3 ure**
    - Dodani endpointi za nalaganje in prikaz slik.
- Testiranje vseh funkcionalnosti: **2 uri**
  
**Naslednji korak:**  
- Upravljanje slik na strežniku


**V teku:** 
-

**Preostale naloge:**
- Upravljanje slik na strežniku.

---

**Zaključek:**
Backend del je uspešno implementiran, vključno s podatkovno bazo, entitetami in kontrolerji.   
Naslednji korak je posodobitev uporabniškega vmesnika in testiranje celotnega sistema.

Frontend del je uspešno implementiran. Omogočeno je nalaganje slik in ogled slik pred kreacijo taska.
Slika se prikaže ob tasku, z dodajanjem novega endpointa v server.js.

Izvedeno je bilo testiranje funkcionalnosti, ki vključuje:
Enostavne teste: ustvarjanje nalog, posodabljanje podatkov nalog in nalaganje slik.
Mejne primere: preverjanje ravnanja s privzetimi slikami, neveljavnimi podatki in praznimi datotekami.
Testiranje API endpointov: preverjanje vseh pomembnih tokov, vključno s preverjanjem avtorizacije uporabnikov.
Testiranje je bilo izvedeno za preverjanje vseh osnovnih in mejnih primerov ter za zagotovitev pravilnega delovanja integracij med backendom in frontendom. 

