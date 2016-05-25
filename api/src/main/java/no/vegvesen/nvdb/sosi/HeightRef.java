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

/**
 * Defines height reference systems as used with the VERT-DATUM element in SOSI files
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public enum HeightRef {
    ELLIPSOID("ELLIP"),  // Ellipsoide jf. KOORDSYS
    LOCAL("LOKAL"),      // Lokal referanseflate
    NKG89("NKG89"),      // Geoide bestemt av NKG i 1989
    NN54("NN54"),        // Norsk Null av 1954. Denne er identisk med NN1954
    NNN57("NNN57"),      // Nord-Norsk Null av 1957. For nyere data er denne gått ut av bruk. Er erstattet av NN54.
    NN2000("NN2000"),    // Norsk Null av 2000. Omregning til/fra NN54 er stedsavhengig og krever grid-fil.
    OSLO("101"),         // Lokalt nett, Oslo
    BÆRUM("102"),        // Lokalt nett, Bærum
    ASKER("103"),        // Lokalt nett, Asker
    LILLEHAMMER("104"),  // Lokalt nett, Lillehammer
    DRAMMEN("105"),      // Lokalt nett, Drammen
    BERGEN("106"),       // Lokalt nett, Bergen/Askøy
    TRONDHEIM("107"),    // Lokalt nett, Trondheim
    BODØ("108"),         // Lokalt nett, Bodø
    KRISTIANSUND("109"), // Lokalt nett, Kristiansund
    ÅLESUND("110");      // Lokalt nett, Ålesund

    String sosiValue; // Value used for VERT-DATUM in SOSI files

    public static HeightRef fromSosiValue(String sosiValue) {
        return Arrays.stream(HeightRef.values())
                .filter(href -> href.sosiValue.equalsIgnoreCase(sosiValue))
                .findFirst()
                .orElseThrow(() -> new SosiException("Could not map SOSI value %s to a value of %s", sosiValue, HeightRef.class.getSimpleName()));
    }

    HeightRef(String sosiValue) {
        this.sosiValue = sosiValue;
    }
}
