module doodledo.doodledo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens doodledo.doodledo to javafx.fxml;
    exports doodledo.doodledo;
}