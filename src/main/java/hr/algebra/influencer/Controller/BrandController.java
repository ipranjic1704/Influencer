package hr.algebra.influencer.Controller;

import hr.algebra.influencer.DataAccessLayer.Implementation.BrandRepozitorij;
import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Model.Brand;
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

public class BrandController implements Initializable
{

    @FXML
    private TableView<Brand> tablica;
    @FXML
    private TableColumn<Brand, Integer> idColumn;
    @FXML
    private TableColumn<Brand, String> nazivColumn;
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

    private final BrandRepozitorij brandRepozitorij = BrandRepozitorij.getInstance();

    private final ObservableList<Brand> sviBrendovi = FXCollections.observableArrayList();
    private Brand odabrani;

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
        sviBrendovi.setAll(brandRepozitorij.getAll().stream()
                .sorted()
                .collect(Collectors.toList()));
        filtriraj(pretragaField.getText());
    }

    private void filtriraj(String tekst)
    {
        if (tekst == null || tekst.isBlank())
        {
            tablica.setItems(sviBrendovi);
            return;
        }
        String trazeno = tekst.toLowerCase();
        ObservableList<Brand> rezultat = sviBrendovi.stream()
                .filter(b -> b.getNaziv().toLowerCase().contains(trazeno))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        tablica.setItems(rezultat);
    }

    private void odaberi(Brand brand)
    {
        odabrani = brand;
        nazivField.setText(brand == null ? "" : brand.getNaziv());
        spremiButton.setText(brand == null ? "Dodaj" : "Spremi");
    }

    @FXML
    private void handleSpremi()
    {
        String naziv = nazivField.getText().trim();
        if (naziv.isEmpty())
        {
            AlertUtil.showWarning("Provjera", "Naziv brenda je obavezan.");
            return;
        }

        try
        {
            if (odabrani == null)
            {
                Brand noviBrand = new Brand(naziv);
                if (brandRepozitorij.isDuplicate(Brand::getNaziv, noviBrand))
                {
                    AlertUtil.showWarning("Provjera", "Brand '" + naziv + "' vec postoji.");
                    return;
                }
                brandRepozitorij.create(noviBrand);
                AppLogger.info("Kreiran brand: " + naziv);
            }
            else
            {
                odabrani.setNaziv(naziv);
                brandRepozitorij.update(odabrani);
                AppLogger.info("Azuriran brand: " + naziv);
            }
        }
        catch (RepoException e)
        {
            AppLogger.greska("Greska pri spremanju branda: " + naziv, e);
            AlertUtil.showError("Greska", "Brand nije moguce spremiti.");
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
            AlertUtil.showWarning("Brisanje", "Prvo odaberite brand iz tablice.");
            return;
        }
        try
        {
            brandRepozitorij.delete(odabrani.getId());
            AppLogger.info("Obrisan brand: " + odabrani.getNaziv());
        }
        catch (RepoException e)
        {
            AppLogger.greska("Greska pri brisanju branda: " + odabrani.getNaziv(), e);
            AlertUtil.showError("Greska", "Brand nije moguce obrisati (mozda ima postojece suradnje).");
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
