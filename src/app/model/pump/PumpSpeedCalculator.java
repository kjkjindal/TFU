package app.model.pump;

import java.util.*;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/24/16
 */
public class PumpSpeedCalculator {

    private static final Map<Integer, Double> speedToStroketimeMap;
    static {
        Map<Integer, Double> map = new HashMap<>();
        map = new HashMap<>();
        map.put(0, 1.45);
        map.put(1, 1.5);
        map.put(2, 1.6);
        map.put(3, 1.75);
        map.put(4, 1.9);
        map.put(5, 2.1);
        map.put(6, 2.6);
        map.put(7, 3.0);
        map.put(8, 3.4);
        map.put(9, 3.8);
        map.put(10, 4.0);
        map.put(11, 4.6);
        map.put(12, 5.0);
        map.put(13, 6.0);
        map.put(14, 7.6);
        map.put(15, 10.5);
        map.put(16, 16.0);
        map.put(17, 34.0);
        map.put(18, 36.0);
        map.put(19, 38.0);
        map.put(20, 39.0);
        map.put(21, 40.0);
        map.put(22, 43.0);
        map.put(23, 46.0);
        map.put(24, 49.0);
        map.put(25, 53.0);
        map.put(26, 57.0);
        map.put(27, 63.0);
        map.put(28, 69.0);
        map.put(29, 79.0);
        map.put(30, 88.0);
        map.put(31, 104.0);
        map.put(32, 124.0);
        map.put(33, 154.0);
        map.put(34, 208.0);
        map.put(35, 308.0);
        map.put(36, 340.0);
        map.put(37, 382.0);
        map.put(38, 430.0);
        map.put(39, 515.0);
        map.put(40, 620.0);
        speedToStroketimeMap = Collections.unmodifiableMap(map);
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
