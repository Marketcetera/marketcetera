package org.marketcetera.bogusfeed;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */

/**
 * Messages for BogusFeed plug-in.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since $Release$
 */
@ClassVersion("$Id: BogusFeedEventTranslator.java 9312 2008-06-05 18:02:01Z colin $") //$NON-NLS-1$
public interface Messages
{
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("bogus");  //$NON-NLS-1$

    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    
    public static final I18NMessage1P UNKNOWN_EVENT_TYPE = new I18NMessage1P(LOGGER,
                                                                             "unknown_event_type"); //$NON-NLS-1$
    public static final I18NMessage1P UNKNOWN_ENTRY_TYPE = new I18NMessage1P(LOGGER,
                                                                             "unknown_entry_type"); //$NON-NLS-1$

}
