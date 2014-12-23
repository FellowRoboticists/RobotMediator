package com.naiveroboticist.robotmediator;

public class RotateCcwState extends BaseState {

    // "stop", "noop" not supported.
    private static final String[] ALLOWED_TRANSITIONS = { 
        "forward",
        "backward",
        "rotate_cw",
        "speed_up",
        "slow_down",
        "stop" };

    public RotateCcwState(ICommand commander) {
        super(commander, ALLOWED_TRANSITIONS);
    }

}
