package kr.sanchez.specdeser.core.jakarta;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.json.stream.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TestSpeculativeParser {

    private static ByteBufferPool bufferPool = new ByteBufferPoolTest();

    @Test
    void testBasicJson() throws IOException {
        AbstractParser.resetAbstractParser();
        String in1 = """
                {
                "id":"foo",
                "name":"aaa",
                "loc":41,
                "num":1234
                }
                """.replaceAll("\\s","");
        String in2 = """
                {
                "id":"foo",
                "name":"bbbb",
                "loc":412,
                "num":1234
                }
                """.replaceAll("\\s","");
        String in3 = """
                {
                "id":"foo",
                "name":"ccccc",
                "loc":432,
                "num":1234
                }
                """.replaceAll("\\s","");
        String in4 = """
                {
                "id":"foo",
                "name":"CarlosSanchez",
                "loc":4242,
                "num":1234
                }
                """.replaceAll("\\s","");
        String in5 = """
                {
                "id":"foo",
                "name":"DanieleBonetta",
                "loc":2424,
                "num":1234
                }
                """.replaceAll("\\s","");
        String in6 = """
                {
                "id":"foo",
                "name":"NombreApellidos",
                "loc":12345678,
                "num":1234
                }
                """.replaceAll("\\s","");
        InputStream is1 = new ByteArrayInputStream(in1.getBytes(StandardCharsets.UTF_8));
        InputStream is2 = new ByteArrayInputStream(in2.getBytes(StandardCharsets.UTF_8));
        InputStream is3 = new ByteArrayInputStream(in3.getBytes(StandardCharsets.UTF_8));
        InputStream is4 = new ByteArrayInputStream(in4.getBytes(StandardCharsets.UTF_8));
        InputStream is5 = new ByteArrayInputStream(in5.getBytes(StandardCharsets.UTF_8));
        InputStream is6 = new ByteArrayInputStream(in6.getBytes(StandardCharsets.UTF_8));
        InputStream[] is = new InputStream[]{is1,is2,is3};
        for (int i = 0; i < 333; i++) {
            for (InputStream stream: is) {
                AbstractParser abstractParser = AbstractParser.create(stream, bufferPool);
                runParser(abstractParser);
                stream.reset();
            }
        }
        List expected1 = Arrays.asList("{","id","foo","name","CarlosSanchez","loc",4242,"num",1234,"}");
        List expected2 = Arrays.asList("{","id","foo","name","DanieleBonetta","loc",2424,"num",1234,"}");
        List expected3 = Arrays.asList("{","id","foo","name","NombreApellidos","loc",12345678,"num",1234,"}");
        List list1 = new ArrayList();
        List list2 = new ArrayList();
        List list3 = new ArrayList();
        AbstractParser parser = AbstractParser.create(is4, bufferPool);
        runParserAndCheck(list1,parser);
        parser = AbstractParser.create(is5, bufferPool);
        runParserAndCheck(list2,parser);
        parser = AbstractParser.create(is6, bufferPool);
        runParserAndCheck(list3,parser);
        Assertions.assertTrue(expected1.equals(list1) && expected2.equals(list2) && expected3.equals(list3));
    }

    @Test
    void testAnyJson() throws IOException {
        AbstractParser.resetAbstractParser();
        String in1 = """
                {
                "id":1,
                "value": "Text"
                }
                """.replaceAll("\\s","");
        String in2 = """
                {
                "id":2,
                "value": 12345
                }
                """.replaceAll("\\s","");
        String in3 = """
                {
                "id":3,
                "value": true
                }
                """.replaceAll("\\s","");
        InputStream is1 = new ByteArrayInputStream(in1.getBytes(StandardCharsets.UTF_8));
        InputStream is2 = new ByteArrayInputStream(in2.getBytes(StandardCharsets.UTF_8));
        InputStream is3 = new ByteArrayInputStream(in3.getBytes(StandardCharsets.UTF_8));
        InputStream[] is = new InputStream[]{is1,is2,is3};
        for (int i = 0; i < 333; i++) {
            for (InputStream stream: is) {
                AbstractParser abstractParser = AbstractParser.create(stream, bufferPool);
                runParser(abstractParser);
                stream.reset();
            }
        }
        String in4 = """
                {
                "id":4,
                "value": "Test"
                }
                """.replaceAll("\\s","");
        String in5 = """
                {
                "id":5,
                "value": 54321
                }
                """.replaceAll("\\s","");
        String in6 = """
                {
                "id":6,
                "value": false
                }
                """.replaceAll("\\s","");
        InputStream is4 = new ByteArrayInputStream(in4.getBytes(StandardCharsets.UTF_8));
        InputStream is5 = new ByteArrayInputStream(in5.getBytes(StandardCharsets.UTF_8));
        InputStream is6 = new ByteArrayInputStream(in6.getBytes(StandardCharsets.UTF_8));
        List list1 = new ArrayList();
        List list2 = new ArrayList();
        List list3 = new ArrayList();
        AbstractParser parser = AbstractParser.create(is4, bufferPool);
        runParserAndCheck(list1,parser);
        parser = AbstractParser.create(is5, bufferPool);
        runParserAndCheck(list2,parser);
        parser = AbstractParser.create(is6, bufferPool);
        runParserAndCheck(list3,parser);
        List expected1 = Arrays.asList("{","id",4,"value","Test","}");
        List expected2 = Arrays.asList("{","id",5,"value",54321,"}");
        List expected3 = Arrays.asList("{","id",6,"value",false,"}");
        Assertions.assertTrue(expected1.equals(list1) && expected2.equals(list2) && expected3.equals(list3));
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

    private void runParserAndCheck(List result, JsonParser parser) throws IOException {
        while(parser.hasNext()) {
            JsonParser.Event evt = parser.next();
            System.out.print(evt + ", ");
            switch(evt) {
                case START_ARRAY -> {
                    System.out.println("[");
                    result.add("[");
                }
                case END_ARRAY -> {
                    System.out.println("]");
                    result.add("]");
                }
                case KEY_NAME, VALUE_STRING -> {
                    String ret = parser.getString();
                    System.out.println(ret);
                    result.add(ret);
                }
                case VALUE_NUMBER -> {
                    if (parser.isIntegralNumber()) {
                        int ret = parser.getInt();
                        System.out.println(ret);
                        result.add(ret);
                    } else {
                        BigDecimal ret = parser.getBigDecimal();
                        System.out.println(ret);
                        result.add(ret);
                    }
                }
                case START_OBJECT -> {
                    System.out.println("{");
                    result.add("{");
                }
                case END_OBJECT -> {
                    System.out.println("}");
                    result.add("}");
                }
                case VALUE_FALSE -> {
                    System.out.println(false);
                    result.add(false);
                }
                case VALUE_TRUE -> {
                    System.out.println(true);
                    result.add(true);
                }
                case VALUE_NULL -> {
                    System.out.println("null");
                    result.add(null);
                }
                default -> throw new IOException("The event is null");
            }
        }
    }

    private InputStream generateBasicJson(int keys) {
        int type = keys / 4;
        StringBuilder ret = new StringBuilder("{");
        for (int i = 0; i < keys; i++) {
            int t = i % type;
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
