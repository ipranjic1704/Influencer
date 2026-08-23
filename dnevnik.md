# Dnevnik razvoja - Influencer

Ovo je dnevnik rada na projektnom zadatku (Tema 77: Influencer). Vodim ga kronoloski, po danima, s tehnickim detaljima odluka koje sam donosio tijekom izrade.

---

## 17.8.2026.

### Tema i specifikacija

Dobio sam Temu 77 - aplikacija za upravljanje influencerima. Definirao sam entitete:

- **Influencer** - glavni entitet: ime/nadimak, broj pratitelja, prosjecan engagement rate, zemlja, jezik sadrzaja, profilna slika.
- **Platforma** - sifrarnik (Instagram, TikTok, YouTube, Twitch...).
- **Nisa** - sifrarnik (beauty, fitness, tech, lifestyle, gaming...).
- **TipSadrzaja** - sifrarnik (reels, shorts, recenzije, tutoriali...).
- **BrandSuradnja** - marketinska kampanja, s timom influencera (M:N veza, ne 1:N - kampanja je spremnik vise influencera, ne vlasnistvo jednog).

Dodatne znacajke koje planiram: pretraga influencera po nisi/platformi/broju pratitelja, drag & drop influencera u tim marketinske kampanje, XML export profila influencera (s platformama i suradnjama).

### Odluka o bibliotekama

Odlucio sam bazirati pom.xml na minimalnom kosturu: javafx-controls, javafx-fxml, H2, JUnit - i nista vise. Drzim se cistog JavaFX-a bez dodatnih UI frameworka (Bootstrap/ControlsFX/FormsFX mi ovdje ne trebaju). Jedino sto dodajem je Jackson (jackson-databind), jer projekt treba JSON API import znacajku (async Service/Task dohvat vanjskog javnog API-ja za polje "zemlja"). Drag&drop, XML export i HTTP poziv ne trebaju dodatne dependencyje - sve pokrivaju built-in JDK/JavaFX klase (Dragboard/TransferMode, javax.xml.stream, java.net.http).

### Arhitektura

Odlucio sam projekt napraviti modularnim: `module-info.java`, apstraktna nadklasa u modelu, eager singleton repozitoriji. Nazivlje sam posložio ovako: paket `Model` velikim M, `Model.Enum` podpaket, sucelje `Identifiable`, generican `Repozitorij<T>` s metodama getAll/getById/create/update/delete, camelCase hrvatska imena polja.

Model sam zavrsio: `Identifiable` (sucelje), `Entitet` (apstraktna nadklasa - id, equals/hashCode po getClass()+id, apstraktna metoda `opisi()`) - ovo mi omogucava da izbjegnem ponavljanje iste logike u svakom modelu. `Platforma`/`Nisa`/`TipSadrzaja` su cisti M:N sifrarnici bez povratne liste. `BrandSuradnja` drzi `List<Influencer> tim`. `Influencer` drzi `List<Platforma>`, `List<Nisa>`, `List<TipSadrzaja>`, `List<BrandSuradnja>`.

Uveo sam 3 uloge (ne 2): `Uloga` enum = ADMIN, BREND, INFLUENCER, s konstruktorom, nazivom i toString labelom. Pravilo: BREND (i ADMIN) smiju kreirati brand suradnje i dodavati influencere u tim (`Uloga.smijeDodatiSuradnju()`); INFLUENCER se ne smije sam dodati u suradnju - to cu provjeravati u kontroleru kad budem gradio tu znacajku (`Session.smijeDodatiSuradnju()` vec postoji kao gate). Dodao sam `Korisnik.influencerId` (nullable Integer) - FK na Influencer profil, popunjen samo za ulogu INFLUENCER. Buduci da tablica Influencer jos ne postoji, FK je za sad samo obican stupac bez constrainta - pravi FK dodajem kasnije kad napravim Influencer DAL. Seed korisnici: admin/admin, brend/brend, influencer/influencer.

**Vazno pravilo koje sam sebi zapisao:** ADMIN je uvijek superset - smije apsolutno sve (brisati/urediti/dodavati bilo koji entitet, bilo koju suradnju). Kod gatinga buducih kontrolera koristim `Session.isAdmin()` / `Uloga.smijeUredivati()` (ADMIN-only), a ne `smijeDodatiSuradnju()` (ADMIN||BREND) - potonje je namjerno uze i vrijedi samo za akciju "dodaj influencera u tim kampanje".

