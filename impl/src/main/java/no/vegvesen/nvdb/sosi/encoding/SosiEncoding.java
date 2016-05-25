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
package no.vegvesen.nvdb.sosi.encoding;

import no.vegvesen.nvdb.sosi.encoding.charset.SosiCharset;
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
public class SosiEncoding {
    private static final Logger LOGGER = LoggerFactory.getLogger(SosiEncoding.class);

    private static final String CHARSET_ELEMENT = "..TEGNSETT";
    private static final String DEFAULT_CHARSET = "ISO-8859-1";
    private static final byte COMMENT = (byte)'!';

    public static Optional<Charset> charsetOf(byte[] sosi) {
        Detector detector = new Detector(sosi);
        return detector.getCharsetName().map(SosiCharset::forName);
    }

    public static Charset defaultCharset() {
        LOGGER.warn("Using default charset: {}", DEFAULT_CHARSET);
        return SosiCharset.forName(DEFAULT_CHARSET);
    }

    public static Optional<String> charsetNameFromSosiValue(String sosiCharset) {
        switch (sosiCharset.toUpperCase()) {
            case "UTF-8" :
                return Optional.of("UTF-8");
            case "ANSI" :
            case "ISO8859-1" :
                return Optional.of("ISO-8859-1");
            case "ISO8859-10" :
                return Optional.of("ISO-8859-10");
            case "DOSN8" :
            case "ND7" :
            case "DECN7" :
                return Optional.of(sosiCharset);
        }

        LOGGER.warn("Unsupported TEGNSETT value: {}", sosiCharset);
        return Optional.empty();
    }

    public static String[] supportedSosiCharsets() {
        return new String[]{"UTF-8", "ANSI", "ISO8859-1", "ISO8859-10", "DOSN8", "ND7", "DECN7"};
    }

    private static class Detector {
        private byte[] buf;

        private Detector(byte[] sosi) {
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
}
