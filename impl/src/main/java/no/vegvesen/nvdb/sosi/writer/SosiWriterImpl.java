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

import static no.vegvesen.nvdb.sosi.document.SosiValue.ValueType.REF;
import static no.vegvesen.nvdb.sosi.document.SosiValue.ValueType.SERNO;
import static no.vegvesen.nvdb.sosi.document.SosiValue.ValueType.STRING;

/**
 * TODO: Purpose and responsibility
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class SosiWriterImpl implements SosiWriter {
    private final Writer writer;
    private final SosiValueFormatter valueFormatter;
    private long lineNo;

    public SosiWriterImpl(Writer writer) {
        this.writer = writer;
        this.valueFormatter = new DefaultSosiValueFormatter();
    }

    public SosiWriterImpl(Writer writer, SosiValueFormatter valueFormatter) {
        this.writer = writer;
        this.valueFormatter = valueFormatter;
    }

    public SosiWriterImpl(OutputStream stream) {
        this.writer = new OutputStreamWriter(stream);
        this.valueFormatter = new DefaultSosiValueFormatter();
    }

    @Override
    public void write(SosiDocument doc) {
        try {
            lineNo = 1;
            writeElement(1, doc.getHead());
            doc.elements()
                    .filter(e -> !e.getName().equalsIgnoreCase("HODE"))
                    .forEach(e -> writeElement(1, e));
            advanceToLine(lineNo + 1);
            writer.append(".SLUTT\n");
        } catch (IOException e) {
            throw new RuntimeException("Failed to write document", e);
        }
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
            if (!advanceToLine(element.getLocation().getLineNumber())) {
                if (lineNo > 1) {
                    writer.append(" ");
                }
            }
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
            if (!advanceToLine(value.getLocation().getLineNumber())) {
                writer.append(" ");
            }

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

    private boolean advanceToLine(long nextLineNo) throws IOException{
        long prevLineNo = lineNo;
        while (lineNo < nextLineNo) {
            writer.append("\n");
            lineNo++;
        }
        return lineNo > prevLineNo;
    }
}
