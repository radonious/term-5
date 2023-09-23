package com.example.Backend;

import java.util.ArrayList;

public class Sorter {
    private ArrayList<Integer> data;
    private ArrayList<ArrayList<Integer>> pockets;
    private int curr_index;
    private int max_index;
    private boolean flag;
    private int curr_data_ind;
    private int curr_pocket_ind;
    private int curr_number_ind;

    public boolean nextStep() {
        if (curr_index < max_index) {
            for (Integer num : data) {
                pockets.get(getDigit(num, curr_index)).add(num);
            }
            data.clear();
            for (ArrayList<Integer> pocket : pockets) {
                data.addAll(pocket);
                pocket.clear();
            }
            curr_index++;
            return true;
        } else {
            return false;
        }
    }

    public boolean nextSmallStep() {
        if (!flag && curr_index < max_index) {
            for (Integer num : data) {
                pockets.get(getDigit(num, curr_index)).add(num);
            }
            flag = true;
            curr_index++;
            return true;
        } else if (flag) {
            if (curr_data_ind >= data.size()) {
                flag = false;
                curr_data_ind = 0;
                curr_pocket_ind = 0;
                curr_number_ind = 0;
                for (ArrayList<Integer> pocket : pockets) {
                    pocket.clear();
                }
            } else if (curr_number_ind >= pockets.get(curr_pocket_ind).size()) {
                curr_pocket_ind++;
                curr_number_ind = 0;
            } else {
                data.set(curr_data_ind++, pockets.get(curr_pocket_ind).get(curr_number_ind++));
            }
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Integer> getData() {
        return data;
    }

    public void sortStart(ArrayList<Integer> arr) {
        flag = false;
        curr_data_ind = 0;
        curr_pocket_ind = 0;
        curr_number_ind = 0;
        curr_index = 0;
        max_index = maxCountOfDigits(arr);
        data = arr;
        pockets = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            pockets.add(new ArrayList<>());
        }
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

    public void print() {
        for (Integer num : data) {
            System.out.printf("%s ", num);
        }
        System.out.println();
    }
}