Singleton stil kroz cijeli projekt: eager (static final polje inicijalizirano odmah pri ucitavanju klase), ne lazy.

### Prvi commit

Zavrsio sam i pushao: `App.java`, `BazaPodataka`, `DataAccessLayer.Interface.Repozitorij<T>` + `Implementation.KorisnikRepozitorij`, `Utilization` (AlertUtil/SceneUtil/Session), `Controller.LoginController`+`MainController`, `fxml/login.fxml`+`main.fxml`.

Za razmotriti kasnije: `StatusSuradnje` enum jos nema konstruktor+naziv+toString label kao ostali enumi - uskladit cu kad dodem do BrandSuradnja ekrana.

Sljedeci korak koji sam si zacrtao: DataAccessLayer (Repozitorij implementacije) za Influencer i Platforma.

---

## 19.8.2026.

### Prosirenje DDL-a

Prosirio sam `init_ddl.sql`: dodao `Influencer`, `Platforma` i spojnu tablicu `InfluencerPlatforma` (M:N, ON DELETE CASCADE u oba smjera). Influencer i Platforma sam morao definirati prije Korisnik tablice, jer H2 zahtijeva da referencirana tablica vec postoji prije REFERENCES klauzule. `Korisnik.InfluencerID` sad ima pravi FK: `REFERENCES Influencer(IDInfluencer) ON DELETE SET NULL`. Dodao sam seed podatke - 4 platforme (Instagram/TikTok/YouTube/Twitch).

Napomena sebi: lokalna `.mv.db` datoteka je gitignoreana i nastala je prije ovog FK-a, pa je trebam rucno obrisati da `CREATE TABLE IF NOT EXISTS` primijeni novu shemu (H2 nece sam migrirati postojecu bazu).

### Custom exceptioni

Odlucio sam uvesti custom exceptione umjesto golih RuntimeException bacanja. Napravio sam paket `hr.algebra.influencer.Exception` s dvije klase:

- `AppException` - checked (extends Exception), za poslovne/validacijske greske koje hvatam na mjestu poziva (kontroler).
- `RepoException` - unchecked (extends RuntimeException), za SQL/repo greske - zamjenjuje goli `RuntimeException` koji sam prije bacao.

`RepoException` sam ubacio u `KorisnikRepozitorij`, `PlatformaRepozitorij` i `BazaPodataka` staticki blok, zamijenivsi sve `throw new RuntimeException(...)`. `AppException` sam prvi put upotrijebio u `LoginController` - napravio sam privatnu metodu `provjeriPrijavu()` koja baca `AppException("Pogresno korisnicko ime ili lozinka.")` umjesto direktnog if/else, a `handleLogin()` to hvata i prikazuje kroz `AlertUtil.showError`.

Namjerno nisam mijenjao genericko sucelje `Repozitorij<T>` (create/update/delete i dalje ne bacaju checked exception) - `AppException` koristim samo na poziv-mjestu, jer bi mijenjanje sucelja zahtijevalo redizajn svih vec gotovih repozitorija bez stvarne potrebe.

### PlatformaRepozitorij

Napravio sam kao cist sifrarnik CRUD: eager singleton, SELECT_ALL/SELECT_BY_ID/INSERT/UPDATE/DELETE konstante, `mapRow()` metoda, sve SQL greske zavijene u `RepoException`.

### InfluencerRepozitorij

Ovo je glavni entitet s M:N vezom prema Platforma kroz `InfluencerPlatforma`:

- `getAll()`/`getById()` dohvate osnovne podatke preko `mapRow()`, pa posebnim upitom kroz `dohvatiPlatforme(int idInfluencer)` pune listu platformi (N+1 pristup - JOIN upit kroz spojnu tablicu).
- `create()` upisuje red, dohvati generirani kljuc, pa zove `spremiPlatforme(influencer)`.
- `update()` azurira red, pa `obrisiPlatformeVeze(influencer.getId())` + `spremiPlatforme(influencer)` - obrazac "obrisi pa ponovno upisi".
- `delete()` se oslanja na DDL cascade (InfluencerPlatforma ON DELETE CASCADE, Korisnik.InfluencerID ON DELETE SET NULL) - ne cistim veze rucno u kodu.

Sve SQL greske opet kroz `RepoException`. Nisa/TipSadrzaja/BrandSuradnja tablice i repozitoriji jos ne postoje, pa `Influencer.nise/tipoviSadrzaja/brandSuradnje` ostaju namjerno prazne liste dok ih ne izgradim.

