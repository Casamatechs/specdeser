//import com.fasterxml.jackson.core.JsonFactory;
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.core.JsonToken;
//import kr.sanchez.specdeser.benchmark.LiteralDeserializerBenchmark;
//import kr.sanchez.specdeser.benchmark.NumberDeserializerBenchmark;
//import kr.sanchez.specdeser.benchmark.TruffleBenchmark;
//import kr.sanchez.specdeser.benchmark.model.DummyString;
//import kr.sanchez.specdeser.core.exception.DeserializationException;
//import kr.sanchez.specdeser.core.serialization.NumberDeserializer;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//
//public class JmhTest {
//
//    @Test
//    void test() throws DeserializationException {
//        TruffleBenchmark benchmark = new TruffleBenchmark();
//        benchmark.getIndexIntrinsic(new TruffleBenchmark.StateObj());
//    }
//
//    @Test
//    void test2() throws IOException {
//        NumberDeserializerBenchmark benchmark = new NumberDeserializerBenchmark();
//        benchmark.deserializeNaturalJavaBenchmark(new NumberDeserializerBenchmark.StateObj());
//    }
//
//    @Test
//    void test3() throws IOException {
//        NumberDeserializerBenchmark benchmark = new NumberDeserializerBenchmark();
//        benchmark.deserializeDoubleJavaBenchmark(new NumberDeserializerBenchmark.StateObj());
//    }
//
//    @Test
//    void test4() throws IOException {
//        JsonParser parser = new JsonFactory().createParser("{\"key\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\"}".getBytes(StandardCharsets.UTF_8));
//        if (!parser.hasCurrentToken()) {
//            parser.nextToken();
//        }
//        while (parser.currentToken() != JsonToken.VALUE_STRING) {
//            parser.nextToken();
//        }
//        DummyString dummyString = new DummyString();
//        dummyString.key = parser.getValueAsString();
//        Assertions.assertEquals(dummyString.key,"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
//    }
//}
