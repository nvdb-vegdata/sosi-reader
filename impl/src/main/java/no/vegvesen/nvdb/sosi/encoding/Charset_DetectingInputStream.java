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

/**
 * A filter stream that detects the encoding of the original stream.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class Charset_DetectingInputStream extends FilterInputStream {
    private static final int BUF_SIZE = 1024;

    private final byte[] buf = new byte[BUF_SIZE];
    private int bufLen;
    private int curIndex = 0;
    private final Charset charset;

    public Charset_DetectingInputStream(InputStream is) {
        super(is);
        charset = detectEncoding();
    }

    public Charset getCharset() {
        return charset;
    }

    private Charset detectEncoding() {
        fillBuf();
        return EncodingDetector.charsetOf(buf);
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
