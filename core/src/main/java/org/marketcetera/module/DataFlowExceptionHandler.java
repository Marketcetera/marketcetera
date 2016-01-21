package org.marketcetera.module;

import java.io.Serializable;

/* $License$ */

/**
 * Provides a callback for exceptions generated during data flow transactions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface DataFlowExceptionHandler
        extends Serializable
{
    /**
     * Execute upon generation of an exception during a data flow transaction.
     *
     * @param inThrowable a <code>Throwable</code> value
     */
    void onException(Throwable inThrowable);
}
