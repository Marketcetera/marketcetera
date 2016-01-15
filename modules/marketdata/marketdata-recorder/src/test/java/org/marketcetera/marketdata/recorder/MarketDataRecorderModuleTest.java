package org.marketcetera.marketdata.recorder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.core.time.TimeFactoryImpl.COLON;
import static org.marketcetera.core.time.TimeFactoryImpl.HOUR;
import static org.marketcetera.core.time.TimeFactoryImpl.MINUTE;
import static org.marketcetera.core.time.TimeFactoryImpl.SECOND;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.event.EventType;
import org.marketcetera.event.LocalTimezoneTimestampGenerator;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.bogus.BogusFeedModuleFactory;
import org.marketcetera.marketdata.module.TestFeed;
import org.marketcetera.marketdata.module.TestFeedModuleFactory;
import org.marketcetera.module.DataFlowExceptionHandler;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Tests {@link MarketDataRecorderModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataRecorderModuleTest
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        moduleManager = new ModuleManager();
        moduleManager.init();
        moduleManager.start(TestFeedModuleFactory.INSTANCE_URN);
        moduleManager.start(BogusFeedModuleFactory.INSTANCE_URN);
        testMarketDataFeed = TestFeed.instance;
        testDirectory = new File(FileUtils.getTempDirectory(),
                                 MarketDataRecorderModuleTest.class.getSimpleName());
        FileUtils.deleteQuietly(testDirectory);
        FileUtils.forceMkdir(testDirectory);
        SLF4JLoggerProxy.info(this,
                              "Test directory is {}",
                              testDirectory);
        testExceptionHandler = new TestExceptionHandler();
        gcInstrument = Future.fromString("GC-201609");
        // try to catch the almost impossible case that the session reset we're using is after right now
        sessionReset = "00:00:00";
        while(generateConfig(sessionReset).getSessionResetTimestamp().isAfterNow()) {
            Thread.sleep(1000);
        }
    }
    /**
     * Run after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void cleanup()
            throws Exception
    {
        moduleManager.stop();
        FileUtils.deleteQuietly(testDirectory);
    }
    /**
     * Tests that an error while processing quotes triggers exception handling.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testExceptionHandling()
            throws Exception
    {
        String sessionReset = "00:00:00";
        while(generateConfig(sessionReset).getSessionResetTimestamp().isAfterNow()) {
            Thread.sleep(1000);
        }
        ModuleURN instanceUrn = getRecorderModule(testDirectory.getAbsolutePath(),
                                                  sessionReset);
        establishDataFlow(generateMarketDataRequest(Lists.newArrayList(gcInstrument.getFullSymbol()),
                                                    Lists.newArrayList(Content.TOP_OF_BOOK),
                                                    AssetClass.FUTURE),
                          TestFeedModuleFactory.PROVIDER_URN,
                          instanceUrn);
        verifyNoFiles();
        assertTrue(testExceptionHandler.exceptions.isEmpty());
        AskEvent ask = generateAskEvent(gcInstrument,
                                        "EX");
        ask.setEventType(null);
        testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
        assertEquals(1,
                     testExceptionHandler.exceptions.size());
    }
    /**
     * Tests that invalid session reset times are caught.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testInvalidSessionReset()
            throws Exception
    {
        MarketDataRecorderModuleConfiguration config = generateConfig(null);
        assertNull(config.getSessionReset());
        assertNull(config.getSessionResetTimestamp());
        new ExpectedFailure<IllegalArgumentException>(Messages.SESSION_RESET_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                generateConfig("25:00:00");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.SESSION_RESET_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                generateConfig("01:00");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.SESSION_RESET_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                generateConfig("");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.SESSION_RESET_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                generateConfig("xyz-pdq");
            }
        };
    }
    /**
     * Tests that multiple exchanges create multiple files.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testMultipleExchanges()
            throws Exception
    {
        ModuleURN instanceUrn = getRecorderModule(testDirectory.getAbsolutePath(),
                                                  sessionReset);
        establishDataFlow(generateMarketDataRequest(Lists.newArrayList(gcInstrument.getFullSymbol()),
                                                    Lists.newArrayList(Content.TOP_OF_BOOK),
                                                    AssetClass.FUTURE),
                          TestFeedModuleFactory.PROVIDER_URN,
                          instanceUrn);
        verifyNoFiles();
        AskEvent ask = generateAskEvent(gcInstrument,
                                        "EX1");
        ask.setEventType(EventType.SNAPSHOT_FINAL);
        testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
        // collect the files from the test directory
        Collection<File> testFiles = getFiles();
        assertEquals(1,
                     testFiles.size());
        Iterator<File> dataFileIterator = testFiles.iterator();
        File dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        assertTrue(dataFile.getName().contains("EX1"));
        verifyEventCount(dataFile,
                         1);
        ask = generateAskEvent(gcInstrument,
                               "EX2");
        ask.setEventType(EventType.SNAPSHOT_FINAL);
        testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
        testFiles = getFiles();
        assertEquals(2,
                     testFiles.size());
        dataFileIterator = testFiles.iterator();
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        assertTrue(dataFile.getName().contains("EX1"));
        verifyEventCount(dataFile,
                         1);
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        assertTrue(dataFile.getName().contains("EX2"));
        verifyEventCount(dataFile,
                         1);
    }
    /**
     * Tests that multiple symbols create multiple files.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testMultipleSymbols()
            throws Exception
    {
        ModuleURN instanceUrn = getRecorderModule(testDirectory.getAbsolutePath(),
                                                  sessionReset);
        Future zInstrument = Future.fromString("Z-201609");
        establishDataFlow(generateMarketDataRequest(Lists.newArrayList(gcInstrument.getFullSymbol(),zInstrument.getFullSymbol()),
                                                    Lists.newArrayList(Content.TOP_OF_BOOK),
                                                    AssetClass.FUTURE),
                          TestFeedModuleFactory.PROVIDER_URN,
                          instanceUrn);
        verifyNoFiles();
        AskEvent ask = generateAskEvent(gcInstrument,
                                        "EX");
        ask.setEventType(EventType.SNAPSHOT_FINAL);
        testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
        // collect the files from the test directory
        Collection<File> testFiles = getFiles();
        assertEquals(1,
                     testFiles.size());
        Iterator<File> dataFileIterator = testFiles.iterator();
        File dataFile = dataFileIterator.next();
        assertTrue(dataFile.getName().contains(gcInstrument.getFullSymbol()));
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        verifyEventCount(dataFile,
                         1);
        ask = generateAskEvent(zInstrument,
                               "EX");
        ask.setEventType(EventType.SNAPSHOT_FINAL);
        testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
        testFiles = getFiles();
        assertEquals(2,
                     testFiles.size());
        dataFileIterator = testFiles.iterator();
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        assertTrue(dataFile.getName().contains(gcInstrument.getFullSymbol()));
        verifyEventCount(dataFile,
                         1);
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        assertTrue(dataFile.getName().contains(zInstrument.getFullSymbol()));
        verifyEventCount(dataFile,
                         1);
    }
    /**
     * Tests handling of non-quote objects.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testNonQuotes()
            throws Exception
    {
        ModuleURN instanceUrn = getRecorderModule(testDirectory.getAbsolutePath(),
                                                  sessionReset);
        establishDataFlow(generateMarketDataRequest(Lists.newArrayList(gcInstrument.getFullSymbol()),
                                                    Lists.newArrayList(Content.TOP_OF_BOOK),
                                                    AssetClass.FUTURE),
                          TestFeedModuleFactory.PROVIDER_URN,
                          instanceUrn);
        verifyNoFiles();
        assertTrue(testExceptionHandler.exceptions.isEmpty());
        TradeEvent trade = EventTestBase.generateTradeEvent(gcInstrument);
        testMarketDataFeed.sendEvents(Lists.newArrayList((Event)trade));
        assertTrue(testExceptionHandler.exceptions.isEmpty());
        verifyNoFiles();
    }
    /**
     * Tests that, if no session reset is specified, events are always written to the same file.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testNoSessionReset()
            throws Exception
    {
        // pick a session reset that's a few seconds in the future
        DateTime sessionResetTime = new DateTime().plusSeconds(10);
        sessionReset = null;
        ModuleURN instanceUrn = getRecorderModule(testDirectory.getAbsolutePath(),
                                                  sessionReset);
        establishDataFlow(generateMarketDataRequest(Lists.newArrayList(gcInstrument.getFullSymbol()),
                                                    Lists.newArrayList(Content.TOP_OF_BOOK),
                                                    AssetClass.FUTURE),
                          TestFeedModuleFactory.PROVIDER_URN,
                          instanceUrn);
        verifyNoFiles();
        int counter = 0;
        // seed with a SNAPSHOT TYPE
        AskEvent ask = generateAskEvent(gcInstrument,
                                        "EX");
        ask.setEventType(EventType.SNAPSHOT_FINAL);
        testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
        counter += 1;
        // using only UPDATE types, create events until the session reset time has passed 10s ago (20s or so, total)
        while(sessionResetTime.plusSeconds(10).isAfterNow()) {
            ask = generateAskEvent(gcInstrument,
                                   "EX");
            ask.setEventType(EventType.UPDATE_FINAL);
            testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
            counter += 1;
            Thread.sleep(100);
        }
        // there should be one file with total events equal to our counter
        Collection<File> testFiles = getFiles();
        assertEquals(1,
                     testFiles.size());
        Iterator<File> dataFileIterator = testFiles.iterator();
        File dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        int actualEventCount = getEventCount(dataFile);
        assertEquals(counter,
                     actualEventCount);
    }
    /**
     * Tests that an event received after session reset causes a new file to be created.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSessionReset()
            throws Exception
    {
        // pick a session reset that's a few seconds in the future
        DateTime sessionResetTime = new DateTime().plusSeconds(10);
        sessionReset = sessionResetFormatter.print(sessionResetTime);
        SLF4JLoggerProxy.debug(this,
                               "Using {} as session reset",
                               sessionReset);
        ModuleURN instanceUrn = getRecorderModule(testDirectory.getAbsolutePath(),
                                                  sessionReset);
        establishDataFlow(generateMarketDataRequest(Lists.newArrayList(gcInstrument.getFullSymbol()),
                                                    Lists.newArrayList(Content.TOP_OF_BOOK),
                                                    AssetClass.FUTURE),
                          TestFeedModuleFactory.PROVIDER_URN,
                          instanceUrn);
        verifyNoFiles();
        int counter = 0;
        // seed with a SNAPSHOT TYPE
        AskEvent ask = generateAskEvent(gcInstrument,
                                        "EX");
        ask.setEventType(EventType.SNAPSHOT_FINAL);
        testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
        counter += 1;
        // using only UPDATE types, create events until the session reset time has passed 10s ago (20s or so, total)
        while(sessionResetTime.plusSeconds(10).isAfterNow()) {
            ask = generateAskEvent(gcInstrument,
                                   "EX");
            ask.setEventType(EventType.UPDATE_FINAL);
            testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
            counter += 1;
            Thread.sleep(100);
        }
        // there should be two files with total events equal to our counter
        Collection<File> testFiles = getFiles();
        assertEquals(2,
                     testFiles.size());
        Iterator<File> dataFileIterator = testFiles.iterator();
        File dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        int actualEventCount = getEventCount(dataFile);
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        actualEventCount += getEventCount(dataFile);
        assertEquals(counter,
                     actualEventCount);
    }
    /**
     * Tests that ordinal logic works when running beyond one digit.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testMultiDigitOrdinals()
            throws Exception
    {
        ModuleURN instanceUrn = getRecorderModule(testDirectory.getAbsolutePath(),
                                                  sessionReset);
        establishDataFlow(generateMarketDataRequest(Lists.newArrayList(gcInstrument.getFullSymbol()),
                                                    Lists.newArrayList(Content.TOP_OF_BOOK),
                                                    AssetClass.FUTURE),
                          TestFeedModuleFactory.PROVIDER_URN,
                          instanceUrn);
        verifyNoFiles();
        // send a series of SNAPSHOT_FINAL events (each should trigger a new file) until we have at least 10 files, making sure everything works in incrementing ordinals above 10
        int expectedEventCounter = 0;
        for(int i=0;i<101;i++) {
            AskEvent ask = generateAskEvent(gcInstrument,
                                            "EX");
            ask.setEventType(EventType.SNAPSHOT_FINAL);
            testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
            expectedEventCounter += 1;
        }
        int expectedSuffix = 1;
        Collection<File> testFiles = getFiles();
        Iterator<File> dataFileIterator = testFiles.iterator();
        int actualEventCount = 0;
        while(dataFileIterator.hasNext()) {
            File dataFile = dataFileIterator.next();
            assertTrue("Expected " + dataFile.getName() + " to end with ''-"+expectedSuffix+".csv''",
                       dataFile.getAbsolutePath().endsWith("-"+expectedSuffix+++".csv"));
            actualEventCount += getEventCount(dataFile);
        }
        assertEquals(expectedEventCounter,
                     actualEventCount);
        assertEquals(expectedSuffix-1,
                     testFiles.size());
    }
    /**
     * Tests that the file ordinal is incremented when a snapshot boundary is encountered.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testIncrementOrdinal()
            throws Exception
    {
        ModuleURN instanceUrn = getRecorderModule(testDirectory.getAbsolutePath(),
                                                  sessionReset);
        establishDataFlow(generateMarketDataRequest(Lists.newArrayList(gcInstrument.getFullSymbol()),
                                                    Lists.newArrayList(Content.TOP_OF_BOOK),
                                                    AssetClass.FUTURE),
                          TestFeedModuleFactory.PROVIDER_URN,
                          instanceUrn);
        verifyNoFiles();
        // send a SNAPSHOT_FINAL event, verify that we get a file with ordinal "1"
        AskEvent ask = generateAskEvent(gcInstrument,
                                        "EX");
        ask.setEventType(EventType.SNAPSHOT_FINAL);
        testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
        // collect the files from the test directory
        Collection<File> testFiles = getFiles();
        assertEquals(1,
                     testFiles.size());
        Iterator<File> dataFileIterator = testFiles.iterator();
        File dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        verifyEventCount(dataFile,
                         1);
        // send an UPDATE event, should get just the one file
        ask = generateAskEvent(gcInstrument,
                               "EX");
        ask.setEventType(EventType.UPDATE_FINAL);
        testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
        testFiles = getFiles();
        assertEquals(1,
                     testFiles.size());
        dataFileIterator = testFiles.iterator();
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        verifyEventCount(dataFile,
                         2);
        // send an UPDATE_PART event, still one file
        ask = generateAskEvent(gcInstrument,
                               "EX");
        ask.setEventType(EventType.UPDATE_PART);
        testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
        testFiles = getFiles();
        assertEquals(1,
                     testFiles.size());
        dataFileIterator = testFiles.iterator();
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        verifyEventCount(dataFile,
                         3);
        // send a SNAPSHOT_PART event, should now get two files
        ask = generateAskEvent(gcInstrument,
                               "EX");
        ask.setEventType(EventType.SNAPSHOT_PART);
        testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
        testFiles = getFiles();
        assertEquals(2,
                     testFiles.size());
        dataFileIterator = testFiles.iterator();
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        verifyEventCount(dataFile,
                         3);
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-2.csv''",
                   dataFile.getAbsolutePath().endsWith("-2.csv"));
        verifyEventCount(dataFile,
                         1);
        // send a SNAPSHOT_FINAL event, still two files
        ask = generateAskEvent(gcInstrument,
                               "EX");
        ask.setEventType(EventType.SNAPSHOT_FINAL);
        testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
        testFiles = getFiles();
        assertEquals(2,
                     testFiles.size());
        dataFileIterator = testFiles.iterator();
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        verifyEventCount(dataFile,
                         3);
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-2.csv''",
                   dataFile.getAbsolutePath().endsWith("-2.csv"));
        verifyEventCount(dataFile,
                         2);
        // go from SNAPSHOT_PART TO UPDATE, should bump ordinal when moving from SNAPSHOT_FINAL to SNAPSHOT_PART, then stay the same when moving to UPDATE_PART
        ask = generateAskEvent(gcInstrument,
                               "EX");
        ask.setEventType(EventType.SNAPSHOT_PART);
        testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
        testFiles = getFiles();
        assertEquals(3,
                     testFiles.size());
        dataFileIterator = testFiles.iterator();
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        verifyEventCount(dataFile,
                         3);
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-2.csv''",
                   dataFile.getAbsolutePath().endsWith("-2.csv"));
        verifyEventCount(dataFile,
                         2);
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-3.csv''",
                   dataFile.getAbsolutePath().endsWith("-3.csv"));
        verifyEventCount(dataFile,
                         1);
        // another snapshot part
        ask = generateAskEvent(gcInstrument,
                               "EX");
        ask.setEventType(EventType.SNAPSHOT_PART);
        testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
        testFiles = getFiles();
        assertEquals(3,
                     testFiles.size());
        dataFileIterator = testFiles.iterator();
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        verifyEventCount(dataFile,
                         3);
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-2.csv''",
                   dataFile.getAbsolutePath().endsWith("-2.csv"));
        verifyEventCount(dataFile,
                         2);
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-3.csv''",
                   dataFile.getAbsolutePath().endsWith("-3.csv"));
        verifyEventCount(dataFile,
                         2);
        // now switch directly to update_part w/o snapshot_final
        ask = generateAskEvent(gcInstrument,
                               "EX");
        ask.setEventType(EventType.UPDATE_PART);
        testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
        testFiles = getFiles();
        assertEquals(3,
                     testFiles.size());
        dataFileIterator = testFiles.iterator();
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        verifyEventCount(dataFile,
                         3);
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-2.csv''",
                   dataFile.getAbsolutePath().endsWith("-2.csv"));
        verifyEventCount(dataFile,
                         2);
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-3.csv''",
                   dataFile.getAbsolutePath().endsWith("-3.csv"));
        verifyEventCount(dataFile,
                         3);
        // send two snapshot final events
        ask = generateAskEvent(gcInstrument,
                               "EX");
        ask.setEventType(EventType.SNAPSHOT_FINAL);
        testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
        testFiles = getFiles();
        assertEquals(4,
                     testFiles.size());
        dataFileIterator = testFiles.iterator();
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        verifyEventCount(dataFile,
                         3);
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-2.csv''",
                   dataFile.getAbsolutePath().endsWith("-2.csv"));
        verifyEventCount(dataFile,
                         2);
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-3.csv''",
                   dataFile.getAbsolutePath().endsWith("-3.csv"));
        verifyEventCount(dataFile,
                         3);
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-4.csv''",
                   dataFile.getAbsolutePath().endsWith("-4.csv"));
        verifyEventCount(dataFile,
                         1);
        ask = generateAskEvent(gcInstrument,
                               "EX");
        ask.setEventType(EventType.SNAPSHOT_FINAL);
        testMarketDataFeed.sendEvents(Lists.newArrayList((Event)ask));
        testFiles = getFiles();
        assertEquals(5,
                     testFiles.size());
        dataFileIterator = testFiles.iterator();
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-1.csv''",
                   dataFile.getAbsolutePath().endsWith("-1.csv"));
        verifyEventCount(dataFile,
                         3);
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-2.csv''",
                   dataFile.getAbsolutePath().endsWith("-2.csv"));
        verifyEventCount(dataFile,
                         2);
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-3.csv''",
                   dataFile.getAbsolutePath().endsWith("-3.csv"));
        verifyEventCount(dataFile,
                         3);
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-4.csv''",
                   dataFile.getAbsolutePath().endsWith("-4.csv"));
        verifyEventCount(dataFile,
                         1);
        dataFile = dataFileIterator.next();
        assertTrue("Expected " + dataFile.getName() + " to end with ''-5.csv''",
                   dataFile.getAbsolutePath().endsWith("-5.csv"));
        verifyEventCount(dataFile,
                         1);
    }
    /**
     * Verifies that the event count in the given file matches the given expected count.
     *
     * @param inFile a <code>File</code> value
     * @param inExpectedEventCount an <code>int</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyEventCount(File inFile,
                                  int inExpectedEventCount)
            throws Exception
    {
        assertEquals(inExpectedEventCount,
                     getEventCount(inFile));
    }
    /**
     * Gets the event count in the given file.
     *
     * @param inFile a <code>File</code> value
     * @return an <code>int</code> value
     * @throws Exception if an unexpected error occurs
     */
    private int getEventCount(File inFile)
            throws Exception
    {
        return FileUtils.readLines(inFile).size();
    }
    /**
     * Generates an <code>AskEvent</code> with the given values.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inExchange a <code>String</code> value
     * @return an <code>AskEvent</code> value
     */
    private AskEvent generateAskEvent(Instrument inInstrument,
                                      String inExchange)
    {
        QuoteEventBuilder<AskEvent> builder = QuoteEventBuilder.askEvent(inInstrument);
        return builder.withExchange(inExchange)
                      .withPrice(EventTestBase.generateDecimalValue())
                      .withSize(EventTestBase.generateDecimalValue())
                      .withQuoteDate(String.valueOf(System.currentTimeMillis())).create();
    }
    /**
     * Verifies that no files exist in the test directory.
     */
    private void verifyNoFiles()
    {
        assertTrue(FileUtils.listFiles(testDirectory,
                                       null,
                                       false).isEmpty());
    }
    /**
     * Gets the files from the test directory in order.
     *
     * @return a <code>SortedSet&lt;Fle&gt;</code> value
     */
    private SortedSet<File> getFiles()
    {
        SortedSet<File> results = new TreeSet<>(FileOrdinalComparator.instance);
        results.addAll(FileUtils.listFiles(testDirectory,
                                                 null,
                                                 false));
        return results;
    }
    /**
     * Establishes a data flow using the given request from the given provider to the given recorder.
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inProviderUrn a <code>ModuleURN</code> value
     * @param inRecorderUrn a <code>ModuleURN</code. value
     * @return a <code>DataFlowID</code> value
     */
    private DataFlowID establishDataFlow(MarketDataRequest inRequest,
                                         ModuleURN inProviderUrn,
                                         ModuleURN inRecorderUrn)
    {
        DataFlowID flowId = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(inProviderUrn,
                                                                                             inRequest),
                                                                             new DataRequest(inRecorderUrn,
                                                                                             testExceptionHandler,
                                                                                             null) },
                                                         false);
        return flowId;
    }
    /**
     * Generates a market data request using the given attributes.
     *
     * @param inSymbols a <code>Collection&lt;String&gt;</code> value
     * @param inContent a <code>Collection&lt;Content&gt;</code> value
     * @param inAssetClass an <code>AssetClass</code> value
     * @return a <code>MarketDataRequest</code> value
     */
    private MarketDataRequest generateMarketDataRequest(Collection<String> inSymbols,
                                                        Collection<Content> inContent,
                                                        AssetClass inAssetClass)
    {
        MarketDataRequestBuilder requestBuilder = MarketDataRequestBuilder.newRequest()
                .withContent(inContent)
                .withSymbols(inSymbols)
                .withAssetClass(inAssetClass);
        return requestBuilder.create();
    }
    /**
     * Creates and starts a recorder module instance with the given configuration.
     *
     * @param inDirectoryName a <code>String</code> value
     * @param inSessionReset a <code>String</code> value
     * @return a <code>ModuleURN</code> value
     */
    private ModuleURN getRecorderModule(String inDirectoryName,
                                        String inSessionReset)
    {
        ApplicationContext applicationContext = generateApplicationContext(generateConfig(inSessionReset));
        moduleManager.setApplicationContext(applicationContext);
        ModuleURN recorderUrn = moduleManager.createModule(MarketDataRecorderModuleFactory.PROVIDER_URN,
                                                           inDirectoryName);
        moduleManager.start(recorderUrn);
        return recorderUrn;
    }
    /**
     * Generates a module configuration value.
     *
     * @param inSessionReset a <code>String</code> value
     * @return a <code>MarketDataRecorderModuleConfiguration</code> value
     */
    private MarketDataRecorderModuleConfiguration generateConfig(String inSessionReset)
    {
        MarketDataRecorderModuleConfiguration config = new MarketDataRecorderModuleConfiguration();
        config.setSessionReset(inSessionReset);
        config.setTimestampGenerator(new LocalTimezoneTimestampGenerator());
        config.start();
        return config;
    }
    /**
     * Generates an application context including the given config.
     *
     * @param inConfig a <code>MarketDataRecorderModuleConfiguration</code> value
     * @return an <code>ApplicationContext</code> value
     */
    private ApplicationContext generateApplicationContext(MarketDataRecorderModuleConfiguration inConfig)
    {
        GenericApplicationContext context = new GenericApplicationContext();
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        beanFactory.registerSingleton(inConfig.getClass().getSimpleName(),
                                      inConfig);
        context.refresh();
        context.start();
        return context;
    }
    /**
     * Receives exceptions generated during data flows.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class TestExceptionHandler
            implements DataFlowExceptionHandler
    {
        /* (non-Javadoc)
         * @see org.marketcetera.module.DataFlowExceptionHandler#onException(java.lang.Throwable)
         */
        @Override
        public void onException(Throwable inThrowable)
        {
            SLF4JLoggerProxy.debug(MarketDataRecorderModuleTest.class,
                                   inThrowable);
            exceptions.add(inThrowable);
        }
        /**
         * holds the exceptions that have been received
         */
        private final List<Throwable> exceptions = new ArrayList<>();
        private static final long serialVersionUID = 517430082636110107L;
    }
    /**
     * Compares two files to sort by ordinal.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static final class FileOrdinalComparator
            implements Comparator<File>
    {
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(File inO1,
                           File inO2)
        {
            return new CompareToBuilder().append(getOrdinal(inO1),getOrdinal(inO2)).append(inO1.getName(),inO2.getName()).toComparison();
        }
        /**
         * Gets the ordinal value from the given file.
         *
         * @param inFile a <code>File</code> value
         * @return an <code>int</code> value
         */
        private int getOrdinal(File inFile)
        {
            String filename = inFile.getName();
            int finalDashPos = filename.lastIndexOf('-');
            return Integer.parseInt(filename.substring(finalDashPos+1,
                                                       filename.indexOf(".csv")));
        }
        /**
         * comparison instance to use
         */
        private static final FileOrdinalComparator instance = new FileOrdinalComparator();
    }
    /**
     * test instrument
     */
    private Future gcInstrument;
    /**
     * test exception handler value
     */
    private TestExceptionHandler testExceptionHandler;
    /**
     * directory to which to write output files
     */
    private File testDirectory;
    /**
     * test market data feed used to generate specific events
     */
    private TestFeed testMarketDataFeed;
    /**
     * manages modules
     */
    private ModuleManager moduleManager;
    /**
     * test session reset value
     */
    private String sessionReset;
    /**
     * used to manufacture session reset values
     */
    private static final DateTimeFormatter sessionResetFormatter = new DateTimeFormatterBuilder().append(HOUR).append(COLON).append(MINUTE).append(COLON).append(SECOND).toFormatter();
}
