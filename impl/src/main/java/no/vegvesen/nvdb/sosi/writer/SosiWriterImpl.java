// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.writer;

import no.vegvesen.nvdb.sosi.document.SosiDocument;
import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiValue;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Implements a SOSI writer
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class SosiWriterImpl implements SosiWriter {
    private final Writer writer;
    private final SosiValueFormatter valueFormatter;
    private final SosiLayoutFormatter layoutFormatter;

    public SosiWriterImpl(Writer writer) {
        this.writer = writer;
        this.valueFormatter = new DefaultSosiValueFormatter();
        this.layoutFormatter = new DefaultSosiLayoutFormatter();
    }

    public SosiWriterImpl(Writer writer, SosiValueFormatter valueFormatter, SosiLayoutFormatter layoutFormatter) {
        this.writer = writer;
        this.valueFormatter = valueFormatter;
        this.layoutFormatter = layoutFormatter;
    }

    public SosiWriterImpl(OutputStream stream, Charset encoding) {
        this.writer = new OutputStreamWriter(stream, encoding);
        this.valueFormatter = new DefaultSosiValueFormatter();
        this.layoutFormatter = new DefaultSosiLayoutFormatter();
    }

    @Override
    public void write(SosiDocument doc) {
        doc.elements().forEach(e -> writeElement(1, e));
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close writer", e);
        }
    }

    private void writeElement(int level, SosiElement element) {
        try {
            writer.append(layoutFormatter.beforeElement(element));
            writeLevel(level);
            writer.append(element.getName());
            element.values().forEach(v -> writeValue(element, v));
            element.subElements().forEach(e -> writeElement(level + 1, e));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write element " + element.getName(), e);
        }
    }

    private void writeValue(SosiElement element, SosiValue value) {
        try {
            writer.append(layoutFormatter.beforeValue(value));
            String valueAsString = valueFormatter.apply(element, value);
            writer.append(valueAsString);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write value", e);
        }
    }

    private void writeLevel(int level) throws IOException {
        for (int i = 0; i < level; i++) {
            writer.append(".");
        }
    }
}
