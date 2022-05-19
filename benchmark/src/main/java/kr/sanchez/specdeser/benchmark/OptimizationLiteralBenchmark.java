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
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class OptimizationLiteralBenchmark {

    @State(Scope.Benchmark)
    public static class StateObj {
        public final LiteralDeserializer deserializer = new LiteralDeserializer();
        public final byte[] trueUTF8 = "true".getBytes(StandardCharsets.UTF_8);
        public final byte[] falseUTF8 = "false".getBytes(StandardCharsets.UTF_8);
        public final byte[] nullUTF8 = "null".getBytes(StandardCharsets.UTF_8);
        public int dummyJacksonPointer;
    }

    @Benchmark
    public Boolean deserializeNullIfBenchmark(StateObj stateObj) throws DeserializationException {
        return stateObj.deserializer.deserializeIf(stateObj.nullUTF8);
    }

    @Benchmark
    public Boolean deserializeNullIf2Benchmark(StateObj stateObj) throws DeserializationException {
        return stateObj.deserializer.deserializeIf2(stateObj.nullUTF8);
    }

    @Benchmark
    public Boolean deserializeNullSafeBenchmark(StateObj stateObj) throws DeserializationException {
        return stateObj.deserializer.deserializeSafe(stateObj.nullUTF8);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(OptimizationLiteralBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(10)
                .measurementTime(new TimeValue(5, TimeUnit.SECONDS))
//                .output("output/results-literal-opt-".concat(String.valueOf(Date.from(Instant.now()).getTime())))
                .threads(1)
//                .addProfiler(AsyncProfiler.class, "dir=profile-results;libPath=/home/carlos/Java_Tools/async-profiler-2.7-arg-linux-x64/build/libasyncProfiler.so")
                .build();
        new Runner(opt).run();
    }
}
