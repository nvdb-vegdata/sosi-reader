package no.vegvesen.nvdb.sosi.parser;

import no.vegvesen.nvdb.sosi.Sosi;
import no.vegvesen.nvdb.sosi.parser.SosiParser;
import no.vegvesen.nvdb.sosi.parser.SosiParsingException;
import org.junit.Test;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event.START_ELEMENT;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event.START_HEAD;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event.COMMENT;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event.CONCATENATION;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event.VALUE_DEFAULT;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event.END;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event.END_ELEMENT;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event.END_HEAD;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event.VALUE_UNSPECIFIED;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event.VALUE_STRING;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event.VALUE_NUMBER;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event.VALUE_REF;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event.VALUE_SERNO;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.*;

public class SosiParserImplTest {

    @Test
    public void shouldDetectEmptyElements() {
        final String[] invalidSosis = new String[]{
                ".HODE ..VERDI .SLUTT",
                ".HODE .VERDI 123 .SLUTT"
        };

        assertParsingException(invalidSosis, "Elementet har hverken verdier eller subelementer");
    }

    @Test
    public void shouldDetectMissingHead() {
        final String[] invalidSosis = new String[]{
                ".HODET ..VERDI 123 .SLUTT",
                ".IKKEHODE .HODE ..VERDI 123 .SLUTT"
        };

        assertParsingException(invalidSosis, "Filen må starte med et HODE-element");
    }

    @Test
    public void shouldDetectLeapingLevel() {
        final String[] invalidSosis = new String[]{
                ".HODE ...VERDI 123 .SLUTT"
        };

        assertParsingException(invalidSosis, "Elementnivået er for høyt");
    }

    @Test
    public void shouldDetectInvalidValues() {
        final String[] invalidSosis = new String[]{
                ".HODE ..VERDI 12x3 .SLUTT",
                ".HODE ..VERDI 123x .SLUTT",
                ".HODE ..VERDI 123:.SLUTT",
                ".HODE ..VERDI 123.SLUTT",
                ".HODE ..VERDI :12.0 .SLUTT"
        };

        assertParsingException(invalidSosis, "Unexpected char");
    }

    @Test
    public void shouldDetectInvalidConcats() {
        final String[] invalidSosis = new String[]{
                ".HODE ..VERDI 123 & 456 .SLUTT",
                ".HODE ..VERDI '123' & .SLUTT",
                ".HODE ..VERDI & '123' .SLUTT",
                ".HODE ..VERDI '123' & 456 .SLUTT",
                ".HODE ..VERDI 123 & '456' .SLUTT"
        };

        assertParsingException(invalidSosis, "Konkatenering krever strengverdier på begge sider");
    }

    @Test
    public void shouldParseValidSpecialValues() {
        final EventValue[] expectedEvents = new EventValue[]{
                ev(START_HEAD, "HODE"),
                ev(START_ELEMENT, "TEGNSETT"),
                ev(VALUE_STRING, "ISO8859-1"),
                ev(END_ELEMENT),
                ev(END_HEAD),
                ev(START_ELEMENT, "PUNKT"),
                ev(VALUE_SERNO, "1"),
                ev(START_ELEMENT, "NAVN"),
                ev(VALUE_STRING, "Tore\""),
                ev(CONCATENATION),
                ev(VALUE_STRING, " \"Eide' "),
                ev(CONCATENATION),
                ev(VALUE_STRING, "Andersen"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "KVALITET"),
                ev(VALUE_NUMBER, "96"),
                ev(VALUE_UNSPECIFIED),
                ev(VALUE_NUMBER, "0"),
                ev(VALUE_UNSPECIFIED),
                ev(VALUE_NUMBER, "5"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "STØRRELSE"),
                ev(VALUE_DEFAULT),
                ev(END_ELEMENT),
                ev(END_ELEMENT),
                ev(END)
        };

        assertEventSequence("valid_with_special_values.sos", expectedEvents);
    }

    @Test
    public void shouldParseValidSerNoAndRefs() {
        final EventValue[] expectedEvents = new EventValue[]{
                ev(START_HEAD, "HODE"),
                ev(START_ELEMENT, "TEGNSETT"),
                ev(VALUE_STRING, "ISO8859-1"),
                ev(END_ELEMENT),
                ev(END_HEAD),
                ev(START_ELEMENT, "PUNKT"),
                ev(VALUE_SERNO, "123"),
                ev(START_ELEMENT, "PTEMA"),
                ev(VALUE_NUMBER, "0999"),
                ev(END_ELEMENT),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "LINJE"),
                ev(VALUE_SERNO, "2"),
                ev(START_ELEMENT, "PUNKT"),
                ev(VALUE_REF, "123"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "PUNKT"),
                ev(VALUE_SERNO, "234"),
                ev(START_ELEMENT, "PTEMA"),
                ev(VALUE_NUMBER, "0888"),
                ev(END_ELEMENT),
                ev(END_ELEMENT),
                ev(END_ELEMENT),
                ev(END)
        };

