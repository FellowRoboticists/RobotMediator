package com.naiveroboticist.robotmediator;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BaseRobotCommandTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreateBackwardCommand() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        IRobotCommand cmd = BaseRobotCommand.createCommand("backward");
        assertTrue(cmd instanceof BackwardCommand);
    }

    @Test
    public void testCreateBumpCommand() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        IRobotCommand cmd = BaseRobotCommand.createCommand("bump");
        assertTrue(cmd instanceof BumpCommand);
    }

    @Test
    public void testCreateForwardCommand() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        IRobotCommand cmd = BaseRobotCommand.createCommand("forward");
        assertTrue(cmd instanceof ForwardCommand);
    }

    @Test
    public void testCreateNoopCommand() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        IRobotCommand cmd = BaseRobotCommand.createCommand("noop");
        assertTrue(cmd instanceof NoopCommand);
    }

    @Test
    public void testCreateProximityCommand() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        IRobotCommand cmd = BaseRobotCommand.createCommand("proximity");
        assertTrue(cmd instanceof ProximityCommand);
    }

    @Test
    public void testCreateRotateCcwCommand() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        IRobotCommand cmd = BaseRobotCommand.createCommand("rotate_ccw");
        assertTrue(cmd instanceof RotateCcwCommand);
    }

    @Test
    public void testCreateRotateCwCommand() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        IRobotCommand cmd = BaseRobotCommand.createCommand("rotate_cw");
        assertTrue(cmd instanceof RotateCwCommand);
    }

    @Test
    public void testCreateSlowDownCommand() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        IRobotCommand cmd = BaseRobotCommand.createCommand("slow_down");
        assertTrue(cmd instanceof SlowDownCommand);
    }

    @Test
    public void testCreateSpeedUpCommand() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        IRobotCommand cmd = BaseRobotCommand.createCommand("speed_up");
        assertTrue(cmd instanceof SpeedUpCommand);
    }

    @Test
    public void testCreateStartCommand() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        IRobotCommand cmd = BaseRobotCommand.createCommand("start");
        assertTrue(cmd instanceof StartCommand);
    }

    @Test
    public void testCreateStopCommand() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        IRobotCommand cmd = BaseRobotCommand.createCommand("stop");
        assertTrue(cmd instanceof StopCommand);
    }

}
