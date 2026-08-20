package hr.algebra.influencer.Controller.Platforma;

import hr.algebra.influencer.DataAccessLayer.Implementation.PlatformaRepozitorij;
import hr.algebra.influencer.Model.Platforma;
import hr.algebra.influencer.Utilization.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PlatformaDodajController {

    @FXML
    private TextField nazivField;

    private final PlatformaRepozitorij platformaRepozitorij = PlatformaRepozitorij.getInstance();

    @FXML
    private void handleSpremi() {
        String naziv = nazivField.getText().trim();
        if (naziv.isEmpty()) {
            AlertUtil.showWarning("Provjera", "Naziv platforme je obavezan.");
            return;
        }

        Platforma novaPlatforma = new Platforma(naziv);
        if (platformaRepozitorij.isDuplicate(Platforma::getNaziv, novaPlatforma)) {
            AlertUtil.showWarning("Provjera", "Platforma '" + naziv + "' vec postoji.");
            return;
        }
        platformaRepozitorij.create(novaPlatforma);
        zatvori();
    }

    @FXML
    private void handleOdustani() {
        zatvori();
    }

    private void zatvori() {
        Stage stage = (Stage) nazivField.getScene().getWindow();
        stage.close();
    }
}
