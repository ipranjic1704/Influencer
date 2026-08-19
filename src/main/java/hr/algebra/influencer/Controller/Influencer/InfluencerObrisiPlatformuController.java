package hr.algebra.influencer.Controller.Influencer;

import hr.algebra.influencer.DataAccessLayer.Implementation.InfluencerRepozitorij;
import hr.algebra.influencer.Model.Influencer;
import hr.algebra.influencer.Model.Platforma;
import hr.algebra.influencer.Utilization.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class InfluencerObrisiPlatformuController {

    @FXML
    private Label naslovLabel;
    @FXML
    private ComboBox<Platforma> platformaComboBox;

    private final InfluencerRepozitorij influencerRepozitorij = InfluencerRepozitorij.getInstance();
    private Influencer influencer;

    public void setInfluencer(Influencer influencer) {
        this.influencer = influencer;
        naslovLabel.setText("Ukloni platformu za: " + influencer.getImeNadimak());
        platformaComboBox.setItems(FXCollections.observableArrayList(influencer.getPlatforme()));
    }

    @FXML
    private void handleObrisi() {
        Platforma platforma = platformaComboBox.getValue();
        if (platforma == null) {
            AlertUtil.showWarning("Uklanjanje", "Odaberite platformu.");
            return;
        }
        influencer.getPlatforme().remove(platforma);
        influencerRepozitorij.update(influencer);
        zatvori();
    }

    @FXML
    private void handleOdustani() {
        zatvori();
    }

    private void zatvori() {
        Stage stage = (Stage) naslovLabel.getScene().getWindow();
        stage.close();
    }
}
