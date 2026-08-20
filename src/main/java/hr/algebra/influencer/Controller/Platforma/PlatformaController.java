package hr.algebra.influencer.Controller.Platforma;

import hr.algebra.influencer.App;
import hr.algebra.influencer.DataAccessLayer.Implementation.PlatformaRepozitorij;
import hr.algebra.influencer.Model.Platforma;
import hr.algebra.influencer.Utilization.AlertUtil;
import hr.algebra.influencer.Utilization.SceneUtil;
import hr.algebra.influencer.Utilization.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

// Controller za sifrarnik platformi (platforma.fxml). Dodaj/Uredi/Obrisi smiju samo ADMIN -
// vidljivo je svima (i BREND i INFLUENCER vide popis platformi), ali gumbi za izmjenu su onemoguceni.
public class PlatformaController implements Initializable {

    @FXML
    private TableView<Platforma> tablica;
    @FXML
    private TableColumn<Platforma, Integer> idColumn;
    @FXML
    private TableColumn<Platforma, String> nazivColumn;
    @FXML
    private TextField pretragaField;
    @FXML
    private Button dodajButton;
    @FXML
    private Button urediButton;
    @FXML
    private Button brisiButton;

    private final PlatformaRepozitorij platformaRepozitorij = PlatformaRepozitorij.getInstance();

    private final ObservableList<Platforma> svePlatforme = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nazivColumn.setCellValueFactory(new PropertyValueFactory<>("naziv"));

        pretragaField.textProperty().addListener((obs, staro, novo) -> filtriraj(novo));

        if (!Session.isAdmin()) {
            dodajButton.setDisable(true);
            urediButton.setDisable(true);
            brisiButton.setDisable(true);
        }

        osvjezi();
    }

    // Sortiranje (padajuce po nazivu) je odgovornost Platforma.compareTo() - kontroler ga samo koristi.
    private void osvjezi() {
        svePlatforme.setAll(platformaRepozitorij.getAll().stream()
                .sorted()
                .collect(Collectors.toList()));
        filtriraj(pretragaField.getText());
    }

    private void filtriraj(String tekst) {
        if (tekst == null || tekst.isBlank()) {
            tablica.setItems(svePlatforme);
            return;
        }
        String trazeno = tekst.toLowerCase();
        ObservableList<Platforma> rezultat = svePlatforme.stream()
                .filter(p -> p.getNaziv().toLowerCase().contains(trazeno))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        tablica.setItems(rezultat);
    }

    @FXML
    private void handleDodaj() {
        Stage stage = noviModal("Nova platforma");
        SceneUtil.loadScene(App.class.getResource("fxml/Platforma/platforma-dodaj.fxml"), stage, "Nova platforma");
        stage.showAndWait();
        osvjezi();
    }

    @FXML
    private void handleUredi() {
        Platforma odabrana = tablica.getSelectionModel().getSelectedItem();
        if (odabrana == null) {
            AlertUtil.showWarning("Uredjivanje", "Prvo odaberite platformu iz tablice.");
            return;
        }

        Stage stage = noviModal("Uredi platformu");
        FXMLLoader loader = SceneUtil.loadSceneWithLoader(
                App.class.getResource("fxml/Platforma/platforma-uredi.fxml"), stage, "Uredi platformu");
        PlatformaUrediController kontroler = loader.getController();
        kontroler.setPlatforma(odabrana);
        stage.showAndWait();
        osvjezi();
    }

    @FXML
    private void handleBrisi() {
        Platforma odabrana = tablica.getSelectionModel().getSelectedItem();
        if (odabrana == null) {
            AlertUtil.showWarning("Brisanje", "Prvo odaberite platformu iz tablice.");
            return;
        }
        platformaRepozitorij.delete(odabrana.getId());
        osvjezi();
    }

    @FXML
    private void handleOsvjezi() {
        osvjezi();
    }

    private Stage noviModal(String naslov) {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(tablica.getScene().getWindow());
        stage.setTitle(naslov);
        return stage;
    }
}