        assertEventSequence("valid_with_refs.sos", expectedEvents);
    }

    @Test
    public void shouldParseValidGraphicalSosiFile() {
        final EventValue[] expectedEvents = new EventValue[]{
                ev(START_HEAD, "HODE"),
                ev(START_ELEMENT, "TEGNSETT"),
                ev(VALUE_STRING, "ISO8859-1"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "SOSI-VERSJON"),
                ev(VALUE_NUMBER, "8.1"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "SOSI-NIVÅ"),
                ev(VALUE_NUMBER, "1"),
                ev(COMMENT, "!!!!!!!!!!"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "TRANSPAR"),
                ev(START_ELEMENT, "KOORDSYS"),
                ev(VALUE_NUMBER, "22"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "ORIGO-NØ"),
                ev(VALUE_NUMBER, "7034310"),
                ev(VALUE_NUMBER, "569351"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "ENHET"),
                ev(VALUE_NUMBER, "0.010"),
                ev(END_ELEMENT),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "OMRÅDE"),
                ev(START_ELEMENT, "MIN-NØ"),
                ev(VALUE_NUMBER, "7034310"),
                ev(VALUE_NUMBER, "569351"),
                ev(COMMENT, "!!!!!!!!!!!!!!!!!!!"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "MAX-NØ"),
                ev(VALUE_NUMBER, "7034406"),
                ev(VALUE_NUMBER, "569492"),
                ev(COMMENT, "!!!!!!!!!!!!!!!!!!!"),
                ev(END_ELEMENT),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "EIER"),
                ev(VALUE_STRING, "Søbstad AS"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "PRODUSENT"),
                ev(VALUE_STRING, "Sindre Barland"),
                ev(END_ELEMENT),
                ev(END_HEAD),
                ev(START_ELEMENT, "PUNKT"),
                ev(VALUE_SERNO, "1"),
                ev(START_ELEMENT, "OBJTYPE"),
                ev(VALUE_STRING, "Skiltplate_96"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "PTEMA"),
                ev(VALUE_NUMBER, "0999"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "DATAFANGSTDATO"),
                ev(VALUE_NUMBER, "20131127"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "KVALITET"),
                ev(VALUE_NUMBER, "96"),
                ev(VALUE_NUMBER, "5"),
                ev(VALUE_NUMBER, "0"),
                ev(VALUE_NUMBER, "96"),
                ev(VALUE_NUMBER, "5"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "SkiltnummerHB-050_5530"),
                ev(VALUE_NUMBER, "7691"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "Ansiktsside_1894"),
                ev(VALUE_NUMBER, "2714"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "Størrelse_1970"),
                ev(VALUE_NUMBER, "4132"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "Skiltform_1892"),
                ev(VALUE_NUMBER, "2855"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "Belysning_1879"),
                ev(VALUE_NUMBER, "3476"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "Folieklasse_1921"),
                ev(VALUE_NUMBER, "2849"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "Klappskilt_8828"),
                ev(VALUE_NUMBER, "11739"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "NØH"),
                ev(VALUE_NUMBER, "2980"),
                ev(VALUE_NUMBER, "11668"),
                ev(VALUE_NUMBER, "995"),
                ev(END_ELEMENT),
                ev(END_ELEMENT),
                ev(END)
        };

        assertEventSequence("valid_graphical.sos", expectedEvents);
    }

    private void assertParsingException(String[] invalidSosis, String expectedMessage) {
        for (String sosi : invalidSosis) {
            try {
                Reader sosiReader = new StringReader(sosi);
                SosiParser parser = Sosi.createParser(sosiReader);
                while (parser.hasNext()) {
                    parser.next();
                }

                fail("Expected SosiParsingException when parsing SOSI string " + sosi);
            } catch (SosiParsingException e) {
                assertThat(e.getMessage(), containsString(expectedMessage));
            }
        }
    }

    private void assertEventSequence(String filename, EventValue[] expectedEvents) {
        InputStream sosiStream = getResource(filename);
        SosiParser parser = Sosi.createParser(sosiStream);

        int eventNo = 1;
        for (EventValue expected : expectedEvents) {
            Event event = parser.next();
            assertThat("Wrong event type for event no " + eventNo, event, is(expected.getEvent()));
            if (expected.hasValue()) {
                String value = parser.getString();
                assertThat("Wrong event value for event no " + eventNo, value, is(expected.getValue()));
            }
            eventNo++;
        }

        assertThat("Should not be more events from parser", parser.hasNext(), is(false));
    }

    private InputStream getResource(String name) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(name);
        if (isNull(stream)) {
            throw new IllegalArgumentException("Resource " + name + " not found");
        }
        return stream;
    }

    private EventValue ev(Event event, String value) {
        return new EventValue(event, value);
    }

    private EventValue ev(Event event) {
        return new EventValue(event, null);
    }

    private class EventValue {
        private Event event;
        private String value;

        private EventValue(Event event, String value) {
            this.event = event;
            this.value = value;
        }

        public Event getEvent() {
            return event;
        }

        public String getValue() {
            return value;
        }

        public boolean hasValue() {
            return nonNull(value);
        }
    }
}