### FXML ekrani za Influencer

Odlucio sam prvo napraviti Influencer ekrane (kompleksniji, M:N multi-select) prije Platforme (jednostavniji sifrarnik). Strukturu sam posložio ovako - per-entitet podpaket `Controller.Influencer` + `fxml/Influencer/`, svaki CRUD/relacijski ekran ima svoj kontroler i svoj FXML, novi Stage po modalu s `Modality.WINDOW_MODAL`:

- `InfluencerController` + `influencer.fxml` - lista, TableView, pretraga po imeNadimak, gumbi Dodaj/Uredi/Dodaj platformu/Obrisi platformu/Brisi (svi disable-ani ako `!Session.isAdmin()`).
- `InfluencerDodajController` + `influencer-dodaj.fxml` - kreira influencera BEZ platformi (platforme se dodaju posebno).
- `InfluencerUrediController` + `influencer-uredi.fxml` - prefill polja pa `update()`.
- `InfluencerDodajPlatformuController` + `influencer-dodaj-platformu.fxml` i `InfluencerObrisiPlatformuController` + `influencer-obrisi-platformu.fxml` - ComboBox filtriran po `influencer.getPlatforme().contains()`, mutiram listu pa zovem `update()`. Nisam pravio posebne SQL addTo/removeFrom metode u repou - InfluencerRepozitorij vec radi delete-and-reinsert cijele liste na `update()`, pa je to dovoljno i jednostavnije.

`MainController`/`main.fxml` su dobili gumb "Influenceri (count)" koji otvara `influencer.fxml` u novom prozoru (`stage.show()`, ne modalno - lista je glavni prozor tog entiteta).

Vazna tehnicka stvar koju sam morao zapamtiti: buduci da je projekt modularan, `module-info.java` treba eksplicitan `opens`/`exports` za svaki novi paket s FXML kontrolerima - to se ne nasljeduje od roditeljskog paketa. Dodao sam `opens hr.algebra.influencer.Controller.Influencer to javafx.fxml;` i odgovarajuci `exports`, inace FXMLLoader baca `IllegalAccessException` u runtimeu (compile prolazi cisto i bez toga, pa je lako previdjeti).

### Provjera

Pokrenuo sam `mvn javafx:run` - log pokazuje "Shema inicijalizirana", "veza stvorena", BUILD SUCCESS, bez ijedne iznimke. Ovo potvrduje da nova shema (Influencer/Platforma/InfluencerPlatforma/FK) i module-info promjene rade na startu. Preostaje mi jos vizualno proci kroz ekrane (prijava admin/admin -> gumb "Influenceri" -> Dodaj/Uredi/Dodaj platformu/Obrisi platformu/Obrisi) da potvrdim da svi FXML-ovi stvarno rade u praksi.

### Stanje na kraju dana

Sve navedeno (custom exceptioni, PlatformaRepozitorij, InfluencerRepozitorij, Influencer FXML ekrani) je gotovo i spremno za commit. Jos trebam vizualno testirati ekrane prije/nakon commita.

Sljedeci koraci koje razmatram: FXML ekrani za Platformu (sifrarnik jos nema svoj lista/dodaj/uredi ekran iako repo postoji), zatim Nisa/TipSadrzaja/BrandSuradnja (tablice+repozitoriji+ekrani), te trazenje javnog API-ja za polje "zemlja" (Jackson dependency vec pripremljen za to).

---

## 20.8.2026.

### Platforma, Nisa, TipSadrzaja, BrandSuradnja

Nastavio sam prosirivati bazu i sloj podataka. Dodao sam u `init_ddl.sql`: tablice `Nisa`, `TipSadrzaja`, `BrandSuradnja`, te spojne tablice `InfluencerNisa`, `InfluencerTipSadrzaja` i `BrandSuradnjaInfluencer` (sve M:N, ON DELETE CASCADE). Dodao sam seed podatke - 5 nisa (Beauty/Fitness/Tech/Lifestyle/Gaming) i 4 tipa sadrzaja (Reels/Shorts/Recenzije/Tutoriali).

Napravio sam `NisaRepozitorij` i `TipSadrzajaRepozitorij` po istom sifrarnik-obrascu kao `PlatformaRepozitorij` (eager singleton, SELECT/INSERT/UPDATE/DELETE konstante, `mapRow()`, `RepoException`).

