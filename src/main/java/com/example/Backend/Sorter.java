package com.example.Backend;

import java.util.ArrayList;

public class Sorter {
    private ArrayList<Integer> data;
    private ArrayList<ArrayList<Integer>> pockets;
    private int curr_index;
    private int max_index;
    private int curr_data_ind;
    private int curr_pocket_ind;
    private int curr_pocket_number_ind;

    private boolean is_graph_step;
    private STEP_RESULT step_status;

    public enum STEP_RESULT {
        GRAPH,
        POCKET,
        FINISH
    }

    public Sorter() {
        reset();
    }

    private void reset() {
        step_status = STEP_RESULT.POCKET;
        data = new ArrayList<>();
        curr_index = 0;
        max_index = 0;
        is_graph_step = false;
        curr_data_ind = 0;
        curr_pocket_ind = 0;
        curr_pocket_number_ind = 0;
        pockets = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            pockets.add(new ArrayList<>());
        }
    }

    public synchronized void nextStep() {
        if (!is_graph_step && curr_index < max_index) {
            for (Integer num : data) {
                pockets.get(getDigit(num, curr_index)).add(num);
            }
            is_graph_step = true;
            curr_index++;
            step_status = STEP_RESULT.POCKET;
        } else if (is_graph_step) {
            if (curr_data_ind >= data.size() || curr_pocket_ind >= pockets.size()) {
                is_graph_step = false;
                curr_data_ind = 0;
                curr_pocket_ind = 0;
                curr_pocket_number_ind = 0;
                for (ArrayList<Integer> pocket : pockets) {
                    pocket.clear();
                }
            } else if (curr_pocket_number_ind >= pockets.get(curr_pocket_ind).size()) {
                curr_pocket_ind++;
                curr_pocket_number_ind = 0;
            }  else {
                data.set(curr_data_ind++, pockets.get(curr_pocket_ind).get(curr_pocket_number_ind++));
            }
            step_status = STEP_RESULT.GRAPH;
        } else {
            step_status = STEP_RESULT.FINISH;
        }
    }

    public synchronized void nextSmallStep() {
        if (!is_graph_step && curr_index < max_index) {
            pockets.get(getDigit(data.get(curr_data_ind), curr_index)).add(data.get(curr_data_ind));
            curr_data_ind++;
            if (curr_data_ind >= data.size()) {
                curr_data_ind = 0;
                is_graph_step = true;
                curr_index++;
            }
            step_status = STEP_RESULT.POCKET;
        } else if (is_graph_step) {
            if (curr_data_ind >= data.size() || curr_pocket_ind >= pockets.size()) {
                is_graph_step = false;
                curr_data_ind = 0;
                curr_pocket_ind = 0;
                curr_pocket_number_ind = 0;
                for (ArrayList<Integer> pocket : pockets) {
                    pocket.clear();
                }
            } else if (curr_pocket_number_ind >= pockets.get(curr_pocket_ind).size()) {
                curr_pocket_ind++;
                curr_pocket_number_ind = 0;
            } else {
                data.set(curr_data_ind++, pockets.get(curr_pocket_ind).get(curr_pocket_number_ind++));
            }
            step_status = STEP_RESULT.GRAPH;
        } else {
            step_status = STEP_RESULT.FINISH;
        }
    }

    public STEP_RESULT step_status() {
        return step_status;
    }

    public ArrayList<Integer> getData() {
        return data;
    }

    public ArrayList<Integer> getPocket(int num) {
        synchronized (pockets) {
            return pockets.get(num);
        }
    }

    public void sortStart(ArrayList<Integer> arr) {
        reset();
        data = arr;
        max_index = maxCountOfDigits(data);
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

    private int maxCountOfDigits(ArrayList<Integer> arr) {
        int res = 0;
        for (int num : arr) {
            res = Math.max(countOfDigits(num), res);
        }
        return res;
    }
}
