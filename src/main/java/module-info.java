module hr.algebra.influencer {
    requires javafx.controls;
    requires javafx.fxml;

    opens hr.algebra.influencer to javafx.fxml;
    exports hr.algebra.influencer;

    opens hr.algebra.influencer.Model to javafx.base;
    exports hr.algebra.influencer.Model;
}