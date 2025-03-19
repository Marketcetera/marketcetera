package org.marketcetera.marketdata;

import static org.marketcetera.marketdata.Messages.INVALID_DATE;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Date;

import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Modern date/time utility class using java.time APIs.
 * 
 * <p>This is a modern replacement for {@link DateUtils} which uses
 * the Java 8+ time API instead of Joda-Time.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 4.2.0
 */
@ClassVersion("$Id$")
public class DateTimeUtils {
    // Define formatters directly as static fields
    
    /**
     * Date format to millisecond precision with timezone: <code>yyyyMMdd'T'HHmmssSSSz</code>
     */
    public static final DateTimeFormatter MILLIS_WITH_TZ = DateTimeFormatter
            .ofPattern("yyyyMMdd'T'HHmmssSSS'Z'")
            .withZone(ZoneOffset.UTC);
    
    /**
     * Date format to millisecond precision without timezone: <code>yyyyMMdd'T'HHmmssSSS</code>
     */
    public static final DateTimeFormatter MILLIS = DateTimeFormatter
            .ofPattern("yyyyMMdd'T'HHmmssSSS")
            .withZone(ZoneOffset.UTC);
    
    /**
     * Date format to second precision with timezone: <code>yyyyMMdd'T'HHmmssz</code>
     */
    public static final DateTimeFormatter SECONDS_WITH_TZ = DateTimeFormatter
            .ofPattern("yyyyMMdd'T'HHmmss'Z'")
            .withZone(ZoneOffset.UTC);
    
    /**
     * Date format to second precision without timezone: <code>yyyyMMdd'T'HHmmss</code>
     */
    public static final DateTimeFormatter SECONDS = DateTimeFormatter
            .ofPattern("yyyyMMdd'T'HHmmss")
            .withZone(ZoneOffset.UTC);
    
    /**
     * Date format to minute precision with timezone: <code>yyyyMMdd'T'HHmmz</code>
     */
    public static final DateTimeFormatter MINUTES_WITH_TZ = DateTimeFormatter
            .ofPattern("yyyyMMdd'T'HHmm'Z'")
            .withZone(ZoneOffset.UTC);
    
    /**
     * Date format to minute precision without timezone: <code>yyyyMMdd'T'HHmm</code>
     */
    public static final DateTimeFormatter MINUTES = DateTimeFormatter
            .ofPattern("yyyyMMdd'T'HHmm")
            .withZone(ZoneOffset.UTC);
    
    /**
     * Date format to day-of-month precision with timezone: <code>yyyyMMddz</code>
     */
    public static final DateTimeFormatter DAYS_WITH_TZ = DateTimeFormatter
            .ofPattern("yyyyMMdd'Z'")
            .withZone(ZoneOffset.UTC);
    
    /**
     * Date format to day-of-month precision without timezone: <code>yyyyMMdd</code>
     */
    public static final DateTimeFormatter DAYS = DateTimeFormatter
            .ofPattern("yyyyMMdd")
            .withZone(ZoneOffset.UTC);
    
    /**
     * Date format needed for FIX specified UTCTimestamp type
     */
    public static final DateTimeFormatter FIX = DateTimeFormatter
            .ofPattern("yyyyMMdd-HH:mm:ss")
            .withZone(ZoneOffset.UTC);
    
    /**
     * Date format needed for FIX specified UTCTimestamp type (with millis)
     */
    public static final DateTimeFormatter FIX_MILLIS = DateTimeFormatter
            .ofPattern("yyyyMMdd-HH:mm:ss.SSS")
            .withZone(ZoneOffset.UTC);
    
    /**
     * Valid date formats to try when parsing
     */
    private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[] {
        MILLIS_WITH_TZ,
        MILLIS,
        SECONDS_WITH_TZ,
        SECONDS,
        MINUTES_WITH_TZ,
        MINUTES,
        DAYS_WITH_TZ,
        DAYS
    };
    
    /**
     * Default date format for toString operations
     */
    private static final DateTimeFormatter DEFAULT_FORMAT = MILLIS_WITH_TZ;

    /**
     * Converts the given <code>Date</code> value to a <code>String</code> representation usable with
     * {@link MarketDataRequest} objects.
     * 
     * <p>The format of the returned value is ISO 8601 basic format to millisecond precision with
     * time zone offset.  This format can be expressed as: <code>yyyyMMdd'T'HHmmssSSSXXX</code>.
     *
     * @param inDate a <code>Date</code> value
     * @return a <code>String</code> value
     */
    public static String dateToString(Date inDate) {
        if (inDate == null) {
            return null;
        }
        return instantToString(inDate.toInstant());
    }

    /**
     * Converts the given <code>Instant</code> value to a <code>String</code> representation.
     * 
     * @param instant an <code>Instant</code> value
     * @return a <code>String</code> value
     */
    public static String instantToString(Instant instant) {
        if (instant == null) {
            return null;
        }
        return DEFAULT_FORMAT.format(instant);
    }

    /**
     * Converts the given <code>LocalDateTime</code> value to a <code>String</code> representation.
     * The LocalDateTime is assumed to be in UTC.
     * 
     * @param dateTime a <code>LocalDateTime</code> value
     * @return a <code>String</code> value
     */
    public static String localDateTimeToString(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return DEFAULT_FORMAT.format(dateTime.atZone(ZoneOffset.UTC));
    }

