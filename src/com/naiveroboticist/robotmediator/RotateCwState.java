package com.naiveroboticist.robotmediator;

public class RotateCwState extends BaseState {

    // "stop", "noop" not supported.
    private static final String[] ALLOWED_TRANSITIONS = { 
        "forward",
        "backward",
        "rotate_ccw",
        "speed_up",
        "slow_down",
        "stop" };

    public RotateCwState(ICommand commander) {
        super(commander, ALLOWED_TRANSITIONS);
        // TODO Auto-generated constructor stub
    }

}
