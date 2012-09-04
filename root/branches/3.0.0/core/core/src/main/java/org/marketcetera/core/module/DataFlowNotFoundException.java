package org.marketcetera.core.module;

import org.marketcetera.core.util.log.I18NBoundMessage;

/* $License$ */
/**
 * This exception is thrown when a data flow cannot be found.
 *
 * @version $Id: DataFlowNotFoundException.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
public class DataFlowNotFoundException extends DataFlowException {
    /**
     * Creates an instance.
     *
     * @param inMessage the error message.
     */
    DataFlowNotFoundException(I18NBoundMessage inMessage) {
        super(inMessage);
    }

    private static final long serialVersionUID = -6329741564628010597L;
}