    /**
     * Converts the given <code>ZonedDateTime</code> value to a <code>String</code> representation.
     * 
     * @param dateTime a <code>ZonedDateTime</code> value
     * @return a <code>String</code> value
     */
    public static String zonedDateTimeToString(ZonedDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return DEFAULT_FORMAT.format(dateTime);
    }

    /**
     * Converts the given <code>Date</code> value to a <code>String</code> representation 
     * in the given format.
     * 
     * @param inDate a <code>Date</code> value
     * @param formatter a <code>DateTimeFormatter</code> value
     * @return a <code>String</code> value
     * @throws NullPointerException if formatter is null
     */
    public static String dateToString(Date inDate, DateTimeFormatter formatter) {
        if (inDate == null) {
            return null;
        }
        return formatter.format(inDate.toInstant());
    }

    /**
     * Parses the given <code>String</code> to a <code>Date</code> value.
     * 
     * <p>The given <code>String</code> is expected to be formatted in ISO 8601 basic format.
     * The following formats are accepted:
     * <ul>
     *   <li>yyyyMMdd'T'HHmmssSSSZ (e.g. 20090303T224025444Z)</li>
     *   <li>yyyyMMdd'T'HHmmssSSS (e.g. 20090303T224025444)</li>
     *   <li>yyyyMMdd'T'HHmmssZ (e.g. 20090303T224025Z)</li>
     *   <li>yyyyMMdd'T'HHmmss (e.g. 20090303T224025)</li>
     *   <li>yyyyMMdd'T'HHmmZ (e.g. 20090303T2240Z)</li>
     *   <li>yyyyMMdd'T'HHmm (e.g. 20090303T2240)</li>
     *   <li>yyyyMMddZ (e.g. 20090303Z)</li>
     *   <li>yyyyMMdd (e.g. 20090303)</li>
     * </ul>
     * 
     * <p>If the timezone indicator ('Z') is omitted from the given <code>String</code>, 
     * the date/time is assumed to be in UTC. If time components are omitted, they are set to zero. 
     * Fields may not be abbreviated, i.e., minutes must contain two digits even if the value is 
     * less than ten, <code>01</code> instead of <code>1</code>.
     *
     * @param inDateString a <code>String</code> containing a date value to be parsed.
     * @return a <code>Date</code> value 
     * @throws MarketDataRequestException if the given <code>String</code> could not be parsed 
     */
    public static Date stringToDate(String inDateString) throws MarketDataRequestException {
        if (inDateString == null || inDateString.isEmpty()) {
            throw new MarketDataRequestException(new I18NBoundMessage1P(INVALID_DATE, inDateString));
        }
        
        // Special case for timezone offset formats (+-0000 style) since we're using literal 'Z' in our formatters
        if (inDateString.contains("+") || inDateString.contains("-")) {
            try {
                // Try to parse with a more flexible formatter for offset timezone
                DateTimeFormatter offsetFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssSSSSSSx");
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(inDateString, offsetFormatter);
                return Date.from(zonedDateTime.toInstant());
            } catch (DateTimeParseException e) {
                // Continue with other formatters
            }
        }
        
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                // For formats without time components, we need special handling
                if (formatter == DAYS || formatter == DAYS_WITH_TZ) {
                    String adjustedDateString = inDateString;
                    // Strip Z if present for the DAYS formatter
                    if (formatter == DAYS && adjustedDateString.endsWith("Z")) {
                        adjustedDateString = adjustedDateString.substring(0, adjustedDateString.length() - 1);
                    }
                    LocalDate localDate = LocalDate.parse(adjustedDateString, DAYS);
                    return Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());
                } else {
                    // Try to parse with current formatter
                    LocalDateTime localDateTime;
                    try {
                        localDateTime = LocalDateTime.parse(inDateString, formatter);
                    } catch (DateTimeParseException e) {
                        // Continue to next formatter
                        continue;
                    }
                    return Date.from(localDateTime.atZone(ZoneOffset.UTC).toInstant());
                }
            } catch (DateTimeParseException e) {
                // Try the next format
            }
        }
        
        throw new MarketDataRequestException(new I18NBoundMessage1P(INVALID_DATE, inDateString));
    }

    /**
     * Converts a legacy Date to an Instant
     * 
     * @param date the Date to convert
     * @return the equivalent Instant
     */
    public static Instant toInstant(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant();
    }

    /**
     * Converts a legacy Date to a ZonedDateTime in the UTC timezone
     * 
     * @param date the Date to convert
     * @return the equivalent ZonedDateTime
     */
    public static ZonedDateTime toZonedDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return ZonedDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
    }

    /**
     * Converts an Instant to a legacy Date
     * 
     * @param instant the Instant to convert
     * @return the equivalent Date
     */
    public static Date toDate(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Date.from(instant);
    }

    /**
     * Converts a ZonedDateTime to a legacy Date
     * 
     * @param dateTime the ZonedDateTime to convert
     * @return the equivalent Date
     */
    public static Date toDate(ZonedDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return Date.from(dateTime.toInstant());
    }

    /**
     * Converts a LocalDateTime to a legacy Date (assumes UTC timezone)
     * 
     * @param dateTime the LocalDateTime to convert
     * @return the equivalent Date
     */
    public static Date toDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return Date.from(dateTime.toInstant(ZoneOffset.UTC));
    }

    /**
     * Private constructor to prevent instantiation
     */
    private DateTimeUtils() {
        // Utility class, no instantiation
    }
}