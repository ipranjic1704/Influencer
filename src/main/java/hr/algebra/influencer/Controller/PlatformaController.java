package hr.algebra.influencer.Controller;

import hr.algebra.influencer.DataAccessLayer.Implementation.PlatformaRepozitorij;
import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Model.Platforma;
import hr.algebra.influencer.Utilization.AlertUtil;
import hr.algebra.influencer.Utilization.AppLogger;
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

public class PlatformaController implements Initializable
{

    @FXML
    private TableView<Platforma> tablica;
    @FXML
    private TableColumn<Platforma, Integer> idColumn;
    @FXML
    private TableColumn<Platforma, String> nazivColumn;
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

    private final PlatformaRepozitorij platformaRepozitorij = PlatformaRepozitorij.getInstance();

    private final ObservableList<Platforma> svePlatforme = FXCollections.observableArrayList();
    private Platforma odabrana;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nazivColumn.setCellValueFactory(new PropertyValueFactory<>("naziv"));

        pretragaField.textProperty().addListener((obs, staro, novo) -> filtriraj(novo));
        tablica.getSelectionModel().selectedItemProperty().addListener((obs, staro, novo) -> odaberi(novo));

        if (!Session.isAdmin())
        {
            nazivField.setDisable(true);
            spremiButton.setDisable(true);
            novaButton.setDisable(true);
            brisiButton.setDisable(true);
        }

        osvjezi();
    }

    private void osvjezi()
    {
        svePlatforme.setAll(platformaRepozitorij.getAll().stream()
                .sorted()
                .collect(Collectors.toList()));
        filtriraj(pretragaField.getText());
    }

    private void filtriraj(String tekst)
    {
        if (tekst == null || tekst.isBlank())
        {
            tablica.setItems(svePlatforme);
            return;
        }
        String trazeno = tekst.toLowerCase();
        ObservableList<Platforma> rezultat = svePlatforme.stream()
                .filter(p -> p.getNaziv().toLowerCase().contains(trazeno))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        tablica.setItems(rezultat);
    }

    private void odaberi(Platforma platforma)
    {
        odabrana = platforma;
        nazivField.setText(platforma == null ? "" : platforma.getNaziv());
        spremiButton.setText(platforma == null ? "Dodaj" : "Spremi");
    }

    @FXML
    private void handleSpremi()
    {
        String naziv = nazivField.getText().trim();
        if (naziv.isEmpty())
        {
            AlertUtil.showWarning("Provjera", "Naziv platforme je obavezan.");
            return;
        }

        try
        {
            if (odabrana == null)
            {
                Platforma novaPlatforma = new Platforma(naziv);
                if (platformaRepozitorij.isDuplicate(Platforma::getNaziv, novaPlatforma))
                {
                    AlertUtil.showWarning("Provjera", "Platforma '" + naziv + "' vec postoji.");
                    return;
                }
                platformaRepozitorij.create(novaPlatforma);
                AppLogger.info("Kreirana platforma: " + naziv);
            }
            else
            {
                odabrana.setNaziv(naziv);
                platformaRepozitorij.update(odabrana);
                AppLogger.info("Azurirana platforma: " + naziv);
            }
        }
        catch (RepoException e)
        {
            AppLogger.greska("Greska pri spremanju platforme: " + naziv, e);
            AlertUtil.showError("Greska", "Platformu nije moguce spremiti.");
            return;
        }

        tablica.getSelectionModel().clearSelection();
        osvjezi();
    }

    @FXML
    private void handleNova()
    {
        tablica.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleBrisi()
    {
        if (odabrana == null)
        {
            AlertUtil.showWarning("Brisanje", "Prvo odaberite platformu iz tablice.");
            return;
        }
        try
        {
            platformaRepozitorij.delete(odabrana.getId());
            AppLogger.info("Obrisana platforma: " + odabrana.getNaziv());
        }
        catch (RepoException e)
        {
            AppLogger.greska("Greska pri brisanju platforme: " + odabrana.getNaziv(), e);
            AlertUtil.showError("Greska", "Platformu nije moguce obrisati.");
            return;
        }
        tablica.getSelectionModel().clearSelection();
        osvjezi();
    }

    @FXML
    private void handleOsvjezi()
    {
        osvjezi();
    }
}
