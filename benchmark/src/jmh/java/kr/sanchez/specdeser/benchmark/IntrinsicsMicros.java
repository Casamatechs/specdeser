package kr.sanchez.specdeser.benchmark;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonToken;
import kr.sanchez.specdeser.core.jakarta.*;
import org.glassfish.json.JsonParserImpl;
import org.glassfish.json.api.BufferPool;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import sun.misc.Unsafe;

import javax.json.stream.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

import static com.fasterxml.jackson.core.JsonToken.*;
import static kr.sanchez.specdeser.core.util.JsonGenerators.*;

public class IntrinsicsMicros {

    private static final String BASE = "XXXXXXXXXX";

    private static byte[] getInput(int minSize, int tokenAt) {
        StringBuilder s = new StringBuilder(BASE);
        while (s.length() < minSize) {
            s.append(BASE);
        }
        s.append(Math.random());
        byte[] res = s.toString().getBytes(StandardCharsets.US_ASCII);
        if (tokenAt > 0 && tokenAt + 2 < res.length) {
            res[tokenAt] = 'a';
            res[tokenAt + 1] = 'b';
        }
        return res;
    }

    public static void main(String[] args) {
        System.out.println("XXX");

        final byte[] foo = getInput(1000, 1000);

        System.out.println(foo.length);
        int r = SpeculativeParser.indexOfConstant(foo, 0, foo.length, 'a', 'b');

        System.out.println(r);
    }

    @State(Scope.Benchmark)
    public static class StateObj {
        public byte[] test100_1 = getInput(100, 1);
        public byte[] test100_10 = getInput(100, 10);
        public byte[] test100_100 = getInput(100, 100);

        public byte[] test1000_10 = getInput(1000, 10);
        public byte[] test1000_500 = getInput(1000, 500);
        public byte[] test1000_1000 = getInput(1000, 1000);

        public byte[] test100000_10 = getInput(100000, 10);
        public byte[] test100000_50000 = getInput(100000, 50000);
        public byte[] test100000_100000 = getInput(100000, 100000);

        public byte[] randomString2 = generateRandomString(2).getBytes(StandardCharsets.UTF_8);
        public byte[] randomString4 = generateRandomString(4).getBytes(StandardCharsets.UTF_8);
        public byte[] randomString8 = generateRandomString(8).getBytes(StandardCharsets.UTF_8);
        public byte[] randomString16 = generateRandomString(16).getBytes(StandardCharsets.UTF_8);
        public byte[] randomString32 = generateRandomString(32).getBytes(StandardCharsets.UTF_8);
        public byte[] randomString64 = generateRandomString(64).getBytes(StandardCharsets.UTF_8);
        public byte[] randomString128 = generateRandomString(128).getBytes(StandardCharsets.UTF_8);
        public byte[] randomString1024 = generateRandomString(1024).getBytes(StandardCharsets.UTF_8);
        public byte[] randomString16384 = generateRandomString(16384).getBytes(StandardCharsets.UTF_8);

        public byte[] equalsString2 = randomString2.clone();
        public byte[] equalsString4 = randomString4.clone();
        public byte[] equalsString8 = randomString8.clone();
        public byte[] equalsString16 = randomString16.clone();
        public byte[] equalsString32 = randomString32.clone();
        public byte[] equalsString64 = randomString64.clone();
        public byte[] equalsString128 = randomString128.clone();
        public byte[] equalsString1024 = randomString1024.clone();
        public byte[] equalsString16384 = randomString16384.clone();
    }

    private static void runMicroBaseline(byte[] input, int expectedResult, Blackhole bh) {
        int r = indexOfConstantJava(input, 0, input.length, 'a', 'b');
        if (r != expectedResult) {
            throw new RuntimeException("Wrong result " + r);
        }
        bh.consume(r);
    }

    private static void runMicroStringBaseline(byte[] input, byte[] constant, int length, Blackhole bh) {
        boolean b = equalsOfConstant(input, 0, constant, length);
        if (!b) {
            throw new RuntimeException("Wrong result " + b);
        }
        bh.consume(b);
    }

    private static void runMicroSimdEquals(byte[] input, byte[] constant, int length, Blackhole bh) {
        boolean b = SpeculativeParser.equalsOfConstant(input, 0, constant, length);
        if (!b) {
            throw new RuntimeException("Wrong result " + b);
        }
        bh.consume(b);
    }

    private static void runMicroSimd(byte[] input, int expectedResult, Blackhole bh) {
        int r = SpeculativeParser.indexOfConstant(input, 0, input.length, 'a', 'b');
        if (r != expectedResult) {
            throw new RuntimeException("Wrong result " + r);
        }
        bh.consume(r);
    }

    // ===== Baseline

    @Benchmark
    public void intrMicro_baseline100_1(StateObj stateObj, Blackhole bh) {
        runMicroBaseline(stateObj.test100_1, 1, bh);
    }

    @Benchmark
    public void intrMicro_baseline100_10(StateObj stateObj, Blackhole bh) {
        runMicroBaseline(stateObj.test100_10, 10, bh);
    }

