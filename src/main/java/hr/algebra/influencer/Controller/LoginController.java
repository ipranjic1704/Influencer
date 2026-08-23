package hr.algebra.influencer.Controller;

import hr.algebra.influencer.App;
import hr.algebra.influencer.DataAccessLayer.Implementation.KorisnikRepozitorij;
import hr.algebra.influencer.Exception.AppException;
import hr.algebra.influencer.Model.Korisnik;
import hr.algebra.influencer.Utilization.AlertUtil;
import hr.algebra.influencer.Utilization.SceneUtil;
import hr.algebra.influencer.Utilization.Session;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField korisnickoImeField;
    @FXML
    private PasswordField lozinkaField;

    private final KorisnikRepozitorij korisnikRepozitorij = KorisnikRepozitorij.getInstance();

    @FXML
    private void handleLogin() {
        String korisnickoIme = korisnickoImeField.getText().trim();
        String lozinka = lozinkaField.getText();

        try {
            Korisnik korisnik = provjeriPrijavu(korisnickoIme, lozinka);
            Session.login(korisnik);
            Stage stage = (Stage) korisnickoImeField.getScene().getWindow();
            SceneUtil.loadScene(App.class.getResource("fxml/main.fxml"), stage, "Influencer");
        } catch (AppException e) {
            AlertUtil.showError("Pogreska pri prijavi", e.getMessage());
        }
    }

    private Korisnik provjeriPrijavu(String korisnickoIme, String lozinka) throws AppException {
        Korisnik korisnik = korisnikRepozitorij.getByKorisnickoIme(korisnickoIme)
                .orElseThrow(() -> new AppException("Pogresno korisnicko ime ili lozinka."));

        if (!korisnik.getLozinka().equals(lozinka)) {
            throw new AppException("Pogresno korisnicko ime ili lozinka.");
        }
        return korisnik;
    }

    @FXML
    private void handleOtvoriRegistraciju() {
        Stage stage = (Stage) korisnickoImeField.getScene().getWindow();
        SceneUtil.loadScene(App.class.getResource("fxml/registracija.fxml"), stage, "Influencer - Registracija");
    }
}
