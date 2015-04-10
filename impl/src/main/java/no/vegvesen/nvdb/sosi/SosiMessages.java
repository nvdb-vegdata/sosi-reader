// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi;

import no.vegvesen.nvdb.sosi.parser.SosiParser;
import no.vegvesen.nvdb.sosi.parser.SosiTokenizer;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.Objects.nonNull;

/**
 * Defines string formatting method for each constant in the resource file
 *
 * Based on a class from the Glassfish JSON parser (author Jitendra Kotamraju)
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public final class SosiMessages {
    private static final ResourceBundle BUNDLE;

    static {
        BUNDLE = ResourceBundle.getBundle("no.vegvesen.nvdb.sosi.messages", new Locale("nb"));
    }

    // tokenizer messages
    public static String TOKENIZER_UNEXPECTED_CHAR(int unexpected, SosiLocation location) {
        return localize("tokenizer.unexpected.char", unexpected, location);
    }

    public static String TOKENIZER_EXPECTED_CHAR(int unexpected, SosiLocation location, char expected) {
        return localize("tokenizer.expected.char", unexpected, location, expected);
    }

    public static String TOKENIZER_IO_ERR() {
        return localize("tokenizer.io.err");
    }


    // parser messages
    public static String PARSER_GETSTRING_ERR(SosiParser.Event event) {
        return localize("parser.getString.err", event);
    }

    public static String PARSER_ISINTEGRALNUMBER_ERR(SosiParser.Event event) {
        return localize("parser.isIntegralNumber.err", event);
    }

    public static String PARSER_GETINT_ERR(SosiParser.Event event) {
        return localize("parser.getInt.err", event);
    }

    public static String PARSER_GETLONG_ERR(SosiParser.Event event) {
        return localize("parser.getLong.err", event);
    }

    public static String PARSER_GETBIGDECIMAL_ERR(SosiParser.Event event) {
        return localize("parser.getBigDecimal.err", event);
    }

    public static String PARSER_EXPECTED_EOF(SosiTokenizer.SosiToken token) {
        return localize("parser.expected.eof", token);
    }

    public static String PARSER_TOKENIZER_CLOSE_IO() {
        return localize("parser.tokenizer.close.io");
    }

    public static String PARSER_INVALID_TOKEN(SosiTokenizer.SosiToken token, SosiLocation location, String expectedTokens) {
        return localize("parser.invalid.token", token, location, expectedTokens);
    }

    public static String PARSER_HEAD_MUST_BE_FIRST() {
        return localize("parser.grammar.headMustBeFirst");
    }

    public static String PARSER_INVALID_CONCATENATION() {
        return localize("parser.grammar.invalidConcatenation");
    }

    public static String PARSER_EMPTY_ELEMENT() {
        return localize("parser.grammar.emptyElement");
    }

    public static String PARSER_LEVEL_LEAP() {
        return localize("parser.grammar.levelLeap");
    }


    // generator messages
    public static String GENERATOR_FLUSH_IO_ERR() {
        return localize("generator.flush.io.err");
    }

    public static String GENERATOR_CLOSE_IO_ERR() {
        return localize("generator.close.io.err");
    }

    public static String GENERATOR_WRITE_IO_ERR() {
        return localize("generator.write.io.err");
    }

    public static String GENERATOR_ILLEGAL_METHOD(Object scope) {
        return localize("generator.illegal.method", scope);
    }

    public static String GENERATOR_DOUBLE_INFINITE_NAN() {
        return localize("generator.double.infinite.nan");
    }

    public static String GENERATOR_INCOMPLETE_JSON() {
        return localize("generator.incomplete.sosi");
    }

    public static String GENERATOR_ILLEGAL_MULTIPLE_TEXT() {
        return localize("generator.illegal.multiple.text");
    }

    // writer messages
    public static String WRITER_WRITE_ALREADY_CALLED() {
        return localize("writer.write.already.called");
    }


    // reader messages
    public static String READER_READ_ALREADY_CALLED() {
        return localize("reader.read.already.called");
    }

    // element builder messages
    public static String ELEMENTBUILDER_NAME_NULL() {
        return localize("elementbuilder.name.null");
    }

    public static String ELEMENTBUILDER_VALUE_NULL() {
        return localize("elementbuilder.value.null");
    }

    public static String ELEMENTBUILDER_OBJECT_BUILDER_NULL() {
        return localize("elementbuilder.element.builder.null");
    }

    private static String localize(String key, Object ... args) {
        try {
            String msg = BUNDLE.getString(key);
            Locale locale = BUNDLE.getLocale();
            return MessageFormat.format(msg, args);
        } catch (Exception e) {
            return getDefaultMessage(key, args);
        }
    }

    private static String getDefaultMessage(String key, Object ... args) {
        StringBuilder sb = new StringBuilder();
        sb.append("[failed to localize] ");
        sb.append(key);
        if (nonNull(args)) {
            sb.append('(');
            for (int i = 0; i < args.length; ++i) {
                if (i != 0)
                    sb.append(", ");
                sb.append(String.valueOf(args[i]));
            }
            sb.append(')');
        }
        return sb.toString();
    }
}
