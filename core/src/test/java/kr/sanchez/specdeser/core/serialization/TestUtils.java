package kr.sanchez.specdeser.core.serialization;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TestUtils {

    @Test
    void testIsNumericValue() {
        byte[] buff = "23479085".getBytes(StandardCharsets.UTF_8);
        Assertions.assertTrue(isNumericValue(buff));
    }

    @Test
    void testIsNumericValueFalse() {
        byte[] buff = "1234.567".getBytes(StandardCharsets.UTF_8);
        Assertions.assertFalse(isNumericValue(buff));
    }

//    @Test
//    void testIsNumeric64Value() {
//        byte[] buff = "23479085".getBytes(StandardCharsets.UTF_8);
//        Assertions.assertTrue(isNumeric64Value(buff));
//    }
//
//    @Test
//    void testIsNumeric64ValueFalse() {
//        byte[] buff = "1234.567".getBytes(StandardCharsets.UTF_8);
//        Assertions.assertFalse(isNumeric64Value(buff));
//    }

    @Test
    void testIsNumeric64Short() {
        byte[] input = "23479085".getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.wrap(input);
        long buff = buffer.getLong();
        Assertions.assertTrue(isNumeric64Value(buff));
    }

    @Test
    void testIsNumberValue() {
        byte[] buff = "23479085".getBytes(StandardCharsets.UTF_8);
        Assertions.assertTrue(isNumberValue(buff));
    }

    @Test
    void testIsNumberValueFalse() {
        byte[] buff = "1234.567".getBytes(StandardCharsets.UTF_8);
        Assertions.assertFalse(isNumberValue(buff));
    }

    private boolean isNumericValue(byte[] buffer) {
        for (byte b : buffer) {
            if (((b & 0XF0) | (((b + 0x06) & 0XF0)) >> 4) != 0x33) {
                return false;
            }
        }
        return true;
    }

    private boolean isNumberValue(byte[] buffer) {
        for (byte b : buffer) {
            if (((b - '0') & 0xFF) > '9') return false;
        }
        return true;
    }

    private boolean isNumeric64Value(long val) {
//        long val = new BigInteger(buffer).longValue();
        return (((val & 0xF0F0F0F0F0F0F0F0L) |
                (((val + 0x0606060606060606L) & 0xF0F0F0F0F0F0F0F0L) >> 4)) ==
                0x3333333333333333L);
    }
}
