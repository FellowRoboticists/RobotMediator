package com.naiveroboticist.robotmediator;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.naiveroboticist.interfaces.IRobotMotion;

public class SpeedUpCommandTest {
    
    private IRobotCommand mCut;
    
    private static boolean mIncrementSpeedCalled = false;
    private static boolean mReIssueLastMoveCommandCalled = false;
    
    private static final IRobotStateManager mRobotStateManager = new IRobotStateManager() {

        @Override
        public IRobotMotion getRobotMotion() {
            return null;
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
            mIncrementSpeedCalled = true;
        }

        @Override
        public void resetLastMoveCommand() {
        }

        @Override
        public void setLastMoveCommand(String command) {
        }

        @Override
        public void reIssueLastMoveCommand() throws IOException {
            mReIssueLastMoveCommandCalled = true;
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
        mCut = new SpeedUpCommand();
    }

    @After
    public void tearDown() throws Exception {
        mCut = null;
        mIncrementSpeedCalled = false;
        mReIssueLastMoveCommandCalled = false;
    }

    @Test
    public void testPerform() throws IOException {
        mCut.perform(mRobotStateManager);
        assertTrue(mIncrementSpeedCalled);
        assertTrue(mReIssueLastMoveCommandCalled);
    }

}
