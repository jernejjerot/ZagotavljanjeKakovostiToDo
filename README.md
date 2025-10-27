  To-Do App

To-Do aplikacija je zasnovana za učinkovito upravljanje nalog in obveznosti, ki omogoča prijavo uporabnikov, ustvarjanje, urejanje, brisanje nalog ter pregledovanje vseh obstoječih nalog z informacijami o rokih. Aplikacija cilja na izboljšanje produktivnosti in organiziranosti uporabnikov.

   Vizija

Naša vizija za To-Do aplikacijo je ponuditi mladim in odraslim uporabnikom rešitev, ki jim bo pomagala pri učinkovitem obvladovanju vsakodnevnih obveznosti ter doseganju večje organiziranosti in produktivnosti. Aplikacija bo uporabnikom omogočila enostavno načrtovanje in upravljanje šolskih, službenih in domačih obveznosti na enem mestu, pri čemer bodo obveščeni o prihajajočih rokih in odgovornostih. S tem želimo zmanjšati stres, povezan z usklajevanjem številnih nalog, in uporabnikom omogočiti, da se osredotočijo na pomembnejše cilje, pri čemer jim bodo sprotni opomniki in preglednost omogočili pravočasno opravljanje vseh aktivnosti. Za svoje odgovornosti bodo uporabniki lahko uredili datum in uro, prav tako podali kratki opis, kaj bo potrebno postoriti. Prav tako bo možno nastaviti opomnik, ki bo uporabnika obvestil o bližanju roka obveznosti.


   Struktura projekta

    1. Backend (Java Spring Boot)
- Kontrolerji:
  - TaskController: Upravljanje nalog (ustvarjanje, brisanje, posodobitev).
  - UserController: Upravljanje uporabnikov (prijava, ustvarjanje).
  - ResponsibilityController: Upravljanje vrst odgovornosti za naloge.
- Repositoryji (Repositories):
  - TaskRepository, UserRepository, ResponsibilityRepository: Uporaba Spring Data JPA za interakcijo z bazo podatkov.
- Entitete:
  - Task, User, Responsibility: Reprezentacija nalog, uporabnikov in njihovih vrst odgovornosti.
- Viri:
  - application.properties: Konfiguracija aplikacije, vključno z nastavitvami baze podatkov.

 2. Frontend (HTML, CSS, JavaScript)
- HTML in CSS: 
  - index.html: Osnovni uporabniški vmesnik za dodajanje in upravljanje nalog.
  - styles.css: Stilizacija vmesnika.
- JavaScript:
  - server.js: Proxy strežnik za komunikacijo z backendom, omogoča posredovanje zahtevkov z Node.js Express in Axios.
  - Glavni funkcionalnosti vključujejo prijavo uporabnikov, dodajanje in urejanje nalog ter brisanje nalog.


Navodila za namestitev

Zahteve
- Java 17: Potreben za zagon Spring Boot strežnika.
- Maven: Gradbeni sistem za upravljanje odvisnosti.
- Node.js v20.14.0: Za zagon frontend proxy strežnika.

Koraki za namestitev

1. Klonirajte repozitorij
   ```
     bash
     git clone https://github.com/JCvkl1/ToDoApp.git
     cd ToDoApp
   ```

3. Nastavite podatkovno bazo
   - **Ustvarite podatkovno bazo v MySQL**:
    
    ```
    CREATE DATABASE ime_baze;
    ```

    - **Posodobite datoteko "application.properties" v IntelliJ**:

    ```
    spring.datasource.url=jdbc:mysql://localhost:[port]/ime_baze
    spring.datasource.username=tvoje_ime
    spring.datasource.password=tvoje_geslo
    ```
    
     - **Preverite konfiguracijo baze podatkov v application.properties, ki se nahaja v	src/main/resources/.**
     - **Baza podatkov mora biti konfigurirana za uporabo MySQL verzije 8.4. Poskrbite, da je MySQL strežnik aktiven in da je baza pravilno konfigurirana.** 

