package org.marketcetera.marketdata;

import static org.junit.Assert.assertEquals;
import static org.marketcetera.marketdata.DateUtils.*;
import static org.marketcetera.marketdata.Messages.INVALID_DATE;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;

/* $License$ */

/**
 * Tests {@link DateUtil}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public class DateUtilsTest
{
    private static DateFormat testDateFormat;
    /**
     * Initialization that needs to be run once for all tests.
     */
    @BeforeClass
    public static void runOnce()
    {
        testDateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL,
                                                        DateFormat.FULL,
                                                        Locale.US);
        testDateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    }
    /**
     * Tests {@link DateUtils#stringToDate(String)}.
     * 
     * <p>Note that the tests are intentionally not exhaustive as the vast number of permutations
     * would far exceed time available to run them.  Additionally, since the parsing of the dates
     * is actually handled by an external library, to a certain extent, the behavior can be assumed
     * to be valid. 
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void stringToDate()
        throws Exception
    {
        // test some obvious stinkers
        new ExpectedFailure<MarketDataRequestException>(INVALID_DATE) {
            protected void run()
                throws Exception
            {
                DateUtils.stringToDate(null);
            }
        };
        new ExpectedFailure<MarketDataRequestException>(INVALID_DATE) {
            protected void run()
                throws Exception
            {
                DateUtils.stringToDate("");
            }
        };
        // now build a few (incomplete) lists of invalid and valid components
        String[] invalidDateStrings = new String[] { "", "1", "11", "111", "1111", "11111", "111111", "x111111", "-123-4-5", "00000000", "20091301", "20090132", "19960230", "21000229" }; // haha, 2100 will *not* be a leap year
        String[] invalidTimeStrings = new String[] { "", "1", "11", "111", "-123", "-1-2", "12345", "1234567", "12345678", "2500", "2461", "240061" };
        String[] invalidTimeZoneStrings = new String[] { "X", "ZZ", "/1000", "+2500", "-0061" };
        String[] validDateStrings = new String[] { "00000101", "20040229", "99990531" };
        String[] validTimeStrings = new String[] { "0000", "000000", "000000000" };
        String[] validTimeZoneStrings = new String[] { "Z", "z", "+0000", "-1000", "+0530" };
        // iterate over the failure cases
        for(int dateCounter=0;dateCounter<invalidDateStrings.length;dateCounter++) {
            for(int timeCounter=0;timeCounter<invalidTimeStrings.length;timeCounter++) {
                for(int tzCounter=0;tzCounter<invalidTimeZoneStrings.length;tzCounter++) {
                    final String dateString = invalidDateStrings[dateCounter];
                    final String timeString = invalidTimeStrings[timeCounter];
                    final String tzString = invalidTimeZoneStrings[tzCounter];
                    // date alone
                    new ExpectedFailure<MarketDataRequestException>(INVALID_DATE) {
                        protected void run()
                            throws Exception
                        {
                            DateUtils.stringToDate(dateString);
                        }
                    };
                    // date & tz
                    new ExpectedFailure<MarketDataRequestException>(INVALID_DATE) {
                        protected void run()
                            throws Exception
                        {
                            DateUtils.stringToDate(String.format("%s%s",
                                                                 dateString,
                                                                 tzString));
                        }
                    };
                    // date & time
                    new ExpectedFailure<MarketDataRequestException>(INVALID_DATE) {
                        protected void run()
                            throws Exception
                        {
                            DateUtils.stringToDate(String.format("%sT%s",
                                                                 dateString,
                                                                 timeString));
                        }
                    };
                    // date, time, & tz
                    new ExpectedFailure<MarketDataRequestException>(INVALID_DATE) {
                        protected void run()
                            throws Exception
                        {
                            DateUtils.stringToDate(String.format("%sT%s%s",
                                                                 dateString,
                                                                 timeString,
                                                                 tzString));
                        }
                    };
                }
            }
        }
        // iterate over the success cases (success is measured by the ability to return a date rather than throw an exception:
        //  calculating the expected dates without using the library that already calculates them would require significant
        //  complexity.
        for(int dateCounter=0;dateCounter<validDateStrings.length;dateCounter++) {
            for(int timeCounter=0;timeCounter<validTimeStrings.length;timeCounter++) {
                for(int tzCounter=0;tzCounter<invalidTimeZoneStrings.length;tzCounter++) {
                    String dateString = validDateStrings[dateCounter];
                    String timeString = validTimeStrings[timeCounter];
                    String tzString = validTimeZoneStrings[tzCounter];
                    // date only
                    DateUtils.stringToDate(dateString);
                    // date & tz
                    DateUtils.stringToDate(String.format("%s%s",
                                                         dateString,
                                                         tzString));
                    // date & time
                    DateUtils.stringToDate(String.format("%sT%s",
                                                         dateString,
                                                         timeString));
                    // date, time, & tz
                    DateUtils.stringToDate(String.format("%sT%s%s",
                                                         dateString,
                                                         timeString,
                                                         tzString));
                }
            }
        }
        // check a few dates
        // UTC date
        doDateTest("20090319T120000000Z",
                   "20090319T120000000Z",
                   "Thursday, March 19, 2009 8:00:00 AM EDT");
        // PST date
        doDateTest("19700319T0800-0800",
                   "19700319T160000000Z",
                   "Thursday, March 19, 1970 11:00:00 AM EST");
        // no TZ (assumed to be UTC)
        doDateTest("19880319T0000",
                   "19880319T000000000Z",
                   "Friday, March 18, 1988 7:00:00 PM EST");
    }
    /**
     * Tests that specifying a particular format returns the expected result.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void specificFormats()
        throws Exception
    {
        DateTimeFormatter[] formats = new DateTimeFormatter[] { MILLIS_WITH_TZ,MILLIS,SECONDS_WITH_TZ,SECONDS,MINUTES_WITH_TZ,MINUTES,DAYS_WITH_TZ,DAYS };
        new ExpectedFailure<NullPointerException>(null) {
            protected void run()
                throws Exception
            {
                DateUtils.dateToString(new Date(),
                                       null);
            }
        };
        // take a date and make sure it comes out correctly when asking for a specific format
        Date testDate = new Date();
        for(DateTimeFormatter format : formats) {
            assertEquals(format.print(new DateTime(testDate)),
                         DateUtils.dateToString(testDate,
                                                format));
        }
    }
    /**
     * Verifies the given ISO 8601 date is parsed correctly.
     *
     * @param inDateToTest a <code>String</code> value containing a valid ISO 8601 date as defined in
     *  {@link DateUtils#stringToDate(String)} 
     * @param inExpectedUTCDate a <code>String</code> value containing the given test date expressed in
     *  ISO 8601 to millisecond precision in UTC
     * @param inExpectedEasternDate a <code>String</code> value containing the given test date expressed
     *  in {@link DateFormat#FULL} format in US Eastern time
     * @throws Exception if an error occurs
     */
    private static void doDateTest(String inDateToTest,
                                   String inExpectedUTCDate,
                                   String inExpectedEasternDate)
        throws Exception
    {
        Date date = DateUtils.stringToDate(inDateToTest);
        assertEquals(inExpectedUTCDate,
                     DateUtils.dateToString(date));
        assertEquals(testDateFormat.parse(inExpectedEasternDate),
                     date);
    }
}
