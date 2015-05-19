// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.encoding;

import sun.nio.cs.ArrayEncoder;
import sun.nio.cs.Surrogate;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

/**
 * Generic charset encoder for SOSI output stream writers.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
abstract class SosiCharsetEncoder extends CharsetEncoder implements ArrayEncoder {
    SosiCharsetEncoder(Charset cs) {
        super(cs, 1.0f, 1.0f);
    }

    public boolean canEncode(char c) {
        return c <= '\u00FF';
    }

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
                if (c <= '\u00FF') {
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

    protected CoderResult encodeLoop(CharBuffer src, ByteBuffer dst) {
        if (src.hasArray() && dst.hasArray())
            return encodeArrayLoop(src, dst);
        else
            return encodeBufferLoop(src, dst);
    }

    private byte repl = (byte)'?';

    protected void implReplaceWith(byte[] newReplacement) {
        repl = newReplacement[0];
    }

    public int encode(char[] src, int sp, int len, byte[] dst) {
        int dp = 0;
        int slen = Math.min(len, dst.length);
        int sl = sp + slen;
        while (sp < sl) {
            int ret = encodeISOArray(src, sp, dst, dp, slen);
            sp = sp + ret;
            dp = dp + ret;
            if (ret != slen) {
                char c = src[sp++];
                if (Character.isHighSurrogate(c) && sp < sl &&
                        Character.isLowSurrogate(src[sp])) {
                    if (len > dst.length) {
                        sl++;
                        len--;
                    }
                    sp++;
                }
                dst[dp++] = repl;
                slen = Math.min((sl - sp), (dst.length - dp));
            }
        }
        return dp;
    }

    protected abstract byte fromUtf16(char ch);
}
