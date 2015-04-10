package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.Sosi;
import no.vegvesen.nvdb.sosi.document.SosiDocument;
import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiNumber;
import no.vegvesen.nvdb.sosi.document.SosiSerialNumber;
import no.vegvesen.nvdb.sosi.document.SosiString;
import no.vegvesen.nvdb.sosi.document.SosiValue;
import org.junit.Test;

import java.io.StringReader;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;


public class SosiReaderImplTest {
    @Test
    public void shouldBuildSosiDocument() {
        final String sosi =
                ".HODE ..VERDI 123 ..NAVN 'Ola' & \"Nordmann\" ..GRUPPE 1: ...EL medlem ..GRUPPE :1 .SLUTT";

        SosiReader reader = Sosi.createReader(new StringReader(sosi));
        SosiDocument doc = reader.read();

        assertThat(doc.elements().count(), is(1L));
        SosiElement head = doc.getHead();
        assertThat(head.values().count(), is(0L));
        assertThat(head.subElements().count(), is(4L));

        Optional<SosiElement> verdi = head.findSubElement("VERDI");
        assertThat(verdi.isPresent(), is(true));
        assertThat(verdi.get().values().count(), is(1L));

        SosiValue valueOfVerdi = verdi.get().values().findFirst().get();
        assertThat(valueOfVerdi, instanceOf(SosiNumber.class));
        SosiNumber number = (SosiNumber)valueOfVerdi;
        assertThat(number.isIntegral(), is(true));
        assertThat(number.intValue(), is(123));

        Optional<SosiElement> navn = head.findSubElement("NAVN");
        assertThat(navn.isPresent(), is(true));
        assertThat(navn.get().values().count(), is(1L));

        SosiValue valueOfNavn = navn.get().values().findFirst().get();
        assertThat(valueOfNavn, instanceOf(SosiString.class));
        SosiString string = (SosiString)valueOfNavn;
        assertThat(string.getString(), is("OlaNordmann"));

        Optional<SosiElement> gruppe = head.findSubElement("GRUPPE");
        assertThat(gruppe.isPresent(), is(true));
        assertThat(gruppe.get().values().count(), is(1L));

        SosiValue valueOfGruppe = gruppe.get().values().findFirst().get();
        assertThat(valueOfGruppe, instanceOf(SosiSerialNumber.class));
        SosiSerialNumber serNo = (SosiSerialNumber)valueOfGruppe;
        assertThat(serNo.longValue(), is(1L));
    }
}