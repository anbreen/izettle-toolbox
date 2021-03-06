package com.izettle.java;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.WeekFields;

public class InstantAdjusters {

    private InstantAdjusters() {
    }

    /**
     * Truncates an instant to the supplied ChronoUnit.<br>
     * <br>
     * For example calling method with {@link ChronoUnit#HOURS} and using it to adjust the
     * instant 2016-02-24T12:12:55.854Z will adjust the instant into 2016-02-24T12:00:00.000Z <br>
     * If the the instant falls within a offset overlap, for example daylight changes, the earlier
     * offset will be choosen.
     *
     * <br>
     * Adjusting an instant weeks will use the ISO-8601 standard where weeks start on Mondays
     */
    public static TemporalAdjuster truncationBy(ChronoUnit chronoUnit, ZoneId zoneId) {
        requireNonNull(zoneId);
        requireNonNull(chronoUnit);

        if (chronoUnit.compareTo(ChronoUnit.YEARS) > 0) {
            throw new UnsupportedTemporalTypeException("Only ChronoUnits equal or smaller than ChronoUnit.YEARS are supported");
        }

        return temporal -> {
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.from(temporal), zoneId);
            if (chronoUnit.compareTo(ChronoUnit.DAYS) <= 0) {
                zonedDateTime = zonedDateTime.truncatedTo(chronoUnit);
                return temporal.with(zonedDateTime.toInstant());
            } else {
                zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.DAYS);
                if (chronoUnit.compareTo(ChronoUnit.WEEKS) == 0) {
                    zonedDateTime = zonedDateTime.with(WeekFields.ISO.dayOfWeek(), 1);
                }

                if (chronoUnit.compareTo(ChronoUnit.MONTHS) >= 0) {
                    zonedDateTime = zonedDateTime.with(firstDayOfMonth());
                }

                if (chronoUnit.compareTo(ChronoUnit.YEARS) == 0) {
                    zonedDateTime = zonedDateTime.with(firstDayOfYear());
                }

                final Instant truncatedInstant = zonedDateTime
                    .toLocalDate()
                    .atStartOfDay()
                    .atZone(zonedDateTime.getZone())
                    .withEarlierOffsetAtOverlap()
                    .toInstant();

                return temporal.with(truncatedInstant);
            }
        };
    }

    /**
     * Utility method for easily adding time to an instant. This method acts as a complement to the standard
     * <code>Instant::plus</code> method which cannot take larger chrono units than DAY.
     * @param zoneId The time zone to be taken into consideration when adding
     * @param quantity the number of units to add
     * @param chronoUnit The chrono unit to add
     * @return a newly created TemporalAdjuster
     */
    public static TemporalAdjuster additionBy(final long quantity, final ChronoUnit chronoUnit, final ZoneId zoneId) {
        requireNonNull(zoneId);
        requireNonNull(chronoUnit);
        return temporal -> ZonedDateTime
            .ofInstant(Instant.from(temporal), zoneId)
            .plus(quantity, chronoUnit)
            .toInstant();
    }

    /**
     * Utility method for easily subtracting time from an instant. This method acts as a complement to the standard
     * <code>Instant::minus</code> method which cannot take larger chrono units than DAY.
     * @param zoneId The time zone to be taken into consideration when subtracting
     * @param quantity the number of units to subtract
     * @param chronoUnit The chrono unit to subtract
     * @return a newly created TemporalAdjuster
     */
    public static TemporalAdjuster subtractionBy(final long quantity, final ChronoUnit chronoUnit, final ZoneId zoneId) {
        return additionBy(-quantity, chronoUnit, zoneId);
    }
}
