package com.example.bancasd;

import java.security.SecureRandom;
import java.util.Locale;

public final class UUIDv4 {

    public static String getUUID(){
        int len = 20;
        char[] buf = new char[len];
        SecureRandom random = new SecureRandom();
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        char[] symbols = (upper + upper.toLowerCase(Locale.ROOT) + "0123456789").toCharArray();
        for (int i = 0; i < len; i++)
            buf[i] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

}
