package com.naiveroboticist.robotmediator;

import java.nio.ByteBuffer;


public class IRobotReadHandler {
    @SuppressWarnings("unused")
    private static final String TAG = IRobotReadHandler.class.getSimpleName();
    
    private static final int PACKET_LENGTH = 256;
    private static final byte START_BYTE = 0x13;
    private static final int LEN_IDX = 1;
        
    // Sensor Values
    private static final byte BUMP_WDROP = 0x07;
    @SuppressWarnings("unused")
    private static final byte WALL = 0x08;
    @SuppressWarnings("unused")
    private static final byte BUTTONS = 0x12;
    private static final byte DISTANCE = 0x13;
    private static final byte ANGLE = 0x14;
    @SuppressWarnings("unused")
    private static final byte VOLTAGE = 0x16;
    
    private IRobotListener mListener;
    private ByteBuffer mCurrentPacket = ByteBuffer.allocate(PACKET_LENGTH);
    private byte mLastData = 0x00;
    private int mDistance = 0;
    private int mAngle = 0;
    
    public IRobotReadHandler(IRobotListener listener) {
        mListener = listener;
    }
    
    public int getDistance() {
        return mDistance;
    }
    
    public int getAngle() {
        return mAngle;
    }
    
    public void bufferRead(byte[] buffer, int numBytes) {
        int startOfCommand = -1;
        
        if (mCurrentPacket.position() == 0) {
            startOfCommand = seek(buffer);
        } else {
            startOfCommand = 0;
        }
        
        if (startOfCommand == -1) {
            return;
        }
        
        // Load up the packet with the bytes from the buffer 
        // starting with the start of command
        for (int i=startOfCommand; i<numBytes; i++) {
            mCurrentPacket.put(buffer[i]);
        }
        
        if (buffer.length < startOfCommand + 2) {
            return; // LEN_IDX can't be read yet
        }
        
        // START_BYTE found, but not actually part of packet
        if (buffer[startOfCommand + 1] == 0) {
            mCurrentPacket.clear();
            return;
        }
        
        // +3 due to START byte, COUNT byte & CHKSUM bytes included 
        // with all packets
        int packetLength = mCurrentPacket.get(LEN_IDX) + 3;

        if (mCurrentPacket.position() < packetLength) {
            return;
        }

        // Verify the checksum
        int sum = 0;
        for (int i=0; i<packetLength; i++) {
            sum += mCurrentPacket.get(i);
        }
        if ((sum & (byte) 0xff) != 0) {
            // Checksum didn't match
            // Might as well clear it; it is full of badness if
            // the checksum is bad anyway.
            mCurrentPacket.clear();
            return;
        }
        
        // At this point we have a valid packet, let's see what it is
        mCurrentPacket.position(2);
        while (mCurrentPacket.hasRemaining()) {
            switch (mCurrentPacket.get()) {
            case BUMP_WDROP:
                processBumpWheelDrop();
                break;
            case DISTANCE:
                processDistance();
                break;
            case ANGLE:
                processAngle();
                break;
            default:
                // Unhandled packet type
                mCurrentPacket.position(mCurrentPacket.limit()); // Avoid infinite loop
            }
        }
        
        mCurrentPacket.clear();
    }
    
    private void processBumpWheelDrop() {
        byte data = mCurrentPacket.get();
        if (data > 0 && data < 4) {
            if (mLastData == 0) {
                mListener.hitBump(bumperToName(data));
            }
        }
        if (mLastData != 0 && data == 0) {
            mListener.bumpEnd(bumperToName(data));
        }
        if (data > 4) {
            if (mLastData != data) {
                mListener.wheelDrop();
            }
        }
        mLastData = data;

        // Eat the checksum value at the end of the packet
        mCurrentPacket.get();
    }
    
    private void processDistance() {
        int value = (mCurrentPacket.get() << 8 | mCurrentPacket.get());
        if (value > 32767) {
            value -= 65536;
        }
        mDistance += value;
        
        // Eat the checksum value at the end of the packet
        mCurrentPacket.get();
    }
    
    private void processAngle() {
        int value = (mCurrentPacket.get() << 8 | mCurrentPacket.get());
        if (value > 32767) {
            value -= 65536;
        }
        mAngle += value;
        
        // Eat the checksum value at the end of the packet
        mCurrentPacket.get();

    }
    
    private String bumperToName(byte idx) {
        String name = "Nothing";
        switch (idx) {
        case 1:
            name = "right";
            break;
        case 2:
            name = "left";
            break;
        case 3:
            name = "forward";
            break;
        }
        return name;
    }
    
    private int seek(byte[] buffer) {
        int startPosition = -1;
        for (int i=0; i<buffer.length; i++) {
            if (buffer[i] == START_BYTE) {
                startPosition = i;
                break;
            }
        }
        return startPosition;
    }

}
