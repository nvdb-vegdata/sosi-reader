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

package no.vegvesen.nvdb.sosi.encoding;

import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Unit test for the EncodingDetector class
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class SosiEncodingTest {

    @Test
    public void shouldDetectCharsetISO8859_1() {
        String sosi = ".HODE ..TEGNSETT ISO8859-1";
        Optional<Charset> charset = SosiEncoding.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(true));
        assertThat(charset.get().name(), equalTo("ISO-8859-1"));
    }

    @Test
    public void shouldDetectCharsetISO8859_10() {
        String sosi = ".HODE ..TEGNSETT ISO8859-10";
        Optional<Charset> charset = SosiEncoding.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(true));
        assertThat(charset.get().name(), equalTo("ISO-8859-10"));
    }

    @Test
    public void shouldDetectCharsetUTF_8() {
        String sosi = ".HODE ..TEGNSETT UTF-8";
        Optional<Charset> charset = SosiEncoding.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(true));
        assertThat(charset.get().name(), equalTo("UTF-8"));
    }

    @Test
    public void shouldDetectCharsetANSI() {
        String sosi = ".HODE ..TEGNSETT ANSI";
        Optional<Charset> charset = SosiEncoding.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(true));
        assertThat(charset.get().name(), equalTo("ISO-8859-1"));
    }

    @Test
    public void shouldDetectCharsetDECN7() {
        String sosi = ".HODE ..TEGNSETT DECN7";
        Optional<Charset> charset = SosiEncoding.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(true));
        assertThat(charset.get().name(), equalTo("DECN7"));
    }

    @Test
    public void shouldDetectCharsetND7() {
        String sosi = ".HODE ..TEGNSETT ND7";
        Optional<Charset> charset = SosiEncoding.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(true));
        assertThat(charset.get().name(), equalTo("ND7"));
    }

    @Test
    public void shouldDetectCharsetDOSN8() {
        String sosi = ".HODE ..TEGNSETT DOSN8";
        Optional<Charset> charset = SosiEncoding.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(true));
        assertThat(charset.get().name(), equalTo("DOSN8"));
    }

    @Test
    public void shouldDetectUnknownCharset() {
        String sosi = ".HODE ..TEGNSETT XXX";
        Optional<Charset> charset = SosiEncoding.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(false));
    }

    @Test
    public void shouldUseIso8859WhenMissingCharset() {
        String sosi = ".HODE .SLUTT";
        Optional<Charset> charset = SosiEncoding.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(false));
    }

    @Test
    public void shouldDetectNonCommentCharset() {
        String sosi = ".HODE !Dette er en ..TEGNSETT DOSN8 kommentar\n..TEGNSETT DECN7";
        Optional<Charset> charset = SosiEncoding.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(true));
        assertThat(charset.get().name(), equalTo("DECN7"));
    }
}