package kr.sanchez.specdeser.core.jakarta;

import kr.sanchez.specdeser.core.jakarta.metadata.ProfileCollection;
import kr.sanchez.specdeser.core.jakarta.metadata.values.MetadataValue;
import kr.sanchez.specdeser.core.jakarta.metadata.values.ValueType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.json.stream.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TestProfilingParser {

    private final static ByteBufferPool bufferPool = new ByteBufferPoolTest();

    @BeforeEach
    void beforeFunction() {
        ProfileCollection.resetProfileCollection();
        AbstractParser.resetAbstractParser();
    }

    @Test
    void testBasicMetadata() throws IOException {
        String inputString = """
                {"name":"Persona","surname":"Anosrep","city":"Eindhoven","country":"Netherlands"}
                """.replaceAll("\\s","");
        InputStream inputStream = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));
        AbstractParser parser;
        for (int i = 0; i < 100; i++) {
            parser = AbstractParser.create(inputStream, bufferPool);
            runParser(parser);
            inputStream.reset();
        }
        MetadataValue[] collection = ProfileCollection.getMetadataProfileCollection();
        MetadataValue[] expectedValue = new MetadataValue[]{new MetadataValue(ValueType.KEY, "name", "name".getBytes(StandardCharsets.UTF_8)),
                new MetadataValue(ValueType.STRING_CONSTANT, "Persona", "Persona".getBytes(StandardCharsets.UTF_8)),
                new MetadataValue(ValueType.KEY, "surname", "surname".getBytes(StandardCharsets.UTF_8)),
                new MetadataValue(ValueType.STRING_CONSTANT, "Anosrep", "Anosrep".getBytes(StandardCharsets.UTF_8)),
                new MetadataValue(ValueType.KEY, "city", "city".getBytes(StandardCharsets.UTF_8)),
                new MetadataValue(ValueType.STRING_CONSTANT, "Eindhoven", "Eindhoven".getBytes(StandardCharsets.UTF_8)),
                new MetadataValue(ValueType.KEY, "country", "country".getBytes(StandardCharsets.UTF_8)),
                new MetadataValue(ValueType.STRING_CONSTANT, "Netherlands", "Netherlands".getBytes(StandardCharsets.UTF_8))};
        Assertions.assertArrayEquals(collection, expectedValue);
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
        ProfilingParser parser = (ProfilingParser) AbstractParser.create(is1, bufferPool);
        runParser(parser);
        parser = (ProfilingParser) AbstractParser.create(is2, bufferPool);
        runParser(parser);
        parser = (ProfilingParser) AbstractParser.create(is3, bufferPool);
        runParser(parser);
        MetadataValue[] collection = ProfileCollection.getMetadataProfileCollection();
        MetadataValue[] expectedValue = new MetadataValue[]{new MetadataValue(ValueType.KEY, "id", "id".getBytes(StandardCharsets.UTF_8)),
                new MetadataValue(ValueType.STRING_CONSTANT, "foo", "foo".getBytes(StandardCharsets.UTF_8)),
                new MetadataValue(ValueType.KEY, "name", "name".getBytes(StandardCharsets.UTF_8)),
                new MetadataValue(ValueType.STRING_TYPE),
                new MetadataValue(ValueType.KEY, "loc", "loc".getBytes(StandardCharsets.UTF_8)),
                new MetadataValue(ValueType.INT_TYPE)};
        Assertions.assertArrayEquals(collection, expectedValue);
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
                "loc1":"43"
                }
                """.replaceAll("\\s","");
        InputStream is1 = new ByteArrayInputStream(in1.getBytes(StandardCharsets.UTF_8));
        InputStream is2 = new ByteArrayInputStream(in2.getBytes(StandardCharsets.UTF_8));
        InputStream is3 = new ByteArrayInputStream(in3.getBytes(StandardCharsets.UTF_8));
        ProfilingParser parser = (ProfilingParser) AbstractParser.create(is1, bufferPool);
        runParser(parser);
        parser = (ProfilingParser) AbstractParser.create(is2, bufferPool);
        runParser(parser);
        parser = (ProfilingParser) AbstractParser.create(is3, bufferPool);
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
