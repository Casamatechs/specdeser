package kr.sanchez.specdeser.benchmark;

import com.fasterxml.jackson.core.JsonFactory;
import kr.sanchez.specdeser.core.jakarta.AbstractParser;
import kr.sanchez.specdeser.core.jakarta.FallbackParser;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import javax.json.stream.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

public class SpeculativeBenchmark {

    public void parseBasicJsonSpeculative(StateObj stateObj) {

    }

    @State(Scope.Benchmark)
    public static class StateObj {
        public InputStream[] profileBasicJson100Keys50Constants = new InputStream[]{generateBasicJson(100),generateBasicJson(100),generateBasicJson(100),generateBasicJson(100),generateBasicJson(100),generateBasicJson(100),generateBasicJson(100),generateBasicJson(100),generateBasicJson(100),generateBasicJson(100)};
        public InputStream[] speculativeBasicJson100Keys50Constants = new InputStream[]{generateBasicJson(100),generateBasicJson(100),generateBasicJson(100),generateBasicJson(100),generateBasicJson(100),generateBasicJson(100),generateBasicJson(100),generateBasicJson(100),generateBasicJson(100),generateBasicJson(100)};
        public final JsonFactory jsonFactory = JsonFactory.builder().disable(JsonFactory.Feature.CANONICALIZE_FIELD_NAMES).build();
    }

    @Benchmark
    public void parserSpeculativeFull(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson100Keys50Constants[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson100Keys50Constants.length)];
            AbstractParser parser = AbstractParser.create(in);
            while (parser.hasNext()) {
                JsonParser.Event evt = parser.next();
                switch (evt) {
                    case KEY_NAME, VALUE_STRING -> {
                        parser.getString();
                    }
                    case VALUE_NUMBER -> {
                        parser.getInt();
                    }
                    default -> {
                    }
                }
            }
            in.reset();
        } catch (IOException e) {

        }
    }

    @Benchmark
    public void parserSpeculativeRegular(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson100Keys50Constants[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson100Keys50Constants.length)];
            FallbackParser parser = new FallbackParser(in);
            while (parser.hasNext()) {
                JsonParser.Event evt = parser.next();
                switch (evt) {
                    case KEY_NAME, VALUE_STRING -> {
                        parser.getString();
                    }
                    case VALUE_NUMBER -> {
                        parser.getInt();
                    }
                    default -> {
                    }
                }
            }
            in.reset();
        } catch (IOException e) {

        }
    }

    private static InputStream generateBasicJson(int keys) {
//        int type = keys / 4;
        StringBuilder ret = new StringBuilder("{");
        for (int i = 0; i < keys; i++) {
            int t = i % 4;
            String key = "\"key" + i + "\":";
            if (t == 0) {
                ret.append(key).append("\"constant").append(i).append("\",");
            } else if (t == 1) {
                ret.append(key).append("\"string").append(ThreadLocalRandom.current().nextInt(100,10000)).append("\",");
            } else if (t == 2) {
                ret.append(key).append(i * 10 + 42).append(",");
            } else if (t == 3) {
                ret.append(key).append(ThreadLocalRandom.current().nextInt(100,10000));
                if (i != keys-1) {
                    ret.append(",");
                }
            }
        }
        ret.append("}");
        return new ByteArrayInputStream(ret.toString().getBytes(StandardCharsets.UTF_8));
    }
}
