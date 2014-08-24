package com.naiveroboticist.robotmediator;

import java.security.Signature;
import java.util.StringTokenizer;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

public class Dsigner {
    
    private static final String TAG = Dsigner.class.getSimpleName();
    
    public static String verifyServerMessage(Context context, String signedMessage) throws Exception {
        String message = null;
        boolean valid = false;

        Signature s = Signature.getInstance("SHA256withRSA");
        s.initVerify(MediatorSettings.serverKey(context));
        
        // Break the server message into it's component parts
        byte[] msgSignature = null;
        StringTokenizer tokenizer = new StringTokenizer(signedMessage, "|");
        message = tokenizer.nextToken();
        msgSignature = Base64.decode(tokenizer.nextToken(), Base64.DEFAULT);

        s.update(message.getBytes());

        valid = s.verify(msgSignature);
        Log.i(TAG, "Signature valid? " + valid);

        return (valid) ? message : null;
    }
    
    public static String signRobotMessage(Context context, String message) throws Exception {
        String signedMessage = null;

        Signature s = Signature.getInstance("SHA256withRSA");
        s.initSign(MediatorSettings.robotKey(context));

        s.update(message.getBytes());

        String signature = Base64.encodeToString(s.sign(), Base64.NO_WRAP);
        
        signedMessage = message + "|" + signature;

        return signedMessage;
    }

}
