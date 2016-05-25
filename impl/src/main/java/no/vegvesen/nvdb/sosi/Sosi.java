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
package no.vegvesen.nvdb.sosi;

import no.vegvesen.nvdb.sosi.encoding.SosiEncoding;
import no.vegvesen.nvdb.sosi.reader.SosiReader;
import no.vegvesen.nvdb.sosi.reader.SosiReaderImpl;
import no.vegvesen.nvdb.sosi.utils.BufferPoolImpl;
import no.vegvesen.nvdb.sosi.parser.SosiParserImpl;
import no.vegvesen.nvdb.sosi.parser.SosiParser;
import no.vegvesen.nvdb.sosi.writer.SosiLayoutFormatter;
import no.vegvesen.nvdb.sosi.writer.SosiValueFormatter;
import no.vegvesen.nvdb.sosi.writer.SosiWriter;
import no.vegvesen.nvdb.sosi.writer.SosiWriterImpl;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Factory to create {@link no.vegvesen.nvdb.sosi.parser.SosiParser} and {@link SosiReader} instances.
 *
 * <p>
 * For example, a SOSI parser for parsing a simple SOSI string could be created as follows:
 * <pre>
 * <code>
 * StringReader reader = new StringReader(".HODE ..EIER Sosekopp .SLUTT");
 * SosiParser parser = Sosi.createParser(reader);
 * </code>
 * </pre>
 *
 * <p>
 * All of the methods in this class are safe for use by multiple concurrent threads.
 *
 * Based on the javax.json.Json class.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class Sosi {

    private Sosi() {
    }

    /**
     * Detects the encoding of a SOSI file.
     *
     * @param sosi the SOSI file content.
     * @return the encoding
     */
    public static Charset getEncoding(byte[] sosi) {
        return SosiEncoding.charsetOf(sosi).orElseGet(SosiEncoding::defaultCharset);
    }

    /**
     * Creates a SOSI parser from the specified character stream
     *
     * @param reader i/o reader from which SOSI is to be read
     */
    public static SosiParser createParser(Reader reader) {
        return new SosiParserImpl(reader, new BufferPoolImpl());
    }

    /**
     * Creates a SOSI parser from the specified byte stream.
     * The character encoding of the stream is determined
     * as per the <a href="http://tools.ietf.org/rfc/rfc4627.txt">RFC</a>.
     *
     * @param in i/o stream from which SOSI is to be read
     * @throws SosiException if encoding cannot be determined
     *         or i/o error (IOException would be cause of SosiException)
     */
    public static SosiParser createParser(InputStream in) {
        return new SosiParserImpl(in, new BufferPoolImpl());
    }

    /**
     * Creates a SOSI reader which can be used to read SOSI text from the
     * specified character stream.
     *
     * @param reader a i/o reader from which SOSI is read
     */
    public static SosiReader createReader(Reader reader) {
        return new SosiReaderImpl(reader, new BufferPoolImpl());
    }

    /**
     * Creates a SOSI reader which can be used to read SOSI text from the
     * specified byte stream.
     *
     * @param in i/o stream from which SOSI is read
     */
    public static SosiReader createReader(InputStream in) {
        return new SosiReaderImpl(in, new BufferPoolImpl());
    }

    /**
     * Creates a SOSI writer which can be used to write SOSI document to the
     * specified character stream.
     *
     * @param writer a i/o writer to which SOSI is written
     */
    public static SosiWriter createWriter(Writer writer) {
        return new SosiWriterImpl(writer);
    }

    /**
     * Creates a SOSI writer which can be used to write SOSI document to the
     * specified character stream using a custom value formatter.
     *
     * @param writer a i/o writer to which SOSI is written
     */
    public static SosiWriter createWriter(Writer writer, SosiValueFormatter valueFormatter, SosiLayoutFormatter layoutFormatter) {
        return new SosiWriterImpl(writer, valueFormatter, layoutFormatter);
    }

    /**
     * Creates a SOSI writer which can be used to write SOSI document to the
     * specified byte stream.
     *
     * @param out i/o stream to which SOSI is written
     * @param encoding the desired character encoding
     */
    public static SosiWriter createWriter(OutputStream out, Charset encoding) {
        return new SosiWriterImpl(out, encoding);
    }
}
