package kr.sanchez.specdeser.benchmark;

//import com.fasterxml.jackson.core.JsonParser;
import kr.sanchez.specdeser.core.exception.DeserializationException;
import kr.sanchez.specdeser.core.serialization.NumberDeserializer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.profile.AsyncProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class NumberDeserializerBenchmark {

    @State(Scope.Benchmark)
    public static class StateObj {
        public final NumberDeserializer deserializer = new NumberDeserializer();
        public final byte[] naturalUTF8 = "12345".getBytes(StandardCharsets.UTF_8);
        public final byte[] negativeIntUTF8 = "-2434".getBytes(StandardCharsets.UTF_8);
        public final byte[] doubleUTF8 = "12.34".getBytes(StandardCharsets.UTF_8);
        public final byte[] negDoubleUTF8 = "-12.3".getBytes(StandardCharsets.UTF_8);
        public final byte[] longNaturalUTF8 = "2147483747".getBytes(StandardCharsets.UTF_8);
        public final byte[] expUTF8 = "15e10".getBytes(StandardCharsets.UTF_8);
        public final byte[] negExpUTF8 = "15e-5".getBytes(StandardCharsets.UTF_8);
        public final String natural = "12345";
        public final String negativeInt = "-2434";
        public final String doubleNumber = "12.34";
        public final String negDouble = "-12.3";
        public final String longNatural = "2147483747";
        public final String exp = "15e10";
        public final String negExp = "15e-5";
//        private final JsonFactory factory = new JsonFactory();
//        public JsonParser parserNaturalUTF8, parserNegativeIntUTF8, parserDoubleUTF8,
//                parserNegDoubleUTF8, parserLongNaturalUTF8, parserExpUTF8, parserNegExpUTF8;
//
//        {
//            try {
//                parserNaturalUTF8 = factory.createParser(naturalUTF8);
//                parserNegativeIntUTF8 = factory.createParser(negativeIntUTF8);
//                parserDoubleUTF8 =
//                parserNegDoubleUTF8 = factory.createParser(negDoubleUTF8);
//                parserLongNaturalUTF8 = factory.createParser(longNaturalUTF8);
//                parserExpUTF8 = factory.createParser(expUTF8);
//                parserNegExpUTF8 = factory.createParser(negExpUTF8);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

//        public int dummyJacksonPointer;

    }

    @Benchmark
    public Number deserializeNaturalBenchmark(StateObj stateObj) throws DeserializationException {
        return stateObj.deserializer.deserialize(stateObj.naturalUTF8);
    }

    @Benchmark
    public Number deserializeNaturalJavaBenchmark(StateObj stateObj) throws IOException {
        return Integer.getInteger(stateObj.natural);
        //        JsonParser parser = stateObj.factory.createParser(stateObj.naturalUTF8);
//        parser.nextToken();
//        return parser.getNumberValue();
    }

    @Benchmark
    public Number deserializeNegativeIntBenchmark(StateObj stateObj) throws DeserializationException {
        return stateObj.deserializer.deserialize(stateObj.negativeIntUTF8);
    }

    @Benchmark
    public Number deserializeNegativeJavaIntBenchmark(StateObj stateObj) throws IOException {
        return Integer.getInteger(stateObj.negativeInt);
        //        JsonParser parser = stateObj.factory.createParser(stateObj.negativeIntUTF8);
//        parser.nextToken();
//        return parser.getNumberValue();
    }

    @Benchmark
    public Number deserializeDoubleBenchmark(StateObj stateObj) throws DeserializationException {
        return stateObj.deserializer.deserialize(stateObj.doubleUTF8);
    }

    @Benchmark
    public Number deserializeDoubleJavaBenchmark(StateObj stateObj) throws IOException {
        return Double.parseDouble(stateObj.doubleNumber);
        //        JsonParser parser = stateObj.factory.createParser(stateObj.doubleUTF8);
//        parser.nextToken();
//        return parser.getNumberValue();
    }

    @Benchmark
    public Number deserializeNegDoubleBenchmark(StateObj stateObj) throws DeserializationException {
        return stateObj.deserializer.deserialize(stateObj.negDoubleUTF8);
    }

    @Benchmark
    public Number deserializeNegDoubleJavaBenchmark(StateObj stateObj) throws IOException {
        return Double.parseDouble(stateObj.negDouble);
        //        JsonParser parser = stateObj.factory.createParser(stateObj.negDoubleUTF8);
//        parser.nextToken();
//        return parser.getNumberValue();
    }

    @Benchmark
    public Number deserializeLongBenchmark(StateObj stateObj) throws DeserializationException {
        return stateObj.deserializer.deserialize(stateObj.longNaturalUTF8);
    }

    @Benchmark
    public Number deserializeLongJavaBenchmark(StateObj stateObj) throws IOException {
        return Long.getLong(stateObj.longNatural);
        //        JsonParser parser = stateObj.factory.createParser(stateObj.longNaturalUTF8);
//        parser.nextToken();
//        return parser.getNumberValue();
    }
    @Benchmark
    public Number deserializeExpBenchmark(StateObj stateObj) throws DeserializationException {
        return stateObj.deserializer.deserialize(stateObj.expUTF8);
    }

    @Benchmark
    public Number deserializeExpJavaBenchmark(StateObj stateObj) throws IOException {
        return Long.getLong(stateObj.exp);
        //        JsonParser parser = stateObj.factory.createParser(stateObj.expUTF8);
//        parser.nextToken();
//        return parser.getNumberValue();
    }

    @Benchmark
    public Number deserializeNegExpBenchmark(StateObj stateObj) throws DeserializationException {
        return stateObj.deserializer.deserialize(stateObj.negExpUTF8);
    }

    @Benchmark
    public Number deserializeNegExpJavaBenchmark(StateObj stateObj) throws IOException {
        return Double.parseDouble(stateObj.negExp);
        //        JsonParser parser = stateObj.factory.createParser(stateObj.negExpUTF8);
//        parser.nextToken();
//        return parser.getNumberValue();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(NumberDeserializerBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(10)
                .measurementTime(new TimeValue(5, TimeUnit.SECONDS))
//                .output("output/results-number")
                .threads(1)
//                .addProfiler(AsyncProfiler.class, "dir=profile-results;libPath=/home/carlos/Java_Tools/async-profiler-2.7-arg-linux-x64/build/libasyncProfiler.so")
                .build();
        new Runner(opt).run();
    }

}
