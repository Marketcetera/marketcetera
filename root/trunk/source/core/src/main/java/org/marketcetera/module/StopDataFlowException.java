package org.marketcetera.module;

import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * This exception is thrown when the module receiving data cannot
 * receive any more data and wants to stop the data flow thats emitting
 * data into it. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public class StopDataFlowException extends ReceiveDataException {
    private static final long serialVersionUID = 6507623674619493868L;

    /**
     * Creates an instance.
     *
     * @param inMessage the error message
     */
    public StopDataFlowException(I18NBoundMessage inMessage) {
        super(inMessage);
    }

    /**
     * Creates an instance.
     *
     * @param inCause the underlying cause
     * @param inMessage the error message
     */
    public StopDataFlowException(Throwable inCause,
                                 I18NBoundMessage inMessage) {
        super(inCause, inMessage);
    }
}
