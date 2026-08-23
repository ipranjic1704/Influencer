package hr.algebra.influencer.Controller;

import hr.algebra.influencer.DataAccessLayer.Implementation.NisaRepozitorij;
import hr.algebra.influencer.Model.Nisa;
import hr.algebra.influencer.Utilization.AlertUtil;
import hr.algebra.influencer.Utilization.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

// Controller za sifrarnik nisa (nisa.fxml). Lista, dodavanje i uredjivanje su u jednom
// kontroleru i jednom ekranu - odabir retka u tablici puni formu (uredjivanje), a prazna
// forma znaci dodavanje novog retka. Dodaj/Uredi/Obrisi smiju samo ADMIN.
public class NisaController implements Initializable {

    @FXML
    private TableView<Nisa> tablica;
    @FXML
    private TableColumn<Nisa, Integer> idColumn;
    @FXML
    private TableColumn<Nisa, String> nazivColumn;
    @FXML
    private TextField pretragaField;
    @FXML
    private TextField nazivField;
    @FXML
    private Button spremiButton;
    @FXML
    private Button novaButton;
    @FXML
    private Button brisiButton;

    private final NisaRepozitorij nisaRepozitorij = NisaRepozitorij.getInstance();

    private final ObservableList<Nisa> sveNise = FXCollections.observableArrayList();
    private Nisa odabrana;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nazivColumn.setCellValueFactory(new PropertyValueFactory<>("naziv"));

        pretragaField.textProperty().addListener((obs, staro, novo) -> filtriraj(novo));
        tablica.getSelectionModel().selectedItemProperty().addListener((obs, staro, novo) -> odaberi(novo));

        if (!Session.isAdmin()) {
            nazivField.setDisable(true);
            spremiButton.setDisable(true);
            novaButton.setDisable(true);
            brisiButton.setDisable(true);
        }

        osvjezi();
    }

    // Sortiranje (padajuce po nazivu) je odgovornost Nisa.compareTo() - kontroler ga samo koristi.
    private void osvjezi() {
        sveNise.setAll(nisaRepozitorij.getAll().stream()
                .sorted()
                .collect(Collectors.toList()));
        filtriraj(pretragaField.getText());
    }

    private void filtriraj(String tekst) {
        if (tekst == null || tekst.isBlank()) {
            tablica.setItems(sveNise);
            return;
        }
        String trazeno = tekst.toLowerCase();
        ObservableList<Nisa> rezultat = sveNise.stream()
                .filter(n -> n.getNaziv().toLowerCase().contains(trazeno))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        tablica.setItems(rezultat);
    }

    // Puni formu odabranim retkom (uredjivanje) ili je prazni (novi unos) ako je odabir ponisten.
    private void odaberi(Nisa nisa) {
        odabrana = nisa;
        nazivField.setText(nisa == null ? "" : nisa.getNaziv());
        spremiButton.setText(nisa == null ? "Dodaj" : "Spremi");
    }

    @FXML
    private void handleSpremi() {
        String naziv = nazivField.getText().trim();
        if (naziv.isEmpty()) {
            AlertUtil.showWarning("Provjera", "Naziv niše je obavezan.");
            return;
        }

        if (odabrana == null) {
            Nisa novaNisa = new Nisa(naziv);
            if (nisaRepozitorij.isDuplicate(Nisa::getNaziv, novaNisa)) {
                AlertUtil.showWarning("Provjera", "Niša '" + naziv + "' vec postoji.");
                return;
            }
            nisaRepozitorij.create(novaNisa);
        } else {
            odabrana.setNaziv(naziv);
            nisaRepozitorij.update(odabrana);
        }

        tablica.getSelectionModel().clearSelection();
        osvjezi();
    }

    @FXML
    private void handleNova() {
        tablica.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleBrisi() {
        if (odabrana == null) {
            AlertUtil.showWarning("Brisanje", "Prvo odaberite nišu iz tablice.");
            return;
        }
        nisaRepozitorij.delete(odabrana.getId());
        tablica.getSelectionModel().clearSelection();
        osvjezi();
    }

    @FXML
    private void handleOsvjezi() {
        osvjezi();
    }
}
