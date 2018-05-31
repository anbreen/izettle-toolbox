package com.izettle.java;

import static com.izettle.java.InstantAdjusters.additionBy;
import static com.izettle.java.InstantAdjusters.subtractionBy;
import static com.izettle.java.InstantAdjusters.truncationBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import org.junit.Test;

public class InstantAdjustersTest {

    private static final ZoneId ZONE_ID = ZoneId.of("UTC");

    @Test
    public void itShouldTruncateNanoseconds() throws Exception {
        final Instant instant = Instant.parse("2016-02-01T20:50:10Z").with(ChronoField.NANO_OF_SECOND, 50);
        final Instant actual = instant.with(truncationBy(ChronoUnit.NANOS, ZONE_ID));
        final Instant expected = Instant.parse("2016-02-01T20:50:10Z").with(ChronoField.NANO_OF_SECOND, 50);

        assertThat(actual.compareTo(expected)).isEqualTo(0);
    }

    @Test
    public void itShouldTruncateMilliseconds() throws Exception {
        final Instant instant = Instant.parse("2016-02-01T20:50:10Z").with(ChronoField.NANO_OF_SECOND, 50);
        final Instant actual = instant.with(truncationBy(ChronoUnit.MILLIS, ZONE_ID));
        final Instant expected = Instant.parse("2016-02-01T20:50:10Z");

        assertThat(actual.compareTo(expected)).isEqualTo(0);
    }

    @Test
    public void itShouldTruncateSeconds() throws Exception {
        final Instant instant = Instant.parse("2016-02-01T20:50:10Z").with(ChronoField.NANO_OF_SECOND, 50);
        final Instant actual = instant.with(truncationBy(ChronoUnit.SECONDS, ZONE_ID));
        final Instant expected = Instant.parse("2016-02-01T20:50:10Z");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void itShouldTruncateMinutes() throws Exception {
        final Instant instant = Instant.parse("2016-02-01T20:50:10Z");
        final Instant actual = instant.with(truncationBy(ChronoUnit.MINUTES, ZONE_ID));
        final Instant expected = Instant.parse("2016-02-01T20:50:00Z");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void itShouldTruncateHours() throws Exception {
        final Instant instant = Instant.parse("2016-02-01T20:50:01Z");
        final Instant actual = instant.with(truncationBy(ChronoUnit.HOURS, ZONE_ID));
        final Instant expected = Instant.parse("2016-02-01T20:00:00Z");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void itShouldTruncateDays() throws Exception {
        final Instant instant = Instant.parse("2016-02-01T20:50:01Z");
        final Instant actual = instant.with(truncationBy(ChronoUnit.DAYS, ZONE_ID));
        final Instant expected = Instant.parse("2016-02-01T00:00:00Z");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void itShouldTruncateWeeks() throws Exception {
        final Instant instant = Instant.parse("2016-02-10T20:50:01Z");
        final Instant actual = instant.with(truncationBy(ChronoUnit.WEEKS, ZONE_ID));
        final Instant expected = Instant.parse("2016-02-08T00:00:00Z");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void itShouldTruncateYears() throws Exception {
        final Instant instant = Instant.parse("2016-02-03T20:50:01Z");
        final Instant actual = instant.with(truncationBy(ChronoUnit.YEARS, ZONE_ID));
        final Instant expected = Instant.parse("2016-01-01T00:00:00Z");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void itShouldThrowExceptionWhenChronoUnitIsNotSupported() {
        final Throwable thrown = catchThrowable(() -> truncationBy(ChronoUnit.MILLENNIA, ZONE_ID));

        assertThat(thrown).isInstanceOf(UnsupportedTemporalTypeException.class);
    }

    @Test
    public void itShouldAddWeeks() throws Exception {
        final Instant instant = Instant.now();
        final Instant result = instant.with(additionBy(1, ChronoUnit.WEEKS, ZONE_ID));
        final Instant expected = ZonedDateTime
            .ofInstant(instant, ZONE_ID)
            .plus(1, ChronoUnit.WEEKS)
            .toInstant();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void itShouldAddYears() throws Exception {
        final Instant instant = Instant.now();
        final Instant result = instant.with(additionBy(1, ChronoUnit.YEARS, ZONE_ID));
        final Instant expected = ZonedDateTime
            .ofInstant(instant, ZONE_ID)
            .plus(1, ChronoUnit.YEARS)
            .toInstant();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void itShouldSubtractDays() throws Exception {
        final Instant instant = Instant.now();
        final Instant result = instant.with(subtractionBy(1, ChronoUnit.DAYS, ZONE_ID));
        final Instant expected = ZonedDateTime
            .ofInstant(instant, ZONE_ID)
            .minus(1, ChronoUnit.DAYS)
            .toInstant();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void itShouldSubtractMonths() throws Exception {
        final Instant instant = Instant.now();
        final Instant result = instant.with(subtractionBy(2, ChronoUnit.MONTHS, ZONE_ID));
        final Instant expected = ZonedDateTime
            .ofInstant(instant, ZONE_ID)
            .minus(2, ChronoUnit.MONTHS)
            .toInstant();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void itShouldTruncateByMonthAcrossDstOverlap() {
        final ZoneId zoneId = TimeZoneId.AMERICA_SAO_PAULO.toZoneId();
        final Instant beforeOffsetChange = Instant.parse("2017-10-15T02:38:01Z");
        final Instant afterOffsetChange = Instant.parse("2017-10-15T20:38:01Z");
        final Instant expectedInstant = Instant.parse("2017-10-01T03:00:00Z");
        final Instant truncatedBeforeOffsetChange = beforeOffsetChange.with(
            InstantAdjusters.truncationBy(
                ChronoUnit.MONTHS,
                zoneId
        ));
        final Instant truncatedAfterOffsetChange = afterOffsetChange.with(
            InstantAdjusters.truncationBy(
                ChronoUnit.MONTHS,
                zoneId
        ));
        assertThat(truncatedBeforeOffsetChange).isEqualTo(expectedInstant);
        assertThat(truncatedAfterOffsetChange).isEqualTo(expectedInstant);
    }
}
