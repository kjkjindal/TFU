package app.model;

import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class SerialTransport {

    private String portName = "";
    private int serialBaud = 9600;
    private int serialMillisTimeout = 250;

    private InputStream in = null;
    private OutputStream out = null;
    private SerialPort serialPort;
    private boolean isConnected;

    public SerialTransport(String portName, int serialBaud, int serialMillisTimeout) {
        this.portName = portName;
        this.serialBaud = serialBaud;
        this.serialMillisTimeout = serialMillisTimeout;
        this.isConnected = false;
    }

    public static Map<String, CommPortIdentifier> getSerialPorts() {
        Enumeration ports = CommPortIdentifier.getPortIdentifiers();

        Map<String, CommPortIdentifier> map = new HashMap<>();

        while (ports.hasMoreElements()) {
            CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();

            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
                map.put(curPort.getName(), curPort);
        }

        return map;

    }

//    public static HashSet<CommPortIdentifier> getAvailableSerialPorts() throws SerialPortInUseException {
//        HashSet<CommPortIdentifier> h = new HashSet<CommPortIdentifier>();
//        Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();
//        while (thePorts.hasMoreElements()) {
//            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
//            switch (com.getPortType()) {
//                case CommPortIdentifier.PORT_SERIAL:
//                    try {
//                        CommPort thePort = com.open("CommUtil", 50);
//                        thePort.close();
//                        h.add(com);
//                    } catch (PortInUseException e) {
////                        throw new SerialPortInUseException("Port, "  + com.getName() + ", is in use.");
//                        System.out.println("Port, "  + com.getName() + ", is in use.");
//                    } catch (Exception e) {
//                        System.err.println("Failed to open port " +  com.getName());
//                        e.printStackTrace();
//                    }
//            }
//        }
//        return h;
//    }

    public void connect() throws UnsupportedPortTypeException, SerialPortInUseException {
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(this.portName);

            if (!portIdentifier.isCurrentlyOwned()) {
                CommPort commPort = portIdentifier.open(this.getClass().getName(), this.serialMillisTimeout);

                if (commPort instanceof SerialPort) {
                    this.serialPort = (SerialPort) commPort;

                    System.out.println("CONNECTED to: "+commPort.getName());

                    this.serialPort.setSerialPortParams(
                            this.serialBaud,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE
                    );

                    serialPort.disableReceiveTimeout();
                    serialPort.enableReceiveThreshold(1);

                    this.in = this.serialPort.getInputStream();
                    this.out = this.serialPort.getOutputStream();

                    this.isConnected = true;

//                (new Thread(new SerialReader(this.in))).start();
//                (new Thread(new SerialWriter(this.out))).start();

                } else { throw new UnsupportedPortTypeException("Error: Only serial ports are handled by this example"); }
            } else { throw new SerialPortInUseException("Error: Port is currently in use"); }
        } catch (UnsupportedCommOperationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchPortException e) {
            e.printStackTrace();
        } catch (PortInUseException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() throws IOException {
        try {
            if (this.isConnected) {
                this.serialPort.close();
                this.in.close();
                this.out.close();

                System.out.println("DISCONNECTED from: " + this.serialPort.getName());
            }
        } catch (IOException e) { throw new IOException("Failed to close " + this.serialPort.getName(), e); }
    }

    public byte[] read() throws IOException {
        if (this.isConnected) {
            byte[] buffer = new byte[1024];
            int len = -1;

            try {
                while ((len = this.in.read(buffer)) > -1) {
                    System.out.print(new String(buffer, 0, len));
                }
            } catch (IOException e) { e.printStackTrace(); }

            return buffer;

        } else { throw new IOException("Cannot read from serial port "+ this.portName +" Not connected"); }
    }

    public void write(byte[] frame) throws IOException {
        if (isConnected) {
            try {
                System.out.println("Writing: " + Arrays.toString(frame));
                this.out.write(frame);
//                this.out.flush();
            } catch (IOException e) { throw new IOException(e); }
        } else { throw new IOException("Cannot write to serial port "+ this.portName +" Not connected"); }
    }

    public static class SerialReader implements Runnable {

        InputStream in;

        public SerialReader(InputStream in) {
            this.in = in;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int len = -1;
            try {
                while((len = this.in.read(buffer)) > -1) {
                    System.out.print(new String(buffer, 0, len));
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class SerialWriter implements Runnable {

        OutputStream out;

        public SerialWriter(OutputStream out) {
            this.out = out;
        }

        public void run() {
            try {
                int c = 0;
                while((c = System.in.read()) > -1) {
                    this.out.write(c);
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class SerialPortInUseException extends Exception {

        public SerialPortInUseException(String message) {
            super(message);
        }

    }

    public class UnsupportedPortTypeException extends Exception {

        public UnsupportedPortTypeException(String message) {
            super(message);
        }

    }

    public static void main(String[] args) {  }
}