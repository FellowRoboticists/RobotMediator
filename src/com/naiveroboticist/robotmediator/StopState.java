package com.naiveroboticist.robotmediator;

public class StopState extends BaseState {
    
    // "stop", "noop" not supported.
    private static final String[] ALLOWED_TRANSITIONS = { 
        "forward", 
        "backward",
        "rotate_cw",
        "rotate_ccw",
        "speed_up",
        "slow_down"};

    public StopState(ICommand commander) {
        super(commander, ALLOWED_TRANSITIONS);
    }
    
}
