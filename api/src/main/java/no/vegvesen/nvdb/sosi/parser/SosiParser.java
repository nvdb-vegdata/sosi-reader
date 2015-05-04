// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.parser;

import no.vegvesen.nvdb.sosi.SosiLocation;

import java.io.Closeable;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Provides forward, read-only access to SOSI data in a streaming way. This
 * is the most efficient way for reading SOSI data. The class
 * {@link Ino.vegvesen.nvdb.sosi.Sosi} contains methods to create parsers from input
 * sources ({@link java.io.InputStream} and {@link java.io.Reader}).
 *
 * <p>
 * The following example demonstrates how to create a parser from a string
 * that contains simple SOSI data:
 * <pre>
 * <code>
 * SosiParser parser = Sosi.createParser(new StringReader(".HODE ..EIER Tore Torell .SLUTT"));
 * </code>
 * </pre>
 *
 * <p>
 * {@code SosiParser} parses SOSI using the pull parsing programming model.
 * In this model the client code controls the thread and calls the method
 * {@code next()} to advance the parser to the next state after
 * processing each element. The parser can generate the following events:
 * {@code START_HEAD}, {@code END_HEAD}, {@code START_ELEMENT},
 * {@code END_ELEMENT}, {@code COMMENT}, {@code CONCATENATION}, {@code VALUE}, {@code VALUE_DEFAULT},
 * {@code VALUE_UNSPECIFIED}, {@code VALUE_SERNO}, {@code VALUE_REF} and {@code END}.
 *
 * <p>
 * <b>For example</b>, for a simple SOSI file (".HODE ..EIER Tore Torell .SLUTT"), the parser generates
 * the following events (with values in paranthesis:
 *
 * <p>
 * <pre>
 * <B>START_HEAD("HODE")</B><B>START_ELEMENT("EIER")</B><B>VALUE("Tore Torell")</B><B>END_ELEMENT</B><B>END_HEAD</B><B>END</B>
 * </pre>
 *
 * <p>
 * The methods {@code next()} and {@code hasNext()} enable iteration over
 * parser events to process SOSI data. {@code SosiParser} provides get methods
 * to obtain the value at the current state of the parser. For example, the
 * following code shows how to obtain the value of the EIER element from the SOSI above:
 *
 * <p>
 * <pre>
 * <code>
 * Event event = parser.next(); // START_HEAD
 * event = parser.next();       // START_ELEMENT
 * parser.getString();          // "EIER"
 * event = parser.next();       // VALUE
 * parser.getString();          // "Tore Torell"
 * </code>
 * </pre>
 *
 * Based on the javax.json.stream.JsonParser interface.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public interface SosiParser extends /*Auto*/Closeable {

    /**
     * An event from {@code SosiParser}.
     */
    enum Event {
        /**
         * Start of a SOSI head element. The position of the parser is after ".HODE".
         */
        START_HEAD,
        /**
         * End of a SOSI head element. The position of the parser is after level marker of next element.
         */
        END_HEAD,
        /**
         * Start of a regular SOSI element. The position of the parser is after the element name.
         */
        START_ELEMENT,
        /**
         * End of a regular SOSI element. The position of the parser is after level marker of next element.
         */
        END_ELEMENT,
        /**
         * A comment string. The position of the parser is at the end of line character.
         * The method {@link #getString} returns the comment.
         */
        COMMENT,
        /**
         * String value in a SOSI element. The position of the parser is after the value.
         * The method {@link #getString} returns the value as a string.
         */
        VALUE_STRING,
        /**
         * Numeric value in a SOSI element. The position of the parser is after the value.
         * The method {@link #getString} returns the value as a string.
         */
        VALUE_NUMBER,
        /**
         * Unspecified value marker (*) in a SOSI element. The position of the parser is after the value.
         */
        VALUE_UNSPECIFIED,
        /**
         * Default value marker (@) in a SOSI element. The position of the parser is after the value.
         */
        VALUE_DEFAULT,
        /**
         * Serial number value (xxx:) in a SOSI element. The position of the parser is after the value.
         * The method {@link #getString} returns the serial number as a string.
         */
        VALUE_SERNO,
        /**
         * Reference value (:xxx) in a SOSI element. The position of the parser is after the value.
         * The method {@link #getString} returns the reference as a string.
         */
        VALUE_REF,
        /**
         * Concatenation marker (&) between two string values in a SOSI element. The position of the parser is after the value.
         */
        CONCATENATION,
        /**
         * End of a SOSI file. The position of the parser is after ".SLUTT".
         */
        END;

        public boolean isOneOf(Event... events) {
            return Arrays.stream(events).anyMatch(e -> e == this);
        }
    }

    /**
     * Returns {@code true} if there are more parsing states. This method returns
     * {@code false} if the parser reaches the end of the SOSI text.
     *
     * @return {@code true} if there are more parsing states.
     * @throws no.vegvesen.nvdb.sosi.SosiException if an i/o error occurs (IOException
     * would be cause of SosiException)
     * @throws no.vegvesen.nvdb.sosi.parser.SosiParsingException if the parser encounters invalid SOSI
     * when advancing to next state.
     */
    boolean hasNext();

    /**
     * Returns the event for the next parsing state.
     *
     * @throws no.vegvesen.nvdb.sosi.SosiException if an i/o error occurs (IOException
     * would be cause of SosiException)
     * @throws SosiParsingException if the parser encounters invalid SOSI
     * when advancing to next state.
     * @throws java.util.NoSuchElementException if there are no more parsing
     * states.
     */
    Event next();

    /**
     * Returns a {@code String} for the name of elements or the value of comments or values.
     * This method should only be called when the parser state is {@link Event#START_ELEMENT}, {@link Event#COMMENT},
     * {@link Event#VALUE_STRING}, {@link Event#VALUE_NUMBER}, {@link Event#VALUE_SERNO} or {@link Event#VALUE_REF}.
     *
     * @return a name when the parser state is {@link Event#START_ELEMENT}
     *         a string value when the parser state is {@link Event#VALUE_STRING} or {@link Event#COMMENT}
     *         a number value when the parser state is {@link Event#VALUE_NUMBER}, {@link Event#VALUE_SERNO} or {@link Event#VALUE_REF}
     * @throws IllegalStateException when the parser state is not
     *      {@code START_ELEMENT}, {@code COMMENT}, {@code VALUE_STRING}, {@code VALUE_NUMBER}, {@code VALUE_SERNO} or {@code VALUE_REF}
     */
    String getString();

    /**
     * Returns true if the SOSI number at the current parser state is a
     * integral number. A {@link BigDecimal} may be used to store the value
     * internally and this method semantics are defined using its
     * {@code scale()}. If the scale is zero, then it is considered integral
     * type. This integral type information can be used to invoke an
     * appropriate accessor method to obtain a numeric value as in the
     * following example:
     *
     * <pre>
     * <code>
     * SosiParser parser = ...
     * if (parser.isIntegralNumber()) {
     *     parser.getInt();     // or other methods to get integral value
     * } else {
     *     parser.getBigDecimal();
     * }
     * </code>
     * </pre>
     *
     * @return true if this number is a integral number, otherwise false
     * @throws IllegalStateException when the parser state is not
     *      {@code VALUE_NUMBER}, {@code VALUE_SERNO} or {@code VALUE_REF}
     */
    boolean isIntegralNumber();

    /**
     * Returns a SOSI number as an integer. The returned value is equal
     * to {@code new BigDecimal(getString()).intValue()}. Note that
     * this conversion can lose information about the overall magnitude
     * and precision of the number value as well as return a result with
     * the opposite sign. This method should only be called when the parser
     * state is {@link Event#VALUE_NUMBER}, {@link Event#VALUE_SERNO} or {@link Event#VALUE_REF}.
     *
     * @return an integer for a SOSI number
     * @throws IllegalStateException when the parser state is not
     *      {@code VALUE_NUMBER}, {@code VALUE_SERNO} or {@code VALUE_REF}
     * @see java.math.BigDecimal#intValue()
     */
    int getInt();

    /**
     * Returns a SOSI number as a long. The returned value is equal
     * to {@code new BigDecimal(getString()).longValue()}. Note that this
     * conversion can lose information about the overall magnitude and
     * precision of the number value as well as return a result with
     * the opposite sign. This method is only called when the parser state is
     * {@link Event#VALUE_NUMBER}, {@link Event#VALUE_SERNO} or {@link Event#VALUE_REF}.
     *
     * @return a long for a SOSI number
     * @throws IllegalStateException when the parser state is not
     *      {@code VALUE_NUMBER}, {@code VALUE_SERNO} or {@code VALUE_REF}
     * @see java.math.BigDecimal#longValue()
     */
    long getLong();

    /**
     * Returns a SOSI number as a {@code BigDecimal}. The {@code BigDecimal}
     * is created using {@code new BigDecimal(getString())}. This
     * method should only called when the parser state is
     * {@link Event#VALUE_NUMBER}, {@link Event#VALUE_SERNO} or {@link Event#VALUE_REF}.
     *
     * @return a {@code BigDecimal} for a SOSI number
     * @throws IllegalStateException when the parser state is not
     *      {@code VALUE_NUMBER}, {@code VALUE_SERNO} or {@code VALUE_REF}
     */
    BigDecimal getBigDecimal();

    /**
     * Return the location that corresponds to the parser's current state in
     * the SOSI input source. The location information is only valid in the
     * current parser state (or until the parser is advanced to a next state).
     *
     * @return a non-null location corresponding to the current parser state
     * in SOSI input source
     */
    SosiLocation getLocation();

    /**
     * Method for enabling specified parser feature
     * (check {@link Feature} for list of features)
     */
    SosiParser enable(Feature feature);

    /**
     * Method for disabling specified  feature
     * (check {@link Feature} for list of features)
     */
    SosiParser disable(Feature feature);

    /**
     * Method for checking whether specified {@link Feature} is enabled.
     */
    boolean isEnabled(Feature feature);

    /**
     * Closes this parser and frees any resources associated with the
     * parser. This method closes the underlying input source.
     *
     * @throws no.vegvesen.nvdb.sosi.SosiException if an i/o error occurs (IOException
     * would be cause of SosiException)
     */
    @Override
    void close();

    /**
     * Enumeration that defines all on/off features for parser implementations.
     */
    enum Feature {

        /**
         * Feature that determines whether parser will allow elements
         * with no subelements and no values.
         */
        ALLOW_EMPTY_ELEMENTS(false);

        /**
         * Whether feature is enabled or disabled by default.
         */
        private final boolean defaultState;

        /**
         * Method that calculates bit set (flags) of all features that
         * are enabled by default.
         */
        public static int collectDefaults()
        {
            int flags = 0;
            for (Feature f : values()) {
                if (f.enabledByDefault()) {
                    flags |= f.getMask();
                }
            }
            return flags;
        }

        Feature(boolean defaultState) {
            this.defaultState = defaultState;
        }

        public boolean enabledByDefault() { return defaultState; }

        public int getMask() { return (1 << ordinal()); }
    }
}
