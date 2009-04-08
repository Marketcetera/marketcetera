package org.marketcetera.event;

/* $License$ */

/**
 * Tests {@link AskEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AskEventTest
        extends QuoteEventTestBase
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.QuoteEventTestBase#getEventType()
     */
    @Override
    protected Class<? extends QuoteEvent> getEventType()
    {
        return AskEvent.class;
    }
}
