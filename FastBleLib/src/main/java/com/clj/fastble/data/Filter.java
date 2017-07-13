package com.clj.fastble.data;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by neil on 17-7-8.
 */

public class Filter {

    static public double calcMean(LinkedList list, int num) {
        double sum = 0;
        double mean;
        int i = 0;
        for (Iterator<Integer> iter = list.iterator(); iter.hasNext() && i < num; i++)
        {
            sum += iter.next();
        }
        mean = sum / num;
        return mean;
    }

    static public double variance(LinkedList list, int num) {
        double mean = calcMean(list, num);

        double var = 0;
        int i = 0;
        for (Iterator<Integer> iter = list.iterator(); iter.hasNext() && i < num; i++)
        {
            int value = iter.next();
            var += (value - mean) * (value - mean);
        }

        var /= num;

        return var;

    }

    static public int KalmanFilter(ScanResult scan) {
        LinkedList<Integer> rssiObserve = scan.mRssiList;
        LinkedList<Integer> rssiPred = scan.mRssiPredList;
        int num = (rssiObserve.size() > 15) ? 15 : rssiObserve.size();


        double predX = rssiPred.getFirst();
        double predMeasure = predX;
        double residual = rssiObserve.getFirst() - predMeasure;
        double Q = 1;
        double R = variance(rssiObserve,num);
        double P = scan.estimateStateCov + Q;
        double S = P + R;
        double kg = P / S;
        double estimateX = predX + kg * residual;
        scan.estimateStateCov = P - kg * kg * S;
        return (int) estimateX;

    }

    static public int MeanFilter(ScanResult scan) {
        LinkedList rssiPred = scan.mRssiPredList;
        int num = (rssiPred.size() > 10) ? 10 : rssiPred.size();

        return (int) calcMean(rssiPred, num);
    }

    static public int GassianFilter(ScanResult scan) {
        LinkedList<Integer> rssiObserve = scan.mRssiList;
        LinkedList<Integer> rssiPred = scan.mRssiPredList;
        int num = (rssiObserve.size() > 15) ? 15 : rssiObserve.size();

        double mean = calcMean(rssiObserve, num);
        double cov = variance(rssiObserve, num);
        double std = Math.sqrt(cov);
        LinkedList<Integer> validList = new LinkedList();
        double upper = mean + 2 * std;
        double lower = mean - 2 * std;
        for (Integer rssi: rssiObserve)
        {
            if(rssi <= upper && rssi >= lower) {
                validList.addFirst(rssi);
            }
        }
        double result = (validList.size() > 0) ? calcMean(validList, validList.size()) : mean;
        return (int) result;

    }
}