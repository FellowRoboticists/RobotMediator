package com.naiveroboticist.robotmediator;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.naiveroboticist.interfaces.IRobotMotion;

public class SlowDownCommandTest {
    
    private IRobotCommand mCut;
    
    private static boolean mDecrementSpeedCalled = false;
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
            mDecrementSpeedCalled = true;
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
        mCut = new SlowDownCommand();
    }

    @After
    public void tearDown() throws Exception {
        mCut = null;
        mDecrementSpeedCalled = false;
        mReIssueLastMoveCommandCalled = false;
    }

    @Test
    public void testPerform() throws IOException {
        mCut.perform(mRobotStateManager);
        assertTrue(mDecrementSpeedCalled);
        assertTrue(mReIssueLastMoveCommandCalled);
    }

}
