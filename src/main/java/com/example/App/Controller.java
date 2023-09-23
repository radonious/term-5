package com.example.App;

import com.example.Backend.Sorter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class Controller extends Pane {

    ArrayList<Integer> data;

    @FXML
    private Slider CountSlider;

    @FXML
    private Pane MainPane;

    @FXML
    private Button ShuffleBtn;

    @FXML
    private Button SortBtn;

    @FXML
    private Pane ViewPane;

    public void initialize() {
        data = new ArrayList<>();
        syncNumbersData();
        render();
    }

    private JFreeChart createChart() {

        XYSeries chart_data = new XYSeries("");
        for (int i = 0; i < (int) CountSlider.getValue(); ++i) {
            chart_data.add(i, data.get(i));
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(chart_data);
        JFreeChart chart = ChartFactory.createHistogram("Radix Sort (LSD)", "", "", dataset);
        ValueAxis xAxis = chart.getXYPlot().getDomainAxis();
        xAxis.setRange(-0.5, (int) CountSlider.getValue() - 0.5);
        XYBarRenderer br = (XYBarRenderer) chart.getXYPlot().getRenderer();
        br.setMargin(.1);
        chart.getLegend().setVisible(false);

        return chart;
    }

    private void render() {
        ChartViewer viewer = new ChartViewer(createChart());
        viewer.setPrefSize(ViewPane.getPrefWidth(), ViewPane.getPrefHeight());
        ViewPane.getChildren().clear();
        ViewPane.getChildren().add(viewer);
    }

    private void syncNumbersData() {
        if (!data.isEmpty()) data.clear();
        for (int i = 0; i < (int) CountSlider.getValue(); ++i) {
            data.add(i);
        }
    }

    @FXML
    void countSliderMouseRelease(MouseEvent event) {
        syncNumbersData();
        ViewPane.getChildren().clear();
        ChartViewer viewer = new ChartViewer(createChart());
        viewer.setPrefSize(ViewPane.getPrefWidth(), ViewPane.getPrefHeight());
        ViewPane.getChildren().add(viewer);
    }


    @FXML
    void shuffleBtnClicked(MouseEvent event) {
        shuffleArray();
        render();
    }

    private void shuffleArray() {
        Random rnd = ThreadLocalRandom.current();
        for (int i = data.size() - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = data.get(index);
            data.set(index, data.get(i));
            data.set(i, a);
        }
    }

    @FXML
    void sortBtnClicked(MouseEvent event) throws InterruptedException {
        Sorter sorter = new Sorter();
        sorter.sortStart(data);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!sorter.nextStep()) {
                    timer.cancel();
                }
                Platform.runLater(() -> {
                    sorter.print();
                    data = sorter.getData();
                    render();
                });
            }
        };
        timer.schedule(task, 0, 750);

    }

}
