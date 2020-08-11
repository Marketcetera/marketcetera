package org.marketcetera.util.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
     * Convert the given {@link LocalDateTime} to milliseconds since epoch.
     *
     * @param inTimestamp a <code>LocalDateTime</code> value
     * @return a <code>long</code> value
     */
    public static long toEpochMillis(LocalDateTime inTimestamp)
    {
        return inTimestamp.toEpochSecond(ZoneOffset.UTC);
    }
}
