package kr.sanchez.specdeser.benchmark;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonToken;
import kr.sanchez.specdeser.core.jakarta.AbstractParser;
import kr.sanchez.specdeser.core.jakarta.ByteBufferPool;
import kr.sanchez.specdeser.core.jakarta.ByteBufferPoolImpl;
import kr.sanchez.specdeser.core.util.JsonGenerators;
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

public class SpeculativeAnyTypeBenchmark {

    @State(Scope.Benchmark)
    public static class StateObj {
        InputStream[] input = generateSporifyUsers();

        public static final BufferPool bufferPool = new BufferPoolBenchmark();
        public static final ByteBufferPool byteBufferPool = new ByteBufferPoolImpl();
        public static final JsonFactory jacksonFactory = new JsonFactoryBuilder().build();

        private InputStream[] generateSporifyUsers() {
            final String baseURI = "https://api.spotify.com/v1";
            String[] displayName = new String[]{"Popeye",null,"Tintin",null,"Sonic",null,"Morticia",null,"Goku",null};
            String[] href = new String[]{
                    baseURI+"/popeye001",
                    baseURI+"/popeye002",
                    baseURI+"/tintin003",
                    baseURI+"/tintin004",
                    baseURI+"/sonic0005",
                    baseURI+"/sonic0006",
                    baseURI+"/morticia7",
                    baseURI+"/morticia8",
                    baseURI+"/goku00009",
                    baseURI+"/goku00010",
            };
            String[] id = new String[]{"popeye001","popeye002","tintin003","tintin004","sonic0005","sonic0006",
                    "morticia7","morticia8","goku00009","goku00010"};
            String[] uri = new String[]{"spotify:user:popeye001","spotify:user:popeye002","spotify:user:tintin003",
                    "spotify:user:tintin004","spotify:user:sonic0005","spotify:user:sonic0006",
                    "spotify:user:morticia7","spotify:user:morticia8","spotify:user:goku00009","spotify:user:goku00010"};
            InputStream[] ret = new InputStream[displayName.length];
            for (int i = 0; i < displayName.length; i++) {
                ret[i] = JsonGenerators.generateSpotifyUser(displayName[i],href[i],id[i],uri[i]);
            }
            return ret;
        }
    }

    @Benchmark
    public void parserAnySpeculative(StateObj stateObj) {
        try {
            InputStream in = stateObj.input[ThreadLocalRandom.current().nextInt(0, stateObj.input.length)];
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
                    case VALUE_NULL -> {

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
    public void parserAnyJakarta(StateObj stateObj) {
        try {
            InputStream in = stateObj.input[ThreadLocalRandom.current().nextInt(0, stateObj.input.length)];
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
                    case VALUE_NULL -> {

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
    public void parserAnyJackson(StateObj stateObj) {
        try {
            InputStream in = stateObj.input[ThreadLocalRandom.current().nextInt(0, stateObj.input.length)];
            com.fasterxml.jackson.core.JsonParser parser = SpeculativeKeysBenchmark.StateObj.jacksonFactory.createParser(in);
            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (token == FIELD_NAME) {
                    parser.getCurrentName();
                }
                if (token == VALUE_STRING) {
                    parser.getValueAsString();
                }
                if (token == VALUE_NULL) {

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
}
