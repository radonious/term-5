package com.example.App;

import com.example.Backend.Sorter;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
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

import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Controller implements Initializable {

    ArrayList<Integer> data;

    JFreeChart chart;

    boolean forceExit;

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

    @FXML
    private Slider delaySlider;

    @FXML
    private CheckBox stepCheckBox;

    @FXML
    private Button stopBtn;

    @FXML
    private Label delayLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        CountSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                syncNumbersData();
                render();
            }
        });
        forceExit = false;
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
        chart = ChartFactory.createHistogram("Radix Sort (LSD)", "", "", dataset);
        chart.getXYPlot().setBackgroundPaint(new Color(255, 255, 255));
        ValueAxis xAxis = chart.getXYPlot().getDomainAxis();
        xAxis.setRange(-0.5, (int) CountSlider.getValue() - 0.5);
        xAxis.setAxisLineVisible(false);
        xAxis.setVisible(false);
        ValueAxis yAxis = chart.getXYPlot().getRangeAxis();
        yAxis.setRange(0, (int) CountSlider.getValue());
        yAxis.setAxisLineVisible(false);
//        yAxis.setVisible(false);
        XYBarRenderer br = (XYBarRenderer) chart.getXYPlot().getRenderer();
        br.setMargin(.15);
        chart.getLegend().setVisible(false);
        chart.getXYPlot().setOutlineVisible(false);

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
    void shuffleBtnClicked(MouseEvent event) {
        syncNumbersData();
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
        lockButtons();
        Sorter sorter = new Sorter();
        sorter.sortStart(data);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (stepCheckBox.isSelected() && !sorter.nextSmallStep() || !stepCheckBox.isSelected() && !sorter.nextStep()) {
                    unlockButtons();
                    timer.cancel();
                } else if (forceExit) {
                    forceExit = false;
                    unlockButtons();
                    sorter.sortStart(data);
                    syncNumbersData();
                    timer.cancel();
                }
                Platform.runLater(() -> {
                    // sorter.print();
                    data = sorter.getData();
                    render();
                    if (stepCheckBox.isSelected() && !sorter.nextSmallStep() || !stepCheckBox.isSelected() && !sorter.nextStep()) {
                        recolor();
                    }
                });
            }
        };

        timer.schedule(task, 0, (int) delaySlider.getValue());
    }

    private void recolor() {
        XYBarRenderer br = (XYBarRenderer) chart.getXYPlot().getRenderer();
        br.setSeriesPaint(0, new Color(0, 153, 0));
    }

    private void lockButtons() {
        CountSlider.setDisable(true);
        delaySlider.setDisable(true);
        delayLabel.setDisable(true);
        ShuffleBtn.setDisable(true);
        stepCheckBox.setDisable(true);
        SortBtn.setDisable(true);
        stopBtn.setDisable(false);
    }

    private void unlockButtons() {
        CountSlider.setDisable(false);
        delaySlider.setDisable(false);
        delayLabel.setDisable(false);
        ShuffleBtn.setDisable(false);
        stepCheckBox.setDisable(false);
        SortBtn.setDisable(false);
        stopBtn.setDisable(true);
    }

    @FXML
    void stopBtnClicked(MouseEvent event) {
        forceExit = true;
    }
}
