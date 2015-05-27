// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.encoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Detects the encoding of a SOSI file
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class EncodingDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger(EncodingDetector.class);

    private static final String CHARSET_ELEMENT = "..TEGNSETT";
    private static final String DEFAULT_CHARSET = "ISO-8859-1";
    private static final byte COMMENT = (byte)'!';

    private byte[] buf;

    public static Optional<Charset> charsetOf(byte[] sosi) {
        EncodingDetector detector = new EncodingDetector(sosi);
        return detector.getCharsetName().map(Charset::forName);
    }

    public static Charset defaultCharset() {
        LOGGER.warn("Using default charset: {}", DEFAULT_CHARSET);
        return Charset.forName(DEFAULT_CHARSET);
    }

    private EncodingDetector(byte[] sosi) {
        buf = sosi;
    }

    private Optional<String> getCharsetName() {
        int startPos = indexOf(CHARSET_ELEMENT.getBytes());
        if (startPos > -1) {
            startPos += CHARSET_ELEMENT.length() + 1;
            startPos = advanceBufPos(startPos, isWhitespace());
            int endPos = advanceBufPos(startPos, isWhitespace().negate());

            if (startPos < buf.length) {
                return charsetNameFromSosiValue(new String(buf, startPos, endPos - startPos));
            }
        }

        LOGGER.warn("No TEGNSETT element/value found");
        return Optional.empty();
    }

    public static Optional<String> charsetNameFromSosiValue(String sosiCharset) {
        switch (sosiCharset.toUpperCase()) {
            case "ANSI" :
            case "ISO8859-1" :
            case "ISO8859-10" :
                return Optional.of("ISO-8859-1");
            case "DOSN8" :
            case "ND7" :
            case "DECN7" :
                return Optional.of(sosiCharset);
        }

        LOGGER.warn("Unsupported TEGNSETT value: {}", sosiCharset);
        return Optional.empty();
    }

    private int advanceBufPos(int pos, Predicate<Byte> pred) {
        while (pos < buf.length && pred.test(buf[pos])) {
            pos++;
        }
        return pos;
    }

    private Predicate<Byte> isWhitespace() {
        return ch -> ch == 0x20 || ch == 0x09 || ch == 0x0a || ch == 0x0d;
    }

    private Predicate<Byte> isEndOfLine() {
        return ch -> ch == 0x0a || ch == 0x0d;
    }

    private int indexOf(byte[] subBuf) {
        for (int i = 0; i < buf.length; i++) {
            if (buf[i] == COMMENT) {
                i = advanceBufPos(i, isEndOfLine().negate());
            } else {
                for (int j = 0; j < subBuf.length && i + j < buf.length; j++) {
                    if (buf[i + j] != subBuf[j]) {
                        break;
                    } else if (j == subBuf.length - 1) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }
}