`BrandSuradnjaRepozitorij` je slozeniji - `BrandSuradnja` drzi tim influencera (M:N kroz `BrandSuradnjaInfluencer`), pa sam primijenio isti "delete and re-insert" pristup kao za platforme kod influencera: `dohvatiTim()` (JOIN upit), `spremiTim()`/`obrisiTimVeze()` na create/update. `delete()` se oslanja na DDL cascade.

Prosirio sam `InfluencerRepozitorij` da uz platforme puni i `nise`/`tipoveSadrzaja` (isti N+1 pristup, dvije nove spojne tablice).

Napravio sam prve FXML ekrane za Platformu: `PlatformaController` (lista + pretraga + Dodaj/Uredi/Obrisi gumbi), `PlatformaDodajController` i `PlatformaUrediController` (odvojeni modalni prozori za dodavanje i uredjivanje) - isti obrazac kao Influencer ekrani. Dodao sam gumb "Platforme (count)" u `MainController`/`main.fxml`. `module-info.java` dobio `opens/exports Controller.Platforma`.

---

## 21.8.2026.

### Nisa i TipSadrzaja ekrani, pa refaktor svih šifrarnika

Krenuo sam graditi FXML ekrane za Nisu i TipSadrzaja po uzoru na Platformu (odvojeni Lista/Dodaj/Uredi kontroleri). Dok sam radio, primijetio sam da mi previse raste broj gotovo identicnih datoteka - tri kontrolera i tri FXML-a po svakom sifrarniku, za tri sifrarnika koji rade potpuno isto.

Odlucio sam refaktorirati: umjesto tri kontrolera po entitetu (lista, dodaj, uredi - svaki svoj modalni prozor), sad svaki sifrarnik ima JEDAN kontroler i JEDAN ekran. Tablica i forma za naziv su na istom ekranu - odabir retka u tablici puni formu i mijenja gumb u "Spremi" (uredjivanje), a prazna forma s gumbom "Dodaj" znaci novi unos. Gumb "Novo" ponistava odabir i prazni formu.

Ovo sam primijenio na sva tri sifrarnika radi dosljednosti - `PlatformaController`, `NisaController`, `TipSadrzajaController` sad imaju identicnu strukturu (jedan `Repozitorij<T>`, `ObservableList`, `odabrana`/`odabrani` polje koje prati trenutni redak, `odaberi()` metoda koja puni formu). Maknuo sam `PlatformaDodajController`/`PlatformaUrediController` i odgovarajuce FXML-ove - vise nisu potrebni. Dodao sam gumbe za Nisu i TipSadrzaja u `MainController`/`main.fxml`, te `opens/exports Controller.Nisa` i `Controller.TipSadrzaja` u `module-info.java`.

Provjerio sam da sve kompajlira i da se `mvn javafx:run` diže bez iznimki (shema, konekcija, BUILD SUCCESS).

BrandSuradnja jos nema svoj ekran (repo postoji, treba i tim/drag&drop UI) - to je sljedece na redu, zajedno s async dohvatom "zemlja" polja.

---

## 23.8.2026.

### Ravna struktura paketa za kontrolere

Primijetio sam da mi paket `Controller` postaje razbacan - svaki entitet je imao svoj podpaket (`Controller.Influencer`, `Controller.Platforma`, `Controller.Nisa`, `Controller.TipSadrzaja`) iako svaki podpaket sadrzi samo jedan ili dva kontrolera. Odlucio sam sve kontrolere izmjestiti izravno u `Controller` paket, bez per-entitet podpaketa - `LoginController`, `MainController`, `InfluencerController`, `PlatformaController`, `NisaController`, `TipSadrzajaController` su sad svi u istom, ravnom paketu. FXML datoteke sam ostavio organizirane po entitetu (`fxml/Influencer/`, `fxml/Platforma/`...) jer je to samo organizacija resursa, ne utjece na Java pakete.

Pritom sam ispravio `fx:controller` referencu u `nisa.fxml` koja je jos pokazivala na stari (obrisani) podpaket - ostala je neusklađena od ranije izmjene. Uskladio sam i `platforma.fxml`/`tipsadrzaja.fxml` s novim ravnim paketom. `module-info.java` sam pojednostavio na jedan `opens`/`exports` blok za `Controller` (vise ne treba zaseban blok po podpaketu).

### Spajanje Influencer kontrolera

