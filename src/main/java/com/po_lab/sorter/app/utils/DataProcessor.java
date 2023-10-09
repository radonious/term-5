package com.po_lab.sorter.app.utils;

import javafx.collections.FXCollections;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface DataProcessor {

    static <T> void addOrderedData(List<T> dataList,
                                   int dataNum,
                                   BiFunction<Integer,Object[],T> creator,
                                   Object... newInstanceParams) {
        if (dataNum >= 0) {
            List<T> newDataList = IntStream
                    .range(dataList.size(), dataList.size() + dataNum)
                    .mapToObj(num -> creator.apply(num,newInstanceParams))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            dataList.addAll(newDataList);
        } else {
            throw new IllegalArgumentException("Argument barsNum is less than or equal 0");
        }
    }



    static <T> void deleteLastData(List<T> dataList, int dataNum) {
        int dataListSize = dataList.size();
        if (dataNum >= 0 && dataNum <= dataListSize) {
            dataList.subList(dataListSize - dataNum, dataListSize).clear();
        } else {
            throw new IllegalArgumentException("Argument barsNum is less or equal 0");
        }
    }

    static <T> void resizeDataNum(List<T> dataList,
                                  Number newDataNum,
                                  BiFunction<Integer,Object[],T> creator,
                                  Object... newInstanceParams) {
        int dataSize = dataList.size();
        int dataDif = newDataNum.intValue() - dataSize;
        if (newDataNum.intValue() > dataSize) {
            addOrderedData(dataList,dataDif,creator,newInstanceParams);
        } else if (newDataNum.intValue() < dataSize) {
            deleteLastData(dataList, -dataDif);
        }
    }

    static <T,U> void setDataFromList(List<T> sourceList,
                                    List<U> targetList,
                                    BiConsumer<T,U> setter) {
        IntStream.range(0, sourceList.size()).forEach(
                num->setter.accept(sourceList.get(num),targetList.get(num))
        );
    }

    static <T> void clearData(List<T> dataList) {
        if (!dataList.isEmpty()) {
            dataList.clear();
        }
    }

    static <T> void sortData(List<T> dataList, Comparator<T> comparator) {
        dataList.sort(comparator);
    }

    static <T> void shuffleData(List<T> dataList, Consumer<List<T>> shuffler) {
        shuffler.accept(dataList);
    }

}
