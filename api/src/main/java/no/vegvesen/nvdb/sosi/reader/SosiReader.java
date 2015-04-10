// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.document.SosiDocument;
import no.vegvesen.nvdb.sosi.parser.SosiParser;

import java.io.Closeable;

/**
 * Reads a SOSI {@link SosiDocument object} from an input source.
 *
 * <p>The class {@link Ino.vegvesen.nvdb.sosi.Sosi} contains methods to create readers from
 * input sources ({@link java.io.InputStream} and {@link java.io.Reader}).
 *
 * <p>
 * <a id="SosiReaderExample1"/>
 * The following example demonstrates how to read simple SOSI data from
 * a string:
 * <pre>
 * <code>
 * SosiReader sosiReader = Sosi.createReader(new StringReader(".HODE ..NAVN Tore Torell .SLUTT"));
 * SosiDocument doc = sosiReader.read();
 * sosiReader.close();
 * </code>
 * </pre>
 *
 * Based on an interface from the Glassfish JSON parser (author Jitendra Kotamraju)
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public interface SosiReader extends  /*Auto*/Closeable {
    /**
     * Returns a SOSI document that is represented in
     * the input source. This method needs to be called
     * only once for a reader instance.
     *
     * @return a SOSI document
     * @throws Ino.vegvesen.nvdb.sosi.SosiException if a SOSI document cannot
     *     be created due to i/o error (IOException would be
     * cause of SosiException)
     * @throws Ino.vegvesen.nvdb.sosi.parser.SosiParsingException if a SOSI document
     *     cannot be created due to incorrect representation
     * @throws IllegalStateException if read or close method is already called
     */
    SosiDocument read();

    /**
     * Gets the parser used when reading the SOSI file.
     * @return a SOSI parser
     */
    SosiParser getParser();

    /**
     * Closes this reader and frees any resources associated with the
     * reader. This method closes the underlying input source.
     *
     * @throws Ino.vegvesen.nvdb.sosi.SosiException if an i/o error occurs (IOException would be
     * cause of SosiException)
     */
    @Override
    void close();
}
