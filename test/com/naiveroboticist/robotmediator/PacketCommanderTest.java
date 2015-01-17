package com.naiveroboticist.robotmediator;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.naiveroboticist.interfaces.IRobotMotion;
import com.naiveroboticist.interfaces.IRobotReader;
import com.naiveroboticist.sensor.InvalidPacketError;
import com.naiveroboticist.sensor.Packet;
import com.naiveroboticist.sensor.PacketReader;
import com.naiveroboticist.utils.ByteMethods;

public class PacketCommanderTest {
    
    private static byte[] NO_BUMP = { 0x13, 0x05, 0x07, 0x00, 0x21, 0x00, 0x00, 0x00 };
    private static byte[] BUMP_RIGHT = { 0x13, 0x05, 0x07, 0x01, 0x21, 0x00, 0x00, 0x00 };
    private static byte[] BUMP_LEFT = { 0x13, 0x05, 0x07, 0x02, 0x21, 0x00, 0x00, 0x00 };
    private static byte[] BUMP_BOTH = { 0x13, 0x05, 0x07, 0x03, 0x21, 0x00, 0x00, 0x00 };
    private static byte[] WHEEL_DROP = { 0x13, 0x05, 0x07, 0x04, 0x21, 0x00, 0x00, 0x00 };
    
    private static byte[] DISTANCE_TEMPLATE = { 0x13, 0x05, 0x07, 0x00, 0x21, 0x00, 0x00, 0x00 };
    private PacketCommander mCut;
    
    private static String mCommandToProcess;
    
    private static final IRobotStateManager mRobotStateManager = new IRobotStateManager() {

        @Override
        public IRobotMotion getRobotMotion() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void resetSpeed() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public int getSpeed() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getRotationSpeed() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void decrementSpeed() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void incrementSpeed() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void resetLastMoveCommand() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void setLastMoveCommand(String command) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void reIssueLastMoveCommand() throws IOException {
            // TODO Auto-generated method stub
            
        }

        @Override
        public ICommand getCommander() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void processCommand(String command)
                throws IllegalArgumentException, ClassNotFoundException,
                NoSuchMethodException, InstantiationException,
                IllegalAccessException, InvocationTargetException, IOException {
            mCommandToProcess = command;
            
        }
        
    };
    
    class TestIRobotReader implements IRobotReader {
        
        private byte[] mPayloadBuffer;
        boolean mBufferHasBeenRead;
        
        public TestIRobotReader(byte[] buffer) {
            mPayloadBuffer = buffer;
            mBufferHasBeenRead = false;
        }

        @Override
        public int read(byte[] buffer, int timeoutMillis) throws IOException {
            System.out.println("An attempt is being made to read the buffer");
            if (mBufferHasBeenRead) { return 0; }
            mBufferHasBeenRead = true;
            System.out.println("Packet is being read");
            for (int i=0; i<mPayloadBuffer.length; i++) {
                buffer[i] = mPayloadBuffer[i];
            }
            return mPayloadBuffer.length;
        }
        
    }

    @Before
    public void setUp() throws Exception {
        mCommandToProcess = null;
    }

    @After
    public void tearDown() throws Exception {
        mCut = null;
        mCommandToProcess = null;
    }

    @Test
    public void testNoBump() throws InterruptedException, IllegalArgumentException, InvalidPacketError, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
       mCut = new PacketCommander(new PacketReader(new TestIRobotReader(bumpBuffer(NO_BUMP)), 5, true), mRobotStateManager);
//       new Thread(mCut).start();
//       Thread.sleep(10);
//       mCut.stopAccumulating();
        
       Packet pkt = new Packet(512);
       pkt.put(bumpBuffer(NO_BUMP), 0, 8);
       mCut.processPacket(pkt);
       
       assertNull(mCommandToProcess);
    }
    
    @Test
    public void testBumpRight() throws InterruptedException, IllegalArgumentException, InvalidPacketError, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
       mCut = new PacketCommander(new PacketReader(new TestIRobotReader(bumpBuffer(BUMP_RIGHT)), 5, true), mRobotStateManager);
