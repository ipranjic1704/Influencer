package hr.algebra.influencer.Controller;

import hr.algebra.influencer.App;
import hr.algebra.influencer.DataAccessLayer.Implementation.InfluencerRepozitorij;
import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Model.Influencer;
import hr.algebra.influencer.Service.UvozGradovaService;
import hr.algebra.influencer.Utilization.AlertUtil;
import hr.algebra.influencer.Utilization.AppLogger;
import hr.algebra.influencer.Utilization.SceneUtil;
import hr.algebra.influencer.Utilization.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class MainController
{

    @FXML
    private Label korisnikLabel;
    @FXML
    private Menu adminAlatiMenu;
    @FXML
    private MenuItem uveziGradoveItem;
    @FXML
    private MenuItem obrisiSveItem;
    @FXML
    private Button influenceriButton;
    @FXML
    private Button platformeButton;
    @FXML
    private Button niseButton;
    @FXML
    private Button tipoviSadrzajaButton;
    @FXML
    private Button gradoviButton;
    @FXML
    private Button brendoviButton;
    @FXML
    private Button brandSuradnjeButton;

    private final InfluencerRepozitorij influencerRepozitorij = InfluencerRepozitorij.getInstance();

    private final UvozGradovaService uvozGradovaService = new UvozGradovaService();

    @FXML
    private void initialize()
    {
        korisnikLabel.setText(Session.getTrenutniKorisnik().getKorisnickoIme());

        if (!Session.isAdmin())
        {
            adminAlatiMenu.setDisable(true);
        }
    }

    @FXML
    private void handleOtvoriInfluencere()
    {
        AppLogger.info("Otvoren ekran: Influenceri.");
        Stage stage = new Stage();
        stage.initOwner(korisnikLabel.getScene().getWindow());
        SceneUtil.loadScene(App.class.getResource("fxml/Influencer/influencer.fxml"), stage, "Influenceri");
        stage.show();
    }

    @FXML
    private void handleOtvoriPlatforme()
    {
        AppLogger.info("Otvoren ekran: Platforme.");
        Stage stage = new Stage();
        stage.initOwner(korisnikLabel.getScene().getWindow());
        SceneUtil.loadScene(App.class.getResource("fxml/Platforma/platforma.fxml"), stage, "Platforme");
        stage.show();
    }

    @FXML
    private void handleOtvoriNise()
    {
        AppLogger.info("Otvoren ekran: Niše.");
        Stage stage = new Stage();
        stage.initOwner(korisnikLabel.getScene().getWindow());
        SceneUtil.loadScene(App.class.getResource("fxml/Nisa/nisa.fxml"), stage, "Niše");
        stage.show();
    }

    @FXML
    private void handleOtvoriTipoveSadrzaja()
    {
        AppLogger.info("Otvoren ekran: Tipovi sadržaja.");
        Stage stage = new Stage();
        stage.initOwner(korisnikLabel.getScene().getWindow());
        SceneUtil.loadScene(App.class.getResource("fxml/TipSadrzaja/tipsadrzaja.fxml"), stage, "Tipovi sadržaja");
        stage.show();
    }

    @FXML
    private void handleOtvoriGradove()
    {
        AppLogger.info("Otvoren ekran: Gradovi.");
        Stage stage = new Stage();
        stage.initOwner(korisnikLabel.getScene().getWindow());
        SceneUtil.loadScene(App.class.getResource("fxml/Grad/grad.fxml"), stage, "Gradovi");
        stage.show();
    }

    @FXML
    private void handleOtvoriBrendove()
    {
        AppLogger.info("Otvoren ekran: Brendovi.");
        Stage stage = new Stage();
        stage.initOwner(korisnikLabel.getScene().getWindow());
        SceneUtil.loadScene(App.class.getResource("fxml/Brand/brand.fxml"), stage, "Brendovi");
        stage.show();
    }

    @FXML
    private void handleOtvoriBrandSuradnje()
    {
        AppLogger.info("Otvoren ekran: Brand suradnje.");
        Stage stage = new Stage();
        stage.initOwner(korisnikLabel.getScene().getWindow());
        SceneUtil.loadScene(App.class.getResource("fxml/BrandSuradnja/brandsuradnja.fxml"), stage, "Brand suradnje");
        stage.show();
    }

    @FXML
    private void handleUveziGradove()
    {
        if (uvozGradovaService.isRunning())
        {
            return;
        }

        uveziGradoveItem.setDisable(true);

        Alert napredak = new Alert(Alert.AlertType.INFORMATION);
        napredak.setTitle("Uvoz gradova");
        napredak.setHeaderText(null);
        napredak.contentTextProperty().bind(uvozGradovaService.messageProperty());
        napredak.show();

        uvozGradovaService.setOnSucceeded(e ->
        {
            napredak.contentTextProperty().unbind();
            napredak.close();
            uveziGradoveItem.setDisable(false);
            AppLogger.info("Uvoz gradova zavrsen, uvezeno: " + uvozGradovaService.getValue());
            AlertUtil.showInfo("Uvoz gradova", "Uvezeno novih gradova: " + uvozGradovaService.getValue());
        });
        uvozGradovaService.setOnFailed(e ->
        {
            napredak.contentTextProperty().unbind();
            napredak.close();
            uveziGradoveItem.setDisable(false);
            Throwable greska = uvozGradovaService.getException();
            AppLogger.greska("Uvoz gradova neuspjesan", greska);
            AlertUtil.showError("Uvoz gradova", greska == null ? "Nepoznata greska." : greska.getMessage());
        });

        uvozGradovaService.restart();
    }

    @FXML
    private void handleObrisiSve()
    {
        List<Influencer> sviInfluenceri = influencerRepozitorij.getAll();
        if (sviInfluenceri.isEmpty())
        {
            AlertUtil.showInfo("Obriši sve", "Nema influencera za brisanje.");
            return;
        }

        Alert potvrda = new Alert(Alert.AlertType.CONFIRMATION);
        potvrda.setTitle("Obriši sve influencere");
        potvrda.setHeaderText(null);
        potvrda.setContentText("Sigurno želite trajno obrisati svih " + sviInfluenceri.size() +
                " influencera? Ova radnja se ne može poništiti.");
        Optional<ButtonType> odgovor = potvrda.showAndWait();
        if (odgovor.isEmpty() || odgovor.get() != ButtonType.OK)
        {
            return;
        }

        try
        {
            for (Influencer influencer : sviInfluenceri)
            {
                influencerRepozitorij.delete(influencer.getId());
            }
        }
        catch (RepoException e)
        {
            AppLogger.greska("Greska pri brisanju svih influencera", e);
            AlertUtil.showError("Greska", "Nije moguce obrisati sve influencere.");
            return;
        }
        AppLogger.info("Obrisani svi influenceri, ukupno: " + sviInfluenceri.size());
        AlertUtil.showInfo("Obriši sve", "Obrisano influencera: " + sviInfluenceri.size());
    }

    @FXML
    private void handleOdjava()
    {
        AppLogger.info("Odjava.");
        Session.logout();
        Stage stage = (Stage) korisnikLabel.getScene().getWindow();
        SceneUtil.loadScene(App.class.getResource("fxml/login.fxml"), stage, "Influencer - Prijava");
    }
}
