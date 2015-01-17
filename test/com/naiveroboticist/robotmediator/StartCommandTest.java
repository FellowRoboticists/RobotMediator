package com.naiveroboticist.robotmediator;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.naiveroboticist.interfaces.IRobotMotion;

public class StartCommandTest {
    
    private IRobotCommand mCut;
    
    private static boolean mInitializeCalled = false;
    
    private static IRobotMotion mRobotMotion = new IRobotMotion() {

        @Override
        public void drive(int arg0, int arg1) throws IOException {
        }

        @Override
        public void initialize() throws IOException {
            mInitializeCalled = true;
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
            return 0;
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
        mCut = new StartCommand();
    }

    @After
    public void tearDown() throws Exception {
        mCut = null;
        mInitializeCalled = false;
    }

    @Test
    public void testPerform() throws IOException {
        mCut.perform(mRobotStateManager);
        assertTrue(mInitializeCalled);
    }

}
