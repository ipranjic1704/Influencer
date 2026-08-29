package hr.algebra.influencer.Controller;

import hr.algebra.influencer.App;
import hr.algebra.influencer.DataAccessLayer.Implementation.GradRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.InfluencerRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.NisaRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.PlatformaRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.TipSadrzajaRepozitorij;
import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Model.Grad;
import hr.algebra.influencer.Model.Influencer;
import hr.algebra.influencer.Model.Nisa;
import hr.algebra.influencer.Model.Platforma;
import hr.algebra.influencer.Model.TipSadrzaja;
import hr.algebra.influencer.Utilization.AlertUtil;
import hr.algebra.influencer.Utilization.AppLogger;
import hr.algebra.influencer.Utilization.AssetUtil;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class InfluencerController implements Initializable
{

    @FXML
    private TableView<Influencer> tablica;
    @FXML
    private Label statistikaLabel;
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
    private TableColumn<Influencer, String> niseColumn;
    @FXML
    private TableColumn<Influencer, String> tipoviSadrzajaColumn;
    @FXML
    private TextField pretragaField;
    @FXML
    private ComboBox<Nisa> nisaFilterComboBox;
    @FXML
    private ComboBox<Platforma> platformaFilterComboBox;
    @FXML
    private TextField minPratiteljaFilterField;
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
    private FlowPane niseFlowPane;
    @FXML
    private FlowPane tipoviSadrzajaFlowPane;
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
    private final NisaRepozitorij nisaRepozitorij = NisaRepozitorij.getInstance();
    private final TipSadrzajaRepozitorij tipSadrzajaRepozitorij = TipSadrzajaRepozitorij.getInstance();
    private final GradRepozitorij gradRepozitorij = GradRepozitorij.getInstance();

    private final ObservableList<Influencer> sviInfluenceri = FXCollections.observableArrayList();
    private Influencer odabrani;

    private List<Platforma> svePlatforme = new ArrayList<>();
    private final Map<Integer, CheckBox> platformaCheckboxovi = new LinkedHashMap<>();
    private List<Nisa> sveNise = new ArrayList<>();
    private final Map<Integer, CheckBox> nisaCheckboxovi = new LinkedHashMap<>();
    private List<TipSadrzaja> sviTipovi = new ArrayList<>();
    private final Map<Integer, CheckBox> tipSadrzajaCheckboxovi = new LinkedHashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
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
        niseColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getNise().stream()
                        .map(Nisa::getNaziv)
                        .collect(Collectors.joining(", "))));
        tipoviSadrzajaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getTipoviSadrzaja().stream()
                        .map(TipSadrzaja::getNaziv)
                        .collect(Collectors.joining(", "))));

        svePlatforme = platformaRepozitorij.getAll();
        for (Platforma platforma : svePlatforme)
        {
            CheckBox checkBox = new CheckBox(platforma.getNaziv());
            platformaCheckboxovi.put(platforma.getId(), checkBox);
            platformeFlowPane.getChildren().add(checkBox);
        }

        sveNise = nisaRepozitorij.getAll();
        for (Nisa nisa : sveNise)
        {
            CheckBox checkBox = new CheckBox(nisa.getNaziv());
            nisaCheckboxovi.put(nisa.getId(), checkBox);
            niseFlowPane.getChildren().add(checkBox);
        }

        sviTipovi = tipSadrzajaRepozitorij.getAll();
        for (TipSadrzaja tip : sviTipovi)
        {
            CheckBox checkBox = new CheckBox(tip.getNaziv());
            tipSadrzajaCheckboxovi.put(tip.getId(), checkBox);
            tipoviSadrzajaFlowPane.getChildren().add(checkBox);
        }

        gradComboBox.setItems(FXCollections.observableArrayList(gradRepozitorij.getAll()));

        nisaFilterComboBox.setItems(FXCollections.observableArrayList(sveNise));
        platformaFilterComboBox.setItems(FXCollections.observableArrayList(svePlatforme));

        pretragaField.textProperty().addListener((obs, staro, novo) -> filtriraj());
        nisaFilterComboBox.valueProperty().addListener((obs, staro, novo) -> filtriraj());
        platformaFilterComboBox.valueProperty().addListener((obs, staro, novo) -> filtriraj());
        minPratiteljaFilterField.textProperty().addListener((obs, staro, novo) -> filtriraj());
        tablica.getSelectionModel().selectedItemProperty().addListener((obs, staro, novo) -> odaberi(novo));

        detaljiButton.setDisable(true);

        if (!Session.isAdmin())
        {
            imeNadimakField.setDisable(true);
            brojPratiteljaField.setDisable(true);
            engagementRateField.setDisable(true);
            zemljaField.setDisable(true);
            profilnaSlikaField.setDisable(true);
            jezikSadrzajaField.setDisable(true);
            gradComboBox.setDisable(true);
            platformeFlowPane.setDisable(true);
            niseFlowPane.setDisable(true);
            tipoviSadrzajaFlowPane.setDisable(true);
            spremiButton.setDisable(true);
            novaButton.setDisable(true);
            brisiButton.setDisable(true);
        }

        osvjezi();
    }

    private void osvjezi()
    {
        sviInfluenceri.setAll(influencerRepozitorij.getAll().stream()
                .sorted()
                .collect(Collectors.toList()));
        filtriraj();
    }

    private void filtriraj()
    {
        String tekst = pretragaField.getText();
        Nisa nisaFilter = nisaFilterComboBox.getValue();
        Platforma platformaFilter = platformaFilterComboBox.getValue();
        Integer minPratitelja = parsirajMinPratitelja();

        ObservableList<Influencer> rezultat = sviInfluenceri.stream()
                .filter(i -> tekst == null || tekst.isBlank() || i.getImeNadimak().toLowerCase().contains(tekst.toLowerCase()))
                .filter(i -> nisaFilter == null || i.getNise().contains(nisaFilter))
                .filter(i -> platformaFilter == null || i.getPlatforme().contains(platformaFilter))
                .filter(i -> minPratitelja == null || i.getBrojPratitelja() >= minPratitelja)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        tablica.setItems(rezultat);
        prikaziStatistiku(rezultat);
    }

    private void prikaziStatistiku(List<Influencer> influenceri)
    {
        if (influenceri.isEmpty())
        {
            statistikaLabel.setText("Nema rezultata.");
            return;
        }

        long brojRazlicitihZemalja = influenceri.stream()
                .map(Influencer::getZemlja)
                .distinct()
                .count();

        int maxPratitelja = influenceri.stream()
                .mapToInt(Influencer::getBrojPratitelja)
                .max()
                .orElse(0);

        int minPratitelja = influenceri.stream()
                .mapToInt(Influencer::getBrojPratitelja)
                .min()
                .orElse(0);

        boolean imaMegaInfluencera = influenceri.stream()
                .anyMatch(i -> i.getBrojPratitelja() >= 1_000_000);

        boolean sviImajuIspravanEngagement = influenceri.stream()
                .noneMatch(i -> i.getEngagementRate() < 0);

//        String drugiPoPratiteljima = influenceri.stream()
//                .sorted(Comparator.comparingInt(Influencer::getBrojPratitelja).reversed())
//                .skip(1)
//                .findFirst()
//                .map(Influencer::getImeNadimak)
//                .orElse("-");


        String preporuceni = influenceri.stream()
                .sorted(Comparator.comparingInt(Influencer::getBrojPratitelja).reversed())
                .skip(3)
                .sorted(Comparator.comparingDouble(Influencer::getEngagementRate).reversed())
                .limit(3)
                .map(Influencer::getImeNadimak)
                .collect(Collectors.joining(", "));


        statistikaLabel.setText(String.format(
                "Prikazano: %d · Zemalja: %d · Pratitelji: %d-%d · Mega influencer (≥1M): %s · Engagement u redu: %s · Preporučeno (van TOP 3, najbolji engagement): %s",
                influenceri.size(), brojRazlicitihZemalja, minPratitelja, maxPratitelja,
                imaMegaInfluencera ? "da" : "ne", sviImajuIspravanEngagement ? "da" : "ne",
                preporuceni.isBlank() ? "-" : preporuceni));
    }

    private Integer parsirajMinPratitelja()
    {
        String tekst = minPratiteljaFilterField.getText();
        if (tekst == null || tekst.isBlank())
        {
            return null;
        }
        try
        {
            return Integer.parseInt(tekst.trim());
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    @FXML
    private void handleOcistiFiltere()
    {
        pretragaField.clear();
        nisaFilterComboBox.getSelectionModel().clearSelection();
        platformaFilterComboBox.getSelectionModel().clearSelection();
        minPratiteljaFilterField.clear();
    }

    private void odaberi(Influencer influencer)
    {
        odabrani = influencer;

        imeNadimakField.setText(influencer == null ? "" : influencer.getImeNadimak());
        brojPratiteljaField.setText(influencer == null ? "" : String.valueOf(influencer.getBrojPratitelja()));
        engagementRateField.setText(influencer == null ? "" : String.valueOf(influencer.getEngagementRate()));
        zemljaField.setText(influencer == null ? "" : influencer.getZemlja());
        profilnaSlikaField.setText(influencer == null ? "" : influencer.getProfilnaSlika());
        gradComboBox.setValue(influencer == null ? null : influencer.getGrad());
        jezikSadrzajaField.setText(influencer == null ? "" : influencer.getJezikSadrzaja());

        Consumer<CheckBox> odznaci = checkBox -> checkBox.setSelected(false);
        platformaCheckboxovi.values().forEach(odznaci);
        nisaCheckboxovi.values().forEach(odznaci);
        tipSadrzajaCheckboxovi.values().forEach(odznaci);

        /*
        platformaCheckboxovi.values().forEach(checkBox -> checkBox.setSelected(false));
        nisaCheckboxovi.values().forEach(checkBox -> checkBox.setSelected(false));
        tipSadrzajaCheckboxovi.values().forEach(checkBox -> checkBox.setSelected(false));
        */

        if (influencer != null)
        {
            for (Platforma platforma : influencer.getPlatforme())
            {
                CheckBox checkBox = platformaCheckboxovi.get(platforma.getId());
                if (checkBox != null)
                {
                    checkBox.setSelected(true);
                }
            }
            for (Nisa nisa : influencer.getNise())
            {
                CheckBox checkBox = nisaCheckboxovi.get(nisa.getId());
                if (checkBox != null)
                {
                    checkBox.setSelected(true);
                }
            }
            for (TipSadrzaja tip : influencer.getTipoviSadrzaja())
            {
                CheckBox checkBox = tipSadrzajaCheckboxovi.get(tip.getId());
                if (checkBox != null)
                {
                    checkBox.setSelected(true);
                }
            }
        }

        spremiButton.setText(influencer == null ? "Dodaj" : "Spremi");
        detaljiButton.setDisable(influencer == null);
    }

    @FXML
    private void handleSpremi()
    {
        String imeNadimak = imeNadimakField.getText().trim();
        if (imeNadimak.isEmpty())
        {
            AlertUtil.showWarning("Provjera", "Ime/nadimak influencera je obavezan.");
            return;
        }

        int brojPratitelja;
        try
        {
            brojPratitelja = Integer.parseInt(brojPratiteljaField.getText().trim());
        }
        catch (NumberFormatException e)
        {
            AlertUtil.showWarning("Provjera", "Broj pratitelja mora biti cijeli broj (npr. 15000).");
            return;
        }

        double engagementRate;
        try
        {
            engagementRate = Double.parseDouble(engagementRateField.getText().trim().replace(',', '.'));
        }
        catch (NumberFormatException e)
        {
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
        List<Nisa> odabraneNise = sveNise.stream()
                .filter(nisa -> nisaCheckboxovi.get(nisa.getId()).isSelected())
                .collect(Collectors.toList());
        List<TipSadrzaja> odabraniTipovi = sviTipovi.stream()
                .filter(tip -> tipSadrzajaCheckboxovi.get(tip.getId()).isSelected())
                .collect(Collectors.toList());

        try
        {
            if (odabrani == null)
            {
                Influencer noviInfluencer = new Influencer(imeNadimak, brojPratitelja, engagementRate, zemlja, grad, jezikSadrzaja, profilnaSlika);
                if (influencerRepozitorij.isDuplicate(Influencer::getImeNadimak, noviInfluencer))
                {
                    AlertUtil.showWarning("Provjera", "Influencer '" + imeNadimak + "' vec postoji.");
                    return;
                }
                noviInfluencer.setPlatforme(odabranePlatforme);
                noviInfluencer.setNise(odabraneNise);
                noviInfluencer.setTipoviSadrzaja(odabraniTipovi);
                influencerRepozitorij.create(noviInfluencer);
                AppLogger.info("Kreiran influencer: " + imeNadimak);
            }
            else
            {
                String staraSlika = odabrani.getProfilnaSlika();

                odabrani.setImeNadimak(imeNadimak);
                odabrani.setBrojPratitelja(brojPratitelja);
                odabrani.setEngagementRate(engagementRate);
                odabrani.setZemlja(zemlja);
                odabrani.setGrad(grad);
                odabrani.setJezikSadrzaja(jezikSadrzaja);
                odabrani.setProfilnaSlika(profilnaSlika);
                odabrani.setPlatforme(odabranePlatforme);
                odabrani.setNise(odabraneNise);
                odabrani.setTipoviSadrzaja(odabraniTipovi);
                influencerRepozitorij.update(odabrani);
                AppLogger.info("Azuriran influencer: " + imeNadimak);

                if (!profilnaSlika.equals(staraSlika))
                {
                    AssetUtil.obrisiSliku(staraSlika);
                }
            }
        }
        catch (RepoException e)
        {
            AppLogger.greska("Greska pri spremanju influencera: " + imeNadimak, e);
            AlertUtil.showError("Greska", "Influencera nije moguce spremiti.");
            return;
        }

        tablica.getSelectionModel().clearSelection();
        osvjezi();
    }

    @FXML
    private void handleDetalji()
    {
        Influencer odabraniZaDetalje = tablica.getSelectionModel().getSelectedItem();
        if (odabraniZaDetalje == null)
        {
            AlertUtil.showWarning("Detalji", "Prvo odaberite influencera iz tablice.");
            return;
        }

        AppLogger.info("Pregled detalja influencera: " + odabraniZaDetalje.getImeNadimak());

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
    private void handleNova()
    {
        tablica.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleBrisi()
    {
        if (odabrani == null)
        {
            AlertUtil.showWarning("Brisanje", "Prvo odaberite influencera iz tablice.");
            return;
        }
        try
        {
            influencerRepozitorij.delete(odabrani.getId());
            AppLogger.info("Obrisan influencer: " + odabrani.getImeNadimak());
            AssetUtil.obrisiSliku(odabrani.getProfilnaSlika());
        }
        catch (RepoException e)
        {
            AppLogger.greska("Greska pri brisanju influencera: " + odabrani.getImeNadimak(), e);
            AlertUtil.showError("Greska", "Influencera nije moguce obrisati.");
            return;
        }
        tablica.getSelectionModel().clearSelection();
        osvjezi();
    }

    @FXML
    private void handleOdaberiSliku()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Odaberi sliku influencera iz assets direktorija");
        fileChooser.setInitialDirectory(AssetUtil.getAssetsDir().toFile());
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Slike", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        Stage stage = (Stage) profilnaSlikaField.getScene().getWindow();
        File odabranaDatoteka = fileChooser.showOpenDialog(stage);
        if (odabranaDatoteka == null)
        {
            return;
        }

        profilnaSlikaField.setText(odabranaDatoteka.toURI().toString());
    }

    @FXML
    private void handleOsvjezi()
    {
        handleOcistiFiltere();
        osvjezi();
    }
}
