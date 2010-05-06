package org.marketcetera.marketdata.csv;

import java.util.List;

import org.marketcetera.core.CoreException;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTranslator;
import org.marketcetera.util.misc.ClassVersion;

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
 * @author toli kuznets
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since $Release$
 * @version $Id: CSVFeedEventTranslator.java 4348 2009-09-24 02:33:11Z toli $
 */
@ClassVersion("$Id: CSVFeedEventTranslator.java 4348 2009-09-24 02:33:11Z toli $")
public class CSVFeedEventTranslator
        implements EventTranslator, Messages
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(java.lang.Object)
     */
    public List<Event> toEvent(Object inData,
                               String inHandle)
            throws CoreException
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(org.marketcetera.event.Event)
     */
    public Object fromEvent(Event inEvent)
            throws CoreException
    {
        throw new UnsupportedOperationException();
    }
}
