package org.marketcetera.marketdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;

/**
 * Tests {@link DateTimeUtils}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 4.2.0
 */
public class DateTimeUtilsTest {
    
    /**
     * Tests {@link DateTimeUtils#stringToDate(String)}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void stringToDate() throws Exception {
        // test null and empty strings
        new ExpectedFailure<MarketDataRequestException>(Messages.INVALID_DATE) {
            protected void run() throws Exception {
                DateTimeUtils.stringToDate(null);
            }
        };
        
        new ExpectedFailure<MarketDataRequestException>(Messages.INVALID_DATE) {
            protected void run() throws Exception {
                DateTimeUtils.stringToDate("");
            }
        };
        
        // Test a single definitely invalid date to make sure exception is thrown
        final String invalidDateString = "ThisIsNotADate";
        new ExpectedFailure<MarketDataRequestException>(Messages.INVALID_DATE) {
            protected void run() throws Exception {
                DateTimeUtils.stringToDate(invalidDateString);
            }
        };
        
        // Test a few invalid date formats
        new ExpectedFailure<MarketDataRequestException>(Messages.INVALID_DATE) {
            protected void run() throws Exception {
                DateTimeUtils.stringToDate("20091301"); // Invalid month 13
            }
        };
        
        new ExpectedFailure<MarketDataRequestException>(Messages.INVALID_DATE) {
            protected void run() throws Exception {
                DateTimeUtils.stringToDate("20090132"); // Invalid day 32
            }
        };
        
        // Test a few simple valid strings that should parse correctly
        DateTimeUtils.stringToDate("20200101");
        DateTimeUtils.stringToDate("20200101Z");
        DateTimeUtils.stringToDate("20200101T0000");
        DateTimeUtils.stringToDate("20200101T0000Z");
        
        // Check specific dates and their string representations
        
        // UTC date - March 19, 2009 12:00:00 UTC
        Date date1 = DateTimeUtils.stringToDate("20090319T120000000Z");
        assertEquals("20090319T120000000Z", DateTimeUtils.dateToString(date1));
        
        // no TZ (assumed to be UTC) - March 19, 1988 00:00:00 UTC
        Date date3 = DateTimeUtils.stringToDate("19880319T0000");
        assertEquals("19880319T000000000Z", DateTimeUtils.dateToString(date3));
    }
    
    /**
     * Tests conversion methods between java.util.Date and java.time types.
     * 
     * @throws Exception if an error occurs
     */
    @Test
    public void conversionMethods() throws Exception {
        // Test null handling
        assertNull(DateTimeUtils.toInstant(null));
        assertNull(DateTimeUtils.toZonedDateTime(null));
        assertNull(DateTimeUtils.toDate((Instant) null));
        assertNull(DateTimeUtils.toDate((ZonedDateTime) null));
        assertNull(DateTimeUtils.toDate((LocalDateTime) null));
        assertNull(DateTimeUtils.dateToString(null));
        assertNull(DateTimeUtils.instantToString(null));
        assertNull(DateTimeUtils.localDateTimeToString(null));
        assertNull(DateTimeUtils.zonedDateTimeToString(null));
        
        // Test conversions with a fixed timestamp
        Instant instant = Instant.parse("2020-01-01T12:30:45.123Z");
        Date date = Date.from(instant);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        
        // Date -> java.time
        assertEquals(instant, DateTimeUtils.toInstant(date));
        assertEquals(zonedDateTime, DateTimeUtils.toZonedDateTime(date));
        
        // java.time -> Date
        assertEquals(date, DateTimeUtils.toDate(instant));
        assertEquals(date, DateTimeUtils.toDate(zonedDateTime));
        assertEquals(date, DateTimeUtils.toDate(localDateTime));
        
        // String representations
        String expectedString = "20200101T123045123Z";
        assertEquals(expectedString, DateTimeUtils.dateToString(date));
        assertEquals(expectedString, DateTimeUtils.instantToString(instant));
        assertEquals(expectedString, DateTimeUtils.localDateTimeToString(localDateTime));
        assertEquals(expectedString, DateTimeUtils.zonedDateTimeToString(zonedDateTime));
    }
    
    /**
     * Tests handling of null parameters.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void nullHandling() throws Exception {
        // Create a test date
        Instant instant = Instant.parse("2020-01-01T12:30:45Z");
        Date date = Date.from(instant);
        
        // Test with null date but valid formatter
        assertNull(DateTimeUtils.dateToString(null, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // Test with null java.time formatter
        new ExpectedFailure<NullPointerException>() {
            protected void run() throws Exception {
                DateTimeUtils.dateToString(date, (DateTimeFormatter)null);
            }
        };
        
        // Test with null joda formatter
        new ExpectedFailure<NullPointerException>() {
            protected void run() throws Exception {
                DateTimeUtils.dateToString(date, (org.joda.time.format.DateTimeFormatter)null);
            }
        };
    }
    
    /**
     * Tests both the java.time and joda-time formatter versions of dateToString.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void testBothFormatterTypes() throws Exception {
        // Create a test date
        Instant instant = Instant.parse("2020-01-01T12:30:45Z");
        Date date = Date.from(instant);
        
        // Test with java.time DateTimeFormatter
        String javaTimeResult = DateTimeUtils.dateToString(date, DateTimeFormatter.ISO_INSTANT);
        // The ISO formatter will include seconds and possibly fractional seconds, so we'll just check prefix
        assertTrue(javaTimeResult.startsWith("2020-01-01T12:30:45"));
        
        // Test with joda DateTimeFormatter
        // Create a formatter with the UTC timezone to match java.time ISO_INSTANT
        org.joda.time.format.DateTimeFormatter jodaFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
                                                                          .withZoneUTC();
        String jodaResult = DateTimeUtils.dateToString(date, jodaFormatter);
        assertEquals("2020-01-01 12:30:45", jodaResult);
    }
}