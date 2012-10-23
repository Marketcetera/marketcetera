package org.marketcetera.marketdata.csv;

import java.util.List;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.event.Event;
import org.marketcetera.core.event.EventTranslator;

/**
 * Provides the base implementation of a CSV event translator.
 * 
 * <p>In order to use the CSV market data adapter, it is required to provide
 * a subclass of <code>CSVFeedEventTranslator</code> that handles a single
 * line from the CSV file.  In the subclass, override {@link #toEvent(Object, String)}.
 * Provide the FQN of the subclass to the {@link CSVFeedCredentials} object so the
 * feed knows what event translator to use.  Make sure that the subclass is compiled
 * and available in the classpath.
 * 
 * <p>Note that the subclass implementation of {@link #toEvent(Object, String)} must
 * absolutely be stateless.  A single instance of the subclass will be used to translate
 * all lines for all CSV files in the same session, with no concurrency guarantees.
 * 
 * @since 2.1.0
 * @version $Id: CSVFeedEventTranslator.java 16063 2012-01-31 18:21:55Z colin $
 */
public class CSVFeedEventTranslator
        implements EventTranslator
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.IEventTranslator#translate(java.lang.Object)
     */
    public List<Event> toEvent(Object inData,
                               String inHandle)
            throws CoreException
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.IEventTranslator#translate(org.marketcetera.core.event.Event)
     */
    public Object fromEvent(Event inEvent)
            throws CoreException
    {
        throw new UnsupportedOperationException();
    }
}
