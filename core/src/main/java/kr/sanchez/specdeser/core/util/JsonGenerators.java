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

    public static InputStream generateSpotifyUser(String display_name, String href, String id, String url) {
        StringBuilder ret = new StringBuilder("{");
        ret.append("\"display_name\":");
        if (display_name != null) {
            ret.append("\"").append(display_name).append("\",");
        } else {
            ret.append("null,");
        }
        ret.append("\"href\":\"").append(href).append("\",");
        ret.append("\"id\":\"").append(id).append("\",");
        ret.append("\"type\":\"user\",");
        ret.append("\"url\":\"").append(url).append("\"");
        ret.append("}");
        return new ByteArrayInputStream(ret.toString().getBytes(StandardCharsets.UTF_8));
    }

//    public static InputStream fixedLengthKeysValuesJson(int keys, int lenKeys, int lenValues) {
//        StringBuilder ret = new StringBuilder("{");
//        for (int i = 0; i < keys; i++) {
//            int t = i % 4;
//            String key = "\"key" + generateString(lenKeys-3,String.valueOf(i)) + "\":";
//            if (t == 0) {
//                ret.append(key).append("\"cons").append(generateString(lenValues-4,String.valueOf(i))).append("\",");
//            } else if (t == 1) {
//                ret.append(key).append("\"string").append(ThreadLocalRandom.current().nextInt(100, 10000)).append("\",");
//            } else if (t == 2) {
//                ret.append(key).append(generateNumber(9,String.valueOf(i * 10 + 42))).append(",");
//            } else if (t == 3) {
//                ret.append(key).append(generateNumber(9,String.valueOf(ThreadLocalRandom.current().nextInt(100, 10000))));
//                if (i != keys - 1) {
//                    ret.append(",");
//                }
//            }
//        }
//        ret.append("}");
//        return new ByteArrayInputStream(ret.toString().getBytes(StandardCharsets.UTF_8));
//    }

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
        if (number < 1) number = 1;
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (i++ < ((int)Math.log10(nkeys) - (int)Math.log10(number))) {
            builder.append("0");
        }
        return builder.append(number).toString();
    }

    private static String generateLengthKeyString(int number, int lengthKey) {
        if (number < 1) number = 1;
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (i++ < lengthKey - Math.log10(number) -1) {
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

    private static String generateRandomString(int lengthValue) {
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
