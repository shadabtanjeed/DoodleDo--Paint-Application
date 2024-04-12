module doodledo.doodledo {
    requires javafx.controls;
    requires javafx.fxml;


    opens doodledo.doodledo to javafx.fxml;
    exports doodledo.doodledo;
}