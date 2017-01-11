package app.model.services.serial;

import app.utility.Util;
import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.LinkedTransferQueue;

public class SerialTransport implements SerialPortEventListener{

    private String portName = "";
    private int serialBaud = 9600;
    private int serialMillisTimeout = 250;

    private InputStream in = null;
    private OutputStream out = null;
    private SerialPort serialPort = null;
    private boolean isConnected = false;

    private SerialReader reader;
    private Queue<Byte> byteQueue;

    public SerialTransport(String portName, int serialBaud, int serialMillisTimeout) {
        this.portName = portName;
        this.serialBaud = serialBaud;
        this.serialMillisTimeout = serialMillisTimeout;
        this.isConnected = false;
        this.byteQueue = new LinkedTransferQueue<>();
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

    public void connect() throws IOException {
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(this.portName);

            if (!portIdentifier.isCurrentlyOwned()) {

                System.out.println("isCurrentlyOwned() = " + portIdentifier.isCurrentlyOwned());

                CommPort commPort = portIdentifier.open("FluidXMan", this.serialMillisTimeout);

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

                    this.initListener();
//                this.reader = new SerialReader(this.in, this.byteQueue);
//                new Thread(reader).start();
//                (new Thread(new SerialWriter(this.out))).start();

                } else { throw new UnsupportedPortTypeException("Error: Only serial ports are handled by this example"); }
            } else { throw new SerialPortInUseException("Error: Port is currently in use"); }
        } catch (UnsupportedCommOperationException | NoSuchPortException | IOException | SerialPortInUseException | UnsupportedPortTypeException e) {
            throw new IOException(e);
        } catch (PortInUseException e) {
//            throw new IOException(e);
            e.printStackTrace();
            System.exit(0);
        }
//        catch (TooManyListenersException e) {
//            e.printStackTrace();
//        }
    }

    public void initListener() {
        try
        {
            System.out.println("In initListener()");
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        }
        catch (TooManyListenersException e)
        {
            System.out.println("Too many listenders!");
        }
    }

    public void disconnect() throws IOException {
        try {
            if (this.isConnected) {
//                this.reader.stop();
                this.out.flush();
                this.out.close();
                this.in.close();
                this.serialPort.removeEventListener();

//                this.serialPort.close();

                this.isConnected = false;

                System.out.println("DISCONNECTED from: " + this.serialPort.getName());
            }
        } catch (IOException e) { System.out.println("Failed to close " + this.serialPort.getName()); }
    }

    public void serialEvent(SerialPortEvent evt) {
        if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            System.out.println("In serialEvent()");
//            this.byteQueue.clear();
//            byte[] buffer = new byte[1];
//            int len = -1;
//
//            try {
//                while ((len = this.in.read(buffer)) > -1) {
//                    String str = new String(buffer, 0, len);
////                    byte[] b = str.getBytes();
////
////                    System.out.print(str);
////                    System.out.print("\tb-len: "+b.length);
////                    System.out.print("\tb: "+str.length());
////                    System.out.println("\tchar: "+ (char) b[0]);
////
////                    this.byteQueue.add((byte) (char)b[0]);
//                    this.byteQueue.add(buffer[0]);
//                }
//            } catch (IOException e) { e.printStackTrace(); }
//
//            System.out.println("q: " + this.byteQueue);

            byte[] buffer = new byte[1024];
            int len = -1;

            try {
                len = this.in.read(buffer);

                while (len != -1) {
                    this.byteQueue.add(buffer[0]);
                    len = this.in.read(buffer);
                }
            } catch (IOException e) { e.printStackTrace(); }

        }
    }

    public byte[] read() throws IOException {
        Util.sleep(100);
        if (this.byteQueue.size() > 0) {

            byte[] frame = new byte[this.byteQueue.size()];

            for(int i = 0; i < frame.length; i++)
                frame[i] = this.byteQueue.remove();

            System.out.println("frame = "+Arrays.toString(frame));

            return frame;
        } else {
            return null;
        }

//        if (this.isConnected) {
//            System.out.println("In read()");
//            byte[] buffer = new byte[1024];
//            int len = -1;
//
//            try {
//                while ((len = this.in.read(buffer)) > -1) {
//                    System.out.print(new String(buffer, 0, len));
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
////            this.byteQueue.add(buffer);
////            System.out.println("q: " + this.byteQueue);
//
//            return buffer;
//        } else {
//            throw new IOException("Cannot write to serial port "+ this.portName);
//        }
    }

//    public byte[] readNonblocking(Queue queue) {
//        byte[] buffer = new byte[1024];
//        int len = -1;
//
//        try {
//            len = this.in.read(buffer);
//
//            while (len != -1) {
//                queue.add(buffer[0]);
//                len = this.in.read(buffer);
//            }
//        } catch (IOException e) { e.printStackTrace(); }
//
//    }

    public void write(byte[] frame) throws IOException {
        if (isConnected) {
            try {
                this.out.write(frame);
                System.out.println("Writing byte[]: " + Arrays.toString(frame));
                System.out.println("Writing string: " + new String(frame));
//                this.out.flush();
            } catch (IOException e) { throw new IOException(e); }
        } else { throw new IOException("Cannot write to serial port "+ this.portName +" Not connected"); }
    }

    public static class SerialReader implements Runnable {

        private InputStream in;
        private Queue<Byte> queue;
        private boolean reading;

        public SerialReader(InputStream in, Queue<Byte> queue) {
            this.in = in;
            this.queue = queue;
        }

        public void run() {
//            byte[] buffer = new byte[1024];
//            int len = -1;
//            try {
//                while((len = this.in.read(buffer)) > -1) {
//                    System.out.print(new String(buffer, 0, len));
//                }
//            } catch(IOException e) {
//                e.printStackTrace();
//            }

            this.reading = true;
//            byte[] buffer = new byte[1024];
//            int len = -1;
//            try {
//                while((len = this.in.read(buffer)) > -1) {
//                    System.out.print(new String(buffer, 0, len));
//                }
//                this.queue.add(buffer);
//            } catch(IOException e) {
//                e.printStackTrace();
//            }

            byte[] buffer = new byte[1024];
            int len = -1;

            while (reading) {
                System.out.println("readin");
                try {
                    if ((len = this.in.read(buffer)) != -1) {
//                        System.out.print(new String(buffer, 0, len));

                        this.queue.add(buffer[0]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stop() {
            System.out.println("in stop()");
            this.reading = false;
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

    public static void main(String[] args) {  }
}