// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.encoding.charset;

import no.vegvesen.nvdb.sosi.encoding.SosiCharsetProvider;

import java.nio.charset.Charset;

import static java.util.Objects.isNull;

/**
 * Base class for all SOSI charsets
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public abstract class SosiCharset extends Charset {

    public SosiCharset(String canonicalName) {
        super(canonicalName, null);
    }

    /**
     * Charset.forName uses the system class loader to load custom charset providers.
     * This won't always work in the context of web applications.
     */
    public static Charset forName(String charsetName) {
        SosiCharsetProvider provider = new SosiCharsetProvider();
        Charset charset = provider.charsetForName(charsetName);
        if (isNull(charset)) {
            charset = Charset.forName(charsetName);
        }
        return charset;
    }
}
