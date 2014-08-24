package com.naiveroboticist.robotmediator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

public class MediatorSettings {
    
    public static final String TAG = MediatorSettings.class.getSimpleName();
    
    public static PrivateKey robotKey(Context context) throws Exception {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        
        return getPrivateKey(sharedPref.getString("robot_key", null));
    }

    public static String robotName(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        
        return sharedPref.getString("robot_name", null);
    }

    public static PublicKey serverKey(Context context) throws Exception {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        
        return getPublicKey(sharedPref.getString("telep_key", null));
    }

    public static InetAddress telepHost(Context context) throws UnknownHostException {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        
        return InetAddress.getByName(sharedPref.getString("telep_host", null));
    }

    public static long telepPort(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        
        String portString = sharedPref.getString("telep_port", "0");
        
        return Long.parseLong(portString);
    }
    
    private static PrivateKey getPrivateKey(String thePrivateKey) throws Exception {
        PrivateKey privateKey = null;
        String pvtKey = thePrivateKey.replaceAll("(-+BEGIN RSA PRIVATE KEY-+\\r?\\n|-+END RSA PRIVATE KEY-+\\r?\\n?)", "");
        
    
        byte[] keyBytes = Base64.decode(pvtKey, Base64.DEFAULT);
    
        // generate private key
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        privateKey = keyFactory.generatePrivate(spec);
        
        return privateKey;
    }
    
    private static PublicKey getPublicKey(String thePublicKey) throws Exception {
        PublicKey publicKey = null;
        String pubKey = thePublicKey.replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n|-+END PUBLIC KEY-+\\r?\\n?)", "");

        byte[] keyBytes = Base64.decode(pubKey, Base64.DEFAULT);

        // generate public key
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        publicKey = keyFactory.generatePublic(spec);
        
        return publicKey;
    }
}
