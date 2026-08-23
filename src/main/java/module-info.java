module hr.algebra.influencer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.net.http;
    requires com.h2database;
    requires com.fasterxml.jackson.databind;

    opens hr.algebra.influencer to javafx.fxml;
    exports hr.algebra.influencer;

    opens hr.algebra.influencer.Model to javafx.base;
    exports hr.algebra.influencer.Model;

    opens hr.algebra.influencer.Controller to javafx.fxml;
    exports hr.algebra.influencer.Controller;
}
