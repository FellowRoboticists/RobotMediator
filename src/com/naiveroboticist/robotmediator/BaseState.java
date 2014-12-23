package com.naiveroboticist.robotmediator;

import android.annotation.SuppressLint;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class BaseState {
    private static final String[] SELF_TRANSITION_COMMANDS = { "speed_up", "slow_down" };
    private static final String STATE_CLASS_NAME_FMT = "com.naiveroboticist.robotmediator.%sState";
    private ICommand mCommander;
    private String[] mAllowedTransitions;

    public BaseState(ICommand commander, String[] allowedTransitions) {
        mCommander = commander;
        mAllowedTransitions = allowedTransitions;
    }
    
    public ICommand getCommander() {
        return mCommander;
    }
    
    public BaseState command(String command) throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        BaseState nextState = this;
        if (supportedTransition(command)) {
            nextState = processCommand(command);
        }
        return nextState;
    }
    
    protected void issueCommand(String command) {
        mCommander.command(command);
    }
    
    protected BaseState processCommand(String command) throws ClassNotFoundException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        issueCommand(command);
        return nextState(command);
    }
    
    protected boolean supportedTransition(String transition) {
        return inList(mAllowedTransitions, transition);
    }
    
    protected BaseState nextState(String command) throws ClassNotFoundException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        BaseState state = null;
        if (inList(SELF_TRANSITION_COMMANDS, command)) {
            state = this;
        } else {
            String className = String.format(STATE_CLASS_NAME_FMT, camelCase(command));
            Class<?> clazz = Class.forName(className);
            Constructor<?> c = clazz.getDeclaredConstructor(ICommand.class);
            state = (BaseState) c.newInstance(getCommander());
        }
        return state;
    }
    
    private boolean inList(String[] list, String value) {
        boolean inThere = false;
        for (int i=0; i<list.length; i++) {
            if (value.equals(list[i])) {
                inThere = true;
                break;
            }
        }
        return inThere;
    }
    
    @SuppressLint("DefaultLocale")
    private String camelCase(String st) {
        boolean firstLetter = true;
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<st.length(); i++) {
            String l = st.substring(i, i+1);
            if (firstLetter) {
                sb.append(l.toUpperCase());
                firstLetter = false;
            } else if (l.equals("_")) {
                firstLetter = true;
            } else {
                sb.append(l.toLowerCase());
            }
        }
        
        return sb.toString();
    }

}
