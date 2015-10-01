// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.writer;

/**
 * Defines line ending character sequences for different operating systems.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public enum LineEnding {
    UNIX("\n"), WINDOWS("\r\n");

    String charSequence;

    LineEnding(String charSequence) {
        this.charSequence = charSequence;
    }

    public String getCharSequence() {
        return charSequence;
    }
}
