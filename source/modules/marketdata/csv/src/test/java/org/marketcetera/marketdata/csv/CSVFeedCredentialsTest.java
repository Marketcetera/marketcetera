package org.marketcetera.marketdata.csv;

import static org.junit.Assert.assertEquals;
import static org.marketcetera.marketdata.csv.Messages.INVALID_EVENT_TRANSLATOR;

import java.io.File;

import org.junit.Test;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.module.ExpectedFailure;

/* $License$ */

/**
 * Tests {@link CSVFeedCredentials}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.0
 */
public class CSVFeedCredentialsTest
{
    /**
     * Tests the ability of the credentials object to manage different values for the event delay. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDelay()
            throws Exception
    {
//        final String marketdataDirectory = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
//        new ExpectedFailure<FeedException>(INVALID_EVENT_DELAY) {
//            @Override
//            protected void run()
//                    throws Exception
//            {
//                CSVFeedCredentials.getInstance(Double.MIN_VALUE,
//                                               marketdataDirectory,
//                                               MockCSVFeedEventTranslator.class.getName());
//            }
//        };
//        assertEquals(1.0,
//                     CSVFeedCredentials.getInstance(0,
//                                                    marketdataDirectory,
//                                                    MockCSVFeedEventTranslator.class.getName()).getReplayRate());
//        assertEquals(Long.MAX_VALUE,
//                     CSVFeedCredentials.getInstance(Long.MAX_VALUE,
//                                                    MockCSVFeedEventTranslator.class.getName()).getMillisecondDelay());
    }
    /**
     * Tests the construction of the event translator by specifying the classname.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testEventTranslatorByClassname()
            throws Exception
    {
        final String marketdataDirectory = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
        // null class
        new ExpectedFailure<FeedException>(INVALID_EVENT_TRANSLATOR) {
            @Override
            protected void run()
                    throws Exception
            {
                CSVFeedCredentials.getInstance(0,
                                               marketdataDirectory,
                                               (String)null);
            }
        };
        // empty class
        new ExpectedFailure<FeedException>(INVALID_EVENT_TRANSLATOR,
                                           "") {
            @Override
            protected void run()
                    throws Exception
            {
                CSVFeedCredentials.getInstance(0,
                                               marketdataDirectory,
                                               "");
            }
        };
        // not-a-class
        new ExpectedFailure<FeedException>(INVALID_EVENT_TRANSLATOR,
                                           "this-is-not-a-class") {
            @Override
            protected void run()
                    throws Exception
            {
                CSVFeedCredentials.getInstance(0,
                                               marketdataDirectory,
                                               "this-is-not-a-class");
            }
        };
        // wrong type of class
        new ExpectedFailure<FeedException>(INVALID_EVENT_TRANSLATOR,
                                           String.class.getName()) {
            @Override
            protected void run()
                    throws Exception
            {
                CSVFeedCredentials.getInstance(0,
                                               marketdataDirectory,
                                               String.class.getName());
            }
        };
        assertEquals(new MockCSVFeedEventTranslator().getClass(),
                     CSVFeedCredentials.getInstance(0,
                                                    marketdataDirectory,
                                                    MockCSVFeedEventTranslator.class.getName()).getEventTranslator().getClass());
    }
    /**
     * Tests the construction of the event translator by specifying an instance.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testEventTranslatorByInstance()
            throws Exception
    {
        final String marketdataDirectory = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
        // null class
        new ExpectedFailure<FeedException>(INVALID_EVENT_TRANSLATOR) {
            @Override
            protected void run()
                    throws Exception
            {
                CSVFeedCredentials.getInstance(0,
                                               marketdataDirectory,
                                               (CSVFeedEventTranslator)null);
            }
        };
        CSVFeedEventTranslator translator = new MockCSVFeedEventTranslator();
        assertEquals(translator,
                     CSVFeedCredentials.getInstance(0,
                                                    marketdataDirectory,
                                                    translator).getEventTranslator());
    }
}
