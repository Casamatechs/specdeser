package kr.sanchez.specdeser.core.jakarta.metadata;

public enum ParsingConstants {
    OBJECT("{}"),
    ARRAY("[]"),
    TRUE("true"),
    FALSE("false"),
    NULL("null"),
    STRING("\""),
    COMMA(","),
    COLON(":"),

    OPENOBJECT("{"),
    CLOSEOBJECT("}"),
    OPENOBJECT_STRING("{\""),

    STRING_COLON("\":"),
    STRING_COLON_STRING("\":\""), // This one will only apply when VALUE_TYPE is String.
    STRING_COLON_OPENOBJECT("\":{"),
    STRING_COLON_OPENARRAY("\":["),
    STRING_COLON_TRUE("\":true"),
    STRING_COLON_FALSE("\":false"),
    STRING_COLON_NULL("\":null"),

    STRING_COLON_TRUE_CLOSEOBJECT("\":true}"),
    STRING_COLON_FALSE_CLOSEOBJECT("\":false}"),
    STRING_COLON_NULL_CLOSEOBJECT("\":null}"),

    STRING_COLON_TRUE_COMMA("\":true,"),
    STRING_COLON_FALSE_COMMA("\":false,"),
    STRING_COLON_NULL_COMMA("\":null,"),
    STRING_COLON_TRUE_COMMA_STRING("\":true,\""),
    STRING_COLON_FALSE_COMMA_STRING("\":false,\""),
    STRING_COLON_NULL_COMMA_STRING("\":null,\""),

    STRING_COMMA("\","),
    STRING_COMMA_STRING("\",\""),

    OPENARRAY("["),
    OPENARRAY_OPENOBJECT("[{"),
    OPENARRAY_OPENOBJECT_STRING("[{\""),
    OPENARRAY_STRING("[\""),
    OPENARRAY_TRUE_COMMA("[true,"),
    OPENARRAY_FALSE_COMMA("[false,"),
    OPENARRAY_NULL_COMMA("[null,"),

    COMMA_STRING(",\""),
    COMMA_TRUE_CLOSEARRAY(",true]"),
    COMMA_FALSE_CLOSEARRAY(",false]"),
    COMMA_NULL_CLOSEARRAY(",null]"),
    STRING_CLOSEARRAY("\"]"),

    ;
    public final String token;

    ParsingConstants(String tkn) {
        token = tkn;
    }
}
