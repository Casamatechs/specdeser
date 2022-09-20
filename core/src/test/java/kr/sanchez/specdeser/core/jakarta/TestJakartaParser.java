package kr.sanchez.specdeser.core.jakarta;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.json.stream.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TestJakartaParser {

    @Test
    void testNumberParser() {
        FallbackParser parser = new FallbackParser(new ByteArrayInputStream("123456789".getBytes(StandardCharsets.UTF_8)), new ByteBufferPoolImpl());
        if (parser.next() == JsonParser.Event.VALUE_NUMBER) {
            Assertions.assertEquals(123456789, parser.getInt());
        }
    }

    @Test
    void testNegativeParser() {
        FallbackParser parser = new FallbackParser(new ByteArrayInputStream("-123456789".getBytes(StandardCharsets.UTF_8)), new ByteBufferPoolImpl());
        if (parser.next() == JsonParser.Event.VALUE_NUMBER) {
            Assertions.assertEquals(-123456789, parser.getInt());
        }
    }

    @Test
    void testExpParser() {
        FallbackParser parser = new FallbackParser(new ByteArrayInputStream("2E6".getBytes(StandardCharsets.UTF_8)), new ByteBufferPoolImpl());
        if (parser.next() == JsonParser.Event.VALUE_NUMBER) {
            Assertions.assertEquals(2000000, parser.getInt());
        }
    }

    @Test
    void testFloatingParser() {
        FallbackParser parser = new FallbackParser(new ByteArrayInputStream("1234.5678".getBytes(StandardCharsets.UTF_8)), new ByteBufferPoolImpl());
        if (parser.next() == JsonParser.Event.VALUE_NUMBER) {
            Assertions.assertEquals(1234.5678, parser.getDouble());
        }
    }

    @Test
    void testStringParser() {
        FallbackParser parser = new FallbackParser(new ByteArrayInputStream("\"Pero weno willy compañero\"".getBytes(StandardCharsets.UTF_8)), new ByteBufferPoolImpl());
        if (parser.next() == JsonParser.Event.VALUE_STRING) {
            Assertions.assertEquals("Pero weno willy compañero",parser.getString());
        }
    }

    @Test
    void testTrueParser() {
        FallbackParser parser = new FallbackParser(new ByteArrayInputStream("true".getBytes(StandardCharsets.UTF_8)), new ByteBufferPoolImpl());
        if (parser.next() == JsonParser.Event.VALUE_TRUE) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    void testFalseParser() {
        FallbackParser parser = new FallbackParser(new ByteArrayInputStream("false".getBytes(StandardCharsets.UTF_8)), new ByteBufferPoolImpl());
        if (parser.next() == JsonParser.Event.VALUE_FALSE) {
            Assertions.assertFalse(false);
        }
    }
    @Test
    void testNullParser() {
        FallbackParser parser = new FallbackParser(new ByteArrayInputStream("null".getBytes(StandardCharsets.UTF_8)), new ByteBufferPoolImpl());
        if (parser.next() == JsonParser.Event.VALUE_NULL) {
            Assertions.assertNull(null);
        }
    }

    @Test
    void testBasicParse() {
        FallbackParser parser = new FallbackParser(new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)), new ByteBufferPoolImpl());
        int i = 0;
        while(parser.hasNext()) {
            parser.next();
            i++;
        }
        Assertions.assertEquals(i,2);
    }

    @Test
    void testBasicJsonParser() {
        FallbackParser parser = new FallbackParser(new ByteArrayInputStream("{\"name\":\"Paco\"}".getBytes(StandardCharsets.UTF_8)), new ByteBufferPoolImpl());
        while(parser.hasNext()) {
            JsonParser.Event evt = parser.next();
            System.out.println(evt);
        }
        Assertions.assertTrue(parser.stackEmpty());
    }

    @Test
    void testBasic2JsonParser() throws IOException {
        FallbackParser parser = new FallbackParser(new ByteArrayInputStream("{\"name\":\"Paco\",\"ID\":12345678}".getBytes(StandardCharsets.UTF_8)), new ByteBufferPoolImpl());
        runParser(parser);
        Assertions.assertTrue(parser.stackEmpty());
    }

    @Test
    void testBasic3JsonParser() throws IOException {
        FallbackParser parser = new FallbackParser(new ByteArrayInputStream("{\"name\":\"Paco\",\"ID\":1234.5678,\"tweets\":123e4}".getBytes(StandardCharsets.UTF_8)), new ByteBufferPoolImpl());
        runParser(parser);
        Assertions.assertTrue(parser.stackEmpty());
    }

    @Test
    void testArrayParser() throws IOException {
        FallbackParser parser = new FallbackParser(new ByteArrayInputStream("[1,2,3]".getBytes(StandardCharsets.UTF_8)), new ByteBufferPoolImpl());
        runParser(parser);
        Assertions.assertTrue(parser.stackEmpty());
    }

    @Test
    void testArraySpacesParser() throws IOException {
        FallbackParser parser = new FallbackParser(new ByteArrayInputStream("[1 ,\n 2,\n\n3]".getBytes(StandardCharsets.UTF_8)), new ByteBufferPoolImpl());
        runParser(parser);
        Assertions.assertTrue(parser.stackEmpty());
    }

    @Test
    void testJsonParser() throws IOException {
        String str = "{\n" +
                "  \"eye\": {\n" +
                "    \"tea\": 24.5,\n" +
                "    \"special\": true,\n" +
                "    \"separate\": \"hurry\"\n" +
                "  },\n" +
                "  \"pet\": 165883533,\n" +
                "  \"garden\": \"send\",\n" +
                "  \"levels\": [true, false, false]\n" +
                "}";
        FallbackParser parser = new FallbackParser(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)), new ByteBufferPoolImpl());
        runParser(parser);
        Assertions.assertTrue(parser.stackEmpty());
    }

    @Test
    void testJson2Parser() throws IOException {
        String str = "{\n" +
                "  \"pet\": 165883533,\n" +
                "  \"garden\": \"send\",\n" +
                "  \"levels\": [true, false, false],\n" +
                "  \"eye\": {\n" +
                "    \"tea\": 24.5,\n" +
                "    \"special\": true,\n" +
                "    \"separate\": \"hurry\"\n" +
                "  }\n" +
                "}";
        FallbackParser parser = new FallbackParser(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)), new ByteBufferPoolImpl());
        runParser(parser);
        Assertions.assertTrue(parser.stackEmpty());
    }

    @Test
    void testTweetParser() throws IOException {
        String str = "{\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"conversation_id\": \"1304102743196356610\",\n" +
                "      \"id\": \"1307025659294674945\",\n" +
                "      \"possibly_sensitive\": false,\n" +
                "      \"public_metrics\": {\n" +
                "        \"retweet_count\": 11,\n" +
                "        \"reply_count\": 2,\n" +
                "        \"like_count\": 70,\n" +
                "        \"quote_count\": 1\n" +
                "      },\n" +
                "      \"entities\": {\n" +
                "        \"urls\": [\n" +
                "          {\n" +
                "            \"start\": 74,\n" +
                "            \"end\": 97,\n" +
                "            \"url\": \"https://t.co/oeF3ZHeKQQ\",\n" +
                "            \"expanded_url\": \"https://dev.to/twitterdev/understanding-the-new-tweet-payload-in-the-twitter-api-v2-1fg5\",\n" +
                "            \"display_url\": \"dev.to/twitterdev/und…\",\n" +
                "            \"images\": [\n" +
                "              {\n" +
                "                \"url\": \"https://pbs.twimg.com/news_img/1317156296982867969/2uLfv-Bh?format=jpg&name=orig\",\n" +
                "                \"width\": 1128,\n" +
                "                \"height\": 600\n" +
                "              },\n" +
                "              {\n" +
                "                \"url\": \"https://pbs.twimg.com/news_img/1317156296982867969/2uLfv-Bh?format=jpg&name=150x150\",\n" +
                "                \"width\": 150,\n" +
                "                \"height\": 150\n" +
                "              }\n" +
                "            ],\n" +
                "            \"status\": 200,\n" +
                "            \"title\": \"Understanding the new Tweet payload in the Twitter API v2\",\n" +
                "            \"description\": \"Twitter recently announced the new Twitter API v2, rebuilt from the ground up to deliver new features...\",\n" +
                "            \"unwound_url\": \"https://dev.to/twitterdev/understanding-the-new-tweet-payload-in-the-twitter-api-v2-1fg5\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"text\": \"Here’s an article that highlights the updates in the new Tweet payload v2 https://t.co/oeF3ZHeKQQ\",\n" +
                "      \"in_reply_to_user_id\": \"2244994945\",\n" +
                "      \"created_at\": \"2020-09-18T18:36:15.000Z\",\n" +
                "      \"author_id\": \"2244994945\",\n" +
                "      \"referenced_tweets\": [\n" +
                "        {\n" +
                "          \"type\": \"replied_to\",\n" +
                "          \"id\": \"1304102743196356610\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"lang\": \"en\",\n" +
                "      \"source\": \"Twitter Web App\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"includes\": {\n" +
                "    \"users\": [\n" +
                "      {\n" +
                "        \"created_at\": \"2013-12-14T04:35:55.000Z\",\n" +
                "        \"profile_image_url\": \"https://pbs.twimg.com/profile_images/1283786620521652229/lEODkLTh_normal.jpg\",\n" +
                "        \"entities\": {\n" +
                "          \"url\": {\n" +
                "            \"urls\": [\n" +
                "              {\n" +
                "                \"start\": 0,\n" +
                "                \"end\": 23,\n" +
                "                \"url\": \"https://t.co/3ZX3TNiZCY\",\n" +
                "                \"expanded_url\": \"https://developer.twitter.com/en/community\",\n" +
                "                \"display_url\": \"developer.twitter.com/en/community\"\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          \"description\": {\n" +
                "            \"hashtags\": [\n" +
                "              {\n" +
                "                \"start\": 17,\n" +
                "                \"end\": 28,\n" +
                "                \"tag\": \"TwitterDev\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"start\": 105,\n" +
                "                \"end\": 116,\n" +
                "                \"tag\": \"TwitterAPI\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        },\n" +
                "        \"id\": \"2244994945\",\n" +
                "        \"verified\": true,\n" +
                "        \"location\": \"127.0.0.1\",\n" +
                "        \"description\": \"The voice of the #TwitterDev team and your official source for updates, news, and events, related to the #TwitterAPI.\",\n" +
                "        \"pinned_tweet_id\": \"1293593516040269825\",\n" +
                "        \"username\": \"TwitterDev\",\n" +
                "        \"public_metrics\": {\n" +
                "          \"followers_count\": 513961,\n" +
                "          \"following_count\": 2039,\n" +
                "          \"tweet_count\": 3635,\n" +
                "          \"listed_count\": 1672\n" +
                "        },\n" +
                "        \"name\": \"Twitter Dev\",\n" +
                "        \"url\": \"https://t.co/3ZX3TNiZCY\",\n" +
                "        \"protected\": false\n" +
                "      }\n" +
                "    ],\n" +
                "    \"tweets\": [\n" +
                "      {\n" +
                "        \"conversation_id\": \"1304102743196356610\",\n" +
                "        \"id\": \"1304102743196356610\",\n" +
                "        \"possibly_sensitive\": false,\n" +
                "        \"public_metrics\": {\n" +
                "          \"retweet_count\": 31,\n" +
                "          \"reply_count\": 12,\n" +
                "          \"like_count\": 104,\n" +
                "          \"quote_count\": 4\n" +
                "        },\n" +
                "        \"entities\": {\n" +
                "          \"mentions\": [\n" +
                "            {\n" +
                "              \"start\": 146,\n" +
                "              \"end\": 158,\n" +
                "              \"username\": \"suhemparack\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"urls\": [\n" +
                "            {\n" +
                "              \"start\": 237,\n" +
                "              \"end\": 260,\n" +
                "              \"url\": \"https://t.co/CjneyMpgCq\",\n" +
                "              \"expanded_url\": \"https://twitter.com/TwitterDev/status/1304102743196356610/video/1\",\n" +
                "              \"display_url\": \"pic.twitter.com/CjneyMpgCq\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"hashtags\": [\n" +
                "            {\n" +
                "              \"start\": 8,\n" +
                "              \"end\": 19,\n" +
                "              \"tag\": \"TwitterAPI\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"attachments\": {\n" +
                "          \"media_keys\": [\n" +
                "            \"13_1303848070984024065\"\n" +
                "          ]\n" +
                "        },\n" +
                "        \"text\": \"The new #TwitterAPI includes some improvements to the Tweet payload. You’re probably wondering — what are the main differences? \uD83E\uDDD0\\n\\nIn this video, @SuhemParack compares the v1.1 Tweet payload with what you’ll find using our v2 endpoints. https://t.co/CjneyMpgCq\",\n" +
                "        \"created_at\": \"2020-09-10T17:01:37.000Z\",\n" +
                "        \"author_id\": \"2244994945\",\n" +
                "        \"lang\": \"en\",\n" +
                "        \"source\": \"Twitter Media Studio\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        FallbackParser parser = new FallbackParser(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)), new ByteBufferPoolImpl());
        runParser(parser);
        Assertions.assertTrue(parser.stackEmpty());
    }

    @Test
    void testTweetByteBufferParser() throws IOException {
        String str = "{\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"conversation_id\": \"1304102743196356610\",\n" +
                "      \"id\": \"1307025659294674945\",\n" +
                "      \"possibly_sensitive\": false,\n" +
                "      \"public_metrics\": {\n" +
                "        \"retweet_count\": 11,\n" +
                "        \"reply_count\": 2,\n" +
                "        \"like_count\": 70,\n" +
                "        \"quote_count\": 1\n" +
                "      },\n" +
                "      \"entities\": {\n" +
                "        \"urls\": [\n" +
                "          {\n" +
                "            \"start\": 74,\n" +
                "            \"end\": 97,\n" +
                "            \"url\": \"https://t.co/oeF3ZHeKQQ\",\n" +
                "            \"expanded_url\": \"https://dev.to/twitterdev/understanding-the-new-tweet-payload-in-the-twitter-api-v2-1fg5\",\n" +
                "            \"display_url\": \"dev.to/twitterdev/und…\",\n" +
                "            \"images\": [\n" +
                "              {\n" +
                "                \"url\": \"https://pbs.twimg.com/news_img/1317156296982867969/2uLfv-Bh?format=jpg&name=orig\",\n" +
                "                \"width\": 1128,\n" +
                "                \"height\": 600\n" +
                "              },\n" +
                "              {\n" +
                "                \"url\": \"https://pbs.twimg.com/news_img/1317156296982867969/2uLfv-Bh?format=jpg&name=150x150\",\n" +
                "                \"width\": 150,\n" +
                "                \"height\": 150\n" +
                "              }\n" +
                "            ],\n" +
                "            \"status\": 200,\n" +
                "            \"title\": \"Understanding the new Tweet payload in the Twitter API v2\",\n" +
                "            \"description\": \"Twitter recently announced the new Twitter API v2, rebuilt from the ground up to deliver new features...\",\n" +
                "            \"unwound_url\": \"https://dev.to/twitterdev/understanding-the-new-tweet-payload-in-the-twitter-api-v2-1fg5\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"text\": \"Here’s an article that highlights the updates in the new Tweet payload v2 https://t.co/oeF3ZHeKQQ\",\n" +
                "      \"in_reply_to_user_id\": \"2244994945\",\n" +
                "      \"created_at\": \"2020-09-18T18:36:15.000Z\",\n" +
                "      \"author_id\": \"2244994945\",\n" +
                "      \"referenced_tweets\": [\n" +
                "        {\n" +
                "          \"type\": \"replied_to\",\n" +
                "          \"id\": \"1304102743196356610\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"lang\": \"en\",\n" +
                "      \"source\": \"Twitter Web App\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"includes\": {\n" +
                "    \"users\": [\n" +
                "      {\n" +
                "        \"created_at\": \"2013-12-14T04:35:55.000Z\",\n" +
                "        \"profile_image_url\": \"https://pbs.twimg.com/profile_images/1283786620521652229/lEODkLTh_normal.jpg\",\n" +
                "        \"entities\": {\n" +
                "          \"url\": {\n" +
                "            \"urls\": [\n" +
                "              {\n" +
                "                \"start\": 0,\n" +
                "                \"end\": 23,\n" +
                "                \"url\": \"https://t.co/3ZX3TNiZCY\",\n" +
                "                \"expanded_url\": \"https://developer.twitter.com/en/community\",\n" +
                "                \"display_url\": \"developer.twitter.com/en/community\"\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          \"description\": {\n" +
                "            \"hashtags\": [\n" +
                "              {\n" +
                "                \"start\": 17,\n" +
                "                \"end\": 28,\n" +
                "                \"tag\": \"TwitterDev\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"start\": 105,\n" +
                "                \"end\": 116,\n" +
                "                \"tag\": \"TwitterAPI\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        },\n" +
                "        \"id\": \"2244994945\",\n" +
                "        \"verified\": true,\n" +
                "        \"location\": \"127.0.0.1\",\n" +
                "        \"description\": \"The voice of the #TwitterDev team and your official source for updates, news, and events, related to the #TwitterAPI.\",\n" +
                "        \"pinned_tweet_id\": \"1293593516040269825\",\n" +
                "        \"username\": \"TwitterDev\",\n" +
                "        \"public_metrics\": {\n" +
                "          \"followers_count\": 513961,\n" +
                "          \"following_count\": 2039,\n" +
                "          \"tweet_count\": 3635,\n" +
                "          \"listed_count\": 1672\n" +
                "        },\n" +
                "        \"name\": \"Twitter Dev\",\n" +
                "        \"url\": \"https://t.co/3ZX3TNiZCY\",\n" +
                "        \"protected\": false\n" +
                "      }\n" +
                "    ],\n" +
                "    \"tweets\": [\n" +
                "      {\n" +
                "        \"conversation_id\": \"1304102743196356610\",\n" +
                "        \"id\": \"1304102743196356610\",\n" +
                "        \"possibly_sensitive\": false,\n" +
                "        \"public_metrics\": {\n" +
                "          \"retweet_count\": 31,\n" +
                "          \"reply_count\": 12,\n" +
                "          \"like_count\": 104,\n" +
                "          \"quote_count\": 4\n" +
                "        },\n" +
                "        \"entities\": {\n" +
                "          \"mentions\": [\n" +
                "            {\n" +
                "              \"start\": 146,\n" +
                "              \"end\": 158,\n" +
                "              \"username\": \"suhemparack\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"urls\": [\n" +
                "            {\n" +
                "              \"start\": 237,\n" +
                "              \"end\": 260,\n" +
                "              \"url\": \"https://t.co/CjneyMpgCq\",\n" +
                "              \"expanded_url\": \"https://twitter.com/TwitterDev/status/1304102743196356610/video/1\",\n" +
                "              \"display_url\": \"pic.twitter.com/CjneyMpgCq\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"hashtags\": [\n" +
                "            {\n" +
                "              \"start\": 8,\n" +
                "              \"end\": 19,\n" +
                "              \"tag\": \"TwitterAPI\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"attachments\": {\n" +
                "          \"media_keys\": [\n" +
                "            \"13_1303848070984024065\"\n" +
                "          ]\n" +
                "        },\n" +
                "        \"text\": \"The new #TwitterAPI includes some improvements to the Tweet payload. You’re probably wondering — what are the main differences? \uD83E\uDDD0\\n\\nIn this video, @SuhemParack compares the v1.1 Tweet payload with what you’ll find using our v2 endpoints. https://t.co/CjneyMpgCq\",\n" +
                "        \"created_at\": \"2020-09-10T17:01:37.000Z\",\n" +
                "        \"author_id\": \"2244994945\",\n" +
                "        \"lang\": \"en\",\n" +
                "        \"source\": \"Twitter Media Studio\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}\n";
        FallbackParser parser = new FallbackParser(str.getBytes(StandardCharsets.UTF_8));
        runParser(parser);
        Assertions.assertTrue(parser.stackEmpty());
    }

    private void runParser(JsonParser parser) throws IOException {
        while(parser.hasNext()) {
            JsonParser.Event evt = parser.next();
            System.out.print(evt + ", ");
            switch(evt) {
                case START_ARRAY -> System.out.println("[");
                case END_ARRAY -> System.out.println("]");
                case KEY_NAME, VALUE_STRING -> System.out.println(parser.getString());
                case VALUE_NUMBER -> System.out.println(parser.getBigDecimal());
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
