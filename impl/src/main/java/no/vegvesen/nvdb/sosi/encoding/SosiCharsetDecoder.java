// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.encoding;

import sun.nio.cs.ArrayDecoder;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

/**
 * Generic charset decoder for SOSI input stream readers.
 *
 * Based on the Java ISO-8859-1 charset decoder class.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
abstract class SosiCharsetDecoder extends CharsetDecoder implements ArrayDecoder {
    SosiCharsetDecoder(Charset cs) {
        super(cs, 1.0f, 1.0f);
    }

    private CoderResult decodeArrayLoop(ByteBuffer src, CharBuffer dst) {
        byte[] sa = src.array();
        int sp = src.arrayOffset() + src.position();
        int sl = src.arrayOffset() + src.limit();
        assert (sp <= sl);
        sp = (sp <= sl ? sp : sl);
        char[] da = dst.array();
        int dp = dst.arrayOffset() + dst.position();
        int dl = dst.arrayOffset() + dst.limit();
        assert (dp <= dl);
        dp = (dp <= dl ? dp : dl);

        try {
            while (sp < sl) {
                byte b = sa[sp];
                if (dp >= dl)
                    return CoderResult.OVERFLOW;
                da[dp++] = toUtf16(b);
                sp++;
            }
            return CoderResult.UNDERFLOW;
        } finally {
            src.position(sp - src.arrayOffset());
            dst.position(dp - dst.arrayOffset());
        }
    }

    private CoderResult decodeBufferLoop(ByteBuffer src, CharBuffer dst) {
        int mark = src.position();
        try {
            while (src.hasRemaining()) {
                byte b = src.get();
                if (!dst.hasRemaining())
                    return CoderResult.OVERFLOW;
                dst.put(toUtf16(b));
                mark++;
            }
            return CoderResult.UNDERFLOW;
        } finally {
            src.position(mark);
        }
    }

    protected CoderResult decodeLoop(ByteBuffer src, CharBuffer dst) {
        if (src.hasArray() && dst.hasArray())
            return decodeArrayLoop(src, dst);
        else
            return decodeBufferLoop(src, dst);
    }

    public int decode(byte[] src, int sp, int len, char[] dst) {
        if (len > dst.length)
            len = dst.length;
        int dp = 0;
        while (dp < len)
            dst[dp++] = toUtf16(src[sp++]);
        return dp;
    }

    protected abstract char toUtf16(byte ch);
}