4. Zagon backend strežnika
   mvn spring-boot:run
   Backend bo tekel na http://localhost:8080

5. Zagon frontend proxy strežnika
   - Premaknite se v mapo "frontend".
    ```
       bash
       cd frontend
       npm install
       node server.js
    ```
   - Frontend proxy strežnik bo tekel na http://localhost:3000.

6. Dostop do aplikacije
   - Odprite brskalnik in obiščite http://localhost:3000 za uporabo aplikacije.

Navodila za razvijalce

Prispevanje

1. Forkanje repozitorija in ustvaritev nove veje za svoje spremembe.
2. Upoštevajte spodnje standarde kodiranja.
3. Po končanih spremembah ustvarite PULL Request.

Standardi Kodiranja

1. Java (Backend)
   - Uporabite MVC (Model-View-Controller) arhitekturo.
   - Vzdržujte čisto in modularno kodo v ločenih slojih (Controllers, Services, Repositories).
   - Dokumentirajte metode in API endpoint-e za boljšo berljivost.

2. JavaScript (Frontend) 
   - Uporabite Axios za vse HTTP klice k backend API-ju.
   - Pisanje funkcij za ponovno uporabo in modularnost.
   - Proxy strežnik (server.js) omogoča komunikacijo s Spring Boot API-jem.

Orodja in frameworki

1.   Backend
   - Java Spring Boot 2.x
   - Spring Data JPA za ORM in dostop do podatkov.

2.   Frontend
   - HTML, CSS, JavaScript
   - Axios za enostavno izvajanje HTTP zahtevkov.
   - Express.js za proxy strežnik.

3.   Podatkovna baza
   - MySQL (Konfiguracija v application.properties datoteki).

Besednjak

 Uporabnik: Oseba, ki uporablja aplikacijo za dodajanje, urejanje, pregled in brisanje nalog. Uporabniki se lahko prijavijo in upravljajo svoje naloge.

 Naloga: Glavna enota v aplikaciji, ki predstavlja opravilo ali obveznost, ki jo mora uporabnik izvesti. Vsaka naloga lahko vsebuje ime, opis, rok (datum in čas) ter vrsto odgovornosti.

 Opis naloge: Dodatne informacije o nalogi, ki uporabniku omogočajo boljše razumevanje, kaj mora biti opravljeno.

 Rok naloge: Datum in čas, do katerega naj bi bila naloga izvedena. Rok se uporablja tudi za generiranje opomnikov.

 Odgovornost: Kategorija, ki označuje vrsto naloge, kot na primer "Šola", "Služba" ali "Domače opravilo". Omogoča uporabniku, da organizira naloge po različnih področjih življenja.

 Opomnik: Funkcionalnost, ki opozori uporabnika na bližajoč se rok naloge. Opomniki pomagajo uporabniku, da pravočasno izvede svoje naloge.

 Prijava: Proces, kjer se uporabnik identificira in pridobi dostop do svojih nalog v aplikaciji. Prijava zahteva uporabniško ime (e-mail) in geslo.

 ![Use Case Diagram for To-Do App](./ToDOApp.png)

Dodajanje opravila: Uporabnik lahko doda novo opravilo v aplikacijo. Ta funkcionalnost vključuje možnost nastavitve opomnikov za opravila.
- Pregled opravil: Uporabnik lahko pregleda vsa ustvarjena opravila, vključno s pregledom zgodovine opravljenih nalog.
- Urejanje opravil: Uporabnik lahko ureja obstoječa opravila, kar vključuje dodatne funkcionalnosti, kot so filtriranje in brisanje opravil.
- Prijava: Uporabnik se mora prijaviti, da lahko dostopa do aplikacije.
- Pregled vsebine (Admin): Skrbnik (Admin) lahko pregleda vsebino, kar mu omogoča vpogled v celoten seznam opravil vseh uporabnikov.

![Class Diagram for To-Do App](./ClassDiagram.drawio.png)


