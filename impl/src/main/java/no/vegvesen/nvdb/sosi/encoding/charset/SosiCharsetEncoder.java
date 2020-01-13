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
package no.vegvesen.nvdb.sosi.encoding.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.List;

/**
 * Generic charset encoder for SOSI output stream writers.
 *
 * Based on the Java ISO-8859-1 charset encoder class.
 *
 * @author Tore Eide Andersen (Kantega AS)
 * @author Oystein Steimler (Itema AS)
 */
abstract class SosiCharsetEncoder extends CharsetEncoder {

    private List<Character> chars;

    SosiCharsetEncoder(Charset cs, List<Character> chars) {
        super(cs, 1.0f, 1.0f);
        this.chars = chars;
    }

    @Override
    public boolean canEncode(char c) {
        if ( chars == null ) {
            return c <= '\u00FF';
        } else {
           return chars.contains(c) ;
        }
    }

    @Override
    public boolean isLegalReplacement(byte[] repl) {
        return true;  // we accept any byte value
    }

    private final Surrogate.Parser sgp = new Surrogate.Parser();

    // JVM may replace this method with intrinsic code.
    private int encodeISOArray(char[] sa, int sp, byte[] da, int dp, int len) {
        int i = 0;
        for (; i < len; i++) {
            char c = sa[sp++];
            if (c > '\u00FF')
                break;
            da[dp++] = fromUtf16(c);
        }
        return i;
    }

    private CoderResult encodeArrayLoop(CharBuffer src, ByteBuffer dst) {
        char[] sa = src.array();
        int soff = src.arrayOffset();
        int sp = soff + src.position();
        int sl = soff + src.limit();
        assert (sp <= sl);
        sp = (sp <= sl ? sp : sl);
        byte[] da = dst.array();
        int doff = dst.arrayOffset();
        int dp = doff + dst.position();
        int dl = doff + dst.limit();
        assert (dp <= dl);
        dp = (dp <= dl ? dp : dl);
        int dlen = dl - dp;
        int slen = sl - sp;
        int len  = (dlen < slen) ? dlen : slen;
        try {
            int ret = encodeISOArray(sa, sp, da, dp, len);
            sp = sp + ret;
            dp = dp + ret;
            if (ret != len) {
                if (sgp.parse(sa[sp], sa, sp, sl) < 0)
                    return sgp.error();
                return sgp.unmappableResult();
            }
            if (len < slen)
                return CoderResult.OVERFLOW;
            return CoderResult.UNDERFLOW;
        } finally {
            src.position(sp - soff);
            dst.position(dp - doff);
        }
    }

    private CoderResult encodeBufferLoop(CharBuffer src, ByteBuffer dst) {
        int mark = src.position();
        try {
            while (src.hasRemaining()) {
                char c = src.get();
                if (canEncode(c)) {
                    if (!dst.hasRemaining())
                        return CoderResult.OVERFLOW;
                    dst.put(fromUtf16(c));
                    mark++;
                    continue;
                }
                if (sgp.parse(c, src) < 0)
                    return sgp.error();
                return sgp.unmappableResult();
            }
            return CoderResult.UNDERFLOW;
        } finally {
            src.position(mark);
        }
    }

    @Override
    protected CoderResult encodeLoop(CharBuffer src, ByteBuffer dst) {
        if (src.hasArray() && dst.hasArray())
            return encodeArrayLoop(src, dst);
        else
            return encodeBufferLoop(src, dst);
    }

    private byte repl = (byte)'?';

    @Override
    protected void implReplaceWith(byte[] newReplacement) {
        repl = newReplacement[0];
    }

    protected abstract byte fromUtf16(char ch);
}
