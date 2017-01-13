/*
 * Example of using jSSC library to handle serial port
 * Receive number from Arduino via USB/Serial and display on Label
 * 2, 49, 55, 90, 48, 44, 48, 44, 57, 82, 3, 54
 */
package app.model.services.serial;

import app.utility.Util;
import jssc.SerialPort;

import static jssc.SerialPort.MASK_RXCHAR;

import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

public class JSSCSerialTransport{

    private String portName = "";
    private int serialBaud = 9600;
    private int serialMillisTimeout = 250;
    SerialPort serialPort = null;
    private Queue<Byte> byteQueue = new LinkedTransferQueue<>();

    public JSSCSerialTransport(String portName, int serialBaud, int serialMillisTimeout) {
        this.portName = portName;
        this.serialBaud = serialBaud;
        this.serialMillisTimeout = serialMillisTimeout;
        this.byteQueue = new LinkedTransferQueue<>();
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

    public boolean connect() {

        System.out.println("Attempting to connect to port: " + this.portName + "...");

        boolean success = false;
        SerialPort serialPort = new SerialPort(this.portName);

        try {
            if (serialPort.openPort()) {
                serialPort.setParams(
                        this.serialBaud,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                serialPort.setEventsMask(MASK_RXCHAR);

                serialPort.addEventListener((SerialPortEvent serialPortEvent) -> {
                    if (serialPortEvent.isRXCHAR()) {
                        try {

                            byte[] frame = serialPort.readBytes();

//                            System.out.println("read: "+Arrays.toString(frame));
//                            int value = frame[0] & 0xff;    //convert to int
//                            String st = String.valueOf(value);

                            for (byte b : frame)
                                this.byteQueue.add(b);

                        } catch (SerialPortException ex) {
                            System.out.println("Cannot read from port!\nSerialPortException: " + ex.toString());
                        }

                    }
                });

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
        Util.sleepMillis(200);
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

}