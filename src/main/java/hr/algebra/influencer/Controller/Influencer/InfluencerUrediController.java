package hr.algebra.influencer.Controller.Influencer;

import hr.algebra.influencer.DataAccessLayer.Implementation.InfluencerRepozitorij;
import hr.algebra.influencer.Model.Influencer;
import hr.algebra.influencer.Utilization.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class InfluencerUrediController {

    @FXML
    private TextField imeNadimakField;
    @FXML
    private TextField brojPratiteljaField;
    @FXML
    private TextField engagementRateField;
    @FXML
    private TextField zemljaField;
    @FXML
    private TextField jezikSadrzajaField;
    @FXML
    private TextField profilnaSlikaField;

    private final InfluencerRepozitorij influencerRepozitorij = InfluencerRepozitorij.getInstance();
    private Influencer influencer;

    public void setInfluencer(Influencer influencer) {
        this.influencer = influencer;
        imeNadimakField.setText(influencer.getImeNadimak());
        brojPratiteljaField.setText(String.valueOf(influencer.getBrojPratitelja()));
        engagementRateField.setText(String.valueOf(influencer.getEngagementRate()));
        zemljaField.setText(influencer.getZemlja());
        jezikSadrzajaField.setText(influencer.getJezikSadrzaja());
        profilnaSlikaField.setText(influencer.getProfilnaSlika());
    }

    @FXML
    private void handleSpremi() {
        String imeNadimak = imeNadimakField.getText().trim();
        if (imeNadimak.isEmpty()) {
            AlertUtil.showWarning("Provjera", "Ime/nadimak influencera je obavezan.");
            return;
        }

        int brojPratitelja;
        try {
            brojPratitelja = Integer.parseInt(brojPratiteljaField.getText().trim());
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Provjera", "Broj pratitelja mora biti cijeli broj (npr. 15000).");
            return;
        }

        double engagementRate;
        try {
            engagementRate = Double.parseDouble(engagementRateField.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Provjera", "Engagement rate mora biti broj (npr. 3.5).");
            return;
        }

        influencer.setImeNadimak(imeNadimak);
        influencer.setBrojPratitelja(brojPratitelja);
        influencer.setEngagementRate(engagementRate);
        influencer.setZemlja(zemljaField.getText().trim());
        influencer.setJezikSadrzaja(jezikSadrzajaField.getText().trim());
        influencer.setProfilnaSlika(profilnaSlikaField.getText().trim());
        influencerRepozitorij.update(influencer);
        zatvori();
    }

    @FXML
    private void handleOdustani() {
        zatvori();
    }

    private void zatvori() {
        Stage stage = (Stage) imeNadimakField.getScene().getWindow();
        stage.close();
    }
}
