module hr.algebra.influencer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.h2database;

    opens hr.algebra.influencer to javafx.fxml;
    exports hr.algebra.influencer;

    opens hr.algebra.influencer.Model to javafx.base;
    exports hr.algebra.influencer.Model;

    opens hr.algebra.influencer.Controller to javafx.fxml;
    exports hr.algebra.influencer.Controller;

    opens hr.algebra.influencer.Controller.Influencer to javafx.fxml;
    exports hr.algebra.influencer.Controller.Influencer;
}
