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

import no.vegvesen.nvdb.sosi.SosiException;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * A filter stream that detects the encoding of the original stream.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class CharsetDetectingInputStream extends FilterInputStream {
    private static final int BUF_SIZE = 1024;

    private static final byte[] UTF8_BOM = {(byte)0xEF, (byte)0xBB, (byte)0xBF};

    private final byte[] buf = new byte[BUF_SIZE];
    private int bufLen;
    private int curIndex = 0;
    private final Optional<Charset> charset;

    public CharsetDetectingInputStream(InputStream is) {
        super(is);
        charset = detectEncoding();
    }

    public Optional<Charset> getCharset() {
        return charset;
    }

    private Optional<Charset> detectEncoding() {
        fillBuf();
        Optional<Charset> charset = charsetFromBom();
        if (charset.isPresent()) {
            return charset;
        } else {
            return SosiEncoding.charsetOf(buf);
        }
    }

    private Optional<Charset> charsetFromBom() {
        if (bufLen > UTF8_BOM.length) {
            if (buf[0] == UTF8_BOM[0] && buf[1] == UTF8_BOM[1] && buf[2] == UTF8_BOM[2]) {
                curIndex += UTF8_BOM.length;
                return Optional.of(Charset.forName("UTF-8"));
            }
        }

        return Optional.empty();
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
