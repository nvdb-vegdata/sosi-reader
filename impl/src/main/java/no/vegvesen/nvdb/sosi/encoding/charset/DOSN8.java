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

import no.vegvesen.nvdb.sosi.utils.BiMap;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * The MS-DOS Norwegian 8-bits character set.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class DOSN8 extends SosiCharset {

    private final static BiMap<Byte, Character> charMap;

    static {
        charMap = new BiMap<>();
        charMap.put((byte)146, 'Æ');
        charMap.put((byte)157, 'Ø');
        charMap.put((byte)143, 'Å');
        charMap.put((byte)145, 'æ');
        charMap.put((byte)155, 'ø');
        charMap.put((byte)134, 'å');
    }

    public DOSN8() {
        super("DOSN8");
    }

    @Override
    public boolean contains(Charset cs) {
        return cs instanceof DOSN8;
    }

    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder(this);
    }

    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder(this);
    }

    private static class Decoder extends SosiCharsetDecoder {
        private Decoder(Charset cs) {
            super(cs);
        }

        @Override
        protected char toUtf16(byte ch) {
            byte b = (byte)(ch & 0xff);
            return charMap.getRightOrDefault(b, (char)b);
        }
    }

    private static class Encoder extends SosiCharsetEncoder {
        private Encoder(Charset cs) {
            super(cs);
        }

        @Override
        protected byte fromUtf16(char ch) {
            return charMap.getLeftOrDefault(ch, (byte)ch);
        }
    }
}
