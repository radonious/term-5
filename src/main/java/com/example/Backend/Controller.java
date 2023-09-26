package com.example.Backend;

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

    private ArrayList<Integer> data;

    private ArrayList<Pane> pocket_panes;

    private JFreeChart chart;

    private boolean force_exit_flag;

    private Sorter sorter;

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

    @FXML
    private Pane Pocket0;

    @FXML
    private Pane Pocket1;

    @FXML
    private Pane Pocket2;

    @FXML
    private Pane Pocket3;

    @FXML
    private Pane Pocket4;

    @FXML
    private Pane Pocket5;

    @FXML
    private Pane Pocket6;

    @FXML
    private Pane Pocket7;

    @FXML
    private Pane Pocket8;

    @FXML
    private Pane Pocket9;

    @FXML
    private CheckBox shuffleCheckBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pocket_panes = new ArrayList<>(10);
        pocket_panes.addAll(Arrays.asList(Pocket0, Pocket1, Pocket2, Pocket3, Pocket4, Pocket5, Pocket6, Pocket7, Pocket8, Pocket9));
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
        force_exit_flag = false;
        data = new ArrayList<>();
        sorter = new Sorter();
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

    private JFreeChart createPocketChart(int num) {
        XYSeries chart_data = new XYSeries("");
        ArrayList<Integer> pocket = sorter.getPocket(num);
        for (int i = 0; i < pocket.size(); ++i) {
            chart_data.add(i, pocket.get(i));
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(chart_data);
        chart = ChartFactory.createHistogram("", "", "", dataset);
        chart.getXYPlot().setBackgroundPaint(new Color(255, 255, 255));
        ValueAxis xAxis = chart.getXYPlot().getDomainAxis();
        xAxis.setAxisLineVisible(false);

        ValueAxis yAxis = chart.getXYPlot().getRangeAxis();
        yAxis.setRange(0, (int) CountSlider.getValue());
        yAxis.setAxisLineVisible(false);

        XYBarRenderer br = (XYBarRenderer) chart.getXYPlot().getRenderer();
        chart.getLegend().setVisible(false);
        chart.getXYPlot().setOutlineVisible(false);

        return chart;
    }

    private void renderPockets() {
        int i = 0;
        for (Pane pane : pocket_panes) {
            ChartViewer viewer = new ChartViewer(createPocketChart(i++));
            viewer.setPrefSize(pane.getPrefWidth(), pane.getPrefHeight());
            pane.getChildren().clear();
            pane.getChildren().add(viewer);
        }
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
        if (shuffleCheckBox.isSelected()) {
            for (int i = data.size() - 1; i > 0; i--) {
                data.set(i, rnd.nextInt(data.size()));
            }
        }
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
        sorter.sortStart(data);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (stepCheckBox.isSelected() && !sorter.nextSmallStep() || !stepCheckBox.isSelected() && !sorter.nextStep()) {
                    unlockButtons();
                    timer.cancel();
                } else if (force_exit_flag) {
                    force_exit_flag = false;
                    unlockButtons();
                    sorter.sortStart(data);
                    syncNumbersData();
                    timer.cancel();
                }
                Platform.runLater(() -> {
                    data = sorter.getData();
                    renderPockets();
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
        shuffleCheckBox.setDisable(true);
        stopBtn.setDisable(false);
    }

    private void unlockButtons() {
        CountSlider.setDisable(false);
        delaySlider.setDisable(false);
        delayLabel.setDisable(false);
        ShuffleBtn.setDisable(false);
        stepCheckBox.setDisable(false);
        SortBtn.setDisable(false);
        shuffleCheckBox.setDisable(false);
        stopBtn.setDisable(true);
    }

    @FXML
    void stopBtnClicked(MouseEvent event) {
        force_exit_flag = true;
    }
}
