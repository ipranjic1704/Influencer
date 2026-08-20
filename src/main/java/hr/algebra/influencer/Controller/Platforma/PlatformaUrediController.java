package hr.algebra.influencer.Controller.Platforma;

import hr.algebra.influencer.DataAccessLayer.Implementation.PlatformaRepozitorij;
import hr.algebra.influencer.Model.Platforma;
import hr.algebra.influencer.Utilization.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PlatformaUrediController {

    @FXML
    private TextField nazivField;

    private final PlatformaRepozitorij platformaRepozitorij = PlatformaRepozitorij.getInstance();
    private Platforma platforma;

    public void setPlatforma(Platforma platforma) {
        this.platforma = platforma;
        nazivField.setText(platforma.getNaziv());
    }

    @FXML
    private void handleSpremi() {
        String naziv = nazivField.getText().trim();
        if (naziv.isEmpty()) {
            AlertUtil.showWarning("Provjera", "Naziv platforme je obavezan.");
            return;
        }

        platforma.setNaziv(naziv);
        platformaRepozitorij.update(platforma);
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
