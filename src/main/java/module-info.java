module hr.algebra.influencer
{
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.net.http;
    requires java.xml;
    requires com.h2database;
    requires com.fasterxml.jackson.databind;
    requires jakarta.xml.bind;

    opens hr.algebra.influencer to javafx.fxml;
    exports hr.algebra.influencer;

    opens hr.algebra.influencer.Model to javafx.base;
    exports hr.algebra.influencer.Model;

    opens hr.algebra.influencer.Controller to javafx.fxml;
    exports hr.algebra.influencer.Controller;

    opens hr.algebra.influencer.Xml to jakarta.xml.bind;
    exports hr.algebra.influencer.Xml;
}