Influencer ekran je jos bio na starom obrascu - pet odvojenih kontrolera (`InfluencerController`, `InfluencerDodajController`, `InfluencerUrediController`, `InfluencerDodajPlatformuController`, `InfluencerObrisiPlatformuController`), svaki sa svojim modalnim FXML-om. Spojio sam ih u jedan `InfluencerController` po istom obrascu koji vec koristim za sifrarnike - lista, dodavanje, uredjivanje i upravljanje platformama su sad na jednom ekranu (`influencer.fxml`). Odabir retka u tablici puni formu (sva polja - ime/nadimak, broj pratitelja, engagement rate, zemlja, jezik sadrzaja, profilna slika) i selektira influencerove platforme u `ListView` s visestrukim odabirom; prazna forma i prazan odabir platformi znaci novi unos.

Platforme sam rijesio jednim `ListView<Platforma>` (`SelectionMode.MULTIPLE`) umjesto dva odvojena modalna ekrana (dodaj/obrisi platformu) - odabrane stavke u listi izravno postaju `influencer.setPlatforme(...)` prije `create()`/`update()`, sto se oslanja na "obrisi pa ponovno upisi" strategiju koju `InfluencerRepozitorij` vec ima. Obrisao sam sve pet starih kontrolera i pripadajuce FXML-ove (dodaj/uredi/dodaj-platformu/obrisi-platformu).

Provjerio sam da sve kompajlira cisto i da se `mvn javafx:run` diže bez iznimki nakon svih ovih promjena.

### Popravak biranja platformi

Kad sam sam isprobao ekran, primijetio sam dva propusta: tablica influencera nigdje ne pokazuje koje platforme influencer koristi, a `ListView` s visestrukim odabirom je bio nezgodan za koristenje (klik na jednu platformu ponisti odabir ostalih, treba Ctrl+klik). Dodao sam stupac "Platforme" u tablicu (spojeni nazivi odvojeni zarezom) i zamijenio `SelectionMode.MULTIPLE` pristup s `CheckBoxListCell` - svaka platforma sad ima svoju kvacicu (`BooleanProperty` po platformi, mapirano preko ID-a), pa je vizualno jasno sto je dodijeljeno.

### Polje "Grad" i async dohvat s API-ja

Dodao sam novo polje `grad` (String) influenceru - odvojeno od postojece "Zemlja" (koja ostaje slobodan tekst). Grad se popunjava preko javnog CountriesNow API-ja: upisem naziv zemlje u posebno polje za pretragu, kliknem "Dohvati gradove", i ComboBox za grad se async napuni popisom gradova te zemlje. Napravio sam `GradoviService` (`javafx.concurrent.Service<List<String>>`) koji u `Task.call()` salje POST zahtjev (`java.net.http.HttpClient`) i parsira JSON odgovor Jacksonom (`ObjectMapper.readTree`, citam polje "data") - Service radi u pozadinskoj dretvi pa ne blokira UI dok traje HTTP poziv, isti obrazac koji sam vec planirao za ovu vrstu znacajke. ComboBox za grad je editable, tako da moze prikazati i vec spremljeni grad influencera i ako trenutno nije u dohvacenoj listi.

Dodao sam stupac `Grad` u DDL (`Influencer` tablica), prosirio `Influencer` model (konstruktor, getter/setter), `InfluencerRepozitorij` (SELECT/INSERT/UPDATE/mapRow) i `BrandSuradnjaRepozitorij.dohvatiTim()` (i on gradi `Influencer` objekte pa je trebao isti novi parametar). `module-info.java` je dobio `requires java.net.http` i `requires com.fasterxml.jackson.databind` - prva upotreba oboje u ovom projektu.

Vazna napomena samom sebi: buduci da je lokalna `InfluencerDB.mv.db` vec postojala prije ove izmjene DDL-a, `CREATE TABLE IF NOT EXISTS` je nece sam izmijeniti - moram obrisati lokalnu `.mv.db` datoteku da se nova `Grad` kolona stvarno primijeni (isti slucaj kao kod prvog dodavanja FK-a).

### Redizajn ekrana influencera

Kad sam probao prvu verziju ekrana, nisu mi se svidjeli ni raspored forme ni nacin biranja platformi/gradova, pa sam sve preradio. Platforme sad prikazujem kao `FlowPane` s po jednim `CheckBox`-om za svaku platformu (umjesto liste s kvacicama) - vizualno pregledniji "tag" prikaz. Formu sam podijelio na jasno odvojene, oznacene cjeline (Podaci o influenceru / Grad / Platforme, razdvojene `Separator`-ima), a gumb "Dodaj/Spremi" premjestio u donju traku uz Novo/Obrisi.

