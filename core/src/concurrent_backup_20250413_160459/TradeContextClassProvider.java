package org.marketcetera.trade;

import jakarta.annotation.concurrent.Immutable;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;

/* $License$ */

/**
 * Provides context classes for trade objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@SuppressFBWarnings(value="IS2_INCONSISTENT_SYNC", justification="Immutable")
@ClassVersion("$Id$")
public class TradeContextClassProvider
        implements ContextClassProvider
{
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.ContextClassProvider#getContextClasses()
     */
    @Override
    public Class<?>[] getContextClasses()
    {
        return CLASSES;
    }
    /**
     * static instance
     */
    public static final TradeContextClassProvider INSTANCE = new TradeContextClassProvider();
    /**
     * classes value
     */
    private static final Class<?>[] CLASSES = new Class<?>[] { Instrument.class,ReportBaseImpl.class,FIXMessageWrapper.class };
}
