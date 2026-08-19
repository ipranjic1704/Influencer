package hr.algebra.influencer.Controller.Influencer;

import hr.algebra.influencer.DataAccessLayer.Implementation.InfluencerRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.PlatformaRepozitorij;
import hr.algebra.influencer.Model.Influencer;
import hr.algebra.influencer.Model.Platforma;
import hr.algebra.influencer.Utilization.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class InfluencerDodajPlatformuController {

    @FXML
    private Label naslovLabel;
    @FXML
    private ComboBox<Platforma> platformaComboBox;

    private final InfluencerRepozitorij influencerRepozitorij = InfluencerRepozitorij.getInstance();
    private final PlatformaRepozitorij platformaRepozitorij = PlatformaRepozitorij.getInstance();
    private Influencer influencer;

    public void setInfluencer(Influencer influencer) {
        this.influencer = influencer;
        naslovLabel.setText("Dodaj platformu za: " + influencer.getImeNadimak());

        List<Platforma> dostupne = platformaRepozitorij.getAll().stream()
                .filter(p -> !influencer.getPlatforme().contains(p))
                .collect(Collectors.toList());
        platformaComboBox.setItems(FXCollections.observableArrayList(dostupne));
    }

    @FXML
    private void handleDodaj() {
        Platforma platforma = platformaComboBox.getValue();
        if (platforma == null) {
            AlertUtil.showWarning("Dodavanje", "Odaberite platformu.");
            return;
        }
        influencer.getPlatforme().add(platforma);
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
