package kr.sanchez.specdeser.core.serialization;

import kr.sanchez.specdeser.core.exception.DeserializationException;
import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;

public class TestLiteralDeserializer {
    
    final static byte[] trueUTF8 = "true".getBytes(StandardCharsets.UTF_8);
    final static byte[] falseUTF8 = "false".getBytes(StandardCharsets.UTF_8);
    final static byte[] nullUTF8 = "null".getBytes(StandardCharsets.UTF_8);

    @Nested
    class UTF8Tests {

        LiteralDeserializer deserializer;

        @BeforeEach
        void setUp() {
            deserializer = new LiteralDeserializer();
        }

        @Test
        void deserializeByteArrayTrueTest() throws DeserializationException {
            Assertions.assertTrue(deserializer.deserialize(trueUTF8));
        }

        @Test
        void deserializeByteArrayFalseTest() throws DeserializationException {
            Assertions.assertFalse(deserializer.deserialize(falseUTF8));
        }

        @Test
        void deserializeByteArrayNullTest() throws DeserializationException {
            Assertions.assertNull(deserializer.deserialize(nullUTF8));
        }

        @Test
        void deserializeByteArrayErrorLiteralTest() {
            byte[] invalidLiteral = "unll".getBytes(StandardCharsets.UTF_8);
            Assertions.assertThrows(DeserializationException.class, () -> deserializer.deserialize(invalidLiteral));
        }

        @Test
        void deserializeByteArrayErrorTrueTest() {
            byte[] invalidTrue = "truee".getBytes(StandardCharsets.UTF_8);
            Assertions.assertThrows(DeserializationException.class, () -> deserializer.deserialize(invalidTrue));
        }

        @Test
        void deserializeByteArrayErrorFalseTest() {
            byte[] invalidFalse = "faalse".getBytes(StandardCharsets.UTF_8);
            Assertions.assertThrows(DeserializationException.class, () -> deserializer.deserialize(invalidFalse));
        }

        @Test
        void safeDeserializeTrueTest() throws DeserializationException {
            Assertions.assertTrue(deserializer.deserializeSafe(trueUTF8));
        }

        @Test
        void safeDeserializeFalseTest() throws DeserializationException {
            Assertions.assertFalse(deserializer.deserializeSafe(falseUTF8));
        }

        @Test
        void safeDeserializeNullTest() throws DeserializationException {
            Assertions.assertNull(deserializer.deserializeSafe(nullUTF8));
        }

        @Test
        void safeDeserializeErrorMisspellTest() {
            byte[] misspellTrue = "treu".getBytes(StandardCharsets.UTF_8);
            Assertions.assertThrows(DeserializationException.class, ()-> deserializer.deserializeSafe(misspellTrue));
        }
    }

    @Nested
    class ASCIITests { // This class is not really mandatory for this test since latin letters share the same encoding values for all the encoders.

        LiteralDeserializer asciiDeserializer;

        @BeforeEach
        void setUp() {
            asciiDeserializer = new LiteralDeserializer(StandardCharsets.US_ASCII);
        }

        @Test
        void deserializeByteArrayAsciiTest() throws DeserializationException {
            byte[] asciiTrue = "true".getBytes(StandardCharsets.US_ASCII);
            Assertions.assertTrue(asciiDeserializer.deserialize(asciiTrue));
        }

    }
    
}
