package hr.algebra.influencer.Utilization;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public final class SceneUtil
{

    private SceneUtil()
    {
    }

    public static void loadScene(URL fxmlUrl, Stage stage, String title)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Ne mogu ucitati FXML: " + fxmlUrl, e);
        }
    }

    public static FXMLLoader loadSceneWithLoader(URL fxmlUrl, Stage stage, String title)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
            return loader;
        }
        catch (IOException e)
        {
            throw new RuntimeException("Ne mogu ucitati FXML: " + fxmlUrl, e);
        }
    }
}
