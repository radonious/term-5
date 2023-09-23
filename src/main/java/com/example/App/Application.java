package com.example.App;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.IOException;

public class Application extends javafx.application.Application {
    public static void main(String[] args) {
        launch();
    }

    public static JFreeChart createChart() {

        XYSeries data = new XYSeries("");

        data.add(1, 10);
        data.add(2, 20);
        data.add(3, 30);
        data.add(4, 40);
        data.add(5, 50);
        data.add(6, 600);
        data.add(7, -2050);


        // Create a new XYSeriesCollection object and add the XYSeries object to it
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(data);

        // Create a new JFreeChart object, using the createHistogram() method of the ChartFactory class
        JFreeChart chart = ChartFactory.createHistogram("", "Index", "Value", dataset);

        // Customize the appearance of the chart
        chart.setTitle("Histogram Example");
        chart.getLegend().setVisible(false);


        return chart;
    }

    @Override
    public void start(Stage stage) throws Exception {

        ChartViewer viewer = new ChartViewer(createChart());
        Scene scene = new Scene(viewer, 1000, 800);
        stage.setScene(scene);
        stage.setTitle("Sorting Visualization");
        stage.show();
    }

//    @Override
//    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("index.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 1000, 800);
//        stage.setTitle("Sorting Visualization");
//        stage.setScene(scene);
//        stage.show();
//    }
}