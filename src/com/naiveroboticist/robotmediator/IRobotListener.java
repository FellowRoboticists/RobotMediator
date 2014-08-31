package com.naiveroboticist.robotmediator;

public interface IRobotListener {
    
    public void hitBump(String name);
    public void bumpEnd(String name);
    public void wheelDrop();

}
