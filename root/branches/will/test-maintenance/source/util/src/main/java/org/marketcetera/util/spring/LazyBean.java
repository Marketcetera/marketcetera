package org.marketcetera.util.spring;

import org.marketcetera.util.except.I18NRuntimeException;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A base class for a Spring bean which allows for on-demand
 * post-processing. That is, one of more getters (of <i>computed</i>
 * properties) may be guarded using {@link #ensureProcessed()}, which
 * will trigger processing of the bean's properties via {@link
 * #process()}. After processing, no setter guarded with {@link
 * #assertNotProcessed()} (of <i>raw</i> properties) may be called, or
 * else the bean may find itself in an inconsistent state whereby the
 * computed properties do not match the raw ones. The receiver detects
 * and blocks circular processing attempts, whereby two separate but
 * mutually dependent beans attempt to process each other during their
 * own processing, thereby leading to an endless cycle.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public abstract class LazyBean
{

    // INSTANCE DATA.

    private boolean mProcessed;
    private boolean mProcessing;


    // INSTANCE METHODS.

    /**
     * Sets the receiver's flag indicating that processing is complete
     * to the given value.
     *
     * @param processed The flag.
     */

    private void setProcessed
        (boolean processed)
    {
        mProcessed=processed;
    }

    /**
     * Returns the receiver's flag indicating that processing is
     * complete.
     *
     * @return The flag.
     */

    private boolean getProcessed()
    {
        return mProcessed;
    }

    /**
     * Sets the receiver's flag indicating that processing is ongoing
     * to the given value.
     *
     * @param processing The flag.
     */

    private void setProcessing
        (boolean processing)
    {
        mProcessing=processing;
    }

    /**
     * Returns the receiver's flag indicating that processing is
     * ongoing.
     *
     * @return The flag.
     */

    private boolean getProcessing()
    {
        return mProcessing;
    }

    /**
     * Asserts that the receiver is not processed.
     *
     * @throws I18NRuntimeException Thrown if it is.
     */

    protected void assertNotProcessed()
        throws I18NRuntimeException
    {
        if (getProcessed()) {
            throw new I18NRuntimeException(Messages.LAZY_ALREADY_PROCESSED);
        }
    }

    /**
     * Ensures that the receiver is already processed, possibly by
     * invoking {@link #process()}.
     *
     * @throws I18NRuntimeException Thrown if a nested invocation
     * occurs.
     */

    protected void ensureProcessed()
    {
        synchronized (this) {
            if (getProcessed()) {
                return;
            }
            if (getProcessing()) {
                throw new I18NRuntimeException(Messages.LAZY_IN_PROCESS);
            }
            setProcessing(true);
            try {
                process();
            } finally {
                setProcessing(false);
            }
            setProcessed(true);
        }
    }

    /**
     * Processes the receiver. This method will only be called once
     * through {@link #ensureProcessed()}.
     */

    protected abstract void process();
}
