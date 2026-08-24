package hr.algebra.influencer;

import hr.algebra.influencer.Utilization.SceneUtil;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application
{

    @Override
    public void init()
    {
        BazaPodataka.getConnection();
    }

    @Override
    public void start(Stage stage)
    {
        SceneUtil.loadScene(App.class.getResource("fxml/login.fxml"), stage, "Influencer - Prijava");
        stage.show();
    }

    @Override
    public void stop()
    {
        BazaPodataka.zatvori();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
