package kr.sanchez.specdeser.benchmark;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import kr.sanchez.specdeser.core.jakarta.ByteBufferPool;
import kr.sanchez.specdeser.core.jakarta.ByteBufferPoolImpl;
import kr.sanchez.specdeser.core.jakarta.FallbackParser;
import org.glassfish.json.JsonParserImpl;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import javax.json.stream.JsonParser.Event;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

//Benchmark                                    Mode  Cnt      Score      Error  Units
//JakartaBenchmark.parse250NumberIntrinsic    thrpt    5  50037.837 ± 1318.989  ops/s
//JakartaBenchmark.parser250NumberYasson      thrpt    5  60824.874 ± 1212.694  ops/s
//JakartaBenchmark.parse500NumberIntrinsic    thrpt    5  29026.906 ±  940.106  ops/s
//JakartaBenchmark.parser500NumberYasson      thrpt    5  29287.184 ±  402.415  ops/s
//JakartaBenchmark.parse1000NumberIntrinsic   thrpt    5  15286.522 ±  242.106  ops/s
//JakartaBenchmark.parser1000NumberYasson     thrpt    5  14505.714 ±  295.690  ops/s

//JakartaBenchmark.parser250StringIntrinsic   thrpt    5  45764.247 ±  362.403  ops/s
//JakartaBenchmark.parser250StringYasson      thrpt    5  63550.078 ±  447.712  ops/s
//JakartaBenchmark.parser500StringIntrinsic   thrpt    5  29680.633 ±  209.539  ops/s
//JakartaBenchmark.parser500StringYasson      thrpt    5  34619.414 ±  370.252  ops/s
//JakartaBenchmark.parser1000StringIntrinsic  thrpt    5  16260.798 ±  206.276  ops/s
//JakartaBenchmark.parser1000StringYasson     thrpt    5  18033.274 ±  229.483  ops/s


public class JakartaBenchmark {

