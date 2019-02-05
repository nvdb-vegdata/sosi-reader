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
import java.util.Arrays;
import java.util.List;

/**
 * The ISO-8859-10 8-bits character set (includes Sami characters).
 *
 * @author Tore Eide Andersen (Kantega AS)
 * @author Oystein Steimler (Itema AS)
 */
public class ISO8859_10 extends SosiCharset {

    private final static String[] aliases = {"ISO8859-10", "8859-10", "latin6"};

    private final static List<Character> chars = Arrays.asList(
        /* 0xa0 */
            '\u00a0', '\u0104', '\u0112', '\u0122', '\u012a', '\u0128', '\u0136', '\u00a7',
            '\u013b', '\u0110', '\u0160', '\u0166', '\u017d', '\u00ad', '\u016a', '\u014a',
        /* 0xb0 */
            '\u00b0', '\u0105', '\u0113', '\u0123', '\u012b', '\u0129', '\u0137', '\u00b7',
            '\u013c', '\u0111', '\u0161', '\u0167', '\u017e', '\u2015', '\u016b', '\u014b',
        /* 0xc0 */
            '\u0100', '\u00c1', '\u00c2', '\u00c3', '\u00c4', '\u00c5', '\u00c6', '\u012e',
            '\u010c', '\u00c9', '\u0118', '\u00cb', '\u0116', '\u00cd', '\u00ce', '\u00cf',
        /* 0xd0 */
            '\u00d0', '\u0145', '\u014c', '\u00d3', '\u00d4', '\u00d5', '\u00d6', '\u0168',
            '\u00d8', '\u0172', '\u00da', '\u00db', '\u00dc', '\u00dd', '\u00de', '\u00df',
        /* 0xe0 */
            '\u0101', '\u00e1', '\u00e2', '\u00e3', '\u00e4', '\u00e5', '\u00e6', '\u012f',
            '\u010d', '\u00e9', '\u0119', '\u00eb', '\u0117', '\u00ed', '\u00ee', '\u00ef',
        /* 0xf0 */
            '\u00f0', '\u0146', '\u014d', '\u00f3', '\u00f4', '\u00f5', '\u00f6', '\u0169',
            '\u00f8', '\u0173', '\u00fa', '\u00fb', '\u00fc', '\u00fd', '\u00fe', '\u0138');

    private final static BiMap<Byte, Character> charMap;

    static {
        charMap = createCharMap(0xa0, chars );
    }

    private static BiMap<Byte, Character> createCharMap(int offset, List<Character> characters) {
        BiMap<Byte, Character> charMap = new BiMap<>();
        for (int index = 0; index < characters.size(); index++) {
            charMap.put((byte)(index + offset), characters.get(index));
        }
        return charMap;
    }

    public ISO8859_10() {
        super("ISO-8859-10", aliases);
    }

    @Override
    public boolean contains(Charset cs) {
        return cs instanceof ISO8859_10;
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
            super(cs, chars);
        }

        @Override
        protected byte fromUtf16(char ch) {
            return charMap.getLeftOrDefault(ch, (byte)ch);
        }
    }
}
