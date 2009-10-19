package org.marketcetera.event;

import org.marketcetera.util.misc.ClassVersion;

/**
 * Indicates the action to be taken.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: QuoteEvent.java 10567 2009-05-11 17:01:09Z colin $
 * @since 0.6.0
 */
@ClassVersion("$Id: QuoteEvent.java 10567 2009-05-11 17:01:09Z colin $")
public enum QuoteAction 
{
    ADD,
    CHANGE,
    DELETE
}