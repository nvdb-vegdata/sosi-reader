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
    public void write(SosiElement element) {
        writeElement(1, element);
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
            writer.append(layoutFormatter.afterValue(value));
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