//       new Thread(mCut).start();
//       Thread.sleep(10);
//       mCut.stopAccumulating();
       
       Packet pkt = new Packet(512);
       pkt.put(bumpBuffer(BUMP_RIGHT), 0, 8);
       mCut.processPacket(pkt);

       assertEquals("bump", mCommandToProcess);
    }
    
    @Test
    public void testBumpLeft() throws InterruptedException, IllegalArgumentException, InvalidPacketError, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
       mCut = new PacketCommander(new PacketReader(new TestIRobotReader(bumpBuffer(BUMP_LEFT)), 5, true), mRobotStateManager);
//       new Thread(mCut).start();
//       Thread.sleep(10);
//       mCut.stopAccumulating();
       
       Packet pkt = new Packet(512);
       pkt.put(bumpBuffer(BUMP_LEFT), 0, 8);
       mCut.processPacket(pkt);

       assertEquals("bump", mCommandToProcess);
    }
    
    @Test
    public void testBumpBoth() throws InterruptedException, IllegalArgumentException, InvalidPacketError, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
       mCut = new PacketCommander(new PacketReader(new TestIRobotReader(bumpBuffer(BUMP_BOTH)), 5), mRobotStateManager);
//       new Thread(mCut).start();
//       Thread.sleep(10);
//       mCut.stopAccumulating();
       
       Packet pkt = new Packet(512);
       pkt.put(bumpBuffer(BUMP_BOTH), 0, 8);
       mCut.processPacket(pkt);

       assertEquals("bump", mCommandToProcess);
    }
    
    @Test
    public void testWheelDrop() throws InterruptedException, IllegalArgumentException, InvalidPacketError, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
       mCut = new PacketCommander(new PacketReader(new TestIRobotReader(bumpBuffer(WHEEL_DROP)), 5), mRobotStateManager);
//       new Thread(mCut).start();
//       Thread.sleep(10);
//       mCut.stopAccumulating();
       
       Packet pkt = new Packet(512);
       pkt.put(bumpBuffer(WHEEL_DROP), 0, 8);
       mCut.processPacket(pkt);
       
       assertNull(mCommandToProcess);
    }
    
    @Test
    public void testDistanceInRange() throws InterruptedException, IllegalArgumentException, InvalidPacketError, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
       mCut = new PacketCommander(new PacketReader(new TestIRobotReader(distanceBufferWithValue(15)), 5), mRobotStateManager);
//       new Thread(mCut).start();
//       Thread.sleep(10);
//       mCut.stopAccumulating();
       
       Packet pkt = new Packet(512);
       pkt.put(distanceBufferWithValue(340), 0, 8);
       mCut.processPacket(pkt);
       
       assertEquals("proximity", mCommandToProcess);
    }
    
    @Test
    public void testDistanceNegative() throws InterruptedException, IllegalArgumentException, InvalidPacketError, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
       mCut = new PacketCommander(new PacketReader(new TestIRobotReader(distanceBufferWithValue(-15)), 5), mRobotStateManager);
//       new Thread(mCut).start();
//       Thread.sleep(10);
//       mCut.stopAccumulating();
       
       Packet pkt = new Packet(512);
       pkt.put(distanceBufferWithValue(-15), 0, 8);
       mCut.processPacket(pkt);
       
       assertNull(mCommandToProcess);
    }
    
    
    private byte[] distanceBufferWithValue(int value) {
        int[] valueToConvert = { value };
        byte[] bytes = ByteMethods.wordsToBytes(valueToConvert);
        
        byte[] buffer = new byte[DISTANCE_TEMPLATE.length];
        int convertedIdx = 0;
        for (int i=0; i<DISTANCE_TEMPLATE.length; i++) {
            if (i < 5 || i > 6) {
                buffer[i] = DISTANCE_TEMPLATE[i];
            } else {
                buffer[i] = bytes[convertedIdx++];
            }
        }
        
        buffer[7] = Packet.calculateChecksum(buffer, 0, 7);
        
        return buffer;
    }
    
    private byte[] bumpBuffer(byte[] inBuffer) {
        byte[] buffer = new byte[inBuffer.length];
        
        for (int i=0; i<inBuffer.length; i++) {
            buffer[i] = inBuffer[i];
        }
        
        buffer[7] = Packet.calculateChecksum(buffer, 0, 7);
        
        return buffer;
    }

}
