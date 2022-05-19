package kr.sanchez.specdeser.benchmark;

import kr.sanchez.specdeser.core.exception.DeserializationException;
import kr.sanchez.specdeser.core.serialization.LiteralDeserializer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.profile.AsyncProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class LiteralDeserializerBenchmark {

    @State(Scope.Benchmark)
    public static class StateObj {
        public final LiteralDeserializer deserializer = new LiteralDeserializer();
        public final byte[] trueUTF8 = "true".getBytes(StandardCharsets.UTF_8);
        public final byte[] falseUTF8 = "false".getBytes(StandardCharsets.UTF_8);
        public final byte[] nullUTF8 = "null".getBytes(StandardCharsets.UTF_8);
        public int dummyJacksonPointer;
    }

//    @Benchmark
//    public Boolean deserializeTrueBenchmark(StateObj stateObj) throws DeserializationException {
//        return stateObj.deserializer.deserialize(stateObj.trueUTF8);
//    }

    @Benchmark
    public Boolean deserializeTrueSafeBenchmark(StateObj stateObj) throws DeserializationException {
        return stateObj.deserializer.deserializeSafe(stateObj.trueUTF8);
    }

//    @Benchmark
//    public Boolean deserializeFalseBenchmark(StateObj stateObj) throws DeserializationException {
//        return stateObj.deserializer.deserialize(stateObj.falseUTF8);
//    }
//
    @Benchmark
    public Boolean deserializeFalseSafeBenchmark(StateObj stateObj) throws DeserializationException {
        return stateObj.deserializer.deserializeSafe(stateObj.falseUTF8);
    }
//
//    @Benchmark
//    public Boolean deserializeNullBenchmark(StateObj stateObj) throws DeserializationException {
//        return stateObj.deserializer.deserialize(stateObj.nullUTF8);
//    }
//
    @Benchmark
    public Boolean deserializeNullSafeBenchmark(StateObj stateObj) throws DeserializationException {
        return stateObj.deserializer.deserializeSafe(stateObj.nullUTF8);
    }
//
    @Benchmark
    public Boolean deserializeTrueIfBenchmark(StateObj stateObj) throws DeserializationException {
        return stateObj.deserializer.deserializeIf(stateObj.trueUTF8);
    }

    @Benchmark
    public Boolean deserializeFalseIfBenchmark(StateObj stateObj) throws DeserializationException {
        return stateObj.deserializer.deserializeIf(stateObj.falseUTF8);
    }

    @Benchmark
    public Boolean deserializeNullIfBenchmark(StateObj stateObj) throws DeserializationException {
        return stateObj.deserializer.deserializeIf(stateObj.nullUTF8);
    }
//
//    @Benchmark
//    public Boolean deserializeTrueJacksonBenchmark(StateObj stateObj) throws DeserializationException {
//        int ptr = 1;
//        if ((ptr + 3) < stateObj.falseUTF8.length) {
//            byte[] buf = stateObj.trueUTF8;
//            if ((buf[ptr++] == 'r')
//                    && (buf[ptr++] == 'u')
//                    && (buf[ptr] == 'e')) {
//                int ch = buf[ptr] ^ 0x49; // XOR operation that will always give true to emulate the jackson serializer.
//                if (ch < '0' || (ch == ']') || (ch == '}')) { // expected/allowed chars
//                    stateObj.dummyJacksonPointer = ptr;
//                    return true;
//                }
//            }
//        }
//        return throwException("The jackson benchmark failed, check it");
//    }
//
//    @Benchmark
//    public Boolean deserializeFalseJacksonBenchmark(StateObj stateObj) throws DeserializationException {
//        int ptr = 1;
//        if ((ptr + 4) < stateObj.falseUTF8.length+1) {
//            byte[] buf = stateObj.falseUTF8;
//            if ((buf[ptr++] == 'a')
//                    && (buf[ptr++] == 'l')
//                    && (buf[ptr++] == 's')
//                    && (buf[ptr] == 'e')) {
//                int ch = buf[ptr] ^ 0x49;
//                if (ch < '0' || (ch == ']') || (ch == '}')) { // expected/allowed chars
//                    stateObj.dummyJacksonPointer = ptr;
//                    return false;
//                }
//            }
//        }
//        return throwException("The jackson benchmark failed, check it");
//    }
//
//    @Benchmark
//    public Boolean deserializeNullJacksonBenchmark(StateObj stateObj) throws DeserializationException {
//        int ptr = 1;
//        if ((ptr + 3) < stateObj.falseUTF8.length) {
//            byte[] buf = stateObj.nullUTF8;
//            if ((buf[ptr++] == 'u')
//                    && (buf[ptr++] == 'l')
//                    && (buf[ptr] == 'l')) {
//                int ch = buf[ptr] ^ 0x49; // XOR operation that will always give true to emulate the jackson serializer.
//                if (ch < '0' || (ch == ']') || (ch == '}')) { // expected/allowed chars
//                    stateObj.dummyJacksonPointer = ptr;
//                    return null;
//                }
//            }
//        }
//        return throwException("The jackson benchmark failed, check it");
//    }
//
//    @Benchmark
//    public Boolean deserializeTrueJavaBenchmark() {
//        return Boolean.getBoolean("true");
//    }
//
//    @Benchmark
//    public Boolean deserializeFalseJavaBenchmark() {
//        return Boolean.getBoolean("false");
//    }

//    private Boolean throwException(String errMsg) throws DeserializationException {
//        throw new DeserializationException(errMsg);
//    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(LiteralDeserializerBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(10)
                .measurementTime(new TimeValue(5, TimeUnit.SECONDS))
//                .output("output/results-literal-if")
                .threads(1)
//                .addProfiler(AsyncProfiler.class, "dir=profile-results;libPath=/home/carlos/Java_Tools/async-profiler-2.7-arg-linux-x64/build/libasyncProfiler.so")
                .build();
        new Runner(opt).run();
    }
}
