module wgu.scheduler {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens Main to javafx.fxml;
    exports Main;
    exports Main.Controllers;
    opens Main.Controllers to javafx.fxml;
    exports Main.Utilities;
    opens Main.Utilities to javafx.fxml;
}