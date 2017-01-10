package app.model.devices.pump.tecanapi;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/27/16
 */
public class TecanFrameContents {

    private byte statusByte;
    private String data;

    public TecanFrameContents(byte statusByte, char[] data) {
        this.statusByte = statusByte;
        this.data = (data != null) ? new String(data) : "";
    }

    public byte getStatusByte() { return statusByte; }

    public void setStatusByte(byte statusByte) { this.statusByte = statusByte; }

    public String getData() { return data; }

    public void setData(String data) { this.data = data; }
}
