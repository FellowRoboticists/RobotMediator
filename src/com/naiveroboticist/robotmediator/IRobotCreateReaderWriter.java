package com.naiveroboticist.robotmediator;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.naiveroboticist.interfaces.RobotReaderWriter;

public class IRobotCreateReaderWriter implements RobotReaderWriter {
    private UsbSerialPort mPort;
    
    public IRobotCreateReaderWriter(UsbSerialPort port) {
        mPort = port;
    }

    @Override
    public void sendCommand(byte command) throws IOException {
        byte[] buffer = { command };
        sendCommand(buffer);
    }
    
    @Override
    public void sendCommand(byte command, byte[] payload) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(payload.length + 1);
        buffer.put(command);
        buffer.put(payload);
        sendCommand(buffer.array());
    }
    
    @Override
    public void sendCommand(byte[] buffer) throws IOException {
        mPort.write(buffer, 100);
    }

    @Override
    public int read(byte[] buffer, int timeoutMillis) throws IOException {
        return mPort.read(buffer, timeoutMillis);
    }
}
