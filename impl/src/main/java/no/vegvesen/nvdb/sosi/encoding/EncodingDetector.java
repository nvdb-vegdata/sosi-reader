// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.encoding;

import java.nio.charset.Charset;
import java.util.function.Predicate;

/**
 * Detects the encoding of a SOSI file
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class EncodingDetector {
    private static final String CHARSET_ELEMENT = "TEGNSETT";
    private static final String DEFAULT_CHARSET = "ISO-8859-1";

    private byte[] buf;

    public static Charset charsetOf(byte[] sosi) {
        EncodingDetector detector = new EncodingDetector(sosi);
        return detector.getCharset();
    }

    private EncodingDetector(byte[] sosi) {
        buf = sosi;
    }

    private Charset getCharset() {
        int startPos = indexOf(CHARSET_ELEMENT.getBytes());
        if (startPos > -1) {
            startPos += CHARSET_ELEMENT.length() + 1;
            startPos = advanceBufPos(startPos, isWhitespace());
            int endPos = advanceBufPos(startPos, isWhitespace().negate());

            if (startPos < buf.length) {
                String charsetName = charsetNameFromSosiValue(new String(buf, startPos, endPos - startPos));
                return Charset.forName(charsetName);
            }
        }

        // If no TEGNSETT element found, fall back to default
        return Charset.forName(DEFAULT_CHARSET);
    }

    private String charsetNameFromSosiValue(String sosiCharset) {
        switch (sosiCharset.toUpperCase()) {
            case "ANSI" :
            case "ISO8859-1" :
            case "ISO8859-10" :
                return "ISO-8859-1";
            case "DOSN8" :
            case "ND7" :
            case "DECN7" :
                return sosiCharset;
        }

        return DEFAULT_CHARSET;
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

    private int indexOf(byte[] subBuf) {
        for (int i = 0; i < buf.length; i += subBuf.length) {
            for (int j = subBuf.length - 1; j >= 0; j--) {
                if (buf[i+j] != subBuf[j]) {
                    break;
                } else if (j == 0) {
                    return i;
                }
            }
        }

        return -1;
    }
}
