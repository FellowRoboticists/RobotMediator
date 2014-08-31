package com.naiveroboticist.robotmediator;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.hoho.android.usbserial.driver.UsbSerialPort;

public class RobotCommander {
    // Supported commands
    private static final byte START   = (byte) 0x80;
    private static final byte SAFE    = (byte) 0x83;
    private static final byte DRIVE   = (byte) 0x89;
    private static final byte LED     = (byte) 0x8b;
    private static final byte SONG    = (byte) 0x8c;
    private static final byte PLAY    = (byte) 0x8d;
    private static final byte STREAM  = (byte) 0x94;
    
    // Drive straight
    private static final int DRV_FWD_RAD = 0x7fff;
    
    // Standard payloads
    private static final byte[] SONG_PAYLOAD = { 0x00, 0x01, 0x48, 0xa };
    private static final byte[] PLAY_PAYLOAD = { 0x00 };
    private static final byte[] LED_PAYLOAD = { 0x08, 0x00, (byte) 0xff };
    private static final byte[] STREAM_PAYLOAD = { 0x03, 0x07, 0x13, 0x14 };

    private UsbSerialPort mPort;

    public RobotCommander(UsbSerialPort port) {
        mPort = port;
    }

    public void iRobotInitialize() throws IOException {
        sendCommand(START);
        sendCommand(SAFE);
        sendCommand(SONG, SONG_PAYLOAD);
        sendCommand(PLAY, PLAY_PAYLOAD);
        sendCommand(STREAM, STREAM_PAYLOAD);
        sendCommand(LED, LED_PAYLOAD);
    }
    
    void drive(int fwd, int rad) throws IOException {
        sendCommand(SAFE);
        if (Math.abs(rad) < 0.0001) {
            rad = DRV_FWD_RAD;
        }
        byte[] buffer = { uB(fwd), lB(fwd), uB(rad), lB(rad) };
        sendCommand(DRIVE, buffer);
    }
    
    void rotate(int vel) throws IOException {
        drive(vel, 1);
    }
    
    public void sendCommand(byte command) throws IOException {
        byte[] buffer = { command };
        sendCommand(buffer);
    }
    
    public void sendCommand(byte command, byte[] payload) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(payload.length + 1);
        buffer.put(command);
        buffer.put(payload);
        sendCommand(buffer.array());
    }
    
    public void sendCommand(byte[] buffer) throws IOException {
        mPort.write(buffer, 100);
    }
    
    private byte uB(int word) {
        return (byte) (word >> 8);
    }
    
    private byte lB(int word) {
        return (byte) (word & 0x000000ff);
    }

}