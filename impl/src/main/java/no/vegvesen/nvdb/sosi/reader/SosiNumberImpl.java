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

import no.vegvesen.nvdb.sosi.document.SosiNumber;
import no.vegvesen.nvdb.sosi.SosiLocation;

import java.math.BigDecimal;
import java.math.BigInteger;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

/**
 * Implements a SOSI number.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
abstract class SosiNumberImpl implements SosiNumber {
    private final SosiLocation location;

    static SosiNumber of(int num, SosiLocation location) {
        return new SosiIntNumber(num, location);
    }

    static SosiNumber of(long num, SosiLocation location) {
        return new SosiLongNumber(num, location);
    }

    static SosiNumber of(BigInteger value, SosiLocation location) {
        return new SosiBigDecimalNumber(new BigDecimal(value), location);
    }

    static SosiNumber of(double value, SosiLocation location) {
        return new SosiBigDecimalNumber(BigDecimal.valueOf(value), location);
    }

    static SosiNumber of(BigDecimal value, SosiLocation location) {
        return new SosiBigDecimalNumber(value, location);
    }

    SosiNumberImpl(SosiLocation location) {
        this.location = requireNonNull(location, "location can't be null");
    }

    // Optimized SosiNumber impl for int numbers.
    private static final class SosiIntNumber extends SosiNumberImpl {
        private final int num;
        private BigDecimal bigDecimal;  // assigning it lazily on demand

        SosiIntNumber(int num, SosiLocation location) {
            super(location);
            this.num = num;
        }

        @Override
        public String getString() {
            return Integer.toString(num);
        }

        @Override
        public boolean isIntegral() {
            return true;
        }

        @Override
        public int intValue() {
            return num;
        }

        @Override
        public int intValueExact() {
            return num;
        }

        @Override
        public long longValue() {
            return num;
        }

        @Override
        public long longValueExact() {
            return num;
        }

        @Override
        public double doubleValue() {
            return num;
        }

        @Override
        public BigDecimal bigDecimalValue() {
            // reference assignments are atomic. At the most some more temp
            // BigDecimal objects are created
            BigDecimal bd = bigDecimal;
            if (isNull(bd)) {
                bigDecimal = bd = new BigDecimal(num);
            }
            return bd;
        }

        @Override
        public String toString() {
            return Integer.toString(num);
        }
    }

    // Optimized SosiNumber impl for long numbers.
    private static final class SosiLongNumber extends SosiNumberImpl {
        private final long num;
        private BigDecimal bigDecimal;  // assigning it lazily on demand

        SosiLongNumber(long num, SosiLocation location) {
            super(location);
            this.num = num;
        }

        @Override
        public String getString() {
            return Long.toString(num);
        }

        @Override
        public boolean isIntegral() {
            return true;
        }

        @Override
        public long longValue() {
            return num;
        }

        @Override
        public long longValueExact() {
            return num;
        }

        @Override
        public double doubleValue() {
            return num;
        }

        @Override
        public BigDecimal bigDecimalValue() {
            // reference assignments are atomic. At the most some more temp
            // BigDecimal objects are created
            BigDecimal bd = bigDecimal;
            if (isNull(bd)) {
                bigDecimal = bd = new BigDecimal(num);
            }
            return bd;
        }

        @Override
        public String toString() {
            return Long.toString(num);
        }
    }

    // SosiNumber impl using BigDecimal numbers.
    private static final class SosiBigDecimalNumber extends SosiNumberImpl {
        private final BigDecimal bigDecimal;

        SosiBigDecimalNumber(BigDecimal value, SosiLocation location) {
            super(location);
            this.bigDecimal = value;
        }

        @Override
        public String getString() {
            return bigDecimal.toString();
        }

        @Override
        public BigDecimal bigDecimalValue() {
            return bigDecimal;
        }

    }

    @Override
    public boolean isIntegral() {
        return bigDecimalValue().scale() == 0;
    }

    @Override
    public int intValue() {
        return bigDecimalValue().intValue();
    }

    @Override
    public int intValueExact() {
        return bigDecimalValue().intValueExact();
    }

    @Override
    public long longValue() {
        return bigDecimalValue().longValue();
    }

    @Override
    public long longValueExact() {
        return bigDecimalValue().longValueExact();
    }

    @Override
    public double doubleValue() {
        return bigDecimalValue().doubleValue();
    }

    @Override
    public BigInteger bigIntegerValue() {
        return bigDecimalValue().toBigInteger();
    }

    @Override
    public BigInteger bigIntegerValueExact() {
        return bigDecimalValue().toBigIntegerExact();
    }

    @Override
    public ValueType getValueType() {
        return ValueType.NUMBER;
    }

    @Override
    public SosiLocation getLocation() {
        return location;
    }

    @Override
    public int hashCode() {
        return bigDecimalValue().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SosiNumber)) {
            return false;
        }
        SosiNumber other = (SosiNumber)obj;
        return bigDecimalValue().equals(other.bigDecimalValue());
    }

    @Override
    public String toString() {
        return getString();
    }
}
