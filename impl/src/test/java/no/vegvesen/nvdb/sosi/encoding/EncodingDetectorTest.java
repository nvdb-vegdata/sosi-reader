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
public class EncodingDetectorTest {

    @Test
    public void shouldDetectCharsetISO8859_1() {
        String sosi = ".HODE ..TEGNSETT ISO8859-1";
        Optional<Charset> charset = EncodingDetector.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(true));
        assertThat(charset.get().name(), equalTo("ISO-8859-1"));
    }

    @Test
    public void shouldDetectCharsetISO8859_10() {
        String sosi = ".HODE ..TEGNSETT ISO8859-10";
        Optional<Charset> charset = EncodingDetector.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(true));
        assertThat(charset.get().name(), equalTo("ISO-8859-1"));
    }

    @Test
    public void shouldDetectCharsetANSI() {
        String sosi = ".HODE ..TEGNSETT ANSI";
        Optional<Charset> charset = EncodingDetector.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(true));
        assertThat(charset.get().name(), equalTo("ISO-8859-1"));
    }

    @Test
    public void shouldDetectCharsetDECN7() {
        String sosi = ".HODE ..TEGNSETT DECN7";
        Optional<Charset> charset = EncodingDetector.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(true));
        assertThat(charset.get().name(), equalTo("DECN7"));
    }

    @Test
    public void shouldDetectCharsetND7() {
        String sosi = ".HODE ..TEGNSETT ND7";
        Optional<Charset> charset = EncodingDetector.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(true));
        assertThat(charset.get().name(), equalTo("ND7"));
    }

    @Test
    public void shouldDetectCharsetDOSN8() {
        String sosi = ".HODE ..TEGNSETT DOSN8";
        Optional<Charset> charset = EncodingDetector.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(true));
        assertThat(charset.get().name(), equalTo("DOSN8"));
    }

    @Test
    public void shouldDetectUnknownCharset() {
        String sosi = ".HODE ..TEGNSETT XXX";
        Optional<Charset> charset = EncodingDetector.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(false));
    }

    @Test
    public void shouldUseIso8859WhenMissingCharset() {
        String sosi = ".HODE .SLUTT";
        Optional<Charset> charset = EncodingDetector.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(false));
    }

    @Test
    public void shouldDetectNonCommentCharset() {
        String sosi = ".HODE !Dette er en ..TEGNSETT DOSN8 kommentar\n..TEGNSETT DECN7";
        Optional<Charset> charset = EncodingDetector.charsetOf(sosi.getBytes());
        assertThat(charset.isPresent(), is(true));
        assertThat(charset.get().name(), equalTo("DECN7"));
    }
}