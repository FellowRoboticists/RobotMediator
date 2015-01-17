package com.naiveroboticist.robotmediator;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.naiveroboticist.interfaces.IRobotMotion;

public class ForwardCommandTest {

    private static final int SPEED = 45;
    
    private IRobotCommand mCut;
    private static String mLastMoveCommand = null;
    private static boolean mDriveCalled = false;
    
    private static IRobotMotion mRobotMotion = new IRobotMotion() {

        @Override
        public void drive(int velocity, int angle) throws IOException {
            mDriveCalled = true;
            assertEquals(SPEED, velocity);
            assertEquals(0, angle);
        }

        @Override
        public void initialize() throws IOException {
        }

        @Override
        public void rotate(int arg0) throws IOException {
        }

        @Override
        public void stop() throws IOException {
        }
        
    };
    
    private static IRobotStateManager mRobotStateManager = new IRobotStateManager() {
        
        @Override
        public IRobotMotion getRobotMotion() {
            return mRobotMotion;
        }

        @Override
        public void resetSpeed() {
        }

        @Override
        public int getSpeed() {
            return SPEED;
        }

        @Override
        public int getRotationSpeed() {
            return 0;
        }

        @Override
        public void decrementSpeed() {
        }

        @Override
        public void incrementSpeed() {
        }

        @Override
        public void resetLastMoveCommand() {
        }

        @Override
        public void setLastMoveCommand(String command) {
            mLastMoveCommand = command;
        }

        @Override
        public void reIssueLastMoveCommand() throws IOException {
        }

        @Override
        public ICommand getCommander() {
            return null;
        }

        @Override
        public void processCommand(String command)
                throws IllegalArgumentException, ClassNotFoundException,
                NoSuchMethodException, InstantiationException,
                IllegalAccessException, InvocationTargetException, IOException {
        }
        
    };
    
    @Before
    public void setUp() throws Exception {
        mCut = new ForwardCommand();
    }

    @After
    public void tearDown() throws Exception {
        mCut = null;
    }

    @Test
    public void testPerform() throws IOException {
        mCut.perform(mRobotStateManager);
        assertTrue(mDriveCalled);
        assertEquals("forward", mLastMoveCommand);
    }

}
