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

package no.vegvesen.nvdb.sosi.parser;

import no.vegvesen.nvdb.sosi.Sosi;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;
import static no.vegvesen.nvdb.sosi.TestUtils.getResource;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event.END_REF_ISLAND;
import static no.vegvesen.nvdb.sosi.parser.SosiParser.Event.START_REF_ISLAND;
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

/**
 * Unit test for the SosiParserImpl class.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
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
    public void shouldDistinguishBetweenStringAndNumber() {
        final EventValue[] expectedEvents = new EventValue[]{
                ev(START_HEAD, "HODE"),
                ev(START_ELEMENT, "VERDI"),
                ev(VALUE_NUMBER, "3D2"),
                ev(VALUE_NUMBER, "-12.3e+45"),
                ev(VALUE_STRING, "-12.3e45xxx"),
                ev(VALUE_NUMBER, "-12.3e45"),
                ev(VALUE_NUMBER, "-12.3e-4"),
                ev(VALUE_STRING, "-12.3e-"),
                ev(VALUE_STRING, "-12.3e"),
                ev(VALUE_NUMBER, "123"),
                ev(VALUE_STRING, "12x3"),
                ev(VALUE_STRING, "123x"),
                ev(VALUE_STRING, "x123"),
                ev(VALUE_NUMBER, "12.3"),
                ev(VALUE_STRING, ":x"),
                ev(VALUE_STRING, ":12.0"),
                ev(VALUE_STRING, "E6"),
                ev(VALUE_STRING, "-E6"),
                ev(VALUE_NUMBER, "-.1d2"),
                ev(END_ELEMENT),
                ev(END_HEAD),
                ev(END)
        };

        assertEventSequence("valid_varying_strings_and_numbers.sos", expectedEvents);
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
    public void shouldParseParentheses() {
        final String sosi = ".HODE ..REF :1 (:2 :-3 :4) :5 (:-6) :-7 .SLUTT";

        final EventValue[] expectedEvents = new EventValue[]{
                ev(START_HEAD, "HODE"),
                ev(START_ELEMENT, "REF"),
                ev(VALUE_REF, "1"),
                ev(START_REF_ISLAND),
                ev(VALUE_REF, "2"),
                ev(VALUE_REF, "-3"),
                ev(VALUE_REF, "4"),
                ev(END_REF_ISLAND),
                ev(VALUE_REF, "5"),
                ev(START_REF_ISLAND),
                ev(VALUE_REF, "-6"),
                ev(END_REF_ISLAND),
                ev(VALUE_REF, "-7"),
                ev(END_ELEMENT),
                ev(END_HEAD),
                ev(END)
        };

        assertEventSequenceFromParser(expectedEvents, Sosi.createParser(new StringReader(sosi)));
    }

    @Test
    public void shouldDetectUnmatchedParentheses() {
        final String[] invalidSosis = new String[]{
                //".HODE ..REF (:1 ) .SLUTT",
                ".HODE ..REF ( :1) .SLUTT",
                ".HODE ..REF (:1 :-2 .SLUTT",
                ".HODE ..REF :1 :-2) .SLUTT"
        };

        assertParsingException(invalidSosis, "Parenteser må opptre i par");
    }

    @Test
    public void shouldDetectNonRefValuesInIsland() {
        final String[] invalidSosis = new String[]{
                ".HODE ..REF (:1 ) .SLUTT",
                ".HODE ..REF (:1 Hei) .SLUTT",
                ".HODE ..REF (:1 1 ) .SLUTT"
        };

        assertParsingException(invalidSosis, "Forventede tokens er: [COLON_VALUE,CLOSE_PARENTHESIS]");
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

    @Test
    public void shouldDetectEncoding() {
        Map<String,byte[]> encodings = new HashMap<>();
        encodings.put("ANSI", asByteArray(198, 216, 197, 230, 248, 229));
        encodings.put("ISO8859-1", asByteArray(198, 216, 197, 230, 248, 229));
        encodings.put("ISO8859-10", asByteArray(198, 216, 197, 230, 248, 229));
        encodings.put("UTF-8", asDoubleByteArray(0xc386, 0xc398, 0xc385, 0xc3a6, 0xc3b8, 0xc3a5));
        encodings.put("DOSN8", asByteArray(146, 157, 143, 145, 155, 134));
        encodings.put("ND7", asByteArray(91, 92, 93, 123, 124, 125));
        encodings.put("DECN7", asByteArray(91, 92, 93, 123, 124, 125));

        for (String encoding : encodings.keySet()) {
            ByteBuffer bytes = ByteBuffer.allocate(100);
            bytes.put(".HODE ..TEGNSETT ".getBytes());
            bytes.put(encoding.getBytes());
            bytes.put(" ..VERDI ".getBytes());
            bytes.put(encodings.get(encoding));
            bytes.put(" .SLUTT ".getBytes());

            final EventValue[] expectedEvents = new EventValue[]{
                    ev(START_HEAD, "HODE"),
                    ev(START_ELEMENT, "TEGNSETT"),
                    ev(VALUE_STRING, encoding),
                    ev(END_ELEMENT),
                    ev(START_ELEMENT, "VERDI"),
                    ev(VALUE_STRING, "ÆØÅæøå"),
                    ev(END_ELEMENT),
                    ev(END_HEAD),
                    ev(END)
            };

            SosiParser parser = Sosi.createParser(new ByteArrayInputStream(bytes.array()));
            assertEventSequenceFromParser(expectedEvents, parser);
        }
    }

    @Test
    public void shouldAcceptNonTerminatedQuotedStringWithLinebreak() {
        final EventValue[] expectedEvents = new EventValue[]{
                ev(START_HEAD, "HODE"),
                ev(START_ELEMENT, "FORNAVN"),
                ev(VALUE_STRING, "Tore\""),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "MELLOMNAVN"),
                ev(VALUE_STRING, "Eide'"),
                ev(END_ELEMENT),
                ev(START_ELEMENT, "ETTERNAVN"),
                ev(VALUE_STRING, "Andersen"),
                ev(END_ELEMENT),
                ev(END_HEAD),
                ev(END)
        };

        assertEventSequence("valid_nonterminated_quoted_string.sos", expectedEvents);
    }

    @Test
    public void shouldRejectNonTerminatedQuotedStringWithoutLinebreak() {
        final String[] invalidSingleQuoteStrings = new String[]{
                ".HODE ..VERDI 'Slutter aldri .SLUTT",
                ".HODE ..VERDI 'Slutter aldri'' .SLUTT"
        };
        final String[] invalidDoubleQuoteStrings = new String[]{
                ".HODE ..VERDI \"Slutter aldri .SLUTT",
                ".HODE ..VERDI \"Slutter aldri\"\" .SLUTT"
        };

        assertParsingException(invalidSingleQuoteStrings, "expecting '''");
        assertParsingException(invalidDoubleQuoteStrings, "expecting '\"'");
    }

    private byte[] asByteArray(int... values) {
        byte[] bytes = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            bytes[i] = (byte)values[i];
        }
        return bytes;
    }

    private byte[] asDoubleByteArray(int... values) {
        byte[] bytes = new byte[values.length*2];
        for (int i = 0; i < values.length; i++) {
            bytes[i*2] = (byte)(values[i] >> 8);
            bytes[i*2+1] = (byte)(values[i] & 0xff);
        }
        return bytes;
    }

    private void assertParsingException(String[] invalidSosis, String expectedMessage) {
        int sosiNo = 1;
        for (String sosi : invalidSosis) {
            try {
                Reader sosiReader = new StringReader(sosi);
                SosiParser parser = Sosi.createParser(sosiReader);
                while (parser.hasNext()) {
                    parser.next();
                }

                fail("Expected SosiParsingException when parsing SOSI string " + sosiNo + ": " + sosi);
            } catch (SosiParsingException e) {
                assertThat("Unexpected parsing exception for SOSI string " + sosiNo, e.getMessage(), containsString(expectedMessage));
            }
            sosiNo++;
        }
    }

    private void assertEventSequence(String filename, EventValue[] expectedEvents) {
        InputStream sosiStream = getResource(filename);
        SosiParser parser = Sosi.createParser(sosiStream);

        assertEventSequenceFromParser(expectedEvents, parser);
    }

    private void assertEventSequenceFromParser(EventValue[] expectedEvents, SosiParser parser) {
        int eventNo = 1;
        for (EventValue expected : expectedEvents) {
            Event event = parser.next();
            assertThat("Wrong event type for event no " + eventNo, event, is(expected.getEvent()));
            if (expected.hasValue()) {
                String value = parser.getString();
                assertThat("Wrong event value for event no " + eventNo, value, is(expected.getValue()));

                // Verify that number value can be parsed as number
                if (event.isOneOf(VALUE_NUMBER)) {
                    parser.getBigDecimal();
                }
            }
            eventNo++;
        }

        assertThat("Should not be more events from parser", parser.hasNext(), is(false));
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
