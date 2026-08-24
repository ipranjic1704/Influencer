package hr.algebra.influencer.Controller;

import hr.algebra.influencer.App;
import hr.algebra.influencer.DataAccessLayer.Implementation.InfluencerRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.KorisnikRepozitorij;
import hr.algebra.influencer.Model.Enum.Uloga;
import hr.algebra.influencer.Model.Influencer;
import hr.algebra.influencer.Model.Korisnik;
import hr.algebra.influencer.Utilization.AlertUtil;
import hr.algebra.influencer.Utilization.AppLogger;
import hr.algebra.influencer.Utilization.SceneUtil;
import hr.algebra.influencer.Utilization.Session;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistracijaController
{

    @FXML
    private TextField korisnickoImeField;
    @FXML
    private PasswordField lozinkaField;
    @FXML
    private PasswordField potvrdaLozinkeField;
    @FXML
    private RadioButton brendRadio;
    @FXML
    private RadioButton influencerRadio;
    @FXML
    private TextField imeNadimakField;

    private final KorisnikRepozitorij korisnikRepozitorij = KorisnikRepozitorij.getInstance();
    private final InfluencerRepozitorij influencerRepozitorij = InfluencerRepozitorij.getInstance();

    @FXML
    private void initialize()
    {
        imeNadimakField.setDisable(!influencerRadio.isSelected());
        brendRadio.selectedProperty().addListener((obs, staro, novo) -> imeNadimakField.setDisable(novo));
        influencerRadio.selectedProperty().addListener((obs, staro, novo) -> imeNadimakField.setDisable(!novo));
    }

    @FXML
    private void handleRegistracija()
    {
        String korisnickoIme = korisnickoImeField.getText().trim();
        String lozinka = lozinkaField.getText();
        String potvrda = potvrdaLozinkeField.getText();

        if (korisnickoIme.isEmpty() || lozinka.isEmpty())
        {
            AlertUtil.showWarning("Provjera", "Korisnicko ime i lozinka su obavezni.");
            return;
        }
        if (!lozinka.equals(potvrda))
        {
            AlertUtil.showWarning("Provjera", "Lozinke se ne podudaraju.");
            return;
        }
        if (korisnikRepozitorij.getByKorisnickoIme(korisnickoIme).isPresent())
        {
            AlertUtil.showWarning("Provjera", "Korisnicko ime '" + korisnickoIme + "' je vec zauzeto.");
            return;
        }

        Uloga uloga = influencerRadio.isSelected() ? Uloga.INFLUENCER : Uloga.BREND;
        Korisnik noviKorisnik = new Korisnik(korisnickoIme, lozinka, uloga);

        if (uloga == Uloga.INFLUENCER)
        {
            String imeNadimak = imeNadimakField.getText().trim();
            if (imeNadimak.isEmpty())
            {
                AlertUtil.showWarning("Provjera", "Ime/nadimak influencera je obavezan.");
                return;
            }
            Influencer noviProfil = new Influencer(imeNadimak, 0, 0.0, null, null, null, null);
            influencerRepozitorij.create(noviProfil);
            noviKorisnik.setInfluencerId(noviProfil.getId());
        }

        korisnikRepozitorij.create(noviKorisnik);

        Session.login(noviKorisnik);
        AppLogger.info("Registracija nova korisnika, uloga: " + uloga);
        Stage stage = (Stage) korisnickoImeField.getScene().getWindow();
        SceneUtil.loadScene(App.class.getResource("fxml/main.fxml"), stage, "Influencer");
    }

    @FXML
    private void handleNatragNaPrijavu()
    {
        Stage stage = (Stage) korisnickoImeField.getScene().getWindow();
        SceneUtil.loadScene(App.class.getResource("fxml/login.fxml"), stage, "Influencer - Prijava");
    }
}
