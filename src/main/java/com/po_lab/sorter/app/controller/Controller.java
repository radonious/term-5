package com.po_lab.sorter.app.controller;

import com.po_lab.sorter.app.model.sorter.Sorter;
import com.po_lab.sorter.app.utils.DataProcessor;
import com.po_lab.sorter.app.model.chart.NumberBarChart;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
    private List<NumberBarChart> chartsList;
    private Sorter sorter;
    private NumberBarChart mainChart;
    private BiFunction<Integer,Object[],Integer> numCreator;
    private Consumer<List<Integer>> shuffler, altShuffler;
    @FXML
    private Pane mainPane;
    @FXML
    private Pane viewPane;
    @FXML
    private Pane pocket0;
    @FXML
    private Pane pocket1;
    @FXML
    private Pane pocket2;
    @FXML
    private Pane pocket3;
    @FXML
    private Pane pocket4;
    @FXML
    private Pane pocket5;
    @FXML
    private Pane pocket6;
    @FXML
    private Pane pocket7;
    @FXML
    private Pane pocket8;
    @FXML
    private Pane pocket9;
    @FXML
    private Label delayLabel;
    @FXML
    private CheckBox shuffleCheckBox;
    @FXML
    private CheckBox recalcCheckBox;
    @FXML
    private Button shuffleBtn;
    @FXML
    private Button sortBtn;
    @FXML
    private Button stopBtn;
    @FXML
    private Slider countSlider;
    @FXML
    private Slider delaySlider;
    @FXML
    private Spinner<Integer> pocketSpinner;

    private NumberBarChart createChart() {
        return new NumberBarChart();
    }

    private void initCharts() {
        this.mainChart = createChart();
        DataProcessor.addOrderedData(
                this.mainChart.getYValueList(),
                (int)this.countSlider.getValue(),
                this.numCreator
        );
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
        if (!shuffleCheckBox.isSelected()) {
            DataProcessor.shuffleData(mainChart.getYValueList(), this.shuffler);
        } else {
            DataProcessor.shuffleData(mainChart.getYValueList(),this.altShuffler);
        }
    }

    @FXML
    void sortBtnClicked(MouseEvent event) {
        setControlsDisable(true);
        sorter.setStartState(
                this.mainChart.getYValueList(),
                this.chartsList.stream()
                        .limit(pocketSpinner.getValue())
                        .map(NumberBarChart::getYValueList)
                        .collect(Collectors.toList()));
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // Step
                sorter.nextSmallStep();
                // Finish/Stop handle
                if (sorter.getStepStatus() == Sorter.STEP_RESULT.FINISH) {
                    timer.cancel();
                    mainChart.setShuffledState(false);
                    setControlsDisable(false);
                } else if (forceExitFlag) {
                    timer.cancel();
                    forceExitFlag = false;
                    Platform.runLater(()->{
                        DataProcessor.deleteLastData(mainChart.getYValueList(),(int)countSlider.getValue());
                        DataProcessor.addOrderedBasedData(
                                mainChart.getYValueList(),
                                (int)countSlider.getValue(),
                                10,
                                mainChart.getNumberBase(),
                                numCreator
                        );
                        mainChart.setShuffledState(false);
                        sorter.getPocketList().forEach(List::clear);
                    });
                    setControlsDisable(false);
                }
            }
        };
        timer.schedule(task, 0, (int) delaySlider.getValue());

    }

    private void setControlsDisable(boolean state) {
        shuffleBtn.setDisable(state);
        sortBtn.setDisable(state);
        stopBtn.setDisable(!state);
        shuffleCheckBox.setDisable(state);
        recalcCheckBox.setDisable(state);
        delaySlider.setDisable(state);
        countSlider.setDisable(state);
        pocketSpinner.setDisable(state);
        delayLabel.setDisable(state);
    }

    @FXML
    void stopBtnClicked(MouseEvent event) {
        forceExitFlag = true;
    }

    private void initInterfaces() {
        this.numCreator = (num, objectParams) -> num;
        this.shuffler = Collections::shuffle;
        this.altShuffler = numList -> {
            numList.replaceAll(integer -> {
                int newRandNum = ThreadLocalRandom.current().nextInt((int)countSlider.getValue());
                return DataProcessor.changeNumBase(newRandNum,10,mainChart.getNumberBase());
            });
        };
    }

    private void initSliders() {
        this.countSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (mainChart.isDataShuffled()) {
                DataProcessor.sortData(mainChart.getYValueList(), Integer::compareTo);
                mainChart.setShuffledState(false);
            }
            if (!oldValue.equals(newValue)) {
                DataProcessor.resizeBasedDataNum(
                        this.mainChart.getYValueList(),
                        newValue,
                        10,
                        mainChart.getNumberBase(),
                        this.numCreator
                );
            }
        });
        this.delaySlider.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                animationDelay = newValue.intValue();
            }
        }));
    }

    private void initSpinner() {
        pocketSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue<oldValue) {
                this.chartsList
                        .stream()
                        .skip(newValue)
                        .forEach(chart->chart.setVisible(false));
            } else {
                this.chartsList
                        .stream()
                        .skip(oldValue)
                        .limit(newValue-oldValue)
                        .forEach(chart->chart.setVisible(true));
            }
            if (recalcCheckBox.isSelected()) {
                mainChart.setNumberBase(newValue);
            }
        });
    }

    public void initCheckBox() {
        recalcCheckBox.selectedProperty().addListener((observable,oldValue,newValue)->{
            if (!newValue) {
                mainChart.setNumberBase(10);
            } else {
                mainChart.setNumberBase(pocketSpinner.getValue());
            }
        });
    }

    private void initPocketPaneList() {
        this.pocketPaneList = new ArrayList<>(10);
        this.pocketPaneList.addAll(
                Arrays.asList(
                        pocket0, pocket1,
                        pocket2, pocket3,
                        pocket4,pocket5,
                        pocket6,pocket7,
                        pocket8,pocket9
                )
        );
    }

    private void initChartsList() {
        this.chartsList = pocketPaneList
                .stream()
                .map(pane->(NumberBarChart)pane.getChildren().get(0))
                .toList();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initInterfaces();
        initSliders();
        initSpinner();
        initCheckBox();
        initPocketPaneList();
        initCharts();
        initChartsList();
        forceExitFlag = false;
        sorter = new Sorter();
    }
}