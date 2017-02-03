/*
 * Example of using jSSC library to handle serial port
 * Receive number from Arduino via USB/Serial and display on Label
 * 2, 49, 55, 90, 48, 44, 48, 44, 57, 82, 3, 54
 */
package app.model.services.serial;

import app.utility.Util;
import jssc.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

import static jssc.SerialPort.MASK_RXCHAR;

public class JSSCSerialTransportSingleton {

    private SerialPort serialPort = null;
    private Queue<Byte> byteQueue = new LinkedTransferQueue<>();

    private JSSCSerialTransportSingleton() {
        this.byteQueue = new LinkedTransferQueue<>();
    }

    private static class SingletonHelper{
        private static final JSSCSerialTransportSingleton INSTANCE = new JSSCSerialTransportSingleton();
    }

    public static JSSCSerialTransportSingleton getInstance(){
        return SingletonHelper.INSTANCE;
    }

    public static List<String> getSerialPorts() {

        List<String> portList = new ArrayList<>();

        String[] serialPortNames = SerialPortList.getPortNames();

        for (String name : serialPortNames) {
            System.out.println(name);

            portList.add(name);
        }

        return portList;
    }

    public boolean connect(String portName, int serialBaud) {

        this.disconnect();

        System.out.println("Attempting to connect to port: " + portName + "...");

        boolean success = false;
        SerialPort serialPort = new SerialPort(portName);

        try {
            if (serialPort.openPort()) {
                serialPort.setParams(
                        serialBaud,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                serialPort.setEventsMask(MASK_RXCHAR);

                serialPort.addEventListener(new SerialListener(this.byteQueue));

                this.serialPort = serialPort;
                success = true;

                System.out.println("Connected!");
            } else {
                System.out.println("Failed to connect!");
            }
        } catch (SerialPortException ex) {
            System.out.println("Failed to connect!\nSerialPortException: " + ex.toString());
        }

        return success;
    }

    public void disconnect() {

        if (this.serialPort != null) {

            System.out.println("Attempting to disconnect from port: " + this.serialPort.getPortName() + "...");

            try {
                this.serialPort.removeEventListener();

                if (this.serialPort.isOpened())
                    this.serialPort.closePort();

                this.serialPort = null;

                System.out.println("Disconnected!");
            } catch (SerialPortException ex) {
                System.out.println("Failed to disconnect!\nSerialPortException: " + ex.toString());
            }
        }
    }

    public synchronized void write(byte[] frame) {
        try {
                if (serialPort != null)
                    serialPort.writeBytes(frame);
                else
                    System.out.println("serialPort not connected!");

        } catch (SerialPortException ex) {
            System.out.println("Could not write to port!\nSerialPortException: " + ex.toString());
        }
    }

    public byte[] read() {
        Util.sleepMillis(100);
        if (this.byteQueue.size() > 0) {

            byte[] frame = new byte[this.byteQueue.size()];

            for(int i = 0; i < frame.length; i++)
                frame[i] = this.byteQueue.remove();

            System.out.println("frame = "+ Arrays.toString(frame));

            return frame;
        } else {
            return null;
        }
    }

    private class SerialListener implements SerialPortEventListener {

        private Queue<Byte> byteQueue;

        SerialListener(Queue<Byte> queue) {
            this.byteQueue = queue;
        }

        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
            if (serialPortEvent.isRXCHAR()) {
                try {

                    byte[] frame = serialPort.readBytes();

                    for (byte b : frame)
                        this.byteQueue.add(b);

                } catch (SerialPortException ex) {
                    System.out.println("Cannot read from port!\nSerialPortException: " + ex.toString());
                }

            }
        }

    }

}