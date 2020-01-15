package no.vegvesen.nvdb.sosi.encoding.charset;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class ISO8859_10Test {

    @Test
    public void getISO8859_10FromProvider() {
        Charset cs = SosiCharset.forName("ISO-8859-10");

        assertThat(cs, instanceOf(ISO8859_10.class));
        assertThat(cs, instanceOf(SosiCharset.class));
        assertThat(cs, instanceOf(Charset.class));
    }

    @Test
    public void testEncodeNorwegianCharsBufferLoop() {
        String norwegianChars = "æøåÆØÅ";

        Charset cs = new ISO8859_10();
        ByteBuffer encoded = cs.encode(norwegianChars);

        ByteBuffer expected = ByteBuffer.wrap(new byte[] {(byte)0xe6, (byte)0xf8, (byte)0xe5, (byte)0xc6, (byte)0xd8, (byte)0xc5 });

        assertThat( encoded.array(), equalTo(expected.array()));
    }

    @Test
    public void testEncodeSamiCharsBufferLoop() {
        String samiChars = "ÁŅōĪŋš";

        Charset cs = new ISO8859_10();
        ByteBuffer encoded = cs.encode(samiChars);

        ByteBuffer expected = ByteBuffer.wrap(new byte[] {(byte)0xc1, (byte)0xd1, (byte)0xf2, (byte)0xa4, (byte)0xbf, (byte)0xba});

        assertThat( encoded.array(), equalTo(expected.array()));
    }

    @Test
    public void testEncodeNorwegianCharsArrayLoop() {
        char[] norwegianChars = {'æ', 'ø', 'å', 'Æ', 'Ø', 'Å'};
        CharBuffer norwegianCharsBuffer = CharBuffer.wrap(norwegianChars, 0, norwegianChars.length);
        ByteBuffer encoded = ByteBuffer.allocate(norwegianChars.length);
        Charset cs = new ISO8859_10();

        cs.newEncoder().encode(norwegianCharsBuffer, encoded, true);
        ByteBuffer expected = ByteBuffer.wrap(new byte[] {(byte)0xe6, (byte)0xf8, (byte)0xe5, (byte)0xc6, (byte)0xd8, (byte)0xc5 });

        assertThat( encoded.array(), equalTo(expected.array()));
    }

    @Test
    public void testEncodeSami8bitCharsArrayLoop() {
        char[] samiChars = {'Á', 'Ņ', 'ō', 'Ī', 'ŋ', 'š'};
        CharBuffer samiCharsBuffer = CharBuffer.wrap(samiChars, 0, samiChars.length);
        ByteBuffer encoded = ByteBuffer.allocate(samiChars.length);
        Charset cs = new ISO8859_10();

        cs.newEncoder().encode(samiCharsBuffer, encoded, true);
        ByteBuffer expected = ByteBuffer.wrap(new byte[] {(byte)0xc1, (byte)0xd1, (byte)0xf2, (byte)0xa4, (byte)0xbf, (byte)0xba});

        assertThat( encoded.array(), equalTo(expected.array()));
    }

    /**
     * Test that characters outside the range (0x0200) and characters inside the range but not contained in the
     * character set is encoded to the replacement codepoint (normally 63 or 0x3f which is '?' in ISO-8859).
     */
    @Test
    public void testCharSubstitutionBufferLoop() {
        String unsupportedChars = "\u6771ƀ";

        Charset cs = new ISO8859_10();
        ByteBuffer encoded = cs.encode(unsupportedChars);

        ByteBuffer expected = ByteBuffer.wrap(new byte[] {(byte)0x3f, (byte)0x3f});

        assertThat( encoded.array(), equalTo(expected.array()));
    }

    @Test
    public void testCharSubstitutionArrayLoop() {
        char[] unsupportedChars = {'\u6771', 'ƀ'};
        CharBuffer unsupportedCharsBuffer = CharBuffer.wrap(unsupportedChars, 0, unsupportedChars.length);
        ByteBuffer encoded = ByteBuffer.allocate(unsupportedChars.length);
        Charset cs = new ISO8859_10();


        cs.newEncoder().encode(unsupportedCharsBuffer, encoded, true);
        ByteBuffer expected = ByteBuffer.wrap(new byte[] {(byte)0x3f, (byte)0x3f});

        assertThat( encoded.array(), equalTo(expected.array()));
    }
}
