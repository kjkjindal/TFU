package app.model.devices.pump.tecanapi;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/27/16
 */
public class TecanFrameContents {

    private byte statusByte;
    private byte[] data;

    public TecanFrameContents(byte statusByte, byte[] data) {
        this.statusByte = statusByte;
        this.data = data;
    }

    public byte getStatusByte() { return statusByte; }

    public void setStatusByte(byte statusByte) { this.statusByte = statusByte; }

    public byte[] getData() { return data; }

    public void setData(byte[] data) { this.data = data; }
}
