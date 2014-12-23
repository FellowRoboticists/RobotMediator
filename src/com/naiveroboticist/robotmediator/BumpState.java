package com.naiveroboticist.robotmediator;

public class BumpState extends BaseState {
    
    private static final String[] ALLOWED_TRANSITIONS = { "stop" }; 

    public BumpState(ICommand commander) {
        super(commander, ALLOWED_TRANSITIONS);
    }

}
