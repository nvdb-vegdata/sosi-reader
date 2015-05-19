// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.encoding;

import no.vegvesen.nvdb.sosi.SosiException;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.function.Predicate;

/**
 * A filter stream that detects the encoding of the original stream.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class CharSetDetectingInputStream extends FilterInputStream {
    private static final String CHARSET_ELEMENT = "TEGNSETT";
    private static final String DEFAULT_CHARSET = "ISO-8859-1";
    private static final int BUF_SIZE = 1024;

    private final byte[] buf = new byte[BUF_SIZE];
    private int bufLen;
    private int curIndex = 0;
    private final Charset charset;

    public CharSetDetectingInputStream(InputStream is) {
        super(is);
        charset = detectEncoding();
    }

    public Charset getCharset() {
        return charset;
    }

    private void fillBuf() {
        try {
            for (int i = 0; i < BUF_SIZE; i++) {
                int b = in.read();
                if (b == -1) {
                    return;
                }
                buf[i] = (byte)b;
                bufLen++;
            }

        } catch (IOException e) {
            throw new SosiException("I/O error while auto-detecting the encoding of stream", e);
        }
    }

    private Charset detectEncoding() {
        fillBuf();

        int pos = indexOf(CHARSET_ELEMENT.getBytes());
        if (pos > -1) {
            pos += CHARSET_ELEMENT.length() + 1;
            pos = advanceBufPos(pos, isWhitespace());
            int endPos = advanceBufPos(pos, isWhitespace().negate());

            if (pos < bufLen) {
                String charsetName = charsetNameFromSosiValue(new String(buf, pos, endPos - pos));
                return Charset.forName(charsetName);
            }
        }

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
        while (pos < bufLen && pred.test(buf[pos])) {
            pos++;
        }
        return pos;
    }

    private Predicate<Byte> isWhitespace() {
        return ch -> ch == 0x20 || ch == 0x09 || ch == 0x0a || ch == 0x0d;
    }

    private int indexOf(byte[] subBuf) {
        for (int i = 0; i < bufLen; i += subBuf.length) {
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

    @Override
    public int read() throws IOException {
        if (curIndex < bufLen) {
            return buf[curIndex++];
        }
        return in.read();
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        if (curIndex < bufLen) {
            if (len == 0) {
                return 0;
            }
            if (off < 0 || len < 0 || len > b.length -off) {
                throw new IndexOutOfBoundsException();
            }
            int min = Math.min(bufLen-curIndex, len);
            System.arraycopy(buf, curIndex, b, off, min);
            curIndex += min;
            return min;
        }
        return in.read(b, off, len);
    }
}
