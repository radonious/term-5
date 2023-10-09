package com.po_lab.sorter.app.model.chart;

import com.po_lab.sorter.app.utils.DataProcessor;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NumberBarChart extends BarChart<String,Number> {

    private DoubleProperty barWidthProperty;
    private DoubleProperty gapBetweenBarsProperty;
    private BooleanProperty isDataEmptyProperty;
    private BooleanProperty isDataShuffledProperty;
    private CategoryAxis xAxis;
    private final List<String> categoriesData;
    private Method getCategoriesData;
    private NumberAxis yAxis;
    private final ObservableList<Data<String,Number>> dataList;
    private final ObservableList<Integer> yValueList;
    private BiFunction<Integer,Object[],Data<String,Number>> dataCreator;
    private BiConsumer<Integer,Data<String,Number>> dataSetter;
    private Comparator<Data<String,Number>> dataComparator;
    private Consumer<ObservableList<Data<String,Number>>> dataShuffler;

    public NumberBarChart() {
        this(new CategoryAxis(), new NumberAxis());
    }

    public NumberBarChart(ObservableList<Series<String, Number>> observableList) {
        this(new CategoryAxis(), new NumberAxis(), observableList);
    }

    public NumberBarChart(Axis<String> axis, Axis<Number> axis1) {
        this(axis, axis1, FXCollections.observableArrayList(new Series<>()));
    }

    public NumberBarChart(Axis<String> axis, Axis<Number> axis1, ObservableList<Series<String, Number>> observableList) {
        super(axis, axis1, observableList);
        this.dataList = observableList.get(0).getData();
        this.yValueList = this.dataList
                .stream()
                .map(data->data.getYValue().intValue())
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        layout();
        initAxis();
        initProperties();
        initDataHandlers();
        initDataListeners();
        setDefaultSettings();
        this.categoriesData = getCategoriesData();
    }

    public NumberBarChart(Axis<String> axis, Axis<Number> axis1, ObservableList<Series<String, Number>> observableList, double v) {
        this(axis, axis1, observableList);
        this.setCategoryGap(v);
    }

    public double getBarWidth() {
        return barWidthProperty.get();
    }

    public DoubleProperty barWidthProperty() {
        return barWidthProperty;
    }

    public double getGapBetweenBars() {
        return gapBetweenBarsProperty.get();
    }

    public DoubleProperty gapBetweenBarsProperty() {
        return gapBetweenBarsProperty;
    }

    public boolean isDataEmpty() {
        return isDataEmptyProperty.get();
    }

    public BooleanProperty isDataEmptyProperty() {
        return isDataEmptyProperty;
    }

    public boolean isDataShuffled() {
        return isDataShuffledProperty.get();
    }

    public BooleanProperty isDataShuffledProperty() {
        return isDataShuffledProperty;
    }

    public void setShuffledState(boolean shuffledState) {
        this.isDataShuffledProperty.set(shuffledState);
    }

    public CategoryAxis getCategoryAxis() {
        return (CategoryAxis)this.getXAxis();
    }

    public List<String> getCategoriesData() {
        try {
            return (List<String>)this.getCategoriesData.invoke(this.getCategoryAxis());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCategoriesData(List<String> newCategoriesData) {
        this.categoriesData.clear();
        this.categoriesData.addAll(newCategoriesData);
    }

    public NumberAxis getNumberAxis() {
        return (NumberAxis)this.getYAxis();
    }

    public ObservableList<Data<String, Number>> getDataList() {
        return dataList;
    }

    public void setDataList(ObservableList<Data<String,Number>> newDataList) {
        this.dataList.setAll(newDataList);
    }

    public ObservableList<Integer> getyValueList() {
        return this.yValueList;
    }

    public void setYAxisRange(double lowerBound ,double upperBound) {
        this.yAxis.setLowerBound(lowerBound);
        this.yAxis.setUpperBound(upperBound);
    }

    public void setYAxisRangeByLastData(){
        double maxYValue = 0;
        if (this.dataList.size()>0) {
            maxYValue = this.dataList.get(this.dataList.size()-1).getYValue().doubleValue();
        }
        this.yAxis.setUpperBound(maxYValue+1);
    }

    public void checkBarProperties() {
        System.out.println("IS DATA EMPTY: " + isDataEmptyProperty.get());
        System.out.println("BAR WIDTH: " + barWidthProperty.get());
        System.out.println("BAR GAP: " + gapBetweenBarsProperty.get());
    }

    private void removeItemsListener() {
        try {
            Field itemListenerField = CategoryAxis.class.getDeclaredField("itemsListener");
            itemListenerField.setAccessible(true);
            Object itemsListener = itemListenerField.get(this.getXAxis());
            ((CategoryAxis)this.getXAxis())
                    .getCategories()
                    .removeListener((ListChangeListener<? super String>) itemsListener);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void initAxis(){
        this.xAxis = this.getCategoryAxis();
        this.yAxis = this.getNumberAxis();
        setYAxisRangeByLastData();
        removeItemsListener();
        try {
            this.getCategoriesData = this.xAxis
                    .getClass()
                    .getDeclaredMethod("getAllDataCategories");
            this.getCategoriesData.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void initDataProperties() {
        this.barWidthProperty = new SimpleDoubleProperty();
        this.gapBetweenBarsProperty = new SimpleDoubleProperty();
        this.isDataEmptyProperty = new SimpleBooleanProperty();
        this.isDataShuffledProperty = new SimpleBooleanProperty(false);
        BooleanBinding isDataEmptyBinding = Bindings.isEmpty(this.getData().get(0).getData());
        NumberBinding barWidthBind = Bindings
                .when(isDataEmptyBinding)
                .then(0.0)
                .otherwise(Bindings.createDoubleBinding(
                        () -> {
                            double barWidth = 0.0;
                            if (dataList.size()>0) {
                                barWidth = dataList.get(0).getNode().getBoundsInParent().getWidth();
                            }
                            return barWidth;
                            }, this.dataList)
                );
        NumberBinding gapBetweenBarsBind = Bindings
                .when(isDataEmptyBinding)
                .then(0.0)
                .otherwise(Bindings.createDoubleBinding(
                        () -> {
                            double gap = 0.0;
                            if (dataList.size()>1) {
                                double bar0MaxX = dataList.get(0).getNode().getBoundsInParent().getMaxX();
                                double bar1MinX = dataList.get(1).getNode().getBoundsInParent().getMinX();
                                gap = bar1MinX - bar0MaxX;
                            }
                            return gap;
                            }, this.dataList)
                );
        this.isDataEmptyProperty.bind(isDataEmptyBinding);
        this.barWidthProperty.bind(barWidthBind);
        this.gapBetweenBarsProperty.bind(gapBetweenBarsBind);
    }

    private void initAxisProperties() {
        DoubleBinding upperBoundYAxisBind = Bindings.createDoubleBinding(
                () -> {
                    double maxYValue = 0.0;
                    if (this.dataList.size()>0) {
                        maxYValue = this.dataList
                                .stream()
                                .max(Comparator.comparingDouble(data -> data.getYValue().doubleValue()))
                                .get()
                                .getYValue()
                                .doubleValue();
                    }
                    return maxYValue+1;
                }, this.dataList
        );
        DoubleBinding lowerBoundYAxisBind = Bindings.createDoubleBinding(
                ()->{
                    double minYValue = 0.0;
                    if (this.dataList.size()>0) {
                        minYValue = this.dataList
                                .stream()
                                .min(Comparator.comparingDouble(data -> data.getYValue().doubleValue()))
                                .get()
                                .getYValue()
                                .doubleValue();
                    }
                    return minYValue;
                }, this.dataList
        );
        DoubleBinding tickUnitBinding = Bindings.createDoubleBinding(
                ()->{
                    double tickUnit = 1;
                    int numOfTicks = 5;
                    if (dataList.size()>0) {
                        tickUnit = dataList.size()/numOfTicks+1;
                    }
                    return tickUnit;
                }, this.dataList
        );
        this.yAxis.upperBoundProperty().bind(upperBoundYAxisBind);
        this.yAxis.lowerBoundProperty().bind(lowerBoundYAxisBind);
        this.yAxis.tickUnitProperty().bind(tickUnitBinding);
    }

    private void initProperties() {
        initDataProperties();
        initAxisProperties();
    }

    private void initDataHandlers() {
        this.dataCreator = (num, newInstanceParams) -> new Data<>(String.valueOf(num),num);
        this.dataSetter = (num, data) -> data.setYValue(num);
        this.dataComparator = (data1, data2) -> {
            data1.setYValue(Integer.valueOf(data1.getXValue()));
            data2.setYValue(Integer.valueOf(data2.getXValue()));
            return 0;
        };
        this.dataShuffler = data -> {
            Random random = ThreadLocalRandom.current();
            for (int curIndex = this.dataList.size() - 1; curIndex > 0; curIndex--) {
                int randIndex = random.nextInt(curIndex + 1);
                XYChart.Data<String,Number> curData = this.dataList.get(curIndex);
                XYChart.Data<String,Number> randData = this.dataList.get(randIndex);
                Number tmpVal = curData.getYValue();
                curData.setYValue(randData.getYValue());
                randData.setYValue(tmpVal);
            }
        };
    }

    private void initDataListeners() {
        this.dataList.addListener((ListChangeListener<? super Data<String, Number>>) (change)-> {
            while (change.next()) {
                if (change.wasPermutated()) {
                    System.out.println("PERMUTATED");
                } else if (change.wasUpdated()) {
                    System.out.println("UPDATED");
                } else {
                    DataProcessor.deleteLastData(this.categoriesData,change.getRemovedSize());
                    DataProcessor.addOrderedData(
                            this.categoriesData,
                            change.getAddedSize(),
                            (num, objectParams) -> String.valueOf(num)
                    );
                }
            }
        });
        this.yValueList.addListener((ListChangeListener<? super Integer>) (change)->{
            while (change.next()) {
                if (change.wasPermutated()) {
                    System.out.println("PERMUTATED");
                    DataProcessor.setDataFromList(this.yValueList, this.dataList, this.dataSetter);
                } else if (change.wasUpdated()) {
                    System.out.println("UPDATED1");
                } else {
                    if (change.getRemovedSize() != change.getAddedSize()) {
                        DataProcessor.deleteLastData(this.dataList,change.getRemovedSize());
                        change.getAddedSubList().forEach(num->{
                            this.dataList.add(new Data<>(String.valueOf(this.dataList.size()-1),num));
                        });
                    } else  {
                        DataProcessor.setDataFromList(this.yValueList, this.dataList, this.dataSetter);
                    }
                }
            }
        });
    }
    private void setDefaultSettings() {
        this.xAxis.setAutoRanging(false);
        this.xAxis.setTickLabelsVisible(false);
        this.xAxis.setTickMarkVisible(false);
        this.yAxis.setAutoRanging(false);
        this.yAxis.setVisible(false);
        this.yAxis.setMinorTickVisible(false);
        this.yAxis.setTickMarkVisible(false);
        this.setVerticalGridLinesVisible(false);
        this.setLegendVisible(false);
        this.setAnimated(false);
        this.setCategoryGap(0);
        this.setBarGap(0.05);
    }

    private void forceRefreshProperties() {
        ObservableList<Data<String,Number>> dataListCopy = FXCollections.observableArrayList(dataList);
        dataList.setAll(dataListCopy);
    }

}
