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

import no.vegvesen.nvdb.sosi.encoding.charset.DECN7;
import no.vegvesen.nvdb.sosi.encoding.charset.DOSN8;
import no.vegvesen.nvdb.sosi.encoding.charset.ISO8859_10;
import no.vegvesen.nvdb.sosi.encoding.charset.ND7;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Charset provider for custom SOSI character sets.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class SosiCharsetProvider extends CharsetProvider {
    private final List<Charset> charsets;

    private static final Charset ISO8859_10 = new ISO8859_10();
    private static final Charset DOSN8 = new DOSN8();
    private static final Charset ND7 = new ND7();
    private static final Charset DECN7 = new DECN7();

    public SosiCharsetProvider() {
        this.charsets = Arrays.asList(ISO8859_10, DOSN8, ND7, DECN7);
    }

    @Override
    public Iterator<Charset> charsets() {
        return charsets.iterator();
    }

    @Override
    public Charset charsetForName(String charsetName) {
        return charsets.stream().filter(cs -> cs.name().equals(charsetName)).findFirst().orElse(null);
    }
}
