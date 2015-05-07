// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.SosiException;
import no.vegvesen.nvdb.sosi.SosiMessages;
import no.vegvesen.nvdb.sosi.document.SosiDocument;
import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiValue;
import no.vegvesen.nvdb.sosi.parser.SosiParser;
import no.vegvesen.nvdb.sosi.utils.BufferPool;
import no.vegvesen.nvdb.sosi.parser.SosiParserImpl;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event.END;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event.START_ELEMENT;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event.START_HEAD;

/**
 * SosiReader impl using parser and builders.
 *
 * Based on a class from the Glassfish JSON parser (author Jitendra Kotamraju)
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class SosiReaderImpl implements SosiReader {
    private final SosiParserImpl parser;
    private boolean readDone;
    private final BufferPool bufferPool;

    public SosiReaderImpl(Reader reader, BufferPool bufferPool) {
        parser = new SosiParserImpl(reader, bufferPool);
        this.bufferPool = bufferPool;
    }

    public SosiReaderImpl(InputStream in, BufferPool bufferPool) {
        parser = new SosiParserImpl(in, bufferPool);
        this.bufferPool = bufferPool;
    }

    public SosiReaderImpl(InputStream in, Charset charset, BufferPool bufferPool) {
        parser = new SosiParserImpl(in, charset, bufferPool);
        this.bufferPool = bufferPool;
    }

    @Override
    public SosiDocument read() {
        if (readDone) {
            throw new IllegalStateException(SosiMessages.READER_READ_ALREADY_CALLED());
        }
        readDone = true;

        List<SosiElement> elements = new ArrayList<>();
        while (parser.hasNext()) {
            SosiParser.Event e = parser.next();
            if (e == START_HEAD || e == START_ELEMENT) {
                SosiElement element = readElement(new SosiElementBuilderImpl(parser.getString(), parser.getLocation()));
                elements.add(element);
            } else if (e == END) {
                return SosiDocumentImpl.of(elements);
            }
        }
        throw new SosiException("Internal Error");
    }

    @Override
    public SosiParser getParser() {
        return parser;
    }

    @Override
    public void close() {
        readDone = true;
        parser.close();
    }

    private SosiElement readElement(SosiElementBuilder builder) {
        boolean concatenate = false;
        while(parser.hasNext()) {
            SosiParser.Event e = parser.next();
            switch (e) {
                case START_ELEMENT:
                    String name = parser.getString();
                    SosiElement subElement = readElement(new SosiElementBuilderImpl(name, parser.getLocation()));
                    builder.addSubElement(name, subElement);
                    break;
                case VALUE_STRING:
                    if (concatenate) {
                        builder.concatValue(parser.getString(), parser.getLocation());
                        concatenate = false;
                    } else {
                        builder.addValue(parser.getString(), parser.getLocation());
                    }
                    break;
                case VALUE_NUMBER:
                    if (parser.isDefinitelyInt()) {
                        builder.addValue(parser.getInt(), parser.getLocation());
                    } else {
                        builder.addValue(parser.getBigDecimal(), parser.getLocation());
                    }
                    break;
                case VALUE_DEFAULT:
                    builder.addValue(SosiValue.DEFAULT(parser.getLocation()));
                    break;
                case VALUE_UNSPECIFIED:
                    builder.addValue(SosiValue.UNSPECIFIED(parser.getLocation()));
                    break;
                case VALUE_SERNO:
                    builder.addValue(SosiSerialNumberImpl.of(parser.getLong(), parser.getLocation()));
                    break;
                case VALUE_REF:
                    builder.addValue(SosiRefNumberImpl.of(parser.getLong(), parser.getLocation()));
                    break;
                case END_HEAD:
                case END_ELEMENT:
                    return builder.build();
                case CONCATENATION:
                    concatenate = true;
                    break;
                case COMMENT:
                    break;
                default:
                    throw new SosiException("Unexpected event: " + e.name());
            }
        }
        throw new SosiException("Internal Error");
    }
}
