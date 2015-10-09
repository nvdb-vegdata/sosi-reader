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
        String[] files = new String[]{"valid_no_comments.sos", "valid_real_data.sos", "valid_with_island_refs.sos"};

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