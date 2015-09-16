// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.parser;

import no.vegvesen.nvdb.sosi.SosiException;
import no.vegvesen.nvdb.sosi.SosiMessages;
import no.vegvesen.nvdb.sosi.SosiLocation;
import no.vegvesen.nvdb.sosi.utils.BufferPool;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Arrays;

import static java.util.Objects.isNull;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event;

/**
 * A SOSI Tokenizer
 *
 * Based on a class from the Glassfish JSON parser (author Jitendra Kotamraju)
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public final class SosiTokenizer implements Closeable {

    private final BufferPool bufferPool;
    private final Reader reader;

    // Internal buffer that is used for parsing. It is also used
    // for storing current string and number value token
    private char[] buf;

    // Indexes in buffer
    //
    // XXXssssssssssssXXXXXXXXXXXXXXXXXXXXXXrrrrrrrrrrrrrrXXXXXX
    //    ^           ^                     ^             ^
    //    |           |                     |             |
    //   storeBegin  storeEnd            readBegin      readEnd
    private int readBegin;
    private int readEnd;
    private int storeBegin;
    private int storeEnd;

    // line number of the current pointer of parsing char
    private long lineNo = 1;

    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // ^
    // |
    // bufferOffset
    //
    // offset of the last \r\n or \n. will be used to calculate column number
    // of a token or an error. This may be outside of the buffer.
    private long lastLineOffset = 0;
    // offset in the stream for the start of the buffer, will be used in
    // calculating JsonLocation's stream offset, column no.
    private long bufferOffset = 0;

    private boolean minus;
    private boolean fracOrExp;
    private BigDecimal bd;

    private SosiToken lastToken;

    public enum SosiToken {
        LEVEL(null, true),
        ELEMENT_NAME(null, true),
        VALUE_NUMBER(Event.VALUE_NUMBER, true),
        VALUE_STRING(Event.VALUE_STRING, true),
        EXCLAMATION_MARK(Event.COMMENT, true),    // !
        ASTERISK(Event.VALUE_UNSPECIFIED, false), // *
        AT_MARK(Event.VALUE_DEFAULT, false),      // @
        AMPERSAND(Event.CONCATENATION, false),    // &
        COLON_VALUE(Event.VALUE_REF, true),       // :123
        VALUE_COLON(Event.VALUE_SERNO, true),     // 123:
        EOF(null, false);

        private final SosiParser.Event event;
        private final boolean value;

        SosiToken(SosiParser.Event event, boolean value) {
            this.event = event;
            this.value = value;
        }

        SosiParser.Event getEvent() {
            return event;
        }

        boolean isValue() {
            return value;
        }

        public boolean isOneOf(SosiToken... tokens) {
            return Arrays.stream(tokens).anyMatch(t -> t == this);
        }
    }

    SosiTokenizer(Reader reader, BufferPool bufferPool) {
        this.reader = reader;
        this.bufferPool = bufferPool;
        buf = bufferPool.take();
    }

    private void readString() {
        storeBegin = storeEnd = readBegin-1;

        int ch;
        do {
            ch = readChar();
        } while (!isWhitespace(ch) && ch != -1);

        if (ch != -1) {
            storeEnd = readBegin;
            storeEnd--;
            readBegin--;
        }
    }

    private void readComment() {
        storeBegin = storeEnd = readBegin;

        int ch;
        do {
            ch = readChar();
        } while (ch != 0x0a && ch != 0x0d && ch != -1);

        if (ch != -1) {
            storeEnd = readBegin;
            storeEnd--;
            readBegin--;
        }
    }

    private void readQuotedString(int quotationMark) {
        storeBegin = storeEnd = readBegin;

        int ch = -1, prevCh = -1;
        boolean endOfString = false;
        do {
            prevCh = ch;
            ch = readChar();
            if (ch == -1) {
                throw expectedChar(-1, (char)quotationMark);
            }
            if (ch == quotationMark && prevCh == quotationMark) {
                // Escaped quotation mark should be part of string
                ch = prevCh = -1;
            } else if (ch != quotationMark && prevCh == quotationMark) {
                endOfString = true;
            }
        } while (!endOfString);

        readBegin--;
        storeEnd = readBegin-1;
    }

    // Reads a number char. If the char is within the buffer, directly
    // reads from the buffer. Otherwise, uses read() which takes care
    // of resizing, filling up the buf, adjusting the pointers
    private int readChar() {
        if (readBegin < readEnd) {
            return buf[readBegin++];
        } else {
            storeEnd = readBegin;
            return read();
        }
    }

    /**
     * Reads to next whitespace, interprets as string or number value.
     * @return the interpreted token type
     */
    private SosiToken readStringOrNumber() {
        SosiToken token = SosiToken.VALUE_STRING;
        readString();

        if (buf[storeBegin] == ':' && interpretableAsInteger(storeBegin + 1, storeEnd)) {
            token = SosiToken.COLON_VALUE;
            storeBegin++;
        } else if (buf[storeEnd-1] == ':' && interpretableAsInteger(storeBegin, storeEnd - 1)) {
            token = SosiToken.VALUE_COLON;
            storeEnd--;
        } else if (interpretableAsNumber(storeBegin, storeEnd)) {
            token = SosiToken.VALUE_NUMBER;
        }

        return token;
    }

    /**
     * Tests whether buffer (within given positions) contains an unsigned integer value.
     * @param from start position in buffer (inclusive)
     * @param to end position in buffer (exclusive)
     * @return true if buffer (within given positions) can be interpreted as an unsigned integer value.
     */
    private boolean interpretableAsInteger(int from, int to)  {
        for (int i = from; i < to; i++) {
            if (!isDigit(buf[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests whether buffer (within given positions) contains a signed or unsigned integer or decimal number value.
     * @param from start position in buffer (inclusive)
     * @param to end position in buffer (exclusive)
     * @return true if buffer (within given positions) can be interpreted as a signed or unsigned integer or decimal number value.
     */
    private boolean interpretableAsNumber(int from, int to) {
        int i = from;

        // sign
        if (isSign(buf[i])) {
            this.minus = (buf[i] == '-');
            if (++i == to) {
                return false;
            }
        }

        // int
        boolean intFound = false;
        while (isDigit(buf[i])) {
            if (++i == to) {
                return true;
            }
            intFound = true;
        }

        // frac
        boolean fracFound = false;
        if (buf[i] == '.') {
            this.fracOrExp = true;
            int count = 0;
            do {
                if (++i == to) {
                    return count > 0;
                }
                count++;
            } while (isDigit(buf[i]));
            if (count == 1) {
                return false;
            }
            fracFound = true;
        }

        // exp (only allowed if preceeded by int and/or frac)
        if (intFound || fracFound) {
            if (isExpSpecifier(buf[i])) {
                this.fracOrExp = true;
                if (++i == to) {
                    return false;
                }
                if (isSign(buf[i])) {
                    if (++i == to) {
                        return false;
                    }
                }
                while (isDigit(buf[i])) {
                    if (++i == to) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    private boolean isSign(int ch) {
        return ch == '-' || ch == '+';
    }

    private boolean isExpSpecifier(int ch) {
        return ch == 'e' || ch == 'E' || ch == 'd' || ch == 'D';
    }

    private boolean isWhitespace(int ch) {
        return ch == 0x20 || ch == 0x09 || ch == 0x0a || ch == 0x0d;
    }

    private void readLevel()  {
        storeBegin = storeEnd = readBegin-1;

        int ch;
        do {
            ch = readChar();
        } while (ch == '.');

        readBegin--;
        storeEnd = readBegin;
    }

    SosiToken nextToken() {
        reset();
        int ch = read();

        // whitespace
        while (isWhitespace(ch)) {
            if (ch == '\r') {
                ++lineNo;
                ch = read();
                if (ch == '\n') {
                    lastLineOffset = bufferOffset+readBegin;
                } else {
                    lastLineOffset = bufferOffset+readBegin-1;
                    continue;
                }
            } else if (ch == '\n') {
                ++lineNo;
                lastLineOffset = bufferOffset+readBegin;
            }
            ch = read();
        }

        if (lastToken == SosiToken.LEVEL) {
            readString();
            return lastToken = SosiToken.ELEMENT_NAME;
        } else {
            switch (ch) {
                case '.':
                    readLevel();
                    return lastToken = SosiToken.LEVEL;
                case '"':
                case '\'':
                    readQuotedString((char) ch);
                    return lastToken = SosiToken.VALUE_STRING;
                case '*':
                    return lastToken = SosiToken.ASTERISK;
                case '@':
                    return lastToken = SosiToken.AT_MARK;
                case '&':
                    return lastToken = SosiToken.AMPERSAND;
                case '!':
                    readComment();
                    return lastToken = SosiToken.EXCLAMATION_MARK;
                case -1:
                    return lastToken = SosiToken.EOF;
                default:
                    return lastToken = readStringOrNumber();
            }
        }
    }

    // Gives the location of the last char. Used for
    // SosiParsingException.getLocation
    SosiLocation getLastCharLocation() {
        // Already read the char, so subtracting -1
        return SosiLocation.of(lineNo, bufferOffset +readBegin-lastLineOffset, bufferOffset +readBegin-1);
    }

    // Gives the parser location. Used for SosiParser.getLocation
    SosiLocation getLocation() {
        return SosiLocation.of(lineNo, bufferOffset +readBegin-lastLineOffset+1, bufferOffset +readBegin);
    }

    private int read() {
        try {
            if (readBegin == readEnd) {     // need to fill the buffer
                int len = fillBuf();
                if (len == -1) {
                    return -1;
                }
                assert len != 0;
                readBegin = storeEnd;
                readEnd = readBegin+len;
            }
            return buf[readBegin++];
        } catch (IOException ioe) {
            throw new SosiException(SosiMessages.TOKENIZER_IO_ERR(), ioe);
        }
    }

    private int fillBuf() throws IOException {
        if (storeEnd != 0) {
            int storeLen = storeEnd-storeBegin;
            if (storeLen > 0) {
                // there is some store data
                if (storeLen == buf.length) {
                    // buffer is full, double the capacity
                    char[] doubleBuf = Arrays.copyOf(buf, 2 * buf.length);
                    bufferPool.recycle(buf);
                    buf = doubleBuf;
                } else {
                    // Left shift all the stored data to make space
                    System.arraycopy(buf, storeBegin, buf, 0, storeLen);
                    storeEnd = storeLen;
                    storeBegin = 0;
                    bufferOffset += readBegin-storeEnd;
                }
            } else {
                storeBegin = storeEnd = 0;
                bufferOffset += readBegin;
            }
        } else {
            bufferOffset += readBegin;
        }
        // Fill the rest of the buf
        return reader.read(buf, storeEnd, buf.length-storeEnd);
    }

    // state associated with the current token is no more valid
    private void reset() {
        if (storeEnd != 0) {
            storeBegin = 0;
            storeEnd = 0;
            bd = null;
            minus = false;
            fracOrExp = false;
        }
    }

    String getValue() {
        return new String(buf, storeBegin, storeEnd - storeBegin);
    }

    BigDecimal getBigDecimal() {
        if (isNull(bd)) {
            bd = new BigDecimal(buf, storeBegin, storeEnd-storeBegin);
        }
        return bd;
    }

    int getInt() {
        // no need to create BigDecimal for common integer values (1-9 digits)
        int storeLen = storeEnd-storeBegin;
        if (!fracOrExp && (storeLen <= 9 || (minus && storeLen == 10))) {
            int num = 0;
            int i = minus ? 1 : 0;
            for(; i < storeLen; i++) {
                num = num * 10 + (buf[storeBegin+i] - '0');
            }
            return minus ? -num : num;
        } else {
            return getBigDecimal().intValue();
        }
    }

    // returns true for common integer values (1-9 digits).
    // So there are cases it will return false even though the number is int
    boolean isDefinitelyInt() {
        int storeLen = storeEnd-storeBegin;
        return !fracOrExp && (storeLen <= 9 || (minus && storeLen == 10));
    }

    boolean isIntegral() {
        return !fracOrExp || getBigDecimal().scale() == 0;
    }

    @Override
    public void close() throws IOException {
        reader.close();
        bufferPool.recycle(buf);
    }

    private SosiParsingException expectedChar(int unexpected, char expected) {
        SosiLocation location = getLastCharLocation();
        return new SosiParsingException(
                SosiMessages.TOKENIZER_EXPECTED_CHAR(unexpected, location, expected), location);
    }
}