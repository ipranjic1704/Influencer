package hr.algebra.influencer.Controller;

import hr.algebra.influencer.App;
import hr.algebra.influencer.Utilization.SceneUtil;
import hr.algebra.influencer.Utilization.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private Label korisnikLabel;

    @FXML
    private void initialize() {
        korisnikLabel.setText(Session.getTrenutniKorisnik().getKorisnickoIme());
    }

    @FXML
    private void handleOdjava() {
        Session.logout();
        Stage stage = (Stage) korisnikLabel.getScene().getWindow();
        SceneUtil.loadScene(App.class.getResource("fxml/login.fxml"), stage, "Influencer - Prijava");
    }
}
