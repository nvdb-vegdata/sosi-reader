package no.vegvesen.nvdb.sosi.document;

/**
 * An immutable SOSI datetime value.
 *
 * @author Anders Sørenmo Påsche (Kantega AS)
 */
public interface SosiDateTime extends SosiValue {
    String getDateTime();

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);

    @Override
    String toString();
}