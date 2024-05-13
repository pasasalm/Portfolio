module fi.tuni.progthree.weatherapp {
    requires javafx.controls;
    requires com.google.gson;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.base;
    requires java.logging;

    opens fi.tuni.prog3.weatherapp to javafx.fxml;
    exports fi.tuni.prog3.weatherapp;
}
