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
package no.vegvesen.nvdb.sosi.parser;

import no.vegvesen.nvdb.sosi.SosiException;
import no.vegvesen.nvdb.sosi.SosiMessages;
import no.vegvesen.nvdb.sosi.SosiLocation;
import no.vegvesen.nvdb.sosi.encoding.SosiEncoding;
import no.vegvesen.nvdb.sosi.utils.BufferPool;
import no.vegvesen.nvdb.sosi.encoding.CharsetDetectingInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.Objects.isNull;
import static no.vegvesen.nvdb.sosi.parser.SosiTokenizer.SosiToken;

/**
 * SOSI parser implementation. NoneContext and ElementContext is used to go to next parser state.
 *
 * Based on a class from the Glassfish JSON parser (author Jitendra Kotamraju)
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class SosiParserImpl implements SosiParser {

    private static final String ELEMENT_HEAD = "HODE";
    private static final String ELEMENT_END = "SLUTT";

    private Context currentContext = new NoneContext();
    private Event previousEvent;
    private Event currentEvent;
    private int currentLevel = 0;
    private int levelsToClose = 0;
    private boolean missingOrInvalidCharset = false;
    private boolean headFound = false;
    private boolean endFound = false;
    private boolean openParenthesisFound = false;

    private final Stack stack = new Stack();
    private final StateIterator stateIterator;
    private final SosiTokenizer tokenizer;

    private int features;

    public SosiParserImpl(Reader reader, BufferPool bufferPool) {
        tokenizer = new SosiTokenizer(reader, bufferPool);
        stateIterator = new StateIterator();
        features = Feature.collectDefaults();
    }

    public SosiParserImpl(InputStream in, BufferPool bufferPool) {
        CharsetDetectingInputStream cdin = new CharsetDetectingInputStream(in);
        Optional<Charset> maybeEncoding = cdin.getCharset();
        this.missingOrInvalidCharset = !maybeEncoding.isPresent();
        tokenizer = new SosiTokenizer(
                new InputStreamReader(cdin, maybeEncoding.orElse(SosiEncoding.defaultCharset())), bufferPool);
        stateIterator = new StateIterator();
        features = Feature.collectDefaults();
    }

    public SosiParserImpl(InputStream in, Charset encoding, BufferPool bufferPool) {
        tokenizer = new SosiTokenizer(
                new InputStreamReader(in, encoding), bufferPool);
        stateIterator = new StateIterator();
        features = Feature.collectDefaults();
    }

    @Override
    public String getString() {
        if (currentEvent.isOneOf(Event.START_HEAD, Event.START_ELEMENT, Event.VALUE_STRING, Event.VALUE_NUMBER, Event.VALUE_SERNO, Event.VALUE_REF, Event.COMMENT, Event.END)) {
            String value = tokenizer.getValue();

            if (currentEvent == Event.VALUE_STRING) {
                value = value.replace("\"\"", "\"").replace("''", "'");
            }

            return value;
        }
        throw new IllegalStateException(
                SosiMessages.PARSER_GETSTRING_ERR(currentEvent));
    }

    @Override
    public boolean isIntegralNumber() {
        if (!currentEvent.isOneOf(Event.VALUE_NUMBER, Event.VALUE_SERNO, Event.VALUE_REF)) {
            throw new IllegalStateException(
                    SosiMessages.PARSER_ISINTEGRALNUMBER_ERR(currentEvent));
        }
        return tokenizer.isIntegral();
    }

    @Override
    public int getInt() {
        if (!currentEvent.isOneOf(Event.VALUE_NUMBER, Event.VALUE_SERNO, Event.VALUE_REF)) {
            throw new IllegalStateException(
                    SosiMessages.PARSER_GETINT_ERR(currentEvent));
        }
        return tokenizer.getInt();
    }

    public boolean isDefinitelyInt() {
        return tokenizer.isDefinitelyInt();
    }

    @Override
    public long getLong() {
        if (!currentEvent.isOneOf(Event.VALUE_NUMBER, Event.VALUE_SERNO, Event.VALUE_REF)) {
            throw new IllegalStateException(
                    SosiMessages.PARSER_GETLONG_ERR(currentEvent));
        }
        return tokenizer.getBigDecimal().longValue();
    }

    @Override
    public BigDecimal getBigDecimal() {
        if (!currentEvent.isOneOf(Event.VALUE_NUMBER, Event.VALUE_SERNO, Event.VALUE_REF)) {
            throw new IllegalStateException(
                    SosiMessages.PARSER_GETBIGDECIMAL_ERR(currentEvent));
        }
        return tokenizer.getBigDecimal();
    }

    @Override
    public SosiLocation getLocation() {
        return tokenizer.getLocation();
    }

    private SosiLocation getLastCharLocation() {
        return tokenizer.getLastCharLocation();
    }

    @Override
    public boolean hasNext() {
        return stateIterator.hasNext();
    }

    @Override
    public Event next() {
        return stateIterator.next();
    }

    @Override
    public SosiParser enable(Feature feature)
    {
        features |= feature.getMask();
        return this;
    }

    @Override
    public SosiParser disable(Feature feature)
    {
        features &= ~feature.getMask();
        return this;
    }

    @Override
    public boolean isEnabled(Feature feature) {
        return (features & feature.getMask()) != 0;
    }

    private class StateIterator implements Iterator<Event> {

        @Override
        public boolean hasNext() {
            return !endFound;
        }

        @Override
        public SosiParser.Event next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            previousEvent = currentEvent;
            return currentEvent = currentContext.getNextEvent();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public void close() {
        try {
            tokenizer.close();
        } catch (IOException e) {
            throw new SosiException(SosiMessages.PARSER_TOKENIZER_CLOSE_IO(), e);
        }
    }

    // Using the optimized stack impl as we don't require other things
    // like iterator etc.
    private static final class Stack {
        private Context head;

        private void push(Context context) {
            context.next = head;
            head = context;
        }

        private Context pop() {
            if (isNull(head)) {
                throw new NoSuchElementException();
            }
            Context temp = head;
            head = head.next;
            return temp;
        }

        private boolean isEmpty() {
            return isNull(head);
        }
    }

    private abstract class Context {
        Context next;
        abstract Event getNextEvent();
    }

    private final class NoneContext extends Context {
        @Override
        public Event getNextEvent() {
            SosiToken token = tokenizer.nextToken();
            if (token == SosiToken.LEVEL) {
                int level = tokenizer.getValue().length();
                if (level == 1) {
                    stack.push(currentContext);
                    currentContext = new ElementContext();
                    return currentContext.getNextEvent();
                }
            }

            throw parsingException(token, "[LEVEL]");
        }
    }

    private SosiParsingException parsingException(SosiToken token, String expectedTokens) {
        SosiLocation location = getLastCharLocation();
        return new SosiParsingException(SosiMessages.PARSER_INVALID_TOKEN(token, location, expectedTokens), location);
    }

    private SosiParsingException parsingException(String message) {
        SosiLocation location = getLastCharLocation();
        return new SosiParsingException(message, location);
    }

    private final class ElementContext extends Context {
        private boolean firstValue = true;
        private boolean isHead = false;

        @Override
        public Event getNextEvent() {
            if (levelsToClose > 0) {
                return getClosingEvent();
            }

            SosiToken token = tokenizer.nextToken();
            String tokenValue = tokenizer.getValue();
            if (firstValue) {
                currentLevel++;
                if (token != SosiToken.ELEMENT_NAME) {
                    throw parsingException(token, "[ELEMENT_NAME]");
                }

                firstValue = false;
                isHead = tokenValue.equalsIgnoreCase(ELEMENT_HEAD);
                boolean isEnd = tokenValue.equalsIgnoreCase(ELEMENT_END);

                if (isEnd) {
                    stack.pop();
                    endFound = true;
                    return Event.END;
                } else if (isHead) {
                    if (missingOrInvalidCharset && !isEnabled(Feature.ALLOW_MISSING_OR_INVALID_CHARSET)) {
                        throw parsingException(SosiMessages.PARSER_MISSING_OR_INVALID_CHARSET());
                    }
                    headFound = true;
                    return Event.START_HEAD;
                } else {
                    if (!headFound) {
                        throw parsingException(SosiMessages.PARSER_HEAD_MUST_BE_FIRST());
                    }
                    return Event.START_ELEMENT;
                }
            } else if (token == SosiToken.LEVEL) {
                if (previousEvent == Event.CONCATENATION) {
                    throw parsingException(SosiMessages.PARSER_INVALID_CONCATENATION());
                }
                if (openParenthesisFound) {
                    throw parsingException(SosiMessages.PARSER_UNMATCHED_PARENTHESIS());
                }
                int nextLevel = tokenValue.length();
                if (nextLevel > currentLevel) {
                    if (nextLevel > currentLevel + 1) {
                        throw parsingException(SosiMessages.PARSER_LEVEL_LEAP());
                    }
                    stack.push(currentContext);
                    currentContext = new ElementContext();
                    return currentContext.getNextEvent();
                } else if (nextLevel <= currentLevel) {
                    if (previousEvent == Event.START_HEAD || previousEvent == Event.START_ELEMENT) {
                        if (!isEnabled(Feature.ALLOW_EMPTY_ELEMENTS)) {
                            throw parsingException(SosiMessages.PARSER_EMPTY_ELEMENT());
                        }
                    }
                    levelsToClose = currentLevel - nextLevel + 1;
                    return getClosingEvent();
                }
            } else if (token.isOneOf(SosiToken.EXCLAMATION_MARK, SosiToken.AT_MARK, SosiToken.ASTERISK, SosiToken.AMPERSAND, SosiToken.OPEN_PARENTHESIS, SosiToken.CLOSE_PARENTHESIS, SosiToken.VALUE_STRING, SosiToken.VALUE_NUMBER, SosiToken.VALUE_COLON, SosiToken.COLON_VALUE)) {
                Event event = token.getEvent();
                if (openParenthesisFound && event != Event.VALUE_REF && event != Event.END_REF_ISLAND) {
                    throw parsingException(token, "[COLON_VALUE,CLOSE_PARENTHESIS]");
                }
                if (event == Event.CONCATENATION && previousEvent != Event.VALUE_STRING) {
                    throw parsingException(SosiMessages.PARSER_INVALID_CONCATENATION());
                }
                if (previousEvent == Event.CONCATENATION && event != Event.VALUE_STRING) {
                    throw parsingException(SosiMessages.PARSER_INVALID_CONCATENATION());
                }
                if (event == Event.START_REF_ISLAND) {
                    if (openParenthesisFound) {
                        throw parsingException(SosiMessages.PARSER_NESTED_PARENTHESES());
                    }
                    openParenthesisFound = true;
                }
                if (event == Event.END_REF_ISLAND) {
                    if (!openParenthesisFound) {
                        throw parsingException(SosiMessages.PARSER_UNMATCHED_PARENTHESIS());
                    }
                    openParenthesisFound = false;
                }

                return event;
            }

            throw parsingException(token, "[ELEMENT_NAME|LEVEL|EXCLAMATION_MARK|AT_MARK|ASTERISK|AMPERSAND|OPEN_PARENTHESIS|CLOSE_PARENTHESIS|VALUE_STRING|VALUE_NUMBER|COLON_VALUE|VALUE_COLON|COMMENT]");
        }

        private Event getClosingEvent() {
            if (levelsToClose > 0) {
                levelsToClose--;
                currentLevel--;
                currentContext = stack.pop();
                if (levelsToClose == 0) {
                    stack.push(currentContext);
                    currentContext = new ElementContext();
                }
                return isHead ? Event.END_HEAD : Event.END_ELEMENT;
            } else {
                throw new IllegalStateException("Closing event unexpected at this point");
            }
        }
    }
}
