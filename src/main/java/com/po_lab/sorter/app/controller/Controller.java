package com.po_lab.sorter.app.controller;

import com.po_lab.sorter.app.model.sorter.Sorter;
import com.po_lab.sorter.app.utils.DataProcessor;
import com.po_lab.sorter.app.model.chart.NumberBarChart;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    private boolean forceExitFlag;
    private int animationDelay;
    private List<Integer> data;
    private List<Pane> pocketPaneList;
    private List<List<Integer>> chartsDataList;
    private Sorter sorter;
    private NumberBarChart mainChart;
    private BiFunction<Integer,Object[],Integer> numCreator;
    private Comparator<XYChart.Data<String,Number>> comparator;
    private Consumer<ObservableList<XYChart.Data<String,Number>>> shuffler;
    @FXML
    private Slider countSlider;
    @FXML
    private Pane MainPane;
    @FXML
    private Button ShuffleBtn;
    @FXML
    private Button SortBtn;
    @FXML
    private Pane viewPane;
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

    private NumberBarChart createChart() {
        return new NumberBarChart();
    }

    private void initCharts() {
        this.mainChart = createChart();
        DataProcessor.addOrderedData(this.mainChart.getyValueList(), (int) this.countSlider.getValue(), (num, newInstanceParams) -> num);
        this.mainChart.setPrefSize(viewPane.getPrefWidth(), viewPane.getPrefHeight());
        this.viewPane.getChildren().add(mainChart);
        this.pocketPaneList.forEach(pane -> {
            NumberBarChart chart = createChart();
            DataProcessor.clearData(chart.getDataList());
            chart.setMinSize(0,0);
            chart.setPrefSize(pane.getPrefWidth(), pane.getPrefHeight()+10);
            pane.getChildren().add(chart);
        });
    }

    @FXML
    void shuffleBtnClicked(MouseEvent event) {
        this.mainChart.setShuffledState(true);
        DataProcessor.shuffleData(mainChart.getyValueList(), Collections::shuffle);
    }

    @FXML
    void sortBtnClicked(MouseEvent event) {
        setControlsDisable(true);
        sorter.setStartState(this.mainChart.getyValueList(), this.chartsDataList);
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // Step
                sorter.nextSmallStep();
                // Finish/Stop handle
                if (sorter.getStepStatus() == Sorter.STEP_RESULT.FINISH) {
                    timer.cancel();
                    setControlsDisable(false);
                } else if (forceExitFlag) {
                    timer.cancel();
                    forceExitFlag = false;
                    Platform.runLater(()->{
                        countSlider.setValue(countSlider.getValue()+1);
                        countSlider.setValue(countSlider.getValue()-1);
                        chartsDataList.forEach(List::clear);
                    });

                    setControlsDisable(false);
                }
            }
        };
        timer.schedule(task, 0, (int) delaySlider.getValue());

    }

    private void setControlsDisable(boolean state) {
        countSlider.setDisable(state);
        delaySlider.setDisable(state);
        delayLabel.setDisable(state);
        ShuffleBtn.setDisable(state);
        stepCheckBox.setDisable(state);
        SortBtn.setDisable(state);
        shuffleCheckBox.setDisable(state);
        stopBtn.setDisable(!state);
    }

    @FXML
    void stopBtnClicked(MouseEvent event) {
        forceExitFlag = true;
    }

    private void initInterfaces() {
        this.numCreator = (num, objectParams) -> num;
        this.comparator = (data1, data2) -> {
            data1.setYValue(Integer.valueOf(data1.getXValue()));
            data2.setYValue(Integer.valueOf(data2.getXValue()));
            return 0;
        };
        this.shuffler = data -> {
            Random random = ThreadLocalRandom.current();
            for (int curIndex = mainChart.getDataList().size() - 1; curIndex > 0; curIndex--) {
                int randIndex = random.nextInt(curIndex + 1);
                XYChart.Data<String,Number> curData = mainChart.getDataList().get(curIndex);
                XYChart.Data<String,Number> randData = mainChart.getDataList().get(randIndex);
                double tmpVal = curData.getYValue().doubleValue();
                curData.setYValue(randData.getYValue());
                randData.setYValue(tmpVal);
            }
        };
    }

    private void initSliders() {
        this.countSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            System.out.println("ADSASD");
            if (mainChart.isDataShuffled()) {
                DataProcessor.sortData(mainChart.getyValueList(), Integer::compareTo);
                mainChart.setShuffledState(false);
            }
            if (!oldValue.equals(newValue)) {
                DataProcessor.resizeDataNum(this.mainChart.getyValueList(), newValue, this.numCreator);
            }
        });
        this.delaySlider.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                animationDelay = newValue.intValue();
            }
        }));
    }

    private void initPocketPaneList() {
        this.pocketPaneList = new ArrayList<>(10);
        this.pocketPaneList.addAll(
                Arrays.asList(
                        Pocket0,Pocket1,
                        Pocket2,Pocket3,
                        Pocket4,Pocket5,
                        Pocket6,Pocket7,
                        Pocket8,Pocket9
                )
        );
    }

    private void initChartsDataList() {
        this.chartsDataList = pocketPaneList
                .stream()
                .map(pane->((NumberBarChart)pane.getChildren().get(0)).getyValueList())
                .collect(Collectors.toCollection(ArrayList::new));
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initInterfaces();
        initSliders();
        initPocketPaneList();
        initCharts();
        initChartsDataList();
        forceExitFlag = false;
        sorter = new Sorter();
    }
}