Za dohvat gradova sam rastavio kod na vise manjih, jasno odvojenih dijelova umjesto jedne velike klase koja sve radi: `HttpUtil` (opcenit GET/POST HTTP poziv, u `Utilization` paketu), `JsonParserUtil` (parsira JSON odgovor s API-ja i izvlaci popis gradova, isto u `Utilization`), `DohvatGradovaTask` (u novom `Task` paketu - sadrzi stvarnu logiku dohvata: gradi HTTP zahtjev, zove `HttpUtil`, parsira preko `JsonParserUtil`) i `DohvatGradovaService` (u novom `Service` paketu - tanki omotac koji Task pokrece na pozadinskoj dretvi). Funkcija je ostala ista kao prije (upisem zemlju, kliknem gumb, ComboBox se async napuni gradovima), samo je kod sad citljiviji i podijeljen po odgovornosti - svaka klasa radi tocno jednu stvar.

Dodao sam i stupac "Grad" u tablicu influencera (uz "Platforme"), da se odmah vidi u listi.

### Grad postaje pravi šifrarnik, jezik sadržaja se uvozi s API-ja

Kad sam sve ovo isprobao, odlucio sam da mi se ovakav pristup gradu ipak ne svidja - previse je "rucno" da svaki influencer zasebno zove API. Umjesto toga sam napravio da Grad postane svoj sifrarnik, isto kao Platforma/Nisa/TipSadrzaja: nova tablica `Grad` (mora biti definirana PRIJE `Influencer` tablice u DDL-u jer `Influencer.GradID` na nju referencira kao FK, `ON DELETE SET NULL` da brisanje grada ne obrise influencere), `GradRepozitorij` (identican CRUD obrazac kao `PlatformaRepozitorij`), `GradController`+`grad.fxml` (isti jedan-kontroler sifrarnik ekran). `Influencer.grad` sam promijenio iz `String` u pravu referencu na `Grad` objekt - `InfluencerRepozitorij` sad radi LEFT JOIN na Grad (grad je opcionalan, moze biti NULL), a `BrandSuradnjaRepozitorij.dohvatiTim()` je isto trebao isti JOIN jer i on gradi Influencer objekte.

Uvoz gradova sam prebacio iz forme influencera u glavni izbornik - dodao sam `MenuBar` u `main.fxml` s izbornikom "Admin alati" koji sadrzi stavku "Uvezi gradove". Klik pokrece `UvozGradovaTask`/`UvozGradovaService` (JavaFX Task/Service par) koji jednim pozivom dohvati SVE hrvatske gradove s CountriesNow API-ja (`/countries` endpoint, filtrirano po "Croatia") i sprema samo nove u `Grad` tablicu (preskace duplikate), uz `Alert` koji prikazuje napredak (bind na `messageProperty()`).

Dodao sam i drugu API znacajku - uvoz jezika sadrzaja. Za razliku od grada, ovo NIJE nova sifrarnik-tablica ni klasa - `jezikSadrzaja` ostaje obicno String polje na influenceru kao i prije, samo se sad bira iz popisa umjesto slobodnog upisa. Popis jezika dohvacam preko REST Countries API-ja (`restcountries.com/v3.1/all?fields=languages` - vraca sve drzave svijeta s njihovim sluzbenim jezicima), izvucem jedinstvene nazive jezika iz svih drzava i spremim ih u memoriju (staticka lista u `Session`, ne u bazu - dovoljno je da postoji dok app radi). I ovo je dostupno kao stavka "Uvezi jezike sadrzaja" u istom "Admin alati" izborniku. Buduci da nije trebala nova klasa, samo sam dodao jednu metodu u `JsonParserUtil` (parsira "languages" polje iz JSON odgovora) i dva getter/setter u `Session`.

U formi influencera su "Grad" i "Jezik sadrzaja" sad ComboBox polja (Grad iz sifrarnika, Jezik editable ComboBox iz Session predmemorije) - maknuo sam stari red s "Zemlja za pretragu gradova" i gumbom, vise nije potreban jer se uvoz radi jednom, unaprijed, iz izbornika.

### Sortiranje promijenjeno na uzlazno

