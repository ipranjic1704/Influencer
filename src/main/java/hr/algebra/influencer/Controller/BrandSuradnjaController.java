package hr.algebra.influencer.Controller;

import hr.algebra.influencer.DataAccessLayer.Implementation.BrandRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.BrandSuradnjaRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.InfluencerRepozitorij;
import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Model.Brand;
import hr.algebra.influencer.Model.BrandSuradnja;
import hr.algebra.influencer.Model.Enum.StatusSuradnje;
import hr.algebra.influencer.Model.Influencer;
import hr.algebra.influencer.Utilization.AlertUtil;
import hr.algebra.influencer.Utilization.AppLogger;
import hr.algebra.influencer.Utilization.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BrandSuradnjaController implements Initializable
{

    @FXML
    private TableView<BrandSuradnja> tablica;
    @FXML
    private TableColumn<BrandSuradnja, Integer> idColumn;
    @FXML
    private TableColumn<BrandSuradnja, String> nazivKampanjeColumn;
    @FXML
    private TableColumn<BrandSuradnja, Brand> brandColumn;
    @FXML
    private TableColumn<BrandSuradnja, Integer> godinaColumn;
    @FXML
    private TableColumn<BrandSuradnja, StatusSuradnje> statusColumn;
    @FXML
    private TableColumn<BrandSuradnja, String> timColumn;
    @FXML
    private TextField pretragaField;
    @FXML
    private TextField nazivKampanjeField;
    @FXML
    private TextField godinaField;
    @FXML
    private ComboBox<Brand> brandComboBox;
    @FXML
    private ComboBox<StatusSuradnje> statusComboBox;
    @FXML
    private ListView<Influencer> dostupniListView;
    @FXML
    private ListView<Influencer> timListView;
    @FXML
    private Button ukloniIzTimaButton;
    @FXML
    private Button spremiButton;
    @FXML
    private Button novaButton;
    @FXML
    private Button brisiButton;

    private final BrandSuradnjaRepozitorij brandSuradnjaRepozitorij = BrandSuradnjaRepozitorij.getInstance();
    private final BrandRepozitorij brandRepozitorij = BrandRepozitorij.getInstance();
    private final InfluencerRepozitorij influencerRepozitorij = InfluencerRepozitorij.getInstance();

    private final ObservableList<BrandSuradnja> sveSuradnje = FXCollections.observableArrayList();
    private final ObservableList<Influencer> dostupniInfluenceri = FXCollections.observableArrayList();
    private final ObservableList<Influencer> timInfluenceri = FXCollections.observableArrayList();
    private BrandSuradnja odabrana;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nazivKampanjeColumn.setCellValueFactory(new PropertyValueFactory<>("nazivKampanje"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        godinaColumn.setCellValueFactory(new PropertyValueFactory<>("godina"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        timColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getTim().stream()
                        .map(Influencer::getImeNadimak)
                        .collect(Collectors.joining(", "))));

        brandComboBox.setItems(FXCollections.observableArrayList(brandRepozitorij.getAll()));
        statusComboBox.setItems(FXCollections.observableArrayList(StatusSuradnje.values()));

        dostupniListView.setItems(dostupniInfluenceri);
        timListView.setItems(timInfluenceri);
        omoguciPrevlacenje();

        pretragaField.textProperty().addListener((obs, staro, novo) -> filtriraj(novo));
        tablica.getSelectionModel().selectedItemProperty().addListener((obs, staro, novo) -> odaberi(novo));

        if (!Session.smijeDodatiSuradnju())
        {
            nazivKampanjeField.setDisable(true);
            godinaField.setDisable(true);
            brandComboBox.setDisable(true);
            statusComboBox.setDisable(true);
            dostupniListView.setDisable(true);
            timListView.setDisable(true);
            ukloniIzTimaButton.setDisable(true);
            spremiButton.setDisable(true);
            novaButton.setDisable(true);
            brisiButton.setDisable(true);
        }

        osvjezi();
    }

    private void omoguciPrevlacenje()
    {
        dostupniListView.setCellFactory(lv ->
        {
            ListCell<Influencer> cell = new ListCell<>()
            {
                @Override
                protected void updateItem(Influencer influencer, boolean empty)
                {
                    super.updateItem(influencer, empty);
                    setText(empty || influencer == null ? null : influencer.getImeNadimak());
                }
            };
            cell.setOnDragDetected(event ->
            {
                if (cell.getItem() == null)
                {
                    return;
                }
                Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent sadrzaj = new ClipboardContent();
                sadrzaj.putString(String.valueOf(cell.getItem().getId()));
                db.setContent(sadrzaj);
                event.consume();
            });
            return cell;
        });

        timListView.setOnDragOver(event ->
        {
            if (event.getGestureSource() != timListView && event.getDragboard().hasString())
            {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        timListView.setOnDragDropped(event ->
        {
            Dragboard db = event.getDragboard();
            boolean uspjesno = false;
            if (db.hasString())
            {
                int idInfluencer = Integer.parseInt(db.getString());
                uspjesno = dostupniInfluenceri.stream()
                        .filter(i -> i.getId() == idInfluencer)
                        .findFirst()
                        .map(influencer ->
                        {
                            timInfluenceri.add(influencer);
                            dostupniInfluenceri.remove(influencer);
                            return true;
                        })
                        .orElse(false);
            }
            event.setDropCompleted(uspjesno);
            event.consume();
        });
    }

    private void osvjezi()
    {
        sveSuradnje.setAll(brandSuradnjaRepozitorij.getAll().stream()
                .sorted()
                .collect(Collectors.toList()));
        filtriraj(pretragaField.getText());
    }

    private void filtriraj(String tekst)
    {
        if (tekst == null || tekst.isBlank())
        {
            tablica.setItems(sveSuradnje);
            return;
        }
        String trazeno = tekst.toLowerCase();
        ObservableList<BrandSuradnja> rezultat = sveSuradnje.stream()
                .filter(s -> s.getNazivKampanje().toLowerCase().contains(trazeno))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        tablica.setItems(rezultat);
    }

    private void odaberi(BrandSuradnja suradnja)
    {
        odabrana = suradnja;

        nazivKampanjeField.setText(suradnja == null ? "" : suradnja.getNazivKampanje());
        godinaField.setText(suradnja == null ? "" : String.valueOf(suradnja.getGodina()));
        brandComboBox.setValue(suradnja == null ? null : suradnja.getBrand());
        statusComboBox.setValue(suradnja == null ? StatusSuradnje.PLANIRANA : suradnja.getStatus());

        List<Influencer> sviInfluenceri = influencerRepozitorij.getAll();
        List<Influencer> trenutniTim = suradnja == null ? List.of() : suradnja.getTim();
        timInfluenceri.setAll(trenutniTim);
        dostupniInfluenceri.setAll(sviInfluenceri.stream()
                .filter(i -> !trenutniTim.contains(i))
                .collect(Collectors.toList()));

        spremiButton.setText(suradnja == null ? "Dodaj" : "Spremi");
    }

    @FXML
    private void handleSpremi()
    {
        String nazivKampanje = nazivKampanjeField.getText().trim();
        if (nazivKampanje.isEmpty())
        {
            AlertUtil.showWarning("Provjera", "Naziv kampanje je obavezan.");
            return;
        }

        Brand brand = brandComboBox.getValue();
        if (brand == null)
        {
            AlertUtil.showWarning("Provjera", "Odaberite brand.");
            return;
        }

        StatusSuradnje status = statusComboBox.getValue();
        if (status == null)
        {
            AlertUtil.showWarning("Provjera", "Odaberite status.");
            return;
        }

        int godina;
        try
        {
            godina = Integer.parseInt(godinaField.getText().trim());
        }
        catch (NumberFormatException e)
        {
            AlertUtil.showWarning("Provjera", "Godina mora biti cijeli broj (npr. 2026).");
            return;
        }

        List<Influencer> tim = List.copyOf(timInfluenceri);

        try
        {
            if (odabrana == null)
            {
                BrandSuradnja novaSuradnja = new BrandSuradnja(nazivKampanje, brand, godina, status);
                if (brandSuradnjaRepozitorij.isDuplicate(BrandSuradnja::getNazivKampanje, novaSuradnja))
                {
                    AlertUtil.showWarning("Provjera", "Kampanja '" + nazivKampanje + "' vec postoji.");
                    return;
                }
                novaSuradnja.setTim(tim);
                brandSuradnjaRepozitorij.create(novaSuradnja);
                AppLogger.info("Kreirana brand suradnja: " + nazivKampanje);
            }
            else
            {
                odabrana.setNazivKampanje(nazivKampanje);
                odabrana.setBrand(brand);
                odabrana.setGodina(godina);
                odabrana.setStatus(status);
                odabrana.setTim(tim);
                brandSuradnjaRepozitorij.update(odabrana);
                AppLogger.info("Azurirana brand suradnja: " + nazivKampanje);
            }
        }
        catch (RepoException e)
        {
            AppLogger.greska("Greska pri spremanju brand suradnje: " + nazivKampanje, e);
            AlertUtil.showError("Greska", "Suradnju nije moguce spremiti.");
            return;
        }

        tablica.getSelectionModel().clearSelection();
        osvjezi();
    }

    @FXML
    private void handleUkloniIzTima()
    {
        Influencer odabraniZaUklanjanje = timListView.getSelectionModel().getSelectedItem();
        if (odabraniZaUklanjanje == null)
        {
            AlertUtil.showWarning("Tim", "Prvo odaberite influencera iz tima.");
            return;
        }
        timInfluenceri.remove(odabraniZaUklanjanje);
        dostupniInfluenceri.add(odabraniZaUklanjanje);
    }

    @FXML
    private void handleNova()
    {
        tablica.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleBrisi()
    {
        if (odabrana == null)
        {
            AlertUtil.showWarning("Brisanje", "Prvo odaberite kampanju iz tablice.");
            return;
        }
        try
        {
            brandSuradnjaRepozitorij.delete(odabrana.getId());
            AppLogger.info("Obrisana brand suradnja: " + odabrana.getNazivKampanje());
        }
        catch (RepoException e)
        {
            AppLogger.greska("Greska pri brisanju brand suradnje: " + odabrana.getNazivKampanje(), e);
            AlertUtil.showError("Greska", "Suradnju nije moguce obrisati.");
            return;
        }
        tablica.getSelectionModel().clearSelection();
        osvjezi();
    }

    @FXML
    private void handleOsvjezi()
    {
        osvjezi();
    }
}
