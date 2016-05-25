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
