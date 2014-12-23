package com.naiveroboticist.robotmediator;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProximityStateTest {
    private ProximityState mCut;
    private String mIssuedCommand;
    
    class TestICommand implements ICommand {

        @Override
        public void command(String command) {
            mIssuedCommand = command;
            
        }
    }

    @Before
    public void setUp() throws Exception {
        
        mCut = new ProximityState(new TestICommand());
    }

    @After
    public void tearDown() throws Exception {
        
        mCut = null;
        mIssuedCommand = null;
    }

    @Test
    public void testStopCommand() throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        BaseState st = mCut.command("stop");
        assertNotNull(st);
        assertTrue(st instanceof StopState);
        assertEquals("stop", mIssuedCommand);
    }

    @Test
    public void testForwardCommand() throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        BaseState st = mCut.command("forward");
        
        assertNotNull(st);
        assertTrue(st instanceof ProximityState);
        assertNull(mIssuedCommand);
    }

    @Test
    public void testBackwardCommand() throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        BaseState st = mCut.command("backward");
        
        assertNotNull(st);
        assertTrue(st instanceof ProximityState);
        assertNull(mIssuedCommand);
    }
    
    @Test
    public void testRotateCwCommand() throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        BaseState st = mCut.command("rotate_cw");
        
        assertNotNull(st);
        assertTrue(st instanceof ProximityState);
        assertNull(mIssuedCommand);
    }
    
    @Test
    public void testRotateCcwCommand() throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        BaseState st = mCut.command("rotate_ccw");
        
        assertNotNull(st);
        assertTrue(st instanceof ProximityState);
        assertNull(mIssuedCommand);
    }
    
    @Test
    public void testSpeedUpCommand() throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        BaseState st = mCut.command("speed_up");
        
        assertNotNull(st);
        assertTrue(st instanceof ProximityState);
        assertNull(mIssuedCommand);
    }
    
    @Test
    public void testSlowDownCommand() throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        BaseState st = mCut.command("slow_down");
        
        assertNotNull(st);
        assertTrue(st instanceof ProximityState);
        assertNull(mIssuedCommand);
    }

    @Test
    public void testBumpCommand() throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        BaseState st = mCut.command("bump");
        
        assertNotNull(st);
        assertTrue(st instanceof ProximityState);
        assertNull(mIssuedCommand);
    }

    @Test
    public void testProximityCommand() throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        BaseState st = mCut.command("proximity");
        
        assertNotNull(st);
        assertTrue(st instanceof ProximityState);
        assertNull(mIssuedCommand);
    }

}
