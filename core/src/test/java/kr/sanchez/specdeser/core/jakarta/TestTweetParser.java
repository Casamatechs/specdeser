package kr.sanchez.specdeser.core.jakarta;

import org.glassfish.json.JsonParserImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.json.stream.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestTweetParser {

    @Test
    void testStringJson() throws IOException {
        IntrinsicJakartaParser parser = new IntrinsicJakartaParser(getClass().getClassLoader().getResourceAsStream("string.json"));
        JsonParserImpl parserImpl = new JsonParserImpl(getClass().getClassLoader().getResourceAsStream("string.json"), new BufferPoolImpl());
        List<JsonParser.Event> parserEvents = new ArrayList<>();
        List<JsonParser.Event> parserImplEvents = new ArrayList<>();
        List<Object> parserValues = new ArrayList<>();
        List<Object> parserImplValues = new ArrayList<>();
        runParser(parser, parserEvents, parserValues);
        runParser(parserImpl, parserImplEvents, parserImplValues);
        Assertions.assertTrue(listsEquals(parserEvents, parserImplEvents) && listsEquals(parserValues, parserImplValues));
    }

    @Test
    void escapeByte() throws IOException {
        IntrinsicJakartaParser parser = new IntrinsicJakartaParser(getClass().getClassLoader().getResourceAsStream("escape.json"));
        JsonParserImpl parserImpl = new JsonParserImpl(getClass().getClassLoader().getResourceAsStream("escape.json"), new BufferPoolImpl());
        List<JsonParser.Event> parserEvents = new ArrayList<>();
        List<JsonParser.Event> parserImplEvents = new ArrayList<>();
        List<Object> parserValues = new ArrayList<>();
        List<Object> parserImplValues = new ArrayList<>();
        runParser(parser, parserEvents, parserValues);
        runParser(parserImpl, parserImplEvents, parserImplValues);
        Assertions.assertTrue(listsEquals(parserEvents, parserImplEvents) && listsEquals(parserValues, parserImplValues));
    }

    @Test
    void t1Parser() throws IOException {
        IntrinsicJakartaParser parser = new IntrinsicJakartaParser(getClass().getClassLoader().getResourceAsStream("emoji.json"));
        List<JsonParser.Event> parserEvents = new ArrayList<>();
        List<Object> parserValues = new ArrayList<>();
        runParser(parser, parserEvents, parserValues);
        Assertions.assertTrue(true);
    }

    @Test
    void test1Parser() throws IOException {
        IntrinsicJakartaParser parser = new IntrinsicJakartaParser(getClass().getClassLoader().getResourceAsStream("test_1.json"));
        JsonParserImpl parserImpl = new JsonParserImpl(getClass().getClassLoader().getResourceAsStream("test_1.json"), new BufferPoolImpl());
        List<JsonParser.Event> parserEvents = new ArrayList<>();
        List<JsonParser.Event> parserImplEvents = new ArrayList<>();
        List<Object> parserValues = new ArrayList<>();
        List<Object> parserImplValues = new ArrayList<>();
        runParser(parser, parserEvents, parserValues);
        runParser(parserImpl, parserImplEvents, parserImplValues);
        Assertions.assertTrue(listsEquals(parserEvents, parserImplEvents) && listsEquals(parserValues, parserImplValues));
    }

    @Test
    void testTweetParser() throws IOException {
        IntrinsicJakartaParser parser = new IntrinsicJakartaParser(getClass().getClassLoader().getResourceAsStream("twitter_dictionary.json"));
        JsonParserImpl parserImpl = new JsonParserImpl(getClass().getClassLoader().getResourceAsStream("twitter_dictionary.json"), new BufferPoolImpl());
        List<JsonParser.Event> parserEvents = new ArrayList<>();
        List<JsonParser.Event> parserImplEvents = new ArrayList<>();
        List<Object> parserValues = new ArrayList<>();
        List<Object> parserImplValues = new ArrayList<>();
        runParser(parser, parserEvents, parserValues);
        runParser(parserImpl, parserImplEvents, parserImplValues);
        Assertions.assertTrue(listsEquals(parserEvents, parserImplEvents) && listsEquals(parserValues, parserImplValues));
    }

    private void runParser(JsonParser parser, List<JsonParser.Event> events, List<Object> values) throws IOException {
        while(parser.hasNext()) {
            JsonParser.Event evt = parser.next();
            events.add(evt);
            switch(evt) {
                case START_ARRAY -> values.add("[");
                case END_ARRAY -> values.add("]");
                case KEY_NAME, VALUE_STRING -> values.add(parser.getString());
                case VALUE_NUMBER -> values.add(parser.getBigDecimal());
                case START_OBJECT -> values.add("{");
                case END_OBJECT -> values.add("}");
                case VALUE_FALSE -> values.add(false);
                case VALUE_TRUE -> values.add(true);
                case VALUE_NULL -> values.add("null");
                default -> throw new IOException("The event is null");
            }
        }
    }

    private boolean listsEquals(List list1, List list2) {
        if (list1.size() != list2.size()) {
            System.err.println("The lists are not of the same size");
        }
        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                System.err.println("The elements in position" + i +" are not equal");
                System.err.println(list1.get(i) + ", " + list2.get(i));
                return false;
            }
        }
        return true;
    }
}
