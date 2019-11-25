package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.SosiLocation;
import no.vegvesen.nvdb.sosi.document.SosiDate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.requireNonNull;

/**
 * Implements a SOSI date.
 *
 * @author Anders Sørenmo Påsche (Kantega AS)
 */
public class SosiDateImpl implements SosiDate {
    private final LocalDate value;
    private final SosiLocation location;
    private static final DateTimeFormatter SOSI_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    static SosiDate of(LocalDate value, SosiLocation location) {
        return new SosiDateImpl(value, location);
    }

    SosiDateImpl(LocalDate value, SosiLocation location) {
        this.value = requireNonNull(value, "value can't be null");
        this.location = requireNonNull(location, "location can't be null");
    }

    @Override
    public String getDate() {
        return value.format(SOSI_DATE_FORMATTER);
    }

    @Override
    public ValueType getValueType() {
        return ValueType.DATE;
    }

    @Override
    public SosiLocation getLocation() {
        return location;
    }

    @Override
    public String getString() {
        return getDate();
    }

    @Override
    public String toString() {
        return getString();
    }

    @Override
    public int hashCode() {
        return getDate().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SosiDate)) {
            return false;
        }
        SosiDate other = (SosiDate) obj;
        return getDate().equals(other.getDate());
    }
}
