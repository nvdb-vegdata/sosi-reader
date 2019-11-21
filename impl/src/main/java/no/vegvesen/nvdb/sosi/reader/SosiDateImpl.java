package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.SosiLocation;
import no.vegvesen.nvdb.sosi.document.SosiDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.requireNonNull;

/**
 * Implements a SOSI date/datetime.
 *
 * @author Anders Sørenmo Påsche (Kantega AS)
 */
public class SosiDateImpl implements SosiDate {
    private final LocalDateTime value;
    private final SosiLocation location;
    private static final DateTimeFormatter SOSI_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter SOSI_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    static SosiDate of(LocalDateTime value, SosiLocation location) {
        return new SosiDateImpl(value, location);
    }

    SosiDateImpl(LocalDateTime value, SosiLocation location) {
        this.value = requireNonNull(value, "value can't be null");
        this.location = requireNonNull(location, "location can't be null");
    }

    @Override
    public String getDate() {
        return value.format(SOSI_DATE_FORMATTER);
    }

    @Override
    public String getDateTime() {
        return value.format(SOSI_DATETIME_FORMATTER);
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
        if(value.toLocalTime().equals(LocalTime.MIDNIGHT)) return getDate();
        else return getDateTime();
    }

    @Override
    public String toString() {
        return getString();
    }

    @Override
    public int hashCode() {
        return getDateTime().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SosiDate)) {
            return false;
        }
        SosiDate other = (SosiDate) obj;
        return getDateTime().equals(other.getDateTime());
    }
}
