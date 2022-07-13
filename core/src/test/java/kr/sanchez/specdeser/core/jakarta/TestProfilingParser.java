package kr.sanchez.specdeser.core.jakarta;

import kr.sanchez.specdeser.core.jakarta.metadata.ProfileCollection;
import kr.sanchez.specdeser.core.jakarta.metadata.values.IntegerType;
import kr.sanchez.specdeser.core.jakarta.metadata.values.KeyValue;
import kr.sanchez.specdeser.core.jakarta.metadata.values.StringConstant;
import kr.sanchez.specdeser.core.jakarta.metadata.values.StringType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.json.stream.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestProfilingParser {

    @BeforeEach
    void beforeFunction() {
        ProfileCollection.resetProfileCollection();
        AbstractParser.resetAbstractParser();
    }

    @Test
    void testBasicMetadata() throws IOException {
        String inputString = """
                {"name":"Persona","surname":"Anosrep","city":"Eindhoven","country":"Netherlands"}
                """;
        InputStream inputStream = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));
        ProfilingParser parser;
        for (int i = 0; i < 100; i++) {
            parser = (ProfilingParser) AbstractParser.create(inputStream);
            runParser(parser);
            inputStream.reset();
        }
        List collection = ProfileCollection.getProfileCollection();
        List expectedValue = new ArrayList(Arrays.asList(new KeyValue("name"),
                new StringConstant("Persona"),
                new KeyValue("surname"),
                new StringConstant("Anosrep"),
                new KeyValue("city"),
                new StringConstant("Eindhoven"),
                new KeyValue("country"),
                new StringConstant("Netherlands")));
        Assertions.assertEquals(collection, expectedValue);
    }

    @Test
    void testConstantTypeMetadata() throws IOException {
        String in1 = """
                {
                "id":"foo",
                "name":"aaa",
                "loc":41
                }
                """.replaceAll("\\s","");
        String in2 = """
                {
                "id":"foo",
                "name":"bbb",
                "loc":42
                }
                """.replaceAll("\\s","");
        String in3 = """
                {
                "id":"foo",
                "name":"ccc",
                "loc":43
                }
                """.replaceAll("\\s","");
        InputStream is1 = new ByteArrayInputStream(in1.getBytes(StandardCharsets.UTF_8));
        InputStream is2 = new ByteArrayInputStream(in2.getBytes(StandardCharsets.UTF_8));
        InputStream is3 = new ByteArrayInputStream(in3.getBytes(StandardCharsets.UTF_8));
        ProfilingParser parser = (ProfilingParser) AbstractParser.create(is1);
        runParser(parser);
        parser = (ProfilingParser) AbstractParser.create(is2);
        runParser(parser);
        parser = (ProfilingParser) AbstractParser.create(is3);
        runParser(parser);
        List collection = ProfileCollection.getProfileCollection();
        List expectedValue = new ArrayList(Arrays.asList(new KeyValue("id"),
                new StringConstant("foo"),
                new KeyValue("name"),
                new StringType(),
                new KeyValue("loc"),
                new IntegerType()));
        Assertions.assertEquals(collection, expectedValue);
    }

    @Test
    void testDisableProfilingParser() throws IOException {
        String in1 = """
                {
                "id":"foo",
                "name":"aaa",
                "loc":41
                }
                """.replaceAll("\\s","");
        String in2 = """
                {
                "id":"foo",
                "name":"bbb",
                "loc":42
                }
                """.replaceAll("\\s","");
        String in3 = """
                {
                "id":"foo",
                "name":"ccc",
                "loc":"43"
                }
                """.replaceAll("\\s","");
        InputStream is1 = new ByteArrayInputStream(in1.getBytes(StandardCharsets.UTF_8));
        InputStream is2 = new ByteArrayInputStream(in2.getBytes(StandardCharsets.UTF_8));
        InputStream is3 = new ByteArrayInputStream(in3.getBytes(StandardCharsets.UTF_8));
        ProfilingParser parser = (ProfilingParser) AbstractParser.create(is1);
        runParser(parser);
        parser = (ProfilingParser) AbstractParser.create(is2);
        runParser(parser);
        parser = (ProfilingParser) AbstractParser.create(is3);
        runParser(parser);
        Assertions.assertFalse(AbstractParser.speculationEnabled);
    }

    private void runParser(JsonParser parser) throws IOException {
        while(parser.hasNext()) {
            JsonParser.Event evt = parser.next();
            System.out.print(evt + ", ");
            switch(evt) {
                case START_ARRAY -> System.out.println("[");
                case END_ARRAY -> System.out.println("]");
                case KEY_NAME, VALUE_STRING -> System.out.println(parser.getString());
                case VALUE_NUMBER -> {
                    if (parser.isIntegralNumber()) {
                        System.out.println(parser.getInt());
                    } else {
                        System.out.println(parser.getBigDecimal());
                    }
                }
                case START_OBJECT -> System.out.println("{");
                case END_OBJECT -> System.out.println("}");
                case VALUE_FALSE -> System.out.println(false);
                case VALUE_TRUE -> System.out.println(true);
                case VALUE_NULL -> System.out.println("null");
                default -> throw new IOException("The event is null");
            }
        }
    }
}
