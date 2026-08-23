package hr.algebra.influencer.Controller;

import hr.algebra.influencer.App;
import hr.algebra.influencer.DataAccessLayer.Implementation.GradRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.InfluencerRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.PlatformaRepozitorij;
import hr.algebra.influencer.Model.Grad;
import hr.algebra.influencer.Model.Influencer;
import hr.algebra.influencer.Model.Platforma;
import hr.algebra.influencer.Utilization.AlertUtil;
import hr.algebra.influencer.Utilization.SceneUtil;
import hr.algebra.influencer.Utilization.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

// Controller za influencere (influencer.fxml). Lista, dodavanje, uredjivanje i upravljanje
// platformama su u jednom kontroleru i jednom ekranu - odabir retka u tablici puni formu
// (uredjivanje), a prazna forma znaci dodavanje novog retka. Dodaj/Uredi/Obrisi smiju samo ADMIN.
// Grad se bira iz vec pripremljenog sifrarnika (uvezenog preko "Admin alati" na glavnom ekranu).
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
    private TableColumn<Influencer, Grad> gradColumn;
    @FXML
    private TableColumn<Influencer, Integer> brojPratiteljaColumn;
    @FXML
    private TableColumn<Influencer, Double> engagementRateColumn;
    @FXML
    private TableColumn<Influencer, String> platformeColumn;
    @FXML
    private TextField pretragaField;
    @FXML
    private TextField imeNadimakField;
    @FXML
    private TextField brojPratiteljaField;
    @FXML
    private TextField engagementRateField;
    @FXML
    private TextField zemljaField;
    @FXML
    private TextField profilnaSlikaField;
    @FXML
    private TextField jezikSadrzajaField;
    @FXML
    private ComboBox<Grad> gradComboBox;
    @FXML
    private FlowPane platformeFlowPane;
    @FXML
    private Button spremiButton;
    @FXML
    private Button novaButton;
    @FXML
    private Button brisiButton;
    @FXML
    private Button detaljiButton;

    private final InfluencerRepozitorij influencerRepozitorij = InfluencerRepozitorij.getInstance();
    private final PlatformaRepozitorij platformaRepozitorij = PlatformaRepozitorij.getInstance();
    private final GradRepozitorij gradRepozitorij = GradRepozitorij.getInstance();

    private final ObservableList<Influencer> sviInfluenceri = FXCollections.observableArrayList();
    private Influencer odabrani;

    private List<Platforma> svePlatforme = new ArrayList<>();
    // Jedan CheckBox po platformi (kljuc = IDPlatforma), prikazani u platformeFlowPane.
    private final Map<Integer, CheckBox> platformaCheckboxovi = new LinkedHashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        imeNadimakColumn.setCellValueFactory(new PropertyValueFactory<>("imeNadimak"));
        zemljaColumn.setCellValueFactory(new PropertyValueFactory<>("zemlja"));
        gradColumn.setCellValueFactory(new PropertyValueFactory<>("grad"));
        brojPratiteljaColumn.setCellValueFactory(new PropertyValueFactory<>("brojPratitelja"));
        engagementRateColumn.setCellValueFactory(new PropertyValueFactory<>("engagementRate"));
        platformeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPlatforme().stream()
                        .map(Platforma::getNaziv)
                        .collect(Collectors.joining(", "))));

        svePlatforme = platformaRepozitorij.getAll();
        for (Platforma platforma : svePlatforme) {
            CheckBox checkBox = new CheckBox(platforma.getNaziv());
            platformaCheckboxovi.put(platforma.getId(), checkBox);
            platformeFlowPane.getChildren().add(checkBox);
        }

        // Grad dolazi iz sifrarnika (uvezenog preko "Admin alati" -> "Uvezi gradove").
        gradComboBox.setItems(FXCollections.observableArrayList(gradRepozitorij.getAll()));

        pretragaField.textProperty().addListener((obs, staro, novo) -> filtriraj(novo));
        tablica.getSelectionModel().selectedItemProperty().addListener((obs, staro, novo) -> odaberi(novo));

        // Detalji su read-only pregled (ukljucujuci profilnu sliku) - dostupni svim ulogama, ne
        // samo adminu, pa namjerno NIJE u !isAdmin() bloku ispod. Ukljucen tek kad je redak odabran.
        detaljiButton.setDisable(true);

        if (!Session.isAdmin()) {
            imeNadimakField.setDisable(true);
            brojPratiteljaField.setDisable(true);
            engagementRateField.setDisable(true);
            zemljaField.setDisable(true);
            profilnaSlikaField.setDisable(true);
            jezikSadrzajaField.setDisable(true);
            gradComboBox.setDisable(true);
            platformeFlowPane.setDisable(true);
            spremiButton.setDisable(true);
            novaButton.setDisable(true);
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

    // Puni formu odabranim retkom (uredjivanje) ili je prazni (novi unos) ako je odabir ponisten.
    private void odaberi(Influencer influencer) {
        odabrani = influencer;

        imeNadimakField.setText(influencer == null ? "" : influencer.getImeNadimak());
        brojPratiteljaField.setText(influencer == null ? "" : String.valueOf(influencer.getBrojPratitelja()));
        engagementRateField.setText(influencer == null ? "" : String.valueOf(influencer.getEngagementRate()));
        zemljaField.setText(influencer == null ? "" : influencer.getZemlja());
        profilnaSlikaField.setText(influencer == null ? "" : influencer.getProfilnaSlika());
        gradComboBox.setValue(influencer == null ? null : influencer.getGrad());
        jezikSadrzajaField.setText(influencer == null ? "" : influencer.getJezikSadrzaja());

        platformaCheckboxovi.values().forEach(checkBox -> checkBox.setSelected(false));
        if (influencer != null) {
            for (Platforma platforma : influencer.getPlatforme()) {
                CheckBox checkBox = platformaCheckboxovi.get(platforma.getId());
                if (checkBox != null) {
                    checkBox.setSelected(true);
                }
            }
        }

        spremiButton.setText(influencer == null ? "Dodaj" : "Spremi");
        detaljiButton.setDisable(influencer == null);
    }

    @FXML
    private void handleSpremi() {
        String imeNadimak = imeNadimakField.getText().trim();
        if (imeNadimak.isEmpty()) {
            AlertUtil.showWarning("Provjera", "Ime/nadimak influencera je obavezan.");
            return;
        }

        int brojPratitelja;
        try {
            brojPratitelja = Integer.parseInt(brojPratiteljaField.getText().trim());
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Provjera", "Broj pratitelja mora biti cijeli broj (npr. 15000).");
            return;
        }

        double engagementRate;
        try {
            // replace(',', '.') podrzava europski decimalni zarez (npr. "3,5" -> "3.5").
            engagementRate = Double.parseDouble(engagementRateField.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Provjera", "Engagement rate mora biti broj (npr. 3.5).");
            return;
        }

        String zemlja = zemljaField.getText().trim();
        Grad grad = gradComboBox.getValue();
        String jezikSadrzaja = jezikSadrzajaField.getText().trim();
        String profilnaSlika = profilnaSlikaField.getText().trim();
        List<Platforma> odabranePlatforme = svePlatforme.stream()
                .filter(platforma -> platformaCheckboxovi.get(platforma.getId()).isSelected())
                .collect(Collectors.toList());

        if (odabrani == null) {
            Influencer noviInfluencer = new Influencer(imeNadimak, brojPratitelja, engagementRate, zemlja, grad, jezikSadrzaja, profilnaSlika);
            if (influencerRepozitorij.isDuplicate(Influencer::getImeNadimak, noviInfluencer)) {
                AlertUtil.showWarning("Provjera", "Influencer '" + imeNadimak + "' vec postoji.");
                return;
            }
            noviInfluencer.setPlatforme(odabranePlatforme);
            influencerRepozitorij.create(noviInfluencer);
        } else {
            odabrani.setImeNadimak(imeNadimak);
            odabrani.setBrojPratitelja(brojPratitelja);
            odabrani.setEngagementRate(engagementRate);
            odabrani.setZemlja(zemlja);
            odabrani.setGrad(grad);
            odabrani.setJezikSadrzaja(jezikSadrzaja);
            odabrani.setProfilnaSlika(profilnaSlika);
            odabrani.setPlatforme(odabranePlatforme);
            influencerRepozitorij.update(odabrani);
        }

        tablica.getSelectionModel().clearSelection();
        osvjezi();
    }

    // Otvara read-only prozor s detaljima odabranog influencera, ukljucujuci profilnu sliku
    // (InfluencerDetaljiController.ucitajSliku). Dostupno svim ulogama - vidi napomenu uz detaljiButton.
    @FXML
    private void handleDetalji() {
        Influencer odabraniZaDetalje = tablica.getSelectionModel().getSelectedItem();
        if (odabraniZaDetalje == null) {
            AlertUtil.showWarning("Detalji", "Prvo odaberite influencera iz tablice.");
            return;
        }

        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(tablica.getScene().getWindow());
        FXMLLoader loader = SceneUtil.loadSceneWithLoader(
                App.class.getResource("fxml/Influencer/influencer-detalji.fxml"), stage, "Detalji influencera");
        InfluencerDetaljiController kontroler = loader.getController();
        kontroler.setInfluencer(odabraniZaDetalje);
        stage.showAndWait();
    }

    @FXML
    private void handleNova() {
        tablica.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleBrisi() {
        if (odabrani == null) {
            AlertUtil.showWarning("Brisanje", "Prvo odaberite influencera iz tablice.");
            return;
        }
        influencerRepozitorij.delete(odabrani.getId());
        tablica.getSelectionModel().clearSelection();
        osvjezi();
    }

    @FXML
    private void handleOsvjezi() {
        osvjezi();
    }
}
