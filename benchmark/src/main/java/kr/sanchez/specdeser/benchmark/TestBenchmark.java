package kr.sanchez.specdeser.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class TestBenchmark {

    @State(Scope.Benchmark)
    public static class StateObj {
        public final byte[] validNatural = "1234567890".getBytes(StandardCharsets.UTF_8);
    }

    @Benchmark
    public void parseNumbers1(StateObj stateObj, Blackhole bh) {
        for (byte b : stateObj.validNatural) {
            if (((b - '0') & 0xFF) > 9) {
                bh.consume(false);
                return;
            }
        }
        bh.consume(true);
    }

    @Benchmark
    public void parseNumbers2(StateObj stateObj, Blackhole bh) {
        for (int i = 0; i < stateObj.validNatural.length; i++) {
            int number = stateObj.validNatural[i] - '0';
            if (number < 0 || number > 9) {
                bh.consume(false);
                return;
            }
        }
        bh.consume(true);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TestBenchmark.class.getSimpleName())
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
