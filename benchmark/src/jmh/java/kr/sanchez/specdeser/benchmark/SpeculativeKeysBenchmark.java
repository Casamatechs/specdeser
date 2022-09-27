package kr.sanchez.specdeser.benchmark;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonToken;
import kr.sanchez.specdeser.core.jakarta.AbstractParser;
import kr.sanchez.specdeser.core.jakarta.ByteBufferPool;
import kr.sanchez.specdeser.core.jakarta.ByteBufferPoolImpl;
import kr.sanchez.specdeser.core.jakarta.FallbackParser;
import org.glassfish.json.JsonParserImpl;
import org.glassfish.json.api.BufferPool;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import javax.json.stream.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

import static com.fasterxml.jackson.core.JsonToken.*;
import static kr.sanchez.specdeser.core.util.JsonGenerators.*;

public class SpeculativeKeysBenchmark {

    @State(Scope.Benchmark)
    public static class StateObj {
        public InputStream[] profileBasicJson100Keys = buildBasicJson(10,100);
        public InputStream[] profileBasicJson1000Keys = buildBasicJson(10,1000);
        public InputStream[] profileBasicJson10000Keys = buildBasicJson(10,10000);
        public InputStream[] profileBasicJson100000Keys = buildBasicJson(10,100000);
        public InputStream profileBasicJson100Constants = generateStaticJson(100);
        public InputStream profileBasicJson1000Constants = generateStaticJson(1000);
        public InputStream profileBasicJson10000Constants = generateStaticJson(10000);
        public InputStream profileBasicJson100000Constants = generateStaticJson(100000);
        public static final BufferPool bufferPool = new BufferPoolBenchmark();
        public static final ByteBufferPool byteBufferPool = new ByteBufferPoolImpl();
        public static final JsonFactory jacksonFactory = new JsonFactoryBuilder().build();
    }

    @Benchmark
    public void parserSpeculativeRandom100(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson100Keys[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson1000Keys.length)];
            AbstractParser parser = AbstractParser.create(in, StateObj.byteBufferPool);
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
    public void parserSpeculativeRandom1000(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson1000Keys[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson1000Keys.length)];
            AbstractParser parser = AbstractParser.create(in, StateObj.byteBufferPool);
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
    public void parserSpeculativeRandom10000(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10000Keys[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson1000Keys.length)];
            AbstractParser parser = AbstractParser.create(in, StateObj.byteBufferPool);
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
    public void parserSpeculativeRandom100000(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson100000Keys[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson1000Keys.length)];
            AbstractParser parser = AbstractParser.create(in, StateObj.byteBufferPool);
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
    public void parserSpeculativeConstant100(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson100Constants;
            AbstractParser parser = AbstractParser.create(in, StateObj.byteBufferPool);
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
    public void parserSpeculativeConstant1000(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson1000Constants;
            AbstractParser parser = AbstractParser.create(in, StateObj.byteBufferPool);
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
    public void parserSpeculativeConstant10000(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10000Constants;
            AbstractParser parser = AbstractParser.create(in, StateObj.byteBufferPool);
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
    public void parserSpeculativeConstant100000(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson100000Constants;
            AbstractParser parser = AbstractParser.create(in, StateObj.byteBufferPool);
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
    public void parserSpeculativeFallback100(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson100Keys[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson1000Keys.length)];
            FallbackParser parser = new FallbackParser(in, StateObj.byteBufferPool);
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
    public void parserSpeculativeFallback1000(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson1000Keys[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson1000Keys.length)];
            FallbackParser parser = new FallbackParser(in, StateObj.byteBufferPool);
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
    public void parserSpeculativeFallback10000(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10000Keys[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson1000Keys.length)];
            FallbackParser parser = new FallbackParser(in, StateObj.byteBufferPool);
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
    public void parserSpeculativeFallback100000(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson100000Keys[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson1000Keys.length)];
            FallbackParser parser = new FallbackParser(in, StateObj.byteBufferPool);
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
    public void parserSpeculativeJakarta100(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson100Keys[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson1000Keys.length)];
            JsonParserImpl parser = new JsonParserImpl(in, StandardCharsets.UTF_8, StateObj.bufferPool);
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
    public void parserSpeculativeJakarta1000(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson1000Keys[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson1000Keys.length)];
            JsonParserImpl parser = new JsonParserImpl(in, StandardCharsets.UTF_8, StateObj.bufferPool);
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
    public void parserSpeculativeJakarta10000(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10000Keys[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson1000Keys.length)];
            JsonParserImpl parser = new JsonParserImpl(in, StandardCharsets.UTF_8, StateObj.bufferPool);
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
    public void parserSpeculativeJakarta100000(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson100000Keys[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson1000Keys.length)];
            JsonParserImpl parser = new JsonParserImpl(in, StandardCharsets.UTF_8, StateObj.bufferPool);
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
    public void parserSpeculativeJackson100(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson100Keys[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson1000Keys.length)];
            com.fasterxml.jackson.core.JsonParser parser = StateObj.jacksonFactory.createParser(in);
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
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void parserSpeculativeJackson1000(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson1000Keys[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson1000Keys.length)];
            com.fasterxml.jackson.core.JsonParser parser = StateObj.jacksonFactory.createParser(in);
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
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void parserSpeculativeJackson10000(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson10000Keys[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson1000Keys.length)];
            com.fasterxml.jackson.core.JsonParser parser = StateObj.jacksonFactory.createParser(in);
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
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void parserSpeculativeJackson100000(StateObj stateObj) {
        try {
            InputStream in = stateObj.profileBasicJson100000Keys[ThreadLocalRandom.current().nextInt(0, stateObj.profileBasicJson1000Keys.length)];
            com.fasterxml.jackson.core.JsonParser parser = StateObj.jacksonFactory.createParser(in);
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
            throw new RuntimeException(e);
        }
    }

    private static InputStream[] buildBasicJson(int arraySize, int keys) {
        InputStream[] ret = new InputStream[arraySize];
        for (int i = 0; i < arraySize; i++) {
            ret[i] = basicJson(keys);
        }
        return ret;
    }
}
