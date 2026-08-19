package hr.algebra.influencer.Controller;

import hr.algebra.influencer.App;
import hr.algebra.influencer.DataAccessLayer.Implementation.InfluencerRepozitorij;
import hr.algebra.influencer.Utilization.SceneUtil;
import hr.algebra.influencer.Utilization.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private Label korisnikLabel;
    @FXML
    private Button influenceriButton;

    private final InfluencerRepozitorij influencerRepozitorij = InfluencerRepozitorij.getInstance();

    @FXML
    private void initialize() {
        korisnikLabel.setText(Session.getTrenutniKorisnik().getKorisnickoIme());
        influenceriButton.setText("Influenceri (" + influencerRepozitorij.count() + ")");
    }

    @FXML
    private void handleOtvoriInfluencere() {
        Stage stage = new Stage();
        stage.initOwner(korisnikLabel.getScene().getWindow());
        SceneUtil.loadScene(App.class.getResource("fxml/Influencer/influencer.fxml"), stage, "Influenceri");
        stage.show();
    }

    @FXML
    private void handleOdjava() {
        Session.logout();
        Stage stage = (Stage) korisnikLabel.getScene().getWindow();
        SceneUtil.loadScene(App.class.getResource("fxml/login.fxml"), stage, "Influencer - Prijava");
    }
}
