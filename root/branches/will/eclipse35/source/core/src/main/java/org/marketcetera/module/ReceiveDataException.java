package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */
/**
 * Instances of this exception are thrown when a receiver module is unable to
 * receive data within a data flow. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 * @see DataReceiver#receiveData(DataFlowID, Object) 
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ReceiveDataException extends DataFlowException {
    /**
     * Creates an instance.
     *
     * @param inCause the underlying cause
     */
    public ReceiveDataException(Throwable inCause) {
        super(inCause);
    }

    /**
     * Creates an instance.
     *
     * @param inMessage the exception message.
     */
    public ReceiveDataException(I18NBoundMessage inMessage) {
        super(inMessage);
    }

    /**
     * Creates an instance.
     *
     * @param inCause the underlying cause.
     * @param inMessage the exception message.
     */
    public ReceiveDataException(Throwable inCause, I18NBoundMessage inMessage) {
        super(inCause, inMessage);
    }

    private static final long serialVersionUID = 1L;
}
