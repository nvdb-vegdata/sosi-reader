// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
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
