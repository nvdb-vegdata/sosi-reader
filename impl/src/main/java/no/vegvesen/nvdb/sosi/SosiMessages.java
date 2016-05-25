/*
 * Copyright (c) 2015-2016, Statens vegvesen
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
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

    public static String PARSER_MISSING_OR_INVALID_CHARSET() {
        return localize("parser.grammar.missingOrInvalidCharset");
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

    public static String PARSER_UNMATCHED_PARENTHESIS() {
        return localize("parser.grammar.unmatchedParenthesis");
    }

    public static String PARSER_NESTED_PARENTHESES() {
        return localize("parser.grammar.nestedParentheses");
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
