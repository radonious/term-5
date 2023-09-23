module com.example.sort {
    requires javafx.controls;
    requires javafx.fxml;
    requires jfreechart;
    requires jfreechart.fx;


    opens com.example.App to javafx.fxml;
    exports com.example.App;
}