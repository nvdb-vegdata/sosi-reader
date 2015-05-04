package no.vegvesen.nvdb.sosi.writer;

import no.vegvesen.nvdb.sosi.Sosi;
import no.vegvesen.nvdb.sosi.document.SosiDocument;
import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiNumber;
import no.vegvesen.nvdb.sosi.document.SosiValue;
import no.vegvesen.nvdb.sosi.reader.SosiReader;
import org.junit.Test;

import java.io.StringWriter;

import static java.util.Arrays.asList;
import static no.vegvesen.nvdb.sosi.TestUtils.getResource;
import static no.vegvesen.nvdb.sosi.TestUtils.streamToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * TODO: Purpose and responsibility
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class SosiWriterImplTest {

    @Test
    public void shouldWriteSameAsRead() {
        SosiDocument doc;
        try (SosiReader reader = Sosi.createReader(getResource("valid_no_comments.sos"))) {
            doc = reader.read();
        }

        String sosi;
        StringWriter stringWriter = new StringWriter();
        try (SosiWriter writer = Sosi.createWriter(stringWriter, SosiWriterImplTest::valueFormatter)) {
            writer.write(doc);
            sosi = stringWriter.getBuffer().toString();
        }

        assertSame("valid_no_comments.sos", sosi);
    }

    private void assertSame(String expectedSosiResource, String actualSosi) {
        String expected = streamToString(getResource(expectedSosiResource));
        assertThat(actualSosi, is(expected));
    }

    private static String valueFormatter(SosiElement element, SosiValue value) {
        if (asList("TEGNSETT", "OBJTYPE").contains(element.getName())) {
            return value.getString();
        } else if ("PTEMA".equalsIgnoreCase(element.getName())) {
            return String.format("%04d", ((SosiNumber) value).intValue());
        } else {
            return new DefaultSosiValueFormatter().apply(element, value);
        }
    }
}