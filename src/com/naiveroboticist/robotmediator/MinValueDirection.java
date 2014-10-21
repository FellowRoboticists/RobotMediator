package com.naiveroboticist.robotmediator;

public class MinValueDirection {
    private int nodeValue;
    private int direction;
    private int nullDirection;
    
    public MinValueDirection(int value, int direction) {
        this.nodeValue = value;
        this.direction = direction;
        this.nullDirection = direction;
    }
    
    public boolean directionSet() {
        return this.direction != this.nullDirection;
    }
    
    public void setNodeValue(int value) {
        this.nodeValue = value;
    }
    
    public int getNodeValue() {
        return this.nodeValue;
    }
    
    public void setDirection(int direction) {
        this.direction = direction;
    }
    
    public int getDirection() {
        return this.direction;
    }

}
