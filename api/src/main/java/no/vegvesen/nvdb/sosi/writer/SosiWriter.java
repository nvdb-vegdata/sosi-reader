// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
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