    @Benchmark
    public void intrMicro_baseline100_100(StateObj stateObj, Blackhole bh) {
        runMicroBaseline(stateObj.test100_100, 100, bh);
    }

    @Benchmark
    public void intrMicro_baseline1000_10(StateObj stateObj, Blackhole bh) {
        runMicroBaseline(stateObj.test1000_10, 10, bh);
    }

    @Benchmark
    public void intrMicro_baseline1000_500(StateObj stateObj, Blackhole bh) {
        runMicroBaseline(stateObj.test1000_500, 500, bh);
    }

    @Benchmark
    public void intrMicro_baseline1000_1000(StateObj stateObj, Blackhole bh) {
        runMicroBaseline(stateObj.test1000_1000, 1000, bh);
    }

    @Benchmark
    public void intrMicro_baseline100000_10(StateObj stateObj, Blackhole bh) {
        runMicroBaseline(stateObj.test100000_10, 10, bh);
    }

    @Benchmark
    public void intrMicro_baseline100000_50000(StateObj stateObj, Blackhole bh) {
        runMicroBaseline(stateObj.test100000_50000, 50000, bh);
    }

    @Benchmark
    public void intrMicro_baseline100000_100000(StateObj stateObj, Blackhole bh) {
        runMicroBaseline(stateObj.test100000_100000, 100000, bh);
    }

    @Benchmark
    public void intrMicro_equals_2(StateObj stateObj, Blackhole bh) {
        runMicroStringBaseline(stateObj.randomString2, stateObj.equalsString2, stateObj.equalsString2.length, bh);
    }

    @Benchmark
    public void intrMicro_equals_4(StateObj stateObj, Blackhole bh) {
        runMicroStringBaseline(stateObj.randomString4, stateObj.equalsString4, stateObj.equalsString4.length, bh);
    }

    @Benchmark
    public void intrMicro_equals_8(StateObj stateObj, Blackhole bh) {
        runMicroStringBaseline(stateObj.randomString8, stateObj.equalsString8, stateObj.equalsString8.length, bh);
    }

    @Benchmark
    public void intrMicro_equals_16(StateObj stateObj, Blackhole bh) {
        runMicroStringBaseline(stateObj.randomString16, stateObj.equalsString16, stateObj.equalsString16.length, bh);
    }

    @Benchmark
    public void intrMicro_equals_32(StateObj stateObj, Blackhole bh) {
        runMicroStringBaseline(stateObj.randomString32, stateObj.equalsString32, stateObj.equalsString32.length, bh);
    }

    @Benchmark
    public void intrMicro_equals_64(StateObj stateObj, Blackhole bh) {
        runMicroStringBaseline(stateObj.randomString64, stateObj.equalsString64, stateObj.equalsString64.length, bh);
    }

    @Benchmark
    public void intrMicro_equals_128(StateObj stateObj, Blackhole bh) {
        runMicroStringBaseline(stateObj.randomString128, stateObj.equalsString128, stateObj.equalsString128.length, bh);
    }

    @Benchmark
    public void intrMicro_equals_1024(StateObj stateObj, Blackhole bh) {
        runMicroStringBaseline(stateObj.randomString1024, stateObj.equalsString1024, stateObj.equalsString1024.length, bh);
    }

    @Benchmark
    public void intrMicro_equals_16384(StateObj stateObj, Blackhole bh) {
        runMicroStringBaseline(stateObj.randomString16384, stateObj.equalsString16384, stateObj.equalsString16384.length, bh);
    }

    // ===== SIMD

    @Benchmark
    public void intrMicro_Simd100_1(StateObj stateObj, Blackhole bh) {
        runMicroSimd(stateObj.test100_1, 1, bh);
    }

    @Benchmark
    public void intrMicro_Simd100_10(StateObj stateObj, Blackhole bh) {
        runMicroSimd(stateObj.test100_10, 10, bh);
    }

    @Benchmark
    public void intrMicro_Simd100_100(StateObj stateObj, Blackhole bh) {
        runMicroSimd(stateObj.test100_100, 100, bh);
    }

    @Benchmark
    public void intrMicro_Simd1000_10(StateObj stateObj, Blackhole bh) {
        runMicroSimd(stateObj.test1000_10, 10, bh);
    }

    @Benchmark
    public void intrMicro_Simd1000_500(StateObj stateObj, Blackhole bh) {
        runMicroSimd(stateObj.test1000_500, 500, bh);
    }

    @Benchmark
    public void intrMicro_Simd1000_1000(StateObj stateObj, Blackhole bh) {
        runMicroSimd(stateObj.test1000_1000, 1000, bh);
    }

    @Benchmark
    public void intrMicro_Simd100000_10(StateObj stateObj, Blackhole bh) {
        runMicroSimd(stateObj.test100000_10, 10, bh);
    }

    @Benchmark
    public void intrMicro_Simd100000_50000(StateObj stateObj, Blackhole bh) {
        runMicroSimd(stateObj.test100000_50000, 50000, bh);
    }

