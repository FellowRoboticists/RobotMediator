package com.naiveroboticist.robotmediator;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RotateCwStateTest {
    private RotateCwState mCut;
    private IRobotCommand mIssuedCommand;
    
    class TestICommand implements ICommand {

        @Override
        public void command(IRobotCommand command) {
            mIssuedCommand = command;
            
        }
    }

    @Before
    public void setUp() throws Exception {
        
        mCut = new RotateCwState(new TestICommand());
    }

    @After
    public void tearDown() throws Exception {
        
        mCut = null;
        mIssuedCommand = null;
    }

    @Test
    public void testStopCommand() throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        BaseState st = mCut.command("stop");
        assertNotNull(st);
        assertTrue(st instanceof StopState);
        assertTrue(mIssuedCommand instanceof StopCommand);
    }

    @Test
    public void testForwardCommand() throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        BaseState st = mCut.command("forward");
        
        assertNotNull(st);
        assertTrue(st instanceof ForwardState);
        assertTrue(mIssuedCommand instanceof ForwardCommand);
    }

    @Test
    public void testBackwardCommand() throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        BaseState st = mCut.command("backward");
        
        assertNotNull(st);
        assertTrue(st instanceof BackwardState);
        assertTrue(mIssuedCommand instanceof BackwardCommand);
    }
    
    @Test
    public void testRotateCwCommand() throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        BaseState st = mCut.command("rotate_cw");
        
        assertNotNull(st);
        assertTrue(st instanceof RotateCwState);
        assertNull(mIssuedCommand);
    }
    
    @Test
    public void testRotateCcwCommand() throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        BaseState st = mCut.command("rotate_ccw");
        
        assertNotNull(st);
        assertTrue(st instanceof RotateCcwState);
        assertTrue(mIssuedCommand instanceof RotateCcwCommand);
    }
    
    @Test
    public void testSpeedUpCommand() throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        BaseState st = mCut.command("speed_up");
        
        assertNotNull(st);
        assertTrue(st instanceof RotateCwState);
        assertTrue(mIssuedCommand instanceof SpeedUpCommand);
    }
    
    @Test
    public void testSlowDownCommand() throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        BaseState st = mCut.command("slow_down");
        
        assertNotNull(st);
        assertTrue(st instanceof RotateCwState);
        assertTrue(mIssuedCommand instanceof SlowDownCommand);
    }

    @Test
    public void testBumpCommand() throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        BaseState st = mCut.command("bump");
        
        assertNotNull(st);
        assertTrue(st instanceof RotateCwState);
        assertNull(mIssuedCommand);
    }

    @Test
    public void testProximityCommand() throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        BaseState st = mCut.command("proximity");
        
        assertNotNull(st);
        assertTrue(st instanceof RotateCwState);
        assertNull(mIssuedCommand);
    }

}
