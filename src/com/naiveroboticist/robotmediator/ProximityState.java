package com.naiveroboticist.robotmediator;

public class ProximityState extends BaseState {

    private static final String[] ALLOWED_TRANSITIONS = { "stop" }; 

    public ProximityState(ICommand commander) {
        super(commander, ALLOWED_TRANSITIONS);
    }

}
