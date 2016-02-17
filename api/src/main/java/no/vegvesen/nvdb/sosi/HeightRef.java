// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2016 Statens vegvesen
// ALL RIGHTS RESERVED
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
