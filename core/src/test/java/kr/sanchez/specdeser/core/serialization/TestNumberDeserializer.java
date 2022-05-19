package kr.sanchez.specdeser.core.serialization;

import kr.sanchez.specdeser.core.exception.DeserializationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

public class TestNumberDeserializer {

    final static byte[] naturalUTF8 = "12345".getBytes(StandardCharsets.UTF_8);
    final static byte[] negativeIntUTF8 = "-2434".getBytes(StandardCharsets.UTF_8);
    final static byte[] doubleUFT8 = "12.34".getBytes(StandardCharsets.UTF_8);
    final static byte[] negDoubleUTF8 = "-12.3".getBytes(StandardCharsets.UTF_8);
    final static byte[] longNaturalUTF8 = "2147483747".getBytes(StandardCharsets.UTF_8);
    final static byte[] expUTF8 = "15e10".getBytes(StandardCharsets.UTF_8);
    final static byte[] negExpUTF8 = "15e-5".getBytes(StandardCharsets.UTF_8);

    @Nested
    class UTF8Tests {

        NumberDeserializer deserializer;

        @BeforeEach
        void setUp() {
            deserializer = new NumberDeserializer();
        }

        @Test
        void deserializeNaturalNumber() throws DeserializationException {
            Assertions.assertEquals(deserializer.deserialize(naturalUTF8), 12345);
        }

        @Test
        void deserializeNegativeNumber() throws DeserializationException {
            Assertions.assertEquals(deserializer.deserialize(negativeIntUTF8), -2434);
        }

        @Test
        void deserializeDoubleNumber() throws DeserializationException {
            Assertions.assertEquals(deserializer.deserialize(doubleUFT8), 12.34);
        }

        @Test
        void deserializeNegDoubleNumber() throws DeserializationException {
            Assertions.assertEquals(deserializer.deserialize(negDoubleUTF8), -12.3);
        }

        @Test
        void deserializeLongNumber() throws DeserializationException {
            Assertions.assertEquals(deserializer.deserialize(longNaturalUTF8), Integer.MAX_VALUE+100L);
        }

        @Test
        void deserializeExpNumber() throws DeserializationException {
            Assertions.assertEquals(deserializer.deserialize(expUTF8), (long) 15e10); // Have to do this cast because Java takes exponential numbers as double by default.
        }

        @Test
        void deserializeNegExpNumber() throws DeserializationException {
            double prec_delta = Math.abs(deserializer.deserialize(negExpUTF8).doubleValue() - 15e-5);
            Assertions.assertTrue(prec_delta <= Math.ulp(15e-5));
        }

    }
}
