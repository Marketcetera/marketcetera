package org.marketcetera.util.except;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NMessage;

/**
 * An internationalized runtime exception indicating interruption.
 * 
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NInterruptedRuntimeException
    extends I18NRuntimeException
{

    // CONSTRUCTORS.

    /**
     * Constructs a new throwable with the default interruption
     * message and no underlying cause.
     */

    public I18NInterruptedRuntimeException()
    {
        super(Messages.THREAD_INTERRUPTED);
    }

    /**
     * Constructs a new throwable with the default interruption
     * message and the given underlying cause.
     *
     * @param cause The cause.
     */

    public I18NInterruptedRuntimeException
        (Throwable cause)
    {
        super(cause,Messages.THREAD_INTERRUPTED);
    }

    /**
     * Constructs a new throwable with the given message, but without
     * an underlying cause.
     *
     * @param message The message.
     * @param params The message parameters.
     */

    public I18NInterruptedRuntimeException
        (I18NMessage message,
         Object... params)
    {
        super(message,params);
    }

    /**
     * Constructs a new throwable with the given message and
     * underlying cause.
     *
     * @param cause The cause.
     * @param message The message.
     * @param params The message parameters.
     */

    public I18NInterruptedRuntimeException
        (Throwable cause,
         I18NMessage message,
         Object... params)
    {
        super(cause,message,params);
    }


    // CLASS METHODS.

    /**
     * Checks whether the calling thread has been interrupted, and, if
     * so, throws an exception built using the associated
     * constructor. The interrupted status of the thread is cleared.
     *
     * @throws I18NInterruptedRuntimeException Thrown if the calling
     * thread was interrupted.
     */

    public static void checkInterruption()
        throws I18NInterruptedRuntimeException
    {
        if (Thread.interrupted()) {
            throw new I18NInterruptedRuntimeException();
        }
    }

    /**
     * Checks whether the calling thread has been interrupted, and, if
     * so, throws an exception built using the associated
     * constructor. The interrupted status of the thread is cleared.
     *
     * @param cause The cause.
     *
     * @throws I18NInterruptedRuntimeException Thrown if the calling
     * thread was interrupted.
     */

    public static void checkInterruption
        (Throwable cause)
        throws I18NInterruptedRuntimeException
    {
        if (Thread.interrupted()) {
            throw new I18NInterruptedRuntimeException(cause);
        }
    }

    /**
     * Checks whether the calling thread has been interrupted, and, if
     * so, throws an exception built using the associated
     * constructor. The interrupted status of the thread is cleared.
     *
     * @param message The message.
     * @param params The message parameters.
     *
     * @throws I18NInterruptedRuntimeException Thrown if the calling
     * thread was interrupted.
     */

    public static void checkInterruption
        (I18NMessage message,
         Object... params)
        throws I18NInterruptedRuntimeException
    {
        if (Thread.interrupted()) {
            throw new I18NInterruptedRuntimeException(message,params);
        }
    }

    /**
     * Checks whether the calling thread has been interrupted, and, if
     * so, throws an exception built using the associated
     * constructor. The interrupted status of the thread is cleared.
     *
     * @param cause The cause.
     * @param message The message.
     * @param params The message parameters.
     *
     * @throws I18NInterruptedRuntimeException Thrown if the calling
     * thread was interrupted.
     */

    public static void checkInterruption
        (Throwable cause,
         I18NMessage message,
         Object... params)
        throws I18NInterruptedRuntimeException
    {
        if (Thread.interrupted()) {
            throw new I18NInterruptedRuntimeException(cause,message,params);
        }
    }
}
