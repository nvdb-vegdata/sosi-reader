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
                SosiElement endElement = new SosiElementBuilderImpl(parser.getString(), parser.getLocation()).build();
                elements.add(endElement);
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
        boolean insideRefIsland = false;

        while(parser.hasNext()) {
            SosiParser.Event e = parser.next();
            switch (e) {
                case START_ELEMENT:
                    String name = parser.getString();
                    SosiElement subElement = readElement(new SosiElementBuilderImpl(name, parser.getLocation()));
                    builder.addSubElement(name, subElement);
                    break;
                case START_REF_ISLAND:
                    builder.addValue(SosiRefIslandImpl.of(parser.getLocation()));
                    insideRefIsland = true;
                    break;
                case END_REF_ISLAND:
                    insideRefIsland = false;
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
                    if (insideRefIsland) {
                        builder.addIslandValue(SosiRefNumberImpl.of(parser.getLong(), true, parser.getLocation()));
                    } else {
                        builder.addValue(SosiRefNumberImpl.of(parser.getLong(), false, parser.getLocation()));
                    }
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