1. Uporabnik
  Vloga in namen:
  Glavni razred, ki predstavlja uporabnika sistema. Vsak uporabnik ima osnovne podatke, kot so e-poštni naslov, geslo, ime in opombe.
  Ključne metode:
  login(email: String, geslo: String): Boolean
  omogoča prijavo uporabnika v sistem z uporabo e-pošte in gesla.
  editProfile(details: Uporabnik): void
  omogoča uporabniku urejanje osebnih podatkov.
  editTask(details: Opravilo): void
  omogoča urejanje posameznih opravil, ki jih ima uporabnik.
2. Admin (Podrazred Uporabnika)
  Vloga in namen:
  Posebna vrsta uporabnika z dodatnimi privilegiji, kot je upravljanje drugih uporabnikov in pregled vseh opravil v sistemu.
  Ključne metode:
  manageUsers(users: List<Uporabnik>): void
  Omogoča dodajanje, urejanje ali brisanje uporabnikov.
  viewAllTasks(): List<Opravilo>
  Omogoča pregled vseh opravil v sistemu.
3. Opravilo
  Vloga in namen:
  Predstavlja posamezno opravilo, ki ga uporabnik lahko ustvari in upravlja. Vsebuje podatke, kot so naslov, opis, datum, prioriteta, status in odgovornost.
  Ključne metode:
  setReminder(datum: Date): void
  Nastavi opomnik za določeno opravilo.
  updateStatus(status: StatusOpravila): void
  Posodobi status opravila (npr. "NI OPRAVLJEN" ali "OPRAVLJEN").
  editTask(details: Task): void
  omogoča urejanje podrobnosti opravila.
4. Odgovornost
  Vloga in namen:
  Predstavlja odgovornost, povezano z opravilom. Vsaka odgovornost ima ime (npr. "Šola", "Služba", "Dom").
  Ključne metode:
  editOdgovornost(details: Odgovornost): void
  omogoča urejanje odgovornosti.
5. Lokacija
  Vloga in namen:
  predstavlja lokacijo, kjer se opravlja določeno opravilo. Vsebuje podatke, kot so ime lokacije, koordinate in seznam opravil, povezanih z lokacijo.
  Ključne metode:
  getLocationDetails(): String
  Vrne podrobnosti o lokaciji.
  pridobiOpravilaZaDan(datum: Date): List<Opravilo>
  pridobi vsa opravila, povezana z lokacijo, za določen datum.
  dodajOpraviloNaLokacijo(opravilo: Opravilo): void
  dodaja opravila na določeno lokacijo.
6. Opozorilo
  Vloga in namen:
  Predstavlja obvestila ali opozorila, povezana z opravilom.
  Ključne metode:
  posljiOpozorilo(opravilo: Opravilo): void
  pošlje opozorilo uporabniku, povezano z določenim opravilom.
7. <enum> StatusOpravila
  Vloga in namen:
  enum, ki določa status opravila. Možni statusi so "NI OPRAVLJEN" in "OPRAVLJEN".
8. <enum> PrioritetniNivo
  Vloga in namen:
  enum, ki določa prioriteto opravila. Možnosti so "NIZEK", "SREDNJI" in "VISOK".


## Testiranje

### **Namen testiranja**
Testi preverjajo delovanje repozitorijev v aplikaciji *ToDoApp*, ki upravljajo s podatki o uporabnikih in nalogah.  
Cilj je potrditi pravilnost osnovnih CRUD operacij ter odziv sistema na napačne ali podvojene vnose.

---

####  `testFindByUserId()`
**Namen:** Namen testa je preveriti pravilno delovanje metode findByUserId(), ki mora iz baze vrniti vse naloge (Tasks), ki pripadajo določenemu uporabniku. Funkcija mora biti sposobna poizkati vse zapise, kjer je tuji kljuš user_id enak podanemu indetifikatorju uporabnika.
**Vhod:** 
  - userId: je ID testnega uporabnika
  - podatki v bazi: dve nalogi z različnim opisom in datumom izvedbe in razlićnim statusom
    
