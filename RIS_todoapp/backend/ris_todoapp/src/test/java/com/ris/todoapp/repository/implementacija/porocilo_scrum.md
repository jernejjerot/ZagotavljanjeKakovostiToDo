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

| Naloga                                     | Status       | Opombe                        |
|-------------------------------------------|--------------|--------------------------------|
| Prilagoditev podatkovne baze              | Done         |  ////////////////////////////  |
| Posodobitev entitet in repozitorijev      | Doing        |                                |
| Posodobitev `TaskController`              | ToDo         |                                |
| Posodobitev uporabniškega vmesnika        | ToDo         |                                |
| Posodobitev API povezav (`server.js`)     | ToDo         |                                |
| Upravljanje slik na strežniku             | ToDo         |                                |
| Testiranje funkcionalnosti                | ToDo         |                                |

---

## Ovire in Izzivi


---

## Sprint Skupni Čas
**Zaključene naloge:**
Prilagoditev podatkovne baze 0.5 ur
- **Primeri testnih podatkov dodani**:
  - Vstavljeni primeri nalog z različnimi slikami.
  
**Naslednji korak:**  
- Implementacija Java entitete `Task` s poljem `slike`.
- Posodobitev `TaskController` za nalaganje in pridobivanje slik.


**V teku:** 
-posodobitev Task entitet in Kontrolerjev

**Preostale naloge:** 
*

---

**Zaključek:**
### Sprint Napredek (datum:2024-12-07)
- **Dodajanje stolpca `slike` v tabelo `tasks`**: Zaključeno 
  - Nova struktura tabele `tasks` vključuje:
    - `pictures`: Pot do naložene slike.
