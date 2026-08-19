package hr.algebra.influencer.Controller.Influencer;

import hr.algebra.influencer.DataAccessLayer.Implementation.InfluencerRepozitorij;
import hr.algebra.influencer.Model.Influencer;
import hr.algebra.influencer.Utilization.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

// Controller za formu dodavanja novog influencera (influencer-dodaj.fxml).
// Influencer se kreira bez platformi - platforme se dodaju posebno (dodaj-platformu ekran).
public class InfluencerDodajController {

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
            // replace(',', '.') podrzava europski decimalni zarez (npr. "3,5" -> "3.5").
            engagementRate = Double.parseDouble(engagementRateField.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Provjera", "Engagement rate mora biti broj (npr. 3.5).");
            return;
        }

        String zemlja = zemljaField.getText().trim();
        String jezikSadrzaja = jezikSadrzajaField.getText().trim();
        String profilnaSlika = profilnaSlikaField.getText().trim();

        Influencer noviInfluencer = new Influencer(imeNadimak, brojPratitelja, engagementRate, zemlja, jezikSadrzaja, profilnaSlika);
        if (influencerRepozitorij.isDuplicate(Influencer::getImeNadimak, noviInfluencer)) {
            AlertUtil.showWarning("Provjera", "Influencer '" + imeNadimak + "' vec postoji.");
            return;
        }
        influencerRepozitorij.create(noviInfluencer);
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
