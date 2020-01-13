package no.vegvesen.nvdb.sosi.encoding.charset;

import org.junit.Test;

import java.nio.ByteBuffer;
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
    public void testEncodeNorwegianChars() {
        String norwegianChars = "æøåÆØÅ";

        Charset cs = new ISO8859_10();
        ByteBuffer encoded = cs.encode(norwegianChars);

        ByteBuffer expected = ByteBuffer.wrap(new byte[] {(byte)0xe6, (byte)0xf8, (byte)0xe5, (byte)0xc6, (byte)0xd8, (byte)0xc5 });

        assertThat( encoded.array(), equalTo(expected.array()));
    }

    @Test
    public void testEncodeSami8bitChars() {
        String samiChars = "Áá";

        Charset cs = new ISO8859_10();
        ByteBuffer encoded = cs.encode(samiChars);

        ByteBuffer expected = ByteBuffer.wrap(new byte[] {(byte)0xc1, (byte)0xe1});

        assertThat( encoded.array(), equalTo(expected.array()));
    }

    @Test
    public void testEncodeSami16bitChars() {
        String samiChars = "š";

        Charset cs = new ISO8859_10();
        ByteBuffer encoded = cs.encode(samiChars);

        ByteBuffer expected = ByteBuffer.wrap(new byte[] {(byte)0xba});

        assertThat( encoded.array(), equalTo(expected.array()));
    }

    /**
     * Test that characters outside the range (0x0200) and characters inside the range but not contained in the
     * character set is encoded to the replacement codepoint (normally 63 or 0xef which is '?' in ISO-8859).
     */
    @Test
    public void testCharSubstitution() {
        String unsupportedChar = "\u0200À";

        Charset cs = new ISO8859_10();
        ByteBuffer encoded = cs.encode(unsupportedChar);

        ByteBuffer expected = ByteBuffer.wrap(new byte[] {(byte)0x3f, (byte)0x3f});

        assertThat( encoded.array(), equalTo(expected.array()));

    }

}
