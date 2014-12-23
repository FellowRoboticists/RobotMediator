package com.naiveroboticist.robotmediator;

public class BackwardState extends BaseState {

    // "stop", "noop" not supported.
    private static final String[] ALLOWED_TRANSITIONS = { 
        "forward",
        "rotate_cw",
        "rotate_ccw",
        "speed_up",
        "slow_down",
        "stop" };

    public BackwardState(ICommand commander) {
        super(commander, ALLOWED_TRANSITIONS);
    }

}
