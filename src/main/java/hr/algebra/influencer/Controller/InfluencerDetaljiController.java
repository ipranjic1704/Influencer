package hr.algebra.influencer.Controller;

import hr.algebra.influencer.Model.Influencer;
import hr.algebra.influencer.Model.Platforma;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class InfluencerDetaljiController
{

    @FXML
    private Label naslovLabel;
    @FXML
    private ImageView slikaImageView;
    @FXML
    private Label nemaSlikeLabel;
    @FXML
    private Label imeNadimakLabel;
    @FXML
    private Label zemljaLabel;
    @FXML
    private Label gradLabel;
    @FXML
    private Label brojPratiteljaLabel;
    @FXML
    private Label engagementRateLabel;
    @FXML
    private Label jezikSadrzajaLabel;
    @FXML
    private ListView<Platforma> platformeList;

    public void setInfluencer(Influencer influencer)
    {
        naslovLabel.setText("Detalji - " + influencer.getImeNadimak());
        imeNadimakLabel.setText(influencer.getImeNadimak());
        zemljaLabel.setText(prazno(influencer.getZemlja()));
        gradLabel.setText(influencer.getGrad() == null ? "-" : influencer.getGrad().getNaziv());
        brojPratiteljaLabel.setText(String.valueOf(influencer.getBrojPratitelja()));
        engagementRateLabel.setText(influencer.getEngagementRate() + "%");
        jezikSadrzajaLabel.setText(prazno(influencer.getJezikSadrzaja()));
        platformeList.setItems(FXCollections.observableArrayList(influencer.getPlatforme()));

        ucitajSliku(influencer.getProfilnaSlika());
    }

    private String prazno(String tekst)
    {
        return (tekst == null || tekst.isBlank()) ? "-" : tekst;
    }

    private void ucitajSliku(String url)
    {
        if (url == null || url.isBlank())
        {
            slikaImageView.setVisible(false);
            nemaSlikeLabel.setVisible(true);
            return;
        }

        Image slika = new Image(url, true);
        slikaImageView.setImage(slika);
        slikaImageView.setVisible(true);
        nemaSlikeLabel.setVisible(false);

        slika.errorProperty().addListener((obs, staro, greska) ->
        {
            if (greska)
            {
                slikaImageView.setVisible(false);
                nemaSlikeLabel.setVisible(true);
            }
        });
    }

    @FXML
    private void handleZatvori()
    {
        Stage stage = (Stage) naslovLabel.getScene().getWindow();
        stage.close();
    }
}