    @Benchmark
    public void parser250NumberIntrinsic(StateObj stateObj) {
        try {
            FallbackParser parser = new FallbackParser(stateObj.number250Json, stateObj.byteBufferPool);
            while (parser.hasNext()) {
                Event evt = parser.next();
                switch (evt) { // TODO Remove the switch, and just use if/else
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
            stateObj.number250Json.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void parser250NumberYasson(StateObj stateObj) {
        try {
            JsonParserImpl parser = new JsonParserImpl(stateObj.number250Json, new BufferPoolBenchmark());
            while (parser.hasNext()) {
                Event evt = parser.next();
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
            stateObj.number250Json.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void parser250StringIntrinsic(StateObj stateObj) {
        try {
            FallbackParser parser = new FallbackParser(stateObj.text250Json, stateObj.byteBufferPool);
            while (parser.hasNext()) {
                Event evt = parser.next();
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
            stateObj.text250Json.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void parser250StringYasson(StateObj stateObj) {
        try {
            JsonParserImpl parser = new JsonParserImpl(stateObj.text250Json, new BufferPoolBenchmark());
            while (parser.hasNext()) {
                Event evt = parser.next();
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
            stateObj.text250Json.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void parser500NumberIntrinsic(StateObj stateObj) {
        try {
            FallbackParser parser = new FallbackParser(stateObj.number500Json, stateObj.byteBufferPool);
            while (parser.hasNext()) {
                Event evt = parser.next();
                switch (evt) { // TODO Remove the switch, and just use if/else
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
            stateObj.number500Json.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void parser500NumberYasson(StateObj stateObj) {
        try {
            JsonParserImpl parser = new JsonParserImpl(stateObj.number500Json, new BufferPoolBenchmark());
            while (parser.hasNext()) {
                Event evt = parser.next();
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
            stateObj.number500Json.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void parser500StringIntrinsic(StateObj stateObj) {
        try {
            FallbackParser parser = new FallbackParser(stateObj.text500Json, stateObj.byteBufferPool);
            while (parser.hasNext()) {
                Event evt = parser.next();
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
            stateObj.text500Json.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void parser500StringYasson(StateObj stateObj) {
        try {
            JsonParserImpl parser = new JsonParserImpl(stateObj.text500Json, new BufferPoolBenchmark());
            while (parser.hasNext()) {
                Event evt = parser.next();
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
            stateObj.text500Json.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @Warmup(iterations = 10, time = 5)
    public void parser1000NumberIntrinsic(StateObj stateObj) {
        try {
            FallbackParser parser = new FallbackParser(stateObj.number1000Json, stateObj.byteBufferPool);
            while (parser.hasNext()) {
                Event evt = parser.next();
                switch (evt) { // TODO Remove the switch, and just use if/else
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
            stateObj.number1000Json.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @Warmup(iterations = 10, time = 5)
    public void parser1000NumberJackson(StateObj stateObj) {
        try {
            JsonParser parser = stateObj.jsonFactory.createParser(stateObj.number1000Json);
            if (!parser.hasCurrentToken()) {
                parser.nextToken();
            }
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                parser.getCurrentName();
                JsonToken token = parser.nextToken();
                if (token == JsonToken.VALUE_NUMBER_INT) {
                    parser.getValueAsInt();
                }
            }
            stateObj.number1000Json.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @Warmup(iterations = 10, time = 5)
    public void parser1000NumberYasson(StateObj stateObj) {
        try {
            JsonParserImpl parser = new JsonParserImpl(stateObj.number1000Json, new BufferPoolBenchmark());
            while (parser.hasNext()) {
                Event evt = parser.next();
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
            stateObj.number1000Json.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @Warmup(iterations = 10, time = 5)
    public void parserSameKey1000NumberIntrinsic(StateObj stateObj) {
        try {
            FallbackParser parser = new FallbackParser(stateObj.number1000SameKeyJson, stateObj.byteBufferPool);
            while (parser.hasNext()) {
                Event evt = parser.next();
                switch (evt) { // TODO Remove the switch, and just use if/else
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
            stateObj.number1000SameKeyJson.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @Warmup(iterations = 10, time = 5)
    public void parserSameKey1000NumberJackson(StateObj stateObj) {
        try {
            JsonParser parser = stateObj.jsonFactory.createParser(stateObj.number1000SameKeyJson);
            if (!parser.hasCurrentToken()) {
                parser.nextToken();
            }
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                parser.getCurrentName();
                JsonToken token = parser.nextToken();
                if (token == JsonToken.VALUE_NUMBER_INT) {
                    parser.getValueAsInt();
                }
            }
            stateObj.number1000SameKeyJson.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @Warmup(iterations = 10, time = 5)
    public void parserSameKey1000NumberYasson(StateObj stateObj) {
        try {
            JsonParserImpl parser = new JsonParserImpl(stateObj.number1000SameKeyJson, new BufferPoolBenchmark());
            while (parser.hasNext()) {
                Event evt = parser.next();
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
            stateObj.number1000SameKeyJson.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @Warmup(iterations = 10, time = 5)
    public void parser10NumberJackson(StateObj stateObj) {
        try {
            JsonParser parser = stateObj.jsonFactory.createParser(stateObj.number10Json);
            if (!parser.hasCurrentToken()) {
                parser.nextToken();
            }
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                parser.getCurrentName();
                JsonToken token = parser.nextToken();
                if (token == JsonToken.VALUE_NUMBER_INT) {
                    parser.getValueAsInt();
                }
            }
            stateObj.number10Json.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void parser1000StringIntrinsic(StateObj stateObj) {
        try {
            FallbackParser parser = new FallbackParser(stateObj.text1000Json, stateObj.byteBufferPool);
            while (parser.hasNext()) {
                Event evt = parser.next();
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
            stateObj.text1000Json.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void parser1000StringYasson(StateObj stateObj) {
        try {
            JsonParserImpl parser = new JsonParserImpl(stateObj.text1000Json, new BufferPoolBenchmark());
            while (parser.hasNext()) {
                Event evt = parser.next();
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
            stateObj.text1000Json.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @State(Scope.Benchmark)
    public static class StateObj {
        public final InputStream number10Json = generateNumbersJson(10);
        public final InputStream text10Json = generateStringsJson(10);
        public final InputStream number50Json = generateNumbersJson(50);
        public final InputStream text50Json = generateStringsJson(50);
        public final InputStream number100Json = generateNumbersJson(100);
        public final InputStream text100Json = generateStringsJson(100);
        public final InputStream number250Json = generateNumbersJson(250);
        public final InputStream text250Json = generateStringsJson(250);
        public final InputStream number500Json = generateNumbersJson(500);
        public final InputStream text500Json = generateStringsJson(500);
        public final InputStream number1000Json = generateNumbersJson(1000);
        public final InputStream number1000SameKeyJson = generateNumbersSameKeyJson(1000);
        public final InputStream text1000Json = generateStringsJson(1000);

        public final JsonFactory jsonFactory = JsonFactory.builder().disable(JsonFactory.Feature.CANONICALIZE_FIELD_NAMES).build();

        public final ByteBufferPool byteBufferPool = new ByteBufferPoolImpl();

    }

    private static InputStream generateNumbersJson(int qty) {
        StringBuilder ret = new StringBuilder("{\n");
        for (int i = 1; i < qty; i++) {
            int randomNumber = ThreadLocalRandom.current().nextInt(100_000_000, 1_000_000_000);
            ret.append("    \"numericValue").append(i).append("\" : ").append(randomNumber).append(",\n");
        }
        int randomNumber = ThreadLocalRandom.current().nextInt(100_000_000, 1_000_000_000);
        ret.append("    \"numericValue").append(qty).append("\" : ").append(randomNumber).append("\n}");
        return new ByteArrayInputStream(ret.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static InputStream generateNumbersSameKeyJson(int qty) {
        StringBuilder ret = new StringBuilder("{\n");
        for (int i = 1; i < qty; i++) {
            int randomNumber = ThreadLocalRandom.current().nextInt(100_000_000, 1_000_000_000);
            ret.append("    \"numericValue").append("\" : ").append(randomNumber).append(",\n");
        }
        int randomNumber = ThreadLocalRandom.current().nextInt(100_000_000, 1_000_000_000);
        ret.append("    \"numericValue").append("\" : ").append(randomNumber).append("\n}");
        return new ByteArrayInputStream(ret.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static InputStream generateStringsJson(int qty) {
        StringBuilder ret = new StringBuilder("{\n");
        for (int i = 1; i < qty; i++) {
            ret.append("    \"textValue").append(i).append("\" : \"Hello World\",\n");
        }
        ret.append("    \"textValue").append(qty).append("\" : \"Hello World\"\n}");
        return new ByteArrayInputStream(ret.toString().getBytes(StandardCharsets.UTF_8));
    }
}
