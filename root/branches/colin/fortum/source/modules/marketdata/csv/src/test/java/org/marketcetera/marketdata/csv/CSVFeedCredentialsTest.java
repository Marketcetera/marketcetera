package org.marketcetera.marketdata.csv;

import static org.junit.Assert.assertEquals;
import static org.marketcetera.marketdata.csv.Messages.*;

import org.junit.Test;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.module.ExpectedFailure;

/* $License$ */

/**
 * Tests {@link CSVFeedCredentials}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
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
        new ExpectedFailure<FeedException>(INVALID_EVENT_DELAY) {
            @Override
            protected void run()
                    throws Exception
            {
                CSVFeedCredentials.getInstance(Long.MIN_VALUE,
                                               MockCSVFeedEventTranslator.class.getName());
            }
        };
        assertEquals(0,
                     CSVFeedCredentials.getInstance(0,
                                                    MockCSVFeedEventTranslator.class.getName()).getMillisecondDelay());
        assertEquals(Long.MAX_VALUE,
                     CSVFeedCredentials.getInstance(Long.MAX_VALUE,
                                                    MockCSVFeedEventTranslator.class.getName()).getMillisecondDelay());
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
        // null class
        new ExpectedFailure<FeedException>(INVALID_EVENT_TRANSLATOR) {
            @Override
            protected void run()
                    throws Exception
            {
                CSVFeedCredentials.getInstance(0,
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
                                               String.class.getName());
            }
        };
        assertEquals(new MockCSVFeedEventTranslator().getClass(),
                     CSVFeedCredentials.getInstance(0,
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
        // null class
        new ExpectedFailure<FeedException>(INVALID_EVENT_TRANSLATOR) {
            @Override
            protected void run()
                    throws Exception
            {
                CSVFeedCredentials.getInstance(0,
                                               (CSVFeedEventTranslator)null);
            }
        };
        CSVFeedEventTranslator translator = new MockCSVFeedEventTranslator();
        assertEquals(translator,
                     CSVFeedCredentials.getInstance(0,
                                                    translator).getEventTranslator());
    }
}
