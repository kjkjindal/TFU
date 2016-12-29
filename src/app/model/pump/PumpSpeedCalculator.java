package app.model.pump;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/24/16
 */
public class PumpSpeedCalculator {

    private static final Map<Integer, Double> speedToStroketimeMap;
    static {
        speedToStroketimeMap = new HashMap<>();
        speedToStroketimeMap.put(0, 1.45);
        speedToStroketimeMap.put(1, 1.5);
        speedToStroketimeMap.put(2, 1.6);
        speedToStroketimeMap.put(3, 1.75);
        speedToStroketimeMap.put(4, 1.9);
        speedToStroketimeMap.put(5, 2.1);
        speedToStroketimeMap.put(6, 2.6);
        speedToStroketimeMap.put(7, 3.0);
        speedToStroketimeMap.put(8, 3.4);
        speedToStroketimeMap.put(9, 3.8);
        speedToStroketimeMap.put(10, 4.0);
        speedToStroketimeMap.put(11, 4.6);
        speedToStroketimeMap.put(12, 5.0);
        speedToStroketimeMap.put(13, 6.0);
        speedToStroketimeMap.put(14, 7.6);
        speedToStroketimeMap.put(15, 10.5);
        speedToStroketimeMap.put(16, 16.0);
        speedToStroketimeMap.put(17, 34.0);
        speedToStroketimeMap.put(18, 36.0);
        speedToStroketimeMap.put(19, 38.0);
        speedToStroketimeMap.put(20, 39.0);
        speedToStroketimeMap.put(21, 40.0);
        speedToStroketimeMap.put(22, 43.0);
        speedToStroketimeMap.put(23, 46.0);
        speedToStroketimeMap.put(24, 49.0);
        speedToStroketimeMap.put(25, 53.0);
        speedToStroketimeMap.put(26, 57.0);
        speedToStroketimeMap.put(27, 63.0);
        speedToStroketimeMap.put(28, 69.0);
        speedToStroketimeMap.put(29, 79.0);
        speedToStroketimeMap.put(30, 88.0);
        speedToStroketimeMap.put(31, 104.0);
        speedToStroketimeMap.put(32, 124.0);
        speedToStroketimeMap.put(33, 154.0);
        speedToStroketimeMap.put(34, 208.0);
        speedToStroketimeMap.put(35, 308.0);
        speedToStroketimeMap.put(36, 340.0);
        speedToStroketimeMap.put(37, 382.0);
        speedToStroketimeMap.put(38, 430.0);
        speedToStroketimeMap.put(39, 515.0);
        speedToStroketimeMap.put(40, 620.0);
    }

    public static Map<String, Double> getSpeeds(Pump pump, SpeedUnits units) {
        Map<String, Double> speeds = new HashMap<>();

        int syringeVolume = pump.getSyringeVolume();

        switch (units) {
            case MICROLITERS_PER_SECOND:
                for(Integer key : speedToStroketimeMap.keySet()) {
                    double value = Math.round(syringeVolume / speedToStroketimeMap.get(key)*10.0)/10.0;
                    speeds.put(String.valueOf(value+" uL/sec"), value);
                }
                break;
            case MILLILITERS_PER_MINUTE:
                for(Integer key : speedToStroketimeMap.keySet()) {
                    double value = Math.round((syringeVolume/1000) / (speedToStroketimeMap.get(key)/60)*10.0)/10.0;
                    speeds.put(String.valueOf(value+" mL/min"), value);
                }
                break;
        }

        return speeds;

    }

    public static void main(String[] args) {
        List<Double> speeds = new ArrayList();

        int syringeVolume = 1000;

        for(Integer key : speedToStroketimeMap.keySet()) {
            speeds.add(Math.round(syringeVolume / speedToStroketimeMap.get(key)*10.0)/10.0);
        }
        System.out.println(speeds);
    }

}
