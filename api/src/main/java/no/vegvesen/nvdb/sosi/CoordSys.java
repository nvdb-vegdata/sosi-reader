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
package no.vegvesen.nvdb.sosi;

import java.util.Arrays;
import java.util.Optional;

/**
 * Defines coordinate reference systems as used with the KOORDSYS (SYSKODE) element in SOSI files
 *
 * Source: http://kartverket.no/globalassets/standard/sosi-standarden-del-1-og-2/sosi-standarden/del1_2_realiseringsosigml_45_20120608.pdf
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public enum CoordSys {
    // NGO1948
    NGO1948_AXIS_I_GAUSSKRÜGER(1, 27391),    // NGO-akse I, datum NGO1948, projeksjon Gauss-Krüger EPSG 27391
    NGO1948_AXIS_II_GAUSSKRÜGER(2, 27392),   // NGO-akse II, datum NGO1948, projeksjon Gauss-Krüger EPSG 27392
    NGO1948_AXIS_III_GAUSSKRÜGER(3, 27393),  // NGO-akse III, datum NGO1948, projeksjon Gauss-Krüger EPSG 27393
    NGO1948_AXIS_IV_GAUSSKRÜGER(4, 27394),   // NGO-akse IV, datum NGO1948, projeksjon Gauss-Krüger EPSG 27394
    NGO1948_AXIS_V_GAUSSKRÜGER(5, 27395),    // NGO-akse V, datum NGO1948, projeksjon Gauss-Krüger EPSG 27395
    NGO1948_AXIS_VI_GAUSSKRÜGER(6, 27396),   // NGO-akse VI, datum NGO1948, projeksjon Gauss-Krüger EPSG 27396
    NGO1948_AXIS_VII_GAUSSKRÜGER(7, 27397),  // NGO-akse VII, datum NGO1948, projeksjon Gauss-Krüger EPSG 27397
    NGO1948_AXIS_VIII_GAUSSKRÜGER(8, 27398), // NGO-akse VIII, datum NGO1948, projeksjon Gauss-Krüger EPSG 27398
    NGO1948_GEOGRAPHICAL(9, 4817),           // Ingen projeksjon EPSG 4817

    // WGS84
    WGS84_UTM29(59, 32629),        // UTM Sone 29 basert på WGS84, 2d (horisontal), EPSG 32629
    WGS84_UTM30(60, 32630),        // UTM Sone 30 basert på WGS84, 2d (horisontal), EPSG 32630
    WGS84_UTM31(61, 32631),        // UTM Sone 31 basert på WGS84, 2d (horisontal), EPSG 32631
    WGS84_UTM32(62, 32632),        // UTM Sone 32 basert på WGS84, 2d (horisontal), EPSG 32632
    WGS84_UTM33(63, 32633),        // UTM Sone 33 basert på WGS84, 2d (horisontal), EPSG 32633
    WGS84_UTM34(64, 32634),        // UTM Sone 34 basert på WGS84, 2d (horisontal), EPSG 32634
    WGS84_UTM35(65, 32635),        // UTM Sone 35 basert på WGS84, 2d (horisontal), EPSG 32635
    WGS84_UTM36(66, 32636),        // UTM Sone 36 basert på WGS84, 2d (horisontal), EPSG 32636
    WGS84_GEOGRAPHICAL(184, 4326), // WGS84 Geografisk 2d, ingen projeksjon, EPSG 4326

    // ED50
    ED50_UTM31(31, 23031),     // UTM sone 31 basert på ED50 EPSG 23031
    ED50_UTM32(32, 23032),     // UTM sone 32 basert på ED50 EPSG 23032
    ED50_UTM33(33, 23033),     // UTM sone 33 basert på ED50 EPSG 23033
    ED50_UTM34(34, 23034),     // UTM sone 34 basert på ED50 EPSG 23034
    ED50_UTM35(35, 23035),     // UTM sone 35 basert på ED50 EPSG 23035
    ED50_UTM36(36, 23036),     // UTM sone 36 basert på ED50 EPSG 23036
    ED50_GEOGRAPHICAL(50, -1), // ED 50 Geografisk, ingen projeksjon

    // EUREF89 / ETRS89
    EUREF89_UTM29(19, 25829),       // UTM sone 29, 2d basert på EUREF89, 2d, EPSG 25829. Ref INSPIRE Req 7 (ETRS89-TM29)
    EUREF89_UTM30(20, 25830),       // UTM sone 30, 2d basert på EUREF89, 2d, EPSG 25830. Ref INSPIRE Req 7 (ETRS89-TM30)
    EUREF89_UTM31(21, 25831),       // Ref INSPIRE Req 7 (ETRS89-TM31) EPSG 25831
    EUREF89_UTM32(22, 25832),       // Ref INSPIRE Req 7 (ETRS89-TM32) EPSG 25832
    EUREF89_UTM33(23, 25833),       // Ref INSPIRE Req 7 (ETRS89-TM33)) EPSG 25833
    EUREF89_UTM34(24, 25834),       // Brukes vanligvis ikke i Norge fra 1998. EPSG 25834. Ref INSPIRE Req 7 (ETRS89-TM34)
    EUREF89_UTM35(25, 25835),       // Ref INSPIRE Req 7 (ETRS89-TM35)) EPSG 25835
    EUREF89_UTM36(26, 25836),       // Ref INSPIRE Req 7 (ETRS89-TM36)) EPSG 25836
    EUREF89_GEOGRAPHICAL(84, 4258), // Ingen projeksjon. EPSG 4258 (bredde, lengde), EPSG 4937 (bredde, lengde, ellipsoidisk høyde). Ref: INSPIRE Req 1

    // EUREF89 NTM
    EUREF89_NTM05(205, 5105), // Norsk Transversal Mercator sone 5 (basert på EUREF89) EPSG 5105
    EUREF89_NTM06(206, 5106), // Norsk Transversal Mercator sone 6 (basert på EUREF89) EPSG 5106
    EUREF89_NTM07(207, 5107), // Norsk Transversal Mercator sone 7 (basert på EUREF89) EPSG 5107
    EUREF89_NTM08(208, 5108), // Norsk Transversal Mercator sone 8 (basert på EUREF89) EPSG 5108
    EUREF89_NTM09(209, 5109), // Norsk Transversal Mercator sone 9 (basert på EUREF89) EPSG 5109
    EUREF89_NTM10(210, 5110), // Norsk Transversal Mercator sone 10 (basert på EUREF89) EPSG 5110
    EUREF89_NTM11(211, 5111), // Norsk Transversal Mercator sone 11 (basert på EUREF89) EPSG 5111
    EUREF89_NTM12(212, 5112), // Norsk Transversal Mercator sone 12 (basert på EUREF89) EPSG 5112
    EUREF89_NTM13(213, 5113), // Norsk Transversal Mercator sone 13 (basert på EUREF89) EPSG 5113
    EUREF89_NTM14(214, 5114), // Norsk Transversal Mercator sone 14 (basert på EUREF89) EPSG 5114
    EUREF89_NTM15(215, 5115), // Norsk Transversal Mercator sone 15 (basert på EUREF89) EPSG 5115
    EUREF89_NTM16(216, 5116), // Norsk Transversal Mercator sone 16 (basert på EUREF89) EPSG 5116
    EUREF89_NTM17(217, 5117), // Norsk Transversal Mercator sone 17 (basert på EUREF89) EPSG 5117
    EUREF89_NTM18(218, 5118), // Norsk Transversal Mercator sone 18 (basert på EUREF89) EPSG 5118
    EUREF89_NTM19(219, 5119), // Norsk Transversal Mercator sone 19 (basert på EUREF89) EPSG 5119
    EUREF89_NTM20(220, 5120), // Norsk Transversal Mercator sone 20 (basert på EUREF89) EPSG 5120
    EUREF89_NTM21(221, 5121), // Norsk Transversal Mercator sone 21 (basert på EUREF89) EPSG 5121
    EUREF89_NTM22(222, 5122), // Norsk Transversal Mercator sone 22 (basert på EUREF89) EPSG 5122
    EUREF89_NTM23(223, 5123), // Norsk Transversal Mercator sone 23 (basert på EUREF89) EPSG 5123
    EUREF89_NTM24(224, 5124), // Norsk Transversal Mercator sone 24 (basert på EUREF89) EPSG 5124
    EUREF89_NTM25(225, 5125), // Norsk Transversal Mercator sone 25 (basert på EUREF89) EPSG 5125
    EUREF89_NTM26(226, 5126), // Norsk Transversal Mercator sone 26 (basert på EUREF89) EPSG 5126
    EUREF89_NTM27(227, 5127), // Norsk Transversal Mercator sone 27 (basert på EUREF89) EPSG 5127
    EUREF89_NTM28(228, 5128), // Norsk Transversal Mercator sone 28 (basert på EUREF89) EPSG 5128
    EUREF89_NTM29(229, 5129), // Norsk Transversal Mercator sone 29 (basert på EUREF89) EPSG 5129
    EUREF89_NTM30(230, 5130); // Norsk Transversal Mercator sone 30 (basert på EUREF89) EPSG 5130

    int sosiValue; // Value used for KOORDSYS (SYSKODE) in SOSI files
    int srid;

    public static CoordSys fromSosiValue(int sosiValue) {
        return Arrays.stream(CoordSys.values())
                .filter(cs -> cs.sosiValue == sosiValue)
                .findFirst()
                .orElseThrow(() -> new SosiException("Could not map SOSI value %s to a value of %s", sosiValue, CoordSys.class.getSimpleName()));
    }

    public static CoordSys fromSrid(int srid) {
        return Arrays.stream(CoordSys.values())
                .filter(cs -> cs.srid == srid)
                .findFirst()
                .orElseThrow(() -> new SosiException("Could not map SRID value %s to a value of %s", srid, CoordSys.class.getSimpleName()));
    }

    CoordSys(int sosiValue, int srid) {
        this.sosiValue = sosiValue;
        this.srid = srid;
    }

    public int getSosiValue() {
        return sosiValue;
    }

    public Optional<Integer> getSrid() {
        return srid > 0 ? Optional.of(srid) : Optional.empty();
    }
}
