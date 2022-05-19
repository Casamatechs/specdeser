package kr.sanchez.specdeser.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.AsyncProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class TruffleBenchmark {

    @State(Scope.Benchmark)
    public static class StateObj {
//        public final String trueString = "true";
//        public final String[] literals = new String[] {"false","true","null"};
//        public final byte[] trueStringUTF8 = "true".getBytes(StandardCharsets.UTF_8);
//        public final byte[] trueStringUTF88 = "true".getBytes(StandardCharsets.UTF_8);
//        public final byte[] truuStringUTF8 = "truu".getBytes(StandardCharsets.UTF_8);
//        public final byte[] benchmarkUTF8 = "En un lugar de la Mancha de cuyo nombre no quiero acordarme...".getBytes(StandardCharsets.UTF_8);
//        public final String benchmarkUTF88 = "En un lugar de la Mancha de cuyo nombre no quiero acordarme...";
        public final byte[] validNatural = "23749534".getBytes(StandardCharsets.UTF_8);
        public final byte[] invalidNatural = "1234.567".getBytes(StandardCharsets.UTF_8);
//        long validLong = 0x3233373439353334L;
//        long invalidLong = 0x313233342E353637L;
//        public long offset = 0;
//        public int stride = 0;
//        public int length = benchmarkUTF88.length();
//        public int lookedUp = 'M';
    }

//    @Benchmark
//    public void equalsCompiler(StateObj state, Blackhole bh) {
//        boolean bl = state.trueString.equals(state.literals[1]);
//        bh.consume(bl);
//    }

//    @Benchmark
//    public void equalsJava(StateObj state, Blackhole bh) {
//        byte[] ref = state.trueStringUTF8;
//        byte[] tr = state.trueStringUTF88;
//        if (ref == tr) {
//            bh.consume(true);
//        }
//        if (ref.length == tr.length) {
//            for (int i = 0; i < ref.length; i++) {
//                if (ref[i] != tr[i]) {
//                    bh.consume(false);
//                    break;
//                }
//            }
//            bh.consume(true);
//        }
//        bh.consume(false);
//    }
//
//    @Benchmark
//    public void equalsTrueParser(StateObj state, Blackhole bh) {
//        boolean bl = trueEqualsIntrinsic(state.trueStringUTF8, state.trueStringUTF88, state.trueStringUTF8.length);
//        bh.consume(bl);
//    }

//    @Benchmark
//    public void notEqualsTrueParser(StateObj state, Blackhole bh) {
//        boolean bl = trueEqualsIntrinsic(state.trueStringUTF8, state.truuStringUTF8, state.trueStringUTF8.length);
//        bh.consume(bl);
//    }

//    private boolean trueEqualsIntrinsic(byte[] arrayIn, byte[] comp, int length) {
//        for (int i = 0; i < length; i++) {
//            if (arrayIn[i] != comp[i]) return false;
//        }
//        return true;
//    }
//
//    @Benchmark
//    public void getIndexJava(StateObj state, Blackhole bh) {
//        for (int i = 0; i < state.length; i++) {
//            if (state.benchmarkUTF88.charAt(i) == state.lookedUp) {
//                bh.consume(i);
//            }
//        }
//        bh.consume(-1);
//    }
//
//    @Benchmark
//    public void getIdxBenchmark(StateObj state, Blackhole bh) throws Exception {
//        int idx = getIndexIntrinsic(state.benchmarkUTF8,state.lookedUp, 0, state.benchmarkUTF8.length);
//        bh.consume(idx);
//    }
//
//    private int getIndexIntrinsic(byte[] str, int ch, int idx, int length) {
//        for (int i = idx; i < length; i++) {
//            if (str[i] == ch) {
//                return i;
//            }
//        }
//        return -1;
//    }

//    @Benchmark
//    public void getIndexCompiler(StateObj state, Blackhole bh) {
//        int idx = state.benchmarkUTF88.indexOf(state.lookedUp);
//        bh.consume(idx);
//    }


    @Benchmark
    public void swarValid(StateObj state, Blackhole bh) {
        bh.consume(isNumericValue(state.validNatural));
    }

    @Benchmark
    public void swarInvalid(StateObj state, Blackhole bh) {
        bh.consume(isNumericValue(state.invalidNatural));
    }

    @Benchmark
    public void swar64Valid(StateObj state, Blackhole bh) {
        bh.consume(isNumeric64Value(state.validNatural));
    }

    @Benchmark
    public void swar64Invalid(StateObj state, Blackhole bh) {
        bh.consume(isNumeric64Value(state.invalidNatural));
    }

    @Benchmark
    public void scalarValid(StateObj state, Blackhole bh) {
        bh.consume(isNumberValue(state.validNatural));
    }
    @Benchmark
    public void scalarInvalid(StateObj state, Blackhole bh) {
        bh.consume(isNumberValue(state.invalidNatural));
    }

    private boolean isNumericValue(byte[] buffer) {
        for (byte b : buffer) {
            if (((b & 0XF0) | (((b + 0x06) & 0XF0)) >> 4) != 0x33) {
                return false;
            }
        }
        return true;
    }

    private boolean isNumeric64Value(byte[] buffer) {
        long val = ByteBuffer.wrap(buffer).getLong();
        return (((val & 0xF0F0F0F0F0F0F0F0L) |
                (((val + 0x0606060606060606L) & 0xF0F0F0F0F0F0F0F0L) >> 4)) ==
                0x3333333333333333L);
    }

    private boolean isNumberValue(byte[] buffer) {
        for (byte b : buffer) {
            if (((b - '0') & 0xFF) > '9') return false;
        }
        return true;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TruffleBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(10)
                .measurementTime(new TimeValue(5, TimeUnit.SECONDS))
//                .output("output/truffle-equals-".concat(String.valueOf(Date.from(Instant.now()).getTime())))
                .threads(1)
//                .addProfiler(AsyncProfiler.class, "dir=profile-results;libPath=/home/carlos/Java_Tools/async-profiler-2.7-arg-linux-x64/build/libasyncProfiler.so")
                .build();
        new Runner(opt).run();
    }
}
