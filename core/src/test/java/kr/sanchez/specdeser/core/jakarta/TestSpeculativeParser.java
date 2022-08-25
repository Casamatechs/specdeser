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

public class TestSpeculativeParser {

    @Test
    void testBasicJson() throws IOException {
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
        InputStream is1 = new ByteArrayInputStream(in1.getBytes(StandardCharsets.UTF_8));
        InputStream is2 = new ByteArrayInputStream(in2.getBytes(StandardCharsets.UTF_8));
        InputStream is3 = new ByteArrayInputStream(in3.getBytes(StandardCharsets.UTF_8));
        InputStream[] is = new InputStream[]{is1,is2,is3};
        for (int i = 0; i < 333; i++) {
            for (InputStream stream: is) {
                AbstractParser abstractParser = AbstractParser.create(stream);
                runParser(abstractParser);
                stream.reset();
            }
        }
        List expected1 = Arrays.asList("{","id","foo","name","aaa","loc",41,"num",1234,"}");
        List expected2 = Arrays.asList("{","id","foo","name","bbbb","loc",412,"num",1234,"}");
        List expected3 = Arrays.asList("{","id","foo","name","ccccc","loc",432,"num",1234,"}");
        List list1 = new ArrayList();
        List list2 = new ArrayList();
        List list3 = new ArrayList();
        AbstractParser parser = AbstractParser.create(is1);
        runParserAndCheck(list1,parser);
        parser = AbstractParser.create(is2);
        runParserAndCheck(list2,parser);
        parser = AbstractParser.create(is3);
        runParserAndCheck(list3,parser);
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
}
