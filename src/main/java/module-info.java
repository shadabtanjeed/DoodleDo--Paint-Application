module doodledo.doodledo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;
    requires io;
    requires kernel;
    requires layout;


    opens doodledo.doodledo to javafx.fxml;
    exports doodledo.doodledo;
}