package hr.algebra.influencer.Controller;

import hr.algebra.influencer.DataAccessLayer.Implementation.TipSadrzajaRepozitorij;
import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Model.TipSadrzaja;
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

public class TipSadrzajaController implements Initializable
{

    @FXML
    private TableView<TipSadrzaja> tablica;
    @FXML
    private TableColumn<TipSadrzaja, Integer> idColumn;
    @FXML
    private TableColumn<TipSadrzaja, String> nazivColumn;
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

    private final TipSadrzajaRepozitorij tipSadrzajaRepozitorij = TipSadrzajaRepozitorij.getInstance();

    private final ObservableList<TipSadrzaja> sviTipovi = FXCollections.observableArrayList();
    private TipSadrzaja odabrani;

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
        sviTipovi.setAll(tipSadrzajaRepozitorij.getAll().stream()
                .sorted()
                .collect(Collectors.toList()));
        filtriraj(pretragaField.getText());
    }

    private void filtriraj(String tekst)
    {
        if (tekst == null || tekst.isBlank())
        {
            tablica.setItems(sviTipovi);
            return;
        }
        String trazeno = tekst.toLowerCase();
        ObservableList<TipSadrzaja> rezultat = sviTipovi.stream()
                .filter(t -> t.getNaziv().toLowerCase().contains(trazeno))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        tablica.setItems(rezultat);
    }

    private void odaberi(TipSadrzaja tipSadrzaja)
    {
        odabrani = tipSadrzaja;
        nazivField.setText(tipSadrzaja == null ? "" : tipSadrzaja.getNaziv());
        spremiButton.setText(tipSadrzaja == null ? "Dodaj" : "Spremi");
    }

    @FXML
    private void handleSpremi()
    {
        String naziv = nazivField.getText().trim();
        if (naziv.isEmpty())
        {
            AlertUtil.showWarning("Provjera", "Naziv tipa sadržaja je obavezan.");
            return;
        }

        try
        {
            if (odabrani == null)
            {
                TipSadrzaja noviTip = new TipSadrzaja(naziv);
                if (tipSadrzajaRepozitorij.isDuplicate(TipSadrzaja::getNaziv, noviTip))
                {
                    AlertUtil.showWarning("Provjera", "Tip sadržaja '" + naziv + "' vec postoji.");
                    return;
                }
                tipSadrzajaRepozitorij.create(noviTip);
                AppLogger.info("Kreiran tip sadrzaja: " + naziv);
            }
            else
            {
                odabrani.setNaziv(naziv);
                tipSadrzajaRepozitorij.update(odabrani);
                AppLogger.info("Azuriran tip sadrzaja: " + naziv);
            }
        }
        catch (RepoException e)
        {
            AppLogger.greska("Greska pri spremanju tipa sadrzaja: " + naziv, e);
            AlertUtil.showError("Greska", "Tip sadrzaja nije moguce spremiti.");
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
        if (odabrani == null)
        {
            AlertUtil.showWarning("Brisanje", "Prvo odaberite tip sadržaja iz tablice.");
            return;
        }
        try
        {
            tipSadrzajaRepozitorij.delete(odabrani.getId());
            AppLogger.info("Obrisan tip sadrzaja: " + odabrani.getNaziv());
        }
        catch (RepoException e)
        {
            AppLogger.greska("Greska pri brisanju tipa sadrzaja: " + odabrani.getNaziv(), e);
            AlertUtil.showError("Greska", "Tip sadrzaja nije moguce obrisati.");
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
