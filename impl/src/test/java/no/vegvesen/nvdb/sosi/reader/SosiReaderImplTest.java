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

import no.vegvesen.nvdb.sosi.Sosi;
import no.vegvesen.nvdb.sosi.document.SosiDocument;
import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiNumber;
import no.vegvesen.nvdb.sosi.document.SosiRefIsland;
import no.vegvesen.nvdb.sosi.document.SosiRefNumber;
import no.vegvesen.nvdb.sosi.document.SosiSerialNumber;
import no.vegvesen.nvdb.sosi.document.SosiString;
import no.vegvesen.nvdb.sosi.document.SosiValue;
import no.vegvesen.nvdb.sosi.encoding.SosiEncoding;
import org.junit.Test;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static no.vegvesen.nvdb.sosi.TestUtils.getResource;
import static no.vegvesen.nvdb.sosi.TestUtils.streamToBytes;
import static no.vegvesen.nvdb.sosi.utils.Predicates.hasName;
import static no.vegvesen.nvdb.sosi.utils.Predicates.hasSerialNumber;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

/**
 * Unit test for the SosiReaderImpl class.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class SosiReaderImplTest {
    @Test
    public void shouldBuildSosiDocument() {
        final String sosi =
                ".HODE ..VERDI 123 ..NAVN 'Ola' & \"Nordmann\" ..GRUPPE 1: ...EL medlem ..GRUPPE :1 .SLUTT";

        SosiReader reader = Sosi.createReader(new StringReader(sosi));
        SosiDocument doc = reader.read();

        assertThat(doc.elements().count(), is(2L));
        SosiElement head = doc.getHead();
        assertThat(head.values().count(), is(0L));
        assertThat(head.subElements().count(), is(4L));

        Optional<SosiElement> verdi = head.findSubElement(hasName("VERDI"));
        assertThat(verdi.isPresent(), is(true));
        assertThat(verdi.get().values().count(), is(1L));

        SosiValue valueOfVerdi = verdi.get().values().findFirst().get();
        assertThat(valueOfVerdi, instanceOf(SosiNumber.class));
        SosiNumber number = (SosiNumber)valueOfVerdi;
        assertThat(number.isIntegral(), is(true));
        assertThat(number.intValue(), is(123));

        Optional<SosiElement> navn = head.findSubElement(hasName("NAVN"));
        assertThat(navn.isPresent(), is(true));
        assertThat(navn.get().values().count(), is(1L));

        SosiValue valueOfNavn = navn.get().values().findFirst().get();
        assertThat(valueOfNavn, instanceOf(SosiString.class));
        SosiString string = (SosiString)valueOfNavn;
        assertThat(string.getString(), is("OlaNordmann"));

        Optional<SosiElement> gruppe = head.findSubElement(hasName("GRUPPE"));
        assertThat(gruppe.isPresent(), is(true));
        assertThat(gruppe.get().values().count(), is(1L));

        SosiValue valueOfGruppe = gruppe.get().values().findFirst().get();
        assertThat(valueOfGruppe, instanceOf(SosiSerialNumber.class));
        SosiSerialNumber serNo = (SosiSerialNumber)valueOfGruppe;
        assertThat(serNo.longValue(), is(1L));
    }

    @Test
    public void shouldDetectValidORIGONØ() {
        byte[] sosiBytes = streamToBytes(getResource("valid_real_data2.sos"), 2048);
        Optional<Charset> charset = SosiEncoding.charsetOf(sosiBytes);
        assertThat(charset.isPresent(), is(true));
        assertThat(charset.get().name(), equalTo("ISO-8859-1"));

        SosiReader reader = Sosi.createReader(getResource("valid_real_data2.sos"));
        SosiDocument doc = reader.read();
        assertThat(doc.findElementRecursively(hasName("ORIGO-NØ")).isPresent(), is(true));
    }

    @Test
    public void shouldAddLinebreaksWhenConcatenatingOnDifferentLines() {
        SosiReader reader = Sosi.createReader(getResource("valid_string_concat_on_different_lines.sos"));
        SosiDocument doc = reader.read();

        SosiElement stringEl = doc.findElementRecursively(hasName("STRENG")).orElseThrow(() -> new IllegalStateException("STRENG element not found"));
        List<SosiValue> stringValues = stringEl.values().collect(toList());

        assertThat(stringValues, hasSize(1));
        assertThat(stringValues.get(0).getString(), is("Linje1\nLinje2\nLinje3"));
    }

    @Test
    public void shouldHandleRefIslands() {
        SosiReader reader = Sosi.createReader(getResource("valid_with_island_refs.sos"));
        SosiDocument doc = reader.read();

        // FLATE 11: ..REF :1 (:2 :-3 :4) :5
        SosiElement flate11 = doc.findElement(hasName("FLATE").and(hasSerialNumber(11))).get();
        SosiElement flate11Ref = flate11.findSubElement(hasName("REF")).get();

        List<SosiValue> flate11RefValues = flate11Ref.values().collect(toList());
        assertThat(flate11RefValues, hasSize(3));
        assertThat(flate11RefValues.get(0), instanceOf(SosiRefNumber.class));
        assertThat(((SosiRefNumber)flate11RefValues.get(0)).longValue(), equalTo(1L));
        assertThat(((SosiRefNumber)flate11RefValues.get(0)).isInsideIsland(), is(false));
        assertThat(flate11RefValues.get(1), instanceOf(SosiRefIsland.class));
        assertThat(flate11RefValues.get(2), instanceOf(SosiRefNumber.class));
        assertThat(((SosiRefNumber)flate11RefValues.get(2)).longValue(), equalTo(5L));
        assertThat(((SosiRefNumber)flate11RefValues.get(2)).isInsideIsland(), is(false));

        List<SosiRefNumber> flate11RefIslandRefNos = ((SosiRefIsland)flate11RefValues.get(1)).refNumbers().collect(toList());
        assertThat(flate11RefIslandRefNos, hasSize(3));
        assertThat(flate11RefIslandRefNos.get(0).longValue(), equalTo(2L));
        assertThat(flate11RefIslandRefNos.get(0).isInsideIsland(), is(true));
        assertThat(flate11RefIslandRefNos.get(0).isReversedOrder(), is(false));
        assertThat(flate11RefIslandRefNos.get(1).longValue(), equalTo(3L));
        assertThat(flate11RefIslandRefNos.get(1).isInsideIsland(), is(true));
        assertThat(flate11RefIslandRefNos.get(1).isReversedOrder(), is(true));
        assertThat(flate11RefIslandRefNos.get(2).longValue(), equalTo(4L));
        assertThat(flate11RefIslandRefNos.get(2).isInsideIsland(), is(true));
        assertThat(flate11RefIslandRefNos.get(2).isReversedOrder(), is(false));

        // FLATE 12: ..REF :1 (:2) :-3 (:4) :-5
        SosiElement flate12 = doc.findElement(hasName("FLATE").and(hasSerialNumber(12))).get();
        SosiElement flate12Ref = flate12.findSubElement(hasName("REF")).get();

        List<SosiValue> flate12RefValues = flate12Ref.values().collect(toList());
        assertThat(flate12RefValues, hasSize(5));
        assertThat(flate12RefValues.get(0), instanceOf(SosiRefNumber.class));
        assertThat(((SosiRefNumber)flate12RefValues.get(0)).longValue(), equalTo(1L));
        assertThat(flate12RefValues.get(1), instanceOf(SosiRefIsland.class));
        assertThat(flate12RefValues.get(2), instanceOf(SosiRefNumber.class));
        assertThat(((SosiRefNumber)flate12RefValues.get(2)).longValue(), equalTo(3L));
        assertThat(((SosiRefNumber)flate12RefValues.get(2)).isReversedOrder(), is(true));
        assertThat(flate12RefValues.get(3), instanceOf(SosiRefIsland.class));
        assertThat(flate12RefValues.get(4), instanceOf(SosiRefNumber.class));
        assertThat(((SosiRefNumber)flate12RefValues.get(4)).longValue(), equalTo(5L));
        assertThat(((SosiRefNumber)flate12RefValues.get(4)).isReversedOrder(), is(true));

        List<SosiRefNumber> flate12RefIsland1RefNos = ((SosiRefIsland)flate12RefValues.get(1)).refNumbers().collect(toList());
        assertThat(flate12RefIsland1RefNos, hasSize(1));
        assertThat(flate12RefIsland1RefNos.get(0).longValue(), equalTo(2L));
        assertThat(flate12RefIsland1RefNos.get(0).isInsideIsland(), is(true));

        List<SosiRefNumber> flate12RefIsland2RefNos = ((SosiRefIsland)flate12RefValues.get(3)).refNumbers().collect(toList());
        assertThat(flate12RefIsland2RefNos, hasSize(1));
        assertThat(flate12RefIsland2RefNos.get(0).longValue(), equalTo(4L));
        assertThat(flate12RefIsland2RefNos.get(0).isInsideIsland(), is(true));
    }

    @Test
    public void shouldReadUtf8WithBom() {
        byte[] sosiBytes = streamToBytes(getResource("valid_utf8_with_bom.sos"), 2048);
        Optional<Charset> charset = SosiEncoding.charsetOf(sosiBytes);
        assertThat(charset.isPresent(), is(true));
        assertThat(charset.get().name(), equalTo("UTF-8"));

        SosiReader reader = Sosi.createReader(getResource("valid_utf8_with_bom.sos"));
        SosiDocument doc = reader.read();
        assertThat(doc.findElementRecursively(hasName("ORIGO-NØ")).isPresent(), is(true));
    }
}
