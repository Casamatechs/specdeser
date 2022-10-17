package kr.sanchez.specdeser.benchmark;

import com.fasterxml.jackson.core.JsonToken;
import kr.sanchez.specdeser.core.jakarta.AbstractParser;
import kr.sanchez.specdeser.core.jakarta.SpeculativeParser;
import org.glassfish.json.JsonParserImpl;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import javax.json.stream.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static com.fasterxml.jackson.core.JsonToken.*;
import static kr.sanchez.specdeser.core.util.JsonGenerators.*;

public class SpeculativeFixedLengthBenchmark {

    @State(Scope.Benchmark)
    public static class StateObj {
        public InputStream[] profileBasicJsonFixed10KeysLength100 = buildFixedJson(10,10,100);
        public InputStream[] profileBasicJsonFixed10KeysLength1000 = buildFixedJson(10,10,1000);
        public InputStream[] profileBasicJsonFixed10KeysLength10000 = buildFixedJson(10,10,10000);
        public InputStream[] profileBasicJsonFixed10KeysLength100000 = buildFixedJson(10,10,100000);
        public InputStream[] profileBasicJsonFixed10KeysLength1000000 = buildFixedJson(10,10,1000000);
    }

    public static void main(String[] args) {
        InputStream inJakarta = new StateObj().profileBasicJsonFixed10KeysLength100[ThreadLocalRandom.current().nextInt(0, 10)];
        JsonParser.Event[] jakartaTokens = new JsonParser.Event[22];
        Object[] jakartaObjects = new Object[20];
        JsonParser.Event[] speculativeTokens = new JsonParser.Event[22];
        Object[] speculativeObjects = new Object[20];
        generateJakartaInfo(inJakarta, jakartaTokens, jakartaObjects);
        runProfiling();
        generateSpeculativeInfo(inJakarta, speculativeTokens, speculativeObjects);
        if (Arrays.equals(jakartaTokens, speculativeTokens) && Arrays.equals(jakartaTokens, speculativeTokens)) {
            System.out.println("Test passed");
            System.exit(0);
        } else {
            System.err.println("Test not passed");
            System.exit(1);
        }
    }

    private static void generateJakartaInfo(InputStream input, JsonParser.Event[] tokenArray, Object[] objectsArray) {
        int tokPtr = 0;
        int objPtr = 0;
        JsonParserImpl parser = new JsonParserImpl(input, StandardCharsets.UTF_8, SpeculativeKeysBenchmark.StateObj.bufferPool);
        while(parser.hasNext()) {
            JsonParser.Event evt = parser.next();
            tokenArray[tokPtr++] = evt;
            switch(evt) {
                case KEY_NAME, VALUE_STRING -> {
                    String str = parser.getString();
                    objectsArray[objPtr++] = str;
                }
                case VALUE_NUMBER -> {
                    int num = parser.getInt();
                    objectsArray[objPtr++] = num;
                }
            }
        }
        try {
            input.reset();
            parser.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void generateSpeculativeInfo(InputStream input, JsonParser.Event[] tokenArray, Object[] objectsArray) {
        int tokPtr = 0;
        int objPtr = 0;
        AbstractParser parser = AbstractParser.create(input, SpeculativeKeysBenchmark.StateObj.byteBufferPool);
        assert parser instanceof SpeculativeParser;
        while(parser.hasNext()) {
            JsonParser.Event evt = parser.next();
            tokenArray[tokPtr++] = evt;
            switch(evt) {
                case KEY_NAME, VALUE_STRING -> {
                    String str = parser.getString();
                    objectsArray[objPtr++] = str;
                }
                case VALUE_NUMBER -> {
                    int num = parser.getInt();
                    objectsArray[objPtr++] = num;
                }
            }
        }
        try {
            input.reset();
            parser.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runProfiling() {
        try {
            for (int i = 0; i < 20; i++) {
                InputStream in = new StateObj().profileBasicJsonFixed10KeysLength100[ThreadLocalRandom.current().nextInt(0, 10)];
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
            }
        } catch (IOException e) {

        }
    }

    @Benchmark
    public void parserSpeculativeFixed10Keys100LengthSpeculative(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJsonFixed10KeysLength100[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJsonFixed10KeysLength100.length)];
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
    public void parserSpeculativeFixed10Keys1000LengthSpeculative(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJsonFixed10KeysLength1000[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJsonFixed10KeysLength100.length)];
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
    public void parserSpeculativeFixed10Keys10000LengthSpeculative(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJsonFixed10KeysLength10000[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJsonFixed10KeysLength100.length)];
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
    public void parserSpeculativeFixed10Keys100000LengthSpeculative(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJsonFixed10KeysLength100000[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJsonFixed10KeysLength100.length)];
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
    public void parserSpeculativeFixed10Keys1000000LengthSpeculative(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJsonFixed10KeysLength1000000[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJsonFixed10KeysLength100.length)];
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
    public void parserSpeculativeFixed10Keys1000LengthJakarta(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJsonFixed10KeysLength1000[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJsonFixed10KeysLength100.length)];
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
    public void parserSpeculativeFixed10Keys10000LengthJakarta(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJsonFixed10KeysLength10000[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJsonFixed10KeysLength100.length)];
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
    public void parserSpeculativeFixed10Keys100000LengthJakarta(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJsonFixed10KeysLength100000[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJsonFixed10KeysLength100.length)];
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
    public void parserSpeculativeFixed10Keys1000000LengthJakarta(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJsonFixed10KeysLength1000000[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJsonFixed10KeysLength100.length)];
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
    public void parserSpeculativeFixed10Keys100LengthJakarta(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJsonFixed10KeysLength100[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJsonFixed10KeysLength100.length)];
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
    public void parserSpeculativeFixed10Keys100LengthJackson(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJsonFixed10KeysLength100[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJsonFixed10KeysLength100.length)];
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
    public void parserSpeculativeFixed10Keys1000LengthJackson(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJsonFixed10KeysLength1000[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJsonFixed10KeysLength100.length)];
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
    public void parserSpeculativeFixed10Keys10000LengthJackson(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJsonFixed10KeysLength10000[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJsonFixed10KeysLength100.length)];
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
    public void parserSpeculativeFixed10Keys100000LengthJackson(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJsonFixed10KeysLength100000[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJsonFixed10KeysLength100.length)];
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
    public void parserSpeculativeFixed10Keys1000000LengthJackson(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJsonFixed10KeysLength1000000[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJsonFixed10KeysLength100.length)];
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

    private static InputStream[] buildFixedJson(int arraySize, int nKeys, int valueLength) {
        InputStream[] ret = new InputStream[arraySize];
        for (int i = 0; i < arraySize; i++) {
            ret[i] = fixedValueLength(nKeys, valueLength);
        }
        return ret;
    }
}
