package org.marketcetera.fix;

/* $License$ */

/**
 * Transmits a message related to FIX session restore to an executor that can process the message.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SessionRestorePayloadHandler<T extends SessionRestorePayload>
{
    /**
     * Submits the given message.
     *
     * @param inMessage a <code>T</code> value
     */
    void submit(T inMessage);
}