Svi `compareTo()` u modelima (Influencer, Platforma, Nisa, TipSadrzaja, Grad, Korisnik, BrandSuradnja) su do sad vracali padajuci (Z-A) poredak. Promijenio sam sve na uzlazni (A-Z) - jednostavna izmjena u svakoj klasi (`naziv.compareToIgnoreCase(other.naziv)` umjesto obrnuto), ali dosljedno kroz cijeli projekt jer se svi popisi (tablice, ComboBoxovi) oslanjaju bas na `compareTo()` za sortiranje.

### Uvoz jezika ipak nije radio - REST Countries API je ugasen

Kad sam probao uvoz jezika, nije radio. Istrazio sam - `restcountries.com` v3.1 API (kojeg sam koristio) je u medjuvremenu potpuno ugasen; stara adresa sad vraca gresku "deprecated", a nova verzija (`api.restcountries.com`) trazi API kljuc i registraciju, sto mi ne odgovara (htio sam ostati bez kljuca). Nasao sam zamjenu: otvoreni skup podataka `mledoze/countries` na GitHubu (`raw.githubusercontent.com/mledoze/countries/master/countries.json`) - to je isti skup podataka na kojem se stari restcountries API i temeljio, pa ima IDENTICAN oblik polja "languages" ({"kod": "Naziv"}). Zamijenio sam samo URL u `MainController.handleUveziJezike()` - `JsonParserUtil.parseJezike()` uopce nisam morao mijenjati jer je oblik JSON-a isti.

### Jezik sadrzaja ipak natrag na obican unos

Ni nakon popravka URL-a uvoz jezika nije proradio. Umjesto daljnjeg debugiranja treceg vanjskog servisa, odlucio sam da za ovo polje jednostavno nije vrijedno truda - vratio sam `jezikSadrzaja` na obican `TextField` sa slobodnim unosom, kao sto je i bio prije nego sam uopce pokusao s API-jem. Maknuo sam cijelu granu koda vezanu uz to: stavku "Uvezi jezike sadrzaja" iz "Admin alati" izbornika, `MainController.handleUveziJezike()`, `JsonParserUtil.parseJezike()` i pomocno polje u `Session`. Grad ostaje kako jest (sifrarnik + uvoz s API-ja, to radi ispravno) - samo je Jezik sadrzaja taj koji se vratio na najjednostavnije moguce rjesenje.

### "Obriši sve" admin alat

Dodao sam jos jednu stavku u "Admin alati" izbornik - "Obriši sve influencere". Trajno brise sve retke iz Influencer tablice (veze s platformama/nisama/tipovima sadrzaja/brand suradnjama nestaju same preko ON DELETE CASCADE, definiranog vec u DDL-u). Sifrarnici (Platforma/Nisa/TipSadrzaja/Grad) i korisnicki racuni ostaju netaknuti - ovo je namjerno usko, brise samo influencere. Prije brisanja iskace potvrdni `Alert` (CONFIRMATION) s brojem influencera koji ce biti obrisani - klik izvan/Cancel prekida radnju bez ikakve promjene.

### Detalji s prikazom profilne slike

Polje "Profilna slika" je do sad bilo samo tekstualni zapis URL-a - nigdje se stvarna slika nije prikazivala. Dodao sam gumb "Detalji..." na ekranu influencera koji se aktivira tek kad je redak u tablici odabran, i otvara novi modalni prozor (`InfluencerDetaljiController`+`influencer-detalji.fxml`) s pregledom svih podataka - ukljucujuci `ImageView` koji stvarno ucitava i prikazuje sliku s upisanog URL-a.

Ucitavanje slike je asinkrono (`new Image(url, true)` - drugi parametar `true` znaci background loading, da HTTP dohvat slike ne blokira UI dretvu). Ako polje nema upisan URL, ili se slika ne uspije ucitati (nevaljan link, slika ne postoji), umjesto slomljene slicice prikazujem tekstualnu zamjenu "Nema slike" (`Image.errorProperty()` listener prebacuje vidljivost izmedju `ImageView`-a i tog labela).

Detalji su namjerno read-only i dostupni svim ulogama (ne samo adminu, za razliku od uredjivanja) - to je samo pregled podataka, nema poslovnog razloga to ograniciti.

### Brand kao sifrarnik + BrandSuradnja ekran s drag & dropom

