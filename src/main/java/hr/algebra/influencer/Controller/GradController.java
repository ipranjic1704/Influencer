package hr.algebra.influencer.Controller;

import hr.algebra.influencer.DataAccessLayer.Implementation.GradRepozitorij;
import hr.algebra.influencer.Model.Grad;
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

// Controller za sifrarnik gradova (grad.fxml). Lista, dodavanje i uredjivanje su u jednom
// kontroleru i jednom ekranu - odabir retka u tablici puni formu (uredjivanje), a prazna
// forma znaci dodavanje novog retka. Dodaj/Uredi/Obrisi smiju samo ADMIN. Gradovi se najcesce
// pune preko "Uvezi gradove" u glavnom izborniku (MainController.handleUveziGradove), ali se
// mogu i rucno dodati/urediti/obrisati ovdje.
public class GradController implements Initializable {

    @FXML
    private TableView<Grad> tablica;
    @FXML
    private TableColumn<Grad, Integer> idColumn;
    @FXML
    private TableColumn<Grad, String> nazivColumn;
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

    private final GradRepozitorij gradRepozitorij = GradRepozitorij.getInstance();

    private final ObservableList<Grad> sviGradovi = FXCollections.observableArrayList();
    private Grad odabrani;

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

    private void osvjezi() {
        sviGradovi.setAll(gradRepozitorij.getAll().stream()
                .sorted()
                .collect(Collectors.toList()));
        filtriraj(pretragaField.getText());
    }

    private void filtriraj(String tekst) {
        if (tekst == null || tekst.isBlank()) {
            tablica.setItems(sviGradovi);
            return;
        }
        String trazeno = tekst.toLowerCase();
        ObservableList<Grad> rezultat = sviGradovi.stream()
                .filter(g -> g.getNaziv().toLowerCase().contains(trazeno))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        tablica.setItems(rezultat);
    }

    private void odaberi(Grad grad) {
        odabrani = grad;
        nazivField.setText(grad == null ? "" : grad.getNaziv());
        spremiButton.setText(grad == null ? "Dodaj" : "Spremi");
    }

    @FXML
    private void handleSpremi() {
        String naziv = nazivField.getText().trim();
        if (naziv.isEmpty()) {
            AlertUtil.showWarning("Provjera", "Naziv grada je obavezan.");
            return;
        }

        if (odabrani == null) {
            Grad noviGrad = new Grad(naziv);
            if (gradRepozitorij.isDuplicate(Grad::getNaziv, noviGrad)) {
                AlertUtil.showWarning("Provjera", "Grad '" + naziv + "' vec postoji.");
                return;
            }
            gradRepozitorij.create(noviGrad);
        } else {
            odabrani.setNaziv(naziv);
            gradRepozitorij.update(odabrani);
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
        if (odabrani == null) {
            AlertUtil.showWarning("Brisanje", "Prvo odaberite grad iz tablice.");
            return;
        }
        gradRepozitorij.delete(odabrani.getId());
        tablica.getSelectionModel().clearSelection();
        osvjezi();
    }

    @FXML
    private void handleOsvjezi() {
        osvjezi();
    }
}
