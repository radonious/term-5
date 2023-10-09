package com.po_lab.sorter.app.model.sorter;

import javafx.application.Platform;

import java.util.List;

public class Sorter {
    private List<Integer> data;
    private List<List<Integer>> pockets;
    private int curDigitIndex;
    private int maxDigits;
    private int curDataIndex;
    private int curPocketIndex;
    private int curPocketNumberIndex;

    private boolean isGraphStep;
    private STEP_RESULT stepStatus;

    public enum STEP_RESULT {
        GRAPH,
        POCKET,
        FINISH
    }

    public Sorter() {
        reset();
    }

    public void nextSmallStep() {
        final int curDigitIndexCopy = curDigitIndex;
        final int curDataIndexCopy = curDataIndex;
        final int pocketNumberCopy = curPocketIndex;
        final int curPocketNumberIndexCopy = curPocketNumberIndex;
        if (!isGraphStep && curDigitIndexCopy < maxDigits) {
            Platform.runLater(() -> {
                int number = data.get(curDataIndexCopy);
                pockets.get(getDigit(number,curDigitIndexCopy)).add(number);
            });
            curDataIndex++;
            if (curDataIndex >= data.size()) {
                curDataIndex = 0;
                isGraphStep = true;
                curDigitIndex++;
            }
            stepStatus = STEP_RESULT.POCKET;
        } else if (isGraphStep) {
            if (curDataIndexCopy >= data.size() || pocketNumberCopy >= pockets.size()) {
                isGraphStep = false;
                curDataIndex = 0;
                curPocketIndex = 0;
                curPocketNumberIndex = 0;
                Platform.runLater(()-> pockets.forEach(List::clear));
            } else if (curPocketNumberIndexCopy >= pockets.get(pocketNumberCopy).size()) {
                curPocketIndex++;
                curPocketNumberIndex = 0;
            } else {
                Platform.runLater(()->{
                    data.set(curDataIndexCopy, pockets.get(pocketNumberCopy).get(curPocketNumberIndexCopy));
                });
                curDataIndex++;
                curPocketNumberIndex++;
            }
            stepStatus = STEP_RESULT.GRAPH;
        } else {
            stepStatus = STEP_RESULT.FINISH;
        }
    }

    public STEP_RESULT getStepStatus() {
        return stepStatus;
    }

    public List<Integer> getData() {
        return data;
    }

    public List<Integer> getPocket(int num) {
        synchronized (pockets) {
            return pockets.get(num);
        }
    }

    public void setStartState(List<Integer> arr, List<List<Integer>> pocketsList) {
        reset();
        data = arr;
        pockets = pocketsList;
        maxDigits = maxCountOfDigits(data);
    }

    private void reset() {
        stepStatus = STEP_RESULT.POCKET;
        curDigitIndex = 0;
        maxDigits = 0;
        curDataIndex = 0;
        curPocketIndex = 0;
        curPocketNumberIndex = 0;
        isGraphStep = false;
    }

    private int getDigit(int num, int k) {
        num = Math.abs(num);
        int cur = 0;
        while (num != 0 && cur++ < k) {
            num /= 10;
        }
        return num % 10;
    }

    private int countOfDigits(int num) {
        int res = num == 0 ? 1 : 0;
        while (num != 0) {
            num /= 10;
            ++res;
        }
        return res;
    }

    private int maxCountOfDigits(List<Integer> arr) {
        int res = 0;
        for (int num : arr) {
            res = Math.max(countOfDigits(num), res);
        }
        return res;
    }
}
