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
import java.io.Writer;

import static java.util.Arrays.asList;
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
        String[] files = new String[]{"valid_no_comments.sos", "valid_real_data.sos"};
        for (String file : files) {

            SosiDocument doc;
            try (SosiReader reader = Sosi.createReader(getResource(file))) {
                doc = reader.read();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Writer streamWriter = new OutputStreamWriter(outputStream, doc.getEncoding());
            try (SosiWriter writer = Sosi.createWriter(streamWriter, SosiWriterImplTest::valueFormatter, new LocationBasedSosiLayoutFormatter())) {
                writer.write(doc);
            }

            String sosi = new String(outputStream.toByteArray());
            assertSame(file, sosi);
        }
    }

    private void assertSame(String expectedSosiResource, String actualSosi) {
        String expected = streamToString(getResource(expectedSosiResource));
        assertThat(actualSosi, is(expected));
    }

    private static String valueFormatter(SosiElement element, SosiValue value) {
        if (asList("TEGNSETT", "OBJTYPE", "VERT-DATUM").contains(element.getName())) {
            return value.getString();
        } else if ("PTEMA".equalsIgnoreCase(element.getName())) {
            return String.format("%04d", ((SosiNumber) value).intValue());
        } else {
            return new DefaultSosiValueFormatter().apply(element, value);
        }
    }
}