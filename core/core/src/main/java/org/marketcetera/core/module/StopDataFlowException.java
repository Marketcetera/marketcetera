package org.marketcetera.core.module;

import org.marketcetera.core.util.log.I18NBoundMessage;

/* $License$ */
/**
 * This exception is thrown when the module receiving data cannot
 * receive any more data and wants to stop the data flow thats emitting
 * data into it. 
 *
 * @version $Id: StopDataFlowException.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
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
