// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiValue;
import no.vegvesen.nvdb.sosi.SosiLocation;

import java.math.BigDecimal;
import java.math.BigInteger;


public interface SosiElementBuilder {

    SosiElementBuilder addValue(SosiValue value);

    SosiElementBuilder addIslandValue(SosiValue value);

    SosiElementBuilder addValue(String value, SosiLocation location);

    SosiElementBuilder concatValue(String value, SosiLocation location);

    SosiElementBuilder addValue(BigInteger value, SosiLocation location);

    SosiElementBuilder addValue(BigDecimal value, SosiLocation location);

    SosiElementBuilder addValue(int value, SosiLocation location);

    SosiElementBuilder addValue(long value, SosiLocation location);

    SosiElementBuilder addValue(double value, SosiLocation location);

    SosiElementBuilder addSubElement(String name, SosiElement subElement);

    SosiElement build();
}