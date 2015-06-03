// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
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
