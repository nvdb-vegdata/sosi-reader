package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.SosiLocation;
import no.vegvesen.nvdb.sosi.document.SosiDateTime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.requireNonNull;
/**
 * Implements a SOSI datetime.
 *
 * @author Anders Sørenmo Påsche (Kantega AS)
 */
public class SosiDateTimeImpl  implements SosiDateTime {
    private final LocalDateTime value;
    private final SosiLocation location;
    private static final DateTimeFormatter SOSI_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    static SosiDateTime of(LocalDateTime value, SosiLocation location) {
        return new SosiDateTimeImpl(value, location);
    }

    SosiDateTimeImpl(LocalDateTime value, SosiLocation location) {
        this.value = requireNonNull(value, "value can't be null");
        this.location = requireNonNull(location, "location can't be null");
    }

    @Override
    public String getDateTime() {
        return value.format(SOSI_DATETIME_FORMATTER);
    }

    @Override
    public ValueType getValueType() {
        return ValueType.DATETIME;
    }

    @Override
    public SosiLocation getLocation() {
        return location;
    }

    @Override
    public String getString() {
        return getDateTime();
    }
}
