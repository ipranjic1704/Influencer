package hr.algebra.influencer.Utilization;

import javafx.scene.control.Alert;

public final class AlertUtil
{

    private AlertUtil()
    {
    }

    public static void showInfo(String naslov, String poruka)
    {
        show(Alert.AlertType.INFORMATION, naslov, poruka);
    }

    public static void showError(String naslov, String poruka)
    {
        show(Alert.AlertType.ERROR, naslov, poruka);
    }

    public static void showWarning(String naslov, String poruka)
    {
        show(Alert.AlertType.WARNING, naslov, poruka);
    }

    private static void show(Alert.AlertType tip, String naslov, String poruka)
    {
        Alert alert = new Alert(tip);
        alert.setTitle(naslov);
        alert.setHeaderText(null);
        alert.setContentText(poruka);
        alert.showAndWait();
    }
}