Otkrio sam (dok sam gledao BrandSuradnja model) da "brand" u kampanji nikad nije bio svoj entitet - `nazivBrenda` je bio obicno String polje, slobodan upis bez ikakve zastite od nedosljednog unosa (npr. "Nike"/"nike" kao dva razlicita zapisa). Napravio sam `Brand` sifrarnik (isti obrazac kao Platforma/Grad - tablica, repozitorij, `BrandController`+`brand.fxml`), a `BrandSuradnja.brand` sad je prava referenca (FK `BrandID`, NOT NULL - suradnja bez brenda nema smisla) umjesto stringa.

Napokon sam napravio i sam `BrandSuradnja` ekran, koji je od 20.8. cekao (repo je postojao, UI nije). Forma ima uobicajena polja (naziv kampanje, brand, godina, status), a tim kampanje se sastavlja **drag & dropom**: influencer se povuce iz liste "Dostupni influenceri" u listu "Tim kampanje" (Dragboard nosi samo ID influencera kao String, stvarni objekt se pronadje natrag u dostupnoj listi preko `stream().filter().findFirst()`). Uklanjanje iz tima ide preko obicnog gumba (drag natrag nije trazen). Usput sam dotjerao `StatusSuradnje` enum da ima konstruktor+naziv+`toString()` kao `Uloga` - to je bila stara stavka koju sam sebi zapisao jos 17.8. i nikad nisam vratio.

Dodao sam i gumbe "Brendovi" i "Brand suradnje" na glavni ekran.

### Nedostajala Nisa i TipSadrzaja veza na influenceru

Primijetio sam (zapravo, upozoren) da influencer ekran nikad nije dobio nacin biranja Nise/TipaSadrzaja - kad sam u sesiji 21.8. spajao pet starih Influencer kontrolera u jedan, prenio sam samo Platforme (jer je to bila jedina veza koja je vec imala UI u starom obrascu), a Nisa/TipSadrzaja veze (iako ih `InfluencerRepozitorij` vec cita/sprema od 20.8.) nikad nisu dobile svoj dio forme. Popravio sam - dodao sam `FlowPane`+`CheckBox` sekcije za Nisu i TipSadrzaja, identicno kako Platforme vec rade, plus dva nova stupca u tablici.

### Registracija - Brend ili Influencer

Primijetio sam da INFLUENCER uloga zapravo nema nikakvu kontrolu ni nad cim - `Korisnik.influencerId` (koji povezuje INFLUENCER korisnika s njegovim profilom) nikad nije bio postavljen, cak ni na seed podacima. Dok sam istrazivao, otkrio sam i da tablice (Brandovi, Brand suradnje...) zapravo VEC bile vidljive svim ulogama - samo je uredjivanje bilo zakljucano - pa "citanje" nije trebalo popravljati.

Ono sto je stvarno nedostajalo je registracija - aplikacija je do sad imala samo prijavu, admin racun i dva demo racuna (brend/influencer) su sjedili u DDL seedu, bez naceina da se itko novi sam registrira. Dodao sam ekran za registraciju: korisnik bira zeli li se registrirati kao Brend ili Influencer (Administrator se ne moze samoregistrirati - postoji od pocetka preko inicijalizacijske skripte). Ako odabere Influencer, mora upisati i ime/nadimak - odmah mu se stvara Influencer profil, a `Korisnik.influencerId` ga povezuje s tim profilom. Nakon uspjesne registracije korisnik se odmah prijavljuje (bez potrebe da ponovno upisuje podatke na login ekranu).

### Uklonjeni svi komentari iz koda

Odlucio sam ukloniti sve komentare iz Java koda - kod neka govori sam za sebe kroz imena klasa/metoda/varijabli. Ovo se odnosi na sve datoteke (Model, Controller, DataAccessLayer, Utilization, Task, Service) i vrijedi i za sav buduci kod koji cu pisati. FXML ostaje nepromijenjen.

### Maknuti brojaci s glavnog ekrana

Gumbi na glavnom ekranu ("Influenceri", "Platforme"...) su do sad pokazivali broj zapisa u zagradi (npr. "Influenceri (5)"). Maknuo sam to - gumbi sad prikazuju samo naziv entiteta, bez brojanja. Time su i repozitoriji koji su sluzili samo za brojanje (Platforma/Nisa/TipSadrzaja/Grad) postali nepotrebni u `MainController` - ostao je samo `InfluencerRepozitorij` (koristi ga "Obrisi sve" alat).
