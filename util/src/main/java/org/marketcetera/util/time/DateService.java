package org.marketcetera.util.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/* $License$ */

/**
 * Provides date-related services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class DateService
{
    /**
     * Converts from {@link Date} types to {@link LocalDateTime} types.
     *
     * @param inDate a <code>Date</code> value
     * @return a <code>LocalDateTime</code> value
     */
    public static LocalDateTime toLocalDateTime(Date inDate)
    {
        return LocalDateTime.ofInstant(inDate.toInstant(),
                                       ZoneId.systemDefault());
    }
    /**
     * Converts from {@link Date} types to {@link LocalDateTime} types.
     *
     * @param inDate a <code>Date</code> value
     * @return a <code>LocalDateTime</code> value
     */
    public static LocalDateTime toUtcDateTime(Date inDate)
    {
        return LocalDateTime.ofInstant(inDate.toInstant(),
                                       zoneUTC);
    }
    /**
     * Converts from {@link Date} types to {@link LocalDate} types.
     *
     * @param inDate a <code>Date</code> value
     * @return a <code>LocalDate</code> value
     */
    public static LocalDate toLocalDate(Date inDate)
    {
        return toLocalDateTime(inDate).toLocalDate();
    }
    /**
     * Converts from {@link Date} types to {@link LocalDate} types.
     *
     * @param inDate a <code>Date</code> value
     * @return a <code>LocalDate</code> value
     */
    public static LocalDate toUtcDate(Date inDate)
    {
        return toUtcDateTime(inDate).toLocalDate();
    }
    /**
     * Converts from {@link Date} types to {@link LocalTime} types.
     *
     * @param inDate a <code>Date</code> value
     * @return a <code>LocalTime</code> value
     */
    public static LocalTime toLocalTime(Date inDate)
    {
        return toLocalDateTime(inDate).toLocalTime();
    }
    /**
     * Converts from {@link Date} types to {@link LocalTime} types.
     *
     * @param inDate a <code>Date</code> value
     * @return a <code>LocalTime</code> value
     */
    public static LocalTime toUtcTime(Date inDate)
    {
        return toUtcDateTime(inDate).toLocalTime();
    }
    /**
     * Converts from millis since epoch to {@link LocalDateTime}.
     *
     * @param inMillis a <code>long</code> value
     * @return a <code>LocalDateTime</code> value
     */
    public static LocalDateTime toLocalDateTime(long inMillis)
    {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(inMillis),
                                       ZoneId.systemDefault());
    }
    /**
     * Converts from millis since epoch to {@link LocalDateTime}.
     *
     * @param inMillis a <code>long</code> value
     * @return a <code>LocalDateTime</code> value
     */
    public static LocalDateTime toUtcDateTime(long inMillis)
    {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(inMillis),
                                       zoneUTC);
    }
    /**
     * Convert the given {@link LocalDateTime} to milliseconds since epoch.
     *
     * @param inTimestamp a <code>LocalDateTime</code> value
     * @return a <code>long</code> value
     */
    public static long toLocalEpochMillis(LocalDateTime inTimestamp)
    {
        return inTimestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
    /**
     * Convert the given {@link LocalDateTime} to milliseconds since epoch.
     *
     * @param inTimestamp a <code>LocalDateTime</code> value
     * @return a <code>long</code> value
     */
    public static long toUtcEpochMillis(LocalDateTime inTimestamp)
    {
        return inTimestamp.atZone(zoneUTC).toInstant().toEpochMilli();
    }
    /**
     * Convert the given {@link LocalDateTime} to a {@link Date} value.
     *
     * @param inTimestamp a <code>LocalDateTime</code> value
     * @return a <code>Date</code> value
     */
    public static Date toLocalDate(LocalDateTime inTimestamp)
    {
        return new Date(toLocalEpochMillis(inTimestamp));
    }
    /**
     * Convert the given {@link LocalDateTime} to a {@link Date} value.
     *
     * @param inTimestamp a <code>LocalDateTime</code> value
     * @return a <code>Date</code> value
     */
    public static Date toUtcDate(LocalDateTime inTimestamp)
    {
        return new Date(toUtcEpochMillis(inTimestamp));
    }
    /**
     * Convert the given {@link LocalDate} to a {@link Date} value.
     *
     * @param inUtcDate a <code>LocalDate</code> value
     * @return a <code>Date</code> value
     */
    public static Date toUtcDate(LocalDate inUtcDate)
    {
        LocalDateTime localDateTime = LocalDateTime.of(inUtcDate,
                                                       LocalTime.MIDNIGHT);
        return toUtcDate(localDateTime);
    }
    /**
     * Convert the given {@link LocalTime} to a {@link Date} value.
     *
     * @param inUtcTime a <code>LocalTime</code> value
     * @return a <code>Date</code> value
     */
    public static Date toUtcDate(LocalTime inUtcTime)
    {
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(),
                                                       inUtcTime);
        return toUtcDate(localDateTime);
    }
    /**
     * Convert the given {@link Calendar} to a {@link LocalDateTime} value.
     *
     * @param inTimestamp a <code>Calendar</code> value
     * @return a <code>LocalDateTime</code> value
     */
    public static LocalDateTime toLocalDateTime(Calendar inTimestamp)
    {
        return toLocalDateTime(inTimestamp.getTime());
    }
    /**
     * UTC {@link ZoneId}
     */
    public static final ZoneId zoneUTC = ZoneId.of("UTC");
}
