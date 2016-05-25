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
package no.vegvesen.nvdb.sosi.writer;

import no.vegvesen.nvdb.sosi.document.SosiDocument;
import no.vegvesen.nvdb.sosi.parser.SosiParser;

import java.io.Closeable;

/**
 * Writes a SOSI {@link SosiDocument object} to an output source.
 *
 * <p>
 * <a id="SosiWriterExample1"/>
 * The following example demonstrates how to write simple SOSI data to
 * a string:
 * <pre>
 * <code>
 * SosiDocument doc = ...;
 * StringWriter stringWriter = new StringWriter();
 * SosiWriter sosiWriter = Sosi.createWriter(stringWriter);
 * sosiWriter.write(doc);
 * sosiWriter.close();
 * </code>
 * </pre>
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public interface SosiWriter extends  /*Auto*/Closeable {
    /**
     * Writes specified SOSI document to an OutputStream.
     **/
    void write(SosiDocument doc);

    /**
     * Closes this writer and frees any resources associated with the
     * writer. This method closes the underlying output target.
     **/
    @Override
    void close();
}
