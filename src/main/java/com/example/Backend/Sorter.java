package com.example.Backend;

import com.example.App.Controller;

import java.util.ArrayList;

public class Sorter {
    public ArrayList<Integer> sort(ArrayList<Integer> arr) {
        // Init
        ArrayList<ArrayList<Integer>> pockets = new ArrayList<>(10);
        for(int i=0; i < 10; i++) {
            pockets.add(new ArrayList());
        }
        // Sort
        for (int i = 0; i < maxCountOfDigits(arr); ++i) {
            for (Integer num : arr) {
                pockets.get(getDigit(num, i)).add(num);
            }
            arr.clear();
            for (ArrayList<Integer> pocket : pockets) {
                arr.addAll(pocket);
                pocket.clear();
            }
        }
        // Return
        return arr;
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

    public void print(ArrayList<Integer> arr) {
        for (int i = 0; i <arr.size(); ++i) {
            System.out.printf("%s ", arr.get(i));
        }
        System.out.println();
    }
}
