package hr.algebra.influencer.Controller;

import hr.algebra.influencer.App;
import hr.algebra.influencer.DataAccessLayer.Implementation.KorisnikRepozitorij;
import hr.algebra.influencer.Model.Korisnik;
import hr.algebra.influencer.Utilization.AlertUtil;
import hr.algebra.influencer.Utilization.SceneUtil;
import hr.algebra.influencer.Utilization.Session;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

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

        Optional<Korisnik> korisnik = korisnikRepozitorij.getByKorisnickoIme(korisnickoIme);

        if (korisnik.isPresent() && korisnik.get().getLozinka().equals(lozinka)) {
            Session.login(korisnik.get());
            Stage stage = (Stage) korisnickoImeField.getScene().getWindow();
            SceneUtil.loadScene(App.class.getResource("fxml/main.fxml"), stage, "Influencer");
        } else {
            AlertUtil.showError("Pogreska pri prijavi", "Pogresno korisnicko ime ili lozinka.");
        }
    }
}
