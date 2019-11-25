package no.vegvesen.nvdb.sosi.document;

/**
 * An immutable SOSI date value.
 *
 * @author Anders Sørenmo Påsche (Kantega AS)
 */
public interface SosiDate extends SosiValue {
    String getDate();

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);

    @Override
    String toString();
}