package app.utility;

import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/27/16
 */
public class Util {

    public static void restrictTextFieldLength(TextField tf, int length) {
        tf.lengthProperty().addListener((item, oldValue, newValue) -> {
            if (newValue.intValue() > oldValue.intValue()) {
                // Check if the new character is greater than 4
                if (tf.getText().length() >= length) {
                    // if it's 11th character then just setText to previous one
                    tf.setText(tf.getText().substring(0, length));
                }
            }
        });
    }

    public static void confineTextFieldToNumericAndBind(TextField tf, IntConsumer setter) {
        tf.textProperty().addListener((item, oldVal, newVal) -> {
            // if newVal is an integer and is not empty, then parseInt and setVolume
            if (newVal.matches("\\d*") && !newVal.equals("") ) {
                Integer newVolume = Integer.parseInt(newVal);
                setter.accept(newVolume);
            } else {
                tf.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
    }

    public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }

    public static void sleepMillis(int timeMillis) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleepSecs(int timeSecs) {
        try {
            TimeUnit.SECONDS.sleep(timeSecs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
