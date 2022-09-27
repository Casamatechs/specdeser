package kr.sanchez.specdeser.benchmark;

import com.fasterxml.jackson.core.JsonToken;
import kr.sanchez.specdeser.core.jakarta.AbstractParser;
import kr.sanchez.specdeser.core.util.JsonGenerators;
import org.glassfish.json.JsonParserImpl;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import javax.json.stream.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

import static com.fasterxml.jackson.core.JsonToken.*;

public class SpeculativeFixedKeysLengthBenchmark {

    @State(Scope.Benchmark)
    public static class StateObj {
        public InputStream[] profileBasicJson10Keys10Length = generateFixedKeyJson(10, 10, 10);
        public InputStream[] profileBasicJson10Keys100Length = generateFixedKeyJson(10, 10, 100);
        public InputStream[] profileBasicJson10Keys1000Length = generateFixedKeyJson(10, 10, 1000);
        public InputStream[] profileBasicJson10Keys10000Length = generateFixedKeyJson(10, 10, 10000);
        public InputStream[] profileBasicJson10Keys100000Length = generateFixedKeyJson(10, 10, 100000);
    }

    @Benchmark
    public void parserSpeculativeKeyFixed10Keys10LengthSpeculative(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10Keys10Length[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson10Keys10Length.length)];
            AbstractParser parser = AbstractParser.create(in, SpeculativeKeysBenchmark.StateObj.byteBufferPool);
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
            parser.close();
        } catch (IOException e) {

        }
    }

    @Benchmark
    public void parserSpeculativeKeyFixed10Keys100LengthSpeculative(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10Keys100Length[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson10Keys10Length.length)];
            AbstractParser parser = AbstractParser.create(in, SpeculativeKeysBenchmark.StateObj.byteBufferPool);
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
            parser.close();
        } catch (IOException e) {

        }
    }

    @Benchmark
    public void parserSpeculativeKeyFixed10Keys1000LengthSpeculative(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10Keys1000Length[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson10Keys10Length.length)];
            AbstractParser parser = AbstractParser.create(in, SpeculativeKeysBenchmark.StateObj.byteBufferPool);
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
            parser.close();
        } catch (IOException e) {

        }
    }

    @Benchmark
    public void parserSpeculativeKeyFixed10Keys10000LengthSpeculative(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10Keys10000Length[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson10Keys10Length.length)];
            AbstractParser parser = AbstractParser.create(in, SpeculativeKeysBenchmark.StateObj.byteBufferPool);
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
            parser.close();
        } catch (IOException e) {

        }
    }

    @Benchmark
    public void parserSpeculativeKeyFixed10Keys100000LengthSpeculative(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10Keys100000Length[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson10Keys10Length.length)];
            AbstractParser parser = AbstractParser.create(in, SpeculativeKeysBenchmark.StateObj.byteBufferPool);
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
            parser.close();
        } catch (IOException e) {

        }
    }

    @Benchmark
    public void parserSpeculativeKeyFixed10Keys10LengthJakarta(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10Keys10Length[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson10Keys10Length.length)];
            JsonParserImpl parser = new JsonParserImpl(in, StandardCharsets.UTF_8, SpeculativeKeysBenchmark.StateObj.bufferPool);
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
            parser.close();
        } catch (IOException e) {

        }
    }

    @Benchmark
    public void parserSpeculativeKeyFixed10Keys100LengthJakarta(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10Keys100Length[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson10Keys10Length.length)];
            JsonParserImpl parser = new JsonParserImpl(in, StandardCharsets.UTF_8, SpeculativeKeysBenchmark.StateObj.bufferPool);
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
            parser.close();
        } catch (IOException e) {

        }
    }

    @Benchmark
    public void parserSpeculativeKeyFixed10Keys1000LengthJakarta(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10Keys1000Length[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson10Keys10Length.length)];
            JsonParserImpl parser = new JsonParserImpl(in, StandardCharsets.UTF_8, SpeculativeKeysBenchmark.StateObj.bufferPool);
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
            parser.close();
        } catch (IOException e) {

        }
    }

    @Benchmark
    public void parserSpeculativeKeyFixed10Keys10000LengthJakarta(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10Keys10000Length[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson10Keys10Length.length)];
            JsonParserImpl parser = new JsonParserImpl(in, StandardCharsets.UTF_8, SpeculativeKeysBenchmark.StateObj.bufferPool);
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
            parser.close();
        } catch (IOException e) {

        }
    }

    @Benchmark
    public void parserSpeculativeKeyFixed10Keys100000LengthJakarta(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10Keys100000Length[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson10Keys10Length.length)];
            JsonParserImpl parser = new JsonParserImpl(in, StandardCharsets.UTF_8, SpeculativeKeysBenchmark.StateObj.bufferPool);
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
            parser.close();
        } catch (IOException e) {

        }
    }

    @Benchmark
    public void parserSpeculativeKeyFixed10Keys10LengthJackson(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10Keys10Length[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson10Keys10Length.length)];
            com.fasterxml.jackson.core.JsonParser parser = SpeculativeKeysBenchmark.StateObj.jacksonFactory.createParser(in);
            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (token == FIELD_NAME) {
                    parser.getCurrentName();
                }
                if (token == VALUE_STRING) {
                    parser.getValueAsString();
                }
                if (token == VALUE_NUMBER_INT) {
                    parser.getValueAsInt();
                }
            }
            in.reset();
            parser.close();
        } catch (IOException e) {

        }
    }

    @Benchmark
    public void parserSpeculativeKeyFixed10Keys100LengthJackson(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10Keys100Length[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson10Keys10Length.length)];
            com.fasterxml.jackson.core.JsonParser parser = SpeculativeKeysBenchmark.StateObj.jacksonFactory.createParser(in);
            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (token == FIELD_NAME) {
                    parser.getCurrentName();
                }
                if (token == VALUE_STRING) {
                    parser.getValueAsString();
                }
                if (token == VALUE_NUMBER_INT) {
                    parser.getValueAsInt();
                }
            }
            in.reset();
            parser.close();
        } catch (IOException e) {

        }
    }

    @Benchmark
    public void parserSpeculativeKeyFixed10Keys1000LengthJackson(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10Keys1000Length[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson10Keys10Length.length)];
            com.fasterxml.jackson.core.JsonParser parser = SpeculativeKeysBenchmark.StateObj.jacksonFactory.createParser(in);
            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (token == FIELD_NAME) {
                    parser.getCurrentName();
                }
                if (token == VALUE_STRING) {
                    parser.getValueAsString();
                }
                if (token == VALUE_NUMBER_INT) {
                    parser.getValueAsInt();
                }
            }
            in.reset();
            parser.close();
        } catch (IOException e) {

        }
    }

    @Benchmark
    public void parserSpeculativeKeyFixed10Keys10000LengthJackson(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10Keys10000Length[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson10Keys10Length.length)];
            com.fasterxml.jackson.core.JsonParser parser = SpeculativeKeysBenchmark.StateObj.jacksonFactory.createParser(in);
            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (token == FIELD_NAME) {
                    parser.getCurrentName();
                }
                if (token == VALUE_STRING) {
                    parser.getValueAsString();
                }
                if (token == VALUE_NUMBER_INT) {
                    parser.getValueAsInt();
                }
            }
            in.reset();
            parser.close();
        } catch (IOException e) {

        }
    }

    @Benchmark
    public void parserSpeculativeKeyFixed10Keys100000LengthJackson(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10Keys100000Length[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson10Keys10Length.length)];
            com.fasterxml.jackson.core.JsonParser parser = SpeculativeKeysBenchmark.StateObj.jacksonFactory.createParser(in);
            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (token == FIELD_NAME) {
                    parser.getCurrentName();
                }
                if (token == VALUE_STRING) {
                    parser.getValueAsString();
                }
                if (token == VALUE_NUMBER_INT) {
                    parser.getValueAsInt();
                }
            }
            in.reset();
            parser.close();
        } catch (IOException e) {

        }
    }

    private static InputStream[] generateFixedKeyJson(int arraySize, int nKeys, int lengthKey) {
        InputStream[] arr = new InputStream[arraySize];
        for (int i = 0; i < arraySize; i++) {
            arr[i] = JsonGenerators.fixedKeyLength(nKeys, lengthKey, 10);
        }
        return arr;
    }
}
