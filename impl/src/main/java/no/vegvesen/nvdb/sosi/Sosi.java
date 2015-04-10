// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi;

import no.vegvesen.nvdb.sosi.reader.SosiReader;
import no.vegvesen.nvdb.sosi.reader.SosiReaderImpl;
import no.vegvesen.nvdb.sosi.utils.BufferPoolImpl;
import no.vegvesen.nvdb.sosi.parser.SosiParserImpl;
import no.vegvesen.nvdb.sosi.parser.SosiParser;

import java.io.InputStream;
import java.io.Reader;

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
}
