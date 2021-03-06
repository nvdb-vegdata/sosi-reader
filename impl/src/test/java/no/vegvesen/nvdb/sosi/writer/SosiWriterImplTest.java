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

import no.vegvesen.nvdb.sosi.Sosi;
import no.vegvesen.nvdb.sosi.document.SosiDocument;
import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiNumber;
import no.vegvesen.nvdb.sosi.document.SosiValue;
import no.vegvesen.nvdb.sosi.reader.SosiReader;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;

import static no.vegvesen.nvdb.sosi.TestUtils.getResource;
import static no.vegvesen.nvdb.sosi.TestUtils.streamToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Unit test for the SosiWriterImpl class.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class SosiWriterImplTest {

    @Test
    public void shouldWriteSameAsRead() {
        String[] files = new String[]{
                "valid_no_comments.sos",
                "valid_real_data.sos",
                "valid_with_island_refs.sos",
                "valid_string_concat_on_different_lines.sos"
        };

        for (String file : files) {
            SosiDocument doc = readSosiResource(file);
            String sosi = writeSosi(doc, new LocationBasedSosiLayoutFormatter(LineEnding.UNIX));

            assertSame(file, sosi);
        }
    }

    @Test
    public void shouldSupportWindowsLineEnding() {
        final String sosi = ".HODE\r\n..VERDI 123\r\n.SLUTT";

        SosiDocument doc = readSosiString(sosi);
        String sosiOut = writeSosi(doc, new LocationBasedSosiLayoutFormatter(LineEnding.WINDOWS));

        assertThat(sosiOut, is(sosi));
    }

    @Test
    public void shouldQuoteStringValuesWithWhitespace() {
        String sosi = ".HODE ..VERDI \"Verdi med mellomrom\" ..VERDI VerdiUtenMellomrom .SLUTT";

        SosiDocument doc = readSosiString(sosi);
        String sosiOut = writeSosi(doc, new LocationBasedSosiLayoutFormatter());

        assertThat(sosiOut, is(sosi));
    }

    private SosiDocument readSosiResource(String resource) {
        SosiDocument doc;
        try (SosiReader reader = Sosi.createReader(getResource(resource))) {
            doc = reader.read();
        }
        return doc;
    }

    private SosiDocument readSosiString(String sosi) {
        SosiDocument doc;
        try (SosiReader reader = Sosi.createReader(new StringReader(sosi))) {
            doc = reader.read();
        }
        return doc;
    }

    private String writeSosi(SosiDocument doc, SosiLayoutFormatter layoutFormatter) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Writer streamWriter = new OutputStreamWriter(outputStream, doc.getEncoding());
        try (SosiWriter writer = Sosi.createWriter(streamWriter, SosiWriterImplTest::valueFormatter, layoutFormatter)) {
            writer.write(doc);
        }

        return new String(outputStream.toByteArray());
    }

    private void assertSame(String expectedSosiResource, String actualSosi) {
        String expected = streamToString(getResource(expectedSosiResource));
        assertThat(actualSosi, is(expected));
    }

    private static String valueFormatter(SosiElement element, SosiValue value) {
        if ("PTEMA".equalsIgnoreCase(element.getName())) {
            return String.format("%04d", ((SosiNumber) value).intValue());
        } else {
            return new DefaultSosiValueFormatter().apply(element, value);
        }
    }
}