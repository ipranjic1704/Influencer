package hr.algebra.influencer.Controller.Influencer;

import hr.algebra.influencer.App;
import hr.algebra.influencer.DataAccessLayer.Implementation.InfluencerRepozitorij;
import hr.algebra.influencer.Model.Influencer;
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

public class InfluencerController implements Initializable {

    @FXML
    private TableView<Influencer> tablica;
    @FXML
    private TableColumn<Influencer, Integer> idColumn;
    @FXML
    private TableColumn<Influencer, String> imeNadimakColumn;
    @FXML
    private TableColumn<Influencer, String> zemljaColumn;
    @FXML
    private TableColumn<Influencer, Integer> brojPratiteljaColumn;
    @FXML
    private TableColumn<Influencer, Double> engagementRateColumn;
    @FXML
    private TextField pretragaField;
    @FXML
    private Button dodajButton;
    @FXML
    private Button urediButton;
    @FXML
    private Button dodajPlatformuButton;
    @FXML
    private Button obrisiPlatformuButton;
    @FXML
    private Button brisiButton;

    private final InfluencerRepozitorij influencerRepozitorij = InfluencerRepozitorij.getInstance();

    // Drzim sve ucitane influencere ovdje; tablica prikazuje njih ili njihov filtrirani podskup.
    private final ObservableList<Influencer> sviInfluenceri = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        imeNadimakColumn.setCellValueFactory(new PropertyValueFactory<>("imeNadimak"));
        zemljaColumn.setCellValueFactory(new PropertyValueFactory<>("zemlja"));
        brojPratiteljaColumn.setCellValueFactory(new PropertyValueFactory<>("brojPratitelja"));
        engagementRateColumn.setCellValueFactory(new PropertyValueFactory<>("engagementRate"));

        pretragaField.textProperty().addListener((obs, staro, novo) -> filtriraj(novo));

        if (!Session.isAdmin()) {
            dodajButton.setDisable(true);
            urediButton.setDisable(true);
            dodajPlatformuButton.setDisable(true);
            obrisiPlatformuButton.setDisable(true);
            brisiButton.setDisable(true);
        }

        osvjezi();
    }

    // Sortiranje (padajuce po imenu) je odgovornost Influencer.compareTo() - kontroler ga samo koristi.
    private void osvjezi() {
        sviInfluenceri.setAll(influencerRepozitorij.getAll().stream()
                .sorted()
                .collect(Collectors.toList()));
        filtriraj(pretragaField.getText());
    }

    private void filtriraj(String tekst) {
        if (tekst == null || tekst.isBlank()) {
            tablica.setItems(sviInfluenceri);
            return;
        }
        String trazeno = tekst.toLowerCase();
        ObservableList<Influencer> rezultat = sviInfluenceri.stream()
                .filter(i -> i.getImeNadimak().toLowerCase().contains(trazeno))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        tablica.setItems(rezultat);
    }

    @FXML
    private void handleDodaj() {
        Stage stage = noviModal("Novi influencer");
        SceneUtil.loadScene(App.class.getResource("fxml/Influencer/influencer-dodaj.fxml"), stage, "Novi influencer");
        stage.showAndWait();
        osvjezi();
    }

    @FXML
    private void handleUredi() {
        Influencer odabrani = tablica.getSelectionModel().getSelectedItem();
        if (odabrani == null) {
            AlertUtil.showWarning("Uredjivanje", "Prvo odaberite influencera iz tablice.");
            return;
        }

        Stage stage = noviModal("Uredi influencera");
        FXMLLoader loader = SceneUtil.loadSceneWithLoader(
                App.class.getResource("fxml/Influencer/influencer-uredi.fxml"), stage, "Uredi influencera");
        InfluencerUrediController kontroler = loader.getController();
        kontroler.setInfluencer(odabrani);
        stage.showAndWait();
        osvjezi();
    }

    @FXML
    private void handleDodajPlatformu() {
        Influencer odabrani = tablica.getSelectionModel().getSelectedItem();
        if (odabrani == null) {
            AlertUtil.showWarning("Platforme", "Prvo odaberite influencera iz tablice.");
            return;
        }

        Stage stage = noviModal("Dodaj platformu");
        FXMLLoader loader = SceneUtil.loadSceneWithLoader(
                App.class.getResource("fxml/Influencer/influencer-dodaj-platformu.fxml"), stage, "Dodaj platformu");
        InfluencerDodajPlatformuController kontroler = loader.getController();
        kontroler.setInfluencer(odabrani);
        stage.showAndWait();
        osvjezi();
    }

    @FXML
    private void handleObrisiPlatformu() {
        Influencer odabrani = tablica.getSelectionModel().getSelectedItem();
        if (odabrani == null) {
            AlertUtil.showWarning("Platforme", "Prvo odaberite influencera iz tablice.");
            return;
        }

        Stage stage = noviModal("Ukloni platformu");
        FXMLLoader loader = SceneUtil.loadSceneWithLoader(
                App.class.getResource("fxml/Influencer/influencer-obrisi-platformu.fxml"), stage, "Ukloni platformu");
        InfluencerObrisiPlatformuController kontroler = loader.getController();
        kontroler.setInfluencer(odabrani);
        stage.showAndWait();
        osvjezi();
    }

    @FXML
    private void handleBrisi() {
        Influencer odabrani = tablica.getSelectionModel().getSelectedItem();
        if (odabrani == null) {
            AlertUtil.showWarning("Brisanje", "Prvo odaberite influencera iz tablice.");
            return;
        }
        influencerRepozitorij.delete(odabrani.getId());
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
