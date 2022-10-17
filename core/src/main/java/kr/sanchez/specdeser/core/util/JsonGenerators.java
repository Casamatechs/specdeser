package kr.sanchez.specdeser.core.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

public class JsonGenerators {

    private final static char[] CONSTANTS = new char[]{'1','2','3','4','5','6','7','8','9','0',
            'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
            'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};

    public static InputStream basicJson(int keys) {
        StringBuilder ret = new StringBuilder("{");
        for (int i = 0; i < keys; i++) {
            int t = i % 4;
            String key = "\"key" + i + "\":";
            if (t == 0) {
                ret.append(key).append("\"constant").append(i).append("\",");
            } else if (t == 1) {
                ret.append(key).append("\"string").append(ThreadLocalRandom.current().nextInt(100, 10000)).append("\",");
            } else if (t == 2) {
                ret.append(key).append(i * 10 + 42).append(",");
            } else if (t == 3) {
                ret.append(key).append(ThreadLocalRandom.current().nextInt(100, 10000));
                if (i != keys - 1) {
                    ret.append(",");
                }
            }
        }
        ret.append("}");
        return new ByteArrayInputStream(ret.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static InputStream generateStaticJson(int keys) {
        StringBuilder ret = new StringBuilder("{");
        for (int i = 0; i < keys; i++) {
            int t = i % 4;
            String key = "\"key" + i + "\":";
            if (t == 0) {
                ret.append(key).append("\"constant").append(i).append("\",");
            } else if (t == 1) {
                ret.append(key).append("\"string").append(i).append("\",");
            } else if (t == 2) {
                ret.append(key).append(i * 10 + 42).append(",");
            } else if (t == 3) {
                ret.append(key).append(i * 42 + 10);
                if (i != keys - 1) {
                    ret.append(",");
                }
            }
        }
        ret.append("}");
        return new ByteArrayInputStream(ret.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static InputStream fixedValueLength(int nKeys, int lengthValue) {
        StringBuilder ret = new StringBuilder("{");
        for (int i = 0; i < nKeys; i++) {
            String key = "\"key" + generateKeyString(nKeys, i) + "\":";
            ret.append(key).append("\"").append(generateRandomString(lengthValue)).append("\"");
            if (i < nKeys -1) ret.append(",");
        }
        ret.append("}");
        return new ByteArrayInputStream(ret.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static InputStream fixedKeyLength(int nKeys, int lengthKey, int lengthValue) {
        StringBuilder ret = new StringBuilder("{");
        for (int i = 0; i < nKeys; i++) {
            String key = "\"key" + generateLengthKeyString(i, lengthKey) + "\":";
            ret.append(key).append("\"").append(generateRandomString(lengthValue)).append("\"");
            if (i < nKeys -1) ret.append(",");
        }
        ret.append("}");
        return new ByteArrayInputStream(ret.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static String generateKeyString(int nkeys, int number) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (i++ < ((int)Math.log10(nkeys) - (int)Math.log10(Math.max(number, 1)))) {
            builder.append("0");
        }
        return builder.append(number).toString();
    }

    private static String generateLengthKeyString(int number, int lengthKey) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (i++ < lengthKey - Math.log10(Math.max(number,1)) -1) {
            builder.append("0");
        }
        return builder.append(number).toString();
    }

    private static String generateString(int lengthValue) {
        StringBuilder builder = new StringBuilder();
        int startPoint = ThreadLocalRandom.current().nextInt(0, CONSTANTS.length);
        for (int i = startPoint; i < lengthValue + startPoint; i++) {
            builder.append(CONSTANTS[i % CONSTANTS.length]);
        }
        return builder.toString();
    }

    public static String generateRandomString(int lengthValue) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lengthValue; i++) {
            builder.append(CONSTANTS[ThreadLocalRandom.current().nextInt(0, CONSTANTS.length)]);
        }
        return builder.toString();
    }

    private static String generateNumber(int len, String number) {
        StringBuilder builder = new StringBuilder(number);
        while (len-- > number.length()) {
            builder.append("0");
        }
        return builder.toString();
    }
}