**Pričakovano:** metoda vrne seznam z dvema nalogama, ki pripadata temu uporabniku.  
**Ugotovitev:** test uspešno potrdi pravilno povezavo med uporabnikom in nalogami.

---

####  `testFindByUserIdAndIsCompletedTrue()`
**Namen:** Preveriti delovanje metode testFindByUserIdAndIsCompletedTrue(), ki mora vrniti samo tiste naloge, ki pripradajo določenemu uporabniku in so hkrati označene kot dokončane
**Vhod:**
  - userId: ID tistega uporabnika
  - Stranje baze: ena dokončana in ena nedokončana naloga
    
**Pričakovano:** metoda vrne samo eno, dokončano nalogo.  
**Ugotovitev:** rezultat potrjuje pravilno filtriranje po atributu `isCompleted`.

---

####  `testFindByPicture()`
**Namen:** Namen testa je preveriti metodo findByPicture(), ki mora poizkati naloge na podlagi natančnega ujemanja poti slike
**Vhod:** 
  - picture: »/uploads/task1.jpg«
  - Baza vsebuje zapise z task1.jpg in task2.jpg
    
**Pričakovano:** rezultat vsebuje točno eno nalogo z ustrezno potjo slike.  
**Ugotovitev:** test potrjuje pravilno delovanje metode `findByPicture()`.

---

#### `testSave_DuplicateEmail()`
**Namen:** Preveriti, da aplikacija ne dovoli dveh uporabnikov z enakim emailom. Cilj je potrditi obstoj in delovanje unikatne omejitve na stolpcu email v tabeli user
**Vhod:** 
  - Obstoječi zapis: email = testuser@example.com,  name = »Test«, surname = »User«, admin =  false
  - Nov zapis: email = »testuser@example.com«, name = »Dup«, surname = »User«, admin = false
    
**Pričakovano:** sproži se napaka `DataIntegrityViolationException`.  
**Ugotovitev:** test pade, če v shemi baze manjka unikatna omejitev za e-mail.


--- 

## Upravljanje projekta s Kanban tabelo
Pri razvoju To-Do aplikacije smo za organizacijo dela in boljšo preglednost nalog uporabili Kanban tabelo na GitHub Projects. Kanban tabela omogoča vizualno sledenje napredka pri razvoju in razdelitev nalog na različne faze. Tabela vsebuje naslednje stolpce:

Backlog (Ideje/Načrti): Tukaj so zapisane vse naloge in ideje, ki čakajo na prioritizacijo in razvrstitev.
To Do (V čakanju): Naloge, ki so bile določene kot prioriteta in so pripravljene za izvedbo.
Doing (V teku): Naloge, na katerih ekipa trenutno aktivno dela.
Done (Dokončano): Naloge, ki so uspešno implementirane in testirane.

### Primeri nalog na Kanban tabeli:
Backlog:

Raziskava knjižnice FullCalendar.js.
Načrtovanje API točk za naloge.
Testiranje robnih primerov (npr. naloge brez datumov ali opisov).
To Do:

Integracija FullCalendar.js v datoteko calendar.html.
Pridobivanje uporabniških nalog prek API-ja.
Prikaz nalog kot dogodkov v koledarju.
Doing:

Odpravljanje napake pri inicializaciji koledarja.
Testiranje pravilne integracije nalog in koledarja.
Done:

Uspešna integracija koledarja v aplikacijo.
Naloge se dinamično dodajo v koledar ob ustvarjanju.
Vse funkcionalnosti testirane in potrjene.

### Pomen Kanban tabele
Uporaba Kanban tabele nam je omogočila jasnejšo delitev dela, spremljanje napredka in prioritetno razvrščanje nalog. Poleg tega smo lahko hitro odkrili morebitne ovire in izboljšali sodelovanje znotraj ekipe.

Za pregled celotnega projekta lahko obiščete GitHub Kanban tabelo: TODOAPP_kanban.