    @Benchmark
    public void intrMicro_Simd100000_100000(StateObj stateObj, Blackhole bh) {
        runMicroSimd(stateObj.test100000_100000, 100000, bh);
    }

    @Benchmark
    public void intrMicro_Simdequals_2(StateObj stateObj, Blackhole bh) {
        runMicroSimdEquals(stateObj.randomString2, stateObj.randomString2, stateObj.randomString2.length, bh);
    }

    @Benchmark
    public void intrMicro_Simdequals_4(StateObj stateObj, Blackhole bh) {
        runMicroSimdEquals(stateObj.randomString4, stateObj.randomString4, stateObj.randomString4.length, bh);
    }

    @Benchmark
    public void intrMicro_Simdequals_8(StateObj stateObj, Blackhole bh) {
        runMicroSimdEquals(stateObj.randomString8, stateObj.randomString8, stateObj.randomString8.length, bh);
    }

    @Benchmark
    public void intrMicro_Simdequals_16(StateObj stateObj, Blackhole bh) {
        runMicroSimdEquals(stateObj.randomString16, stateObj.randomString16, stateObj.randomString16.length, bh);
    }

    @Benchmark
    public void intrMicro_Simdequals_32(StateObj stateObj, Blackhole bh) {
        runMicroSimdEquals(stateObj.randomString32, stateObj.randomString32, stateObj.randomString32.length, bh);
    }

    @Benchmark
    public void intrMicro_Simdequals_64(StateObj stateObj, Blackhole bh) {
        runMicroSimdEquals(stateObj.randomString64, stateObj.randomString64, stateObj.randomString64.length, bh);
    }

    @Benchmark
    public void intrMicro_Simdequals_128(StateObj stateObj, Blackhole bh) {
        runMicroSimdEquals(stateObj.randomString128, stateObj.randomString128, stateObj.randomString128.length, bh);
    }

    @Benchmark
    public void intrMicro_Simdequals_1024(StateObj stateObj, Blackhole bh) {
        runMicroSimdEquals(stateObj.randomString1024, stateObj.randomString1024, stateObj.randomString1024.length, bh);
    }

    @Benchmark
    public void intrMicro_Simdequals_16384(StateObj stateObj, Blackhole bh) {
        runMicroSimdEquals(stateObj.randomString16384, stateObj.randomString16384, stateObj.randomString16384.length, bh);
    }

    // === Plain java implementation

    public static int indexOfConstantJava(byte[] input, int startPos, int inputSize, int i1, int i2) {
        return indexOfConstantJava(null, input, 16L, inputSize, 0, false, startPos, i1, i2);
    }

    private static int indexOfConstantJava(Object location, byte[] array, long offset, int length, int stride, boolean isNative, int fromIndex, int i1, int i2) {
        for (int i = fromIndex + 1; i < length; i++) {
            if (readValue(array, offset, stride, i - 1, isNative) == i1 && readValue(array, offset, stride, i, isNative) == i2) return i-1;
        }
        return -1;
    }

    private static int readValue(byte[] array, long offset, int stride, int i, boolean isNative) {
        return runReadS0Managed(array, offset + i);
    }

    private static int runReadS0Managed(byte[] array, long byteOffset) {
        return uInt(UNSAFE.getByte(array, byteOffset));
    }

    private static int uInt(byte value) {
        return Byte.toUnsignedInt(value);
    }

    public static boolean equalsOfConstant(byte[] input, int inputPtr, byte[] constant, int constantLength) {
        return equalsOfConstant(null, input, 16L + inputPtr, false, constant, 16L, false, constantLength, 0);
    }

    private static boolean equalsOfConstant(Object location,
                                            byte[] arrayA, long offsetA, boolean isNativeA,
                                            byte[] arrayB, long offsetB, boolean isNativeB, int length, int stubStride) {
        int strideA = stubStrideToStrideA(stubStride);
        int strideB = stubStrideToStrideB(stubStride);
        for (int i = 0; i < length; i++) {
            if (readValue(arrayA, offsetA, strideA, i, isNativeA) != readValue(arrayB, offsetB, strideB, i, isNativeB)) {
                return false;
            }
        }
        return true;
    }

    private static int stubStrideToStrideA(int stubStride) {
        assert 0 <= stubStride && stubStride < 9 : stubStride;
        return stubStride / 3;
    }

    private static int stubStrideToStrideB(int stubStride) {
        assert 0 <= stubStride && stubStride < 9 : stubStride;
        return stubStride % 3;
    }

    private static final Unsafe UNSAFE = getUnsafe();

    private static Unsafe getUnsafe() {
        try {
            return Unsafe.getUnsafe();
        } catch (SecurityException e1) {
            try {
                Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
                theUnsafeInstance.setAccessible(true);
                return (Unsafe) theUnsafeInstance.get(Unsafe.class);
            } catch (Exception e2) {
                throw new RuntimeException("exception while trying to get Unsafe.theUnsafe via reflection:", e2);
            }
        }
    }


}