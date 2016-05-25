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
package no.vegvesen.nvdb.sosi.document;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * An immutable SOSI number value.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public interface SosiNumber extends SosiValue {

    /**
     * Returns true if this SOSI number is a integral number. This method
     * semantics are defined using {@code bigDecimalValue().scale()}. If the
     * scale is zero, then it is considered integral type. This integral type
     * information can be used to invoke an appropriate accessor method to
     * obtain a numeric value as in the following example:
     *
     * <pre>
     * <code>
     * SosiNumber num = ...
     * if (num.isIntegral()) {
     *     num.longValue();     // or other methods to get integral value
     * } else {
     *     num.doubleValue();   // or other methods to get decimal number value
     * }
     * </code>
     * </pre>
     *
     * @return true if this number is a integral number, otherwise false
     */
    boolean isIntegral();

    /**
     * Returns this SOSI number as an {@code int}. Note that this conversion
     * can lose information about the overall magnitude and precision of the
     * number value as well as return a result with the opposite sign.
     *
     * @return an {@code int} representation of the SOSI number
     * @see java.math.BigDecimal#intValue()
     */
    int intValue();

    /**
     * Returns this SOSI number as an {@code int}.
     *
     * @return an {@code int} representation of the SOSI number
     * @throws ArithmeticException if the number has a nonzero fractional
     *         part or if it does not fit in an {@code int}
     * @see java.math.BigDecimal#intValueExact()
     */
    int intValueExact();

    /**
     * Returns this SOSI number as a {@code long}. Note that this conversion
     * can lose information about the overall magnitude and precision of the
     * number value as well as return a result with the opposite sign.
     *
     * @return a {@code long} representation of the SOSI number.
     * @see java.math.BigDecimal#longValue()
     */
    long longValue();

    /**
     * Returns this SOSI number as a {@code long}.
     *
     * @return a {@code long} representation of the SOSI number
     * @throws ArithmeticException if the number has a non-zero fractional
     *         part or if it does not fit in a {@code long}
     * @see java.math.BigDecimal#longValueExact()
     */
    long longValueExact();

    /**
     * Returns this SOSI number as a {@link java.math.BigInteger} object. This is a
     * a convenience method for {@code bigDecimalValue().toBigInteger()}.
     * Note that this conversion can lose information about the overall
     * magnitude and precision of the number value as well as return a result
     * with the opposite sign.
     *
     * @return a {@code BigInteger} representation of the SOSI number.
     * @see java.math.BigDecimal#toBigInteger()
     */
    BigInteger bigIntegerValue();

    /**
     * Returns this SOSI number as a {@link java.math.BigDecimal} object. This is a
     * convenience method for {@code bigDecimalValue().toBigIntegerExact()}.
     *
     * @return a {@link BigInteger} representation of the SOSI number
     * @throws ArithmeticException if the number has a nonzero fractional part
     * @see java.math.BigDecimal#toBigIntegerExact()
     */
    BigInteger bigIntegerValueExact();

    /**
     * Returns this SOSI number as a {@code double}. This is a
     * a convenience method for {@code bigDecimalValue().doubleValue()}.
     * Note that this conversion can lose information about the overall
     * magnitude and precision of the number value as well as return a result
     * with the opposite sign.
     *
     * @return a {@code double} representation of the SOSI number
     * @see java.math.BigDecimal#doubleValue()
     */
    double doubleValue();

    /**
     * Returns this SOSI number as a {@link java.math.BigDecimal} object.
     *
     * @return a {@link java.math.BigDecimal} representation of the SOSI number
     */
    BigDecimal bigDecimalValue();

    /**
     * Returns a SOSI text representation of the SOSI number. The
     * representation is equivalent to {@link BigDecimal#toString()}.
     *
     * @return SOSI text representation of the number
     */
    @Override
    String toString();

    /**
     * Compares the specified object with this {@code SosiNumber} object for
     * equality. Returns {@code true} if and only if the type of the specified
     * object is also {@code SosiNumber} and their {@link #bigDecimalValue()}
     * objects are <i>equal</i>
     *
     * @param obj the object to be compared for equality with
     *      this {@code SosiNumber}
     * @return {@code true} if the specified object is equal to this
     *      {@code SosiNumber}
     */
    @Override
    boolean equals(Object obj);

    /**
     * Returns the hash code value for this {@code SosiNumber} object.  The
     * hash code of a {@code SosiNumber} object is defined as the hash code of
     * its {@link #bigDecimalValue()} object.
     *
     * @return the hash code value for this {@code SosiNumber} object
     */
    @Override
    int hashCode();
}
