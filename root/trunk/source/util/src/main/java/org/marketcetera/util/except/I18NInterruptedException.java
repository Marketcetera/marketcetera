package org.marketcetera.util.except;

import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/**
 * An internationalized exception indicating interruption.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NInterruptedException
    extends I18NException
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // CONSTRUCTORS.

    /**
     * Constructs a new throwable with the default interruption
     * message and no underlying cause.
     */

    public I18NInterruptedException()
    {
        super(Messages.THREAD_INTERRUPTED);
    }

    /**
     * Constructs a new throwable with the default interruption
     * message and the given underlying cause.
     *
     * @param cause The cause.
     */

    public I18NInterruptedException
        (Throwable cause)
    {
        super(cause,Messages.THREAD_INTERRUPTED);
    }

    /**
     * Constructs a new throwable with the given message, but without
     * an underlying cause.
     *
     * @param message The message.
     */

    public I18NInterruptedException
        (I18NBoundMessage message)
    {
        super(message);
    }

    /**
     * Constructs a new throwable with the given message and
     * underlying cause.
     *
     * @param cause The cause.
     * @param message The message.
     */

    public I18NInterruptedException
        (Throwable cause,
         I18NBoundMessage message)
    {
        super(cause,message);
    }


    // CLASS METHODS.

    /**
     * Checks whether the calling thread has been interrupted, and, if
     * so, throws an exception built using the associated
     * constructor. The interrupted status of the thread is cleared.
     *
     * @throws I18NInterruptedException Thrown if the calling thread
     * was interrupted.
     */

    public static void checkInterruption()
        throws I18NInterruptedException
    {
        if (Thread.currentThread().isInterrupted()) {
            throw new I18NInterruptedException();
        }
    }

    /**
     * Checks whether the calling thread has been interrupted, and, if
     * so, throws an exception built using the associated
     * constructor. The interrupted status of the thread is cleared.
     *
     * @param cause The cause.
     *
     * @throws I18NInterruptedException Thrown if the calling thread
     * was interrupted.
     */

    public static void checkInterruption
        (Throwable cause)
        throws I18NInterruptedException
    {
        if (Thread.currentThread().isInterrupted()) {
            throw new I18NInterruptedException(cause);
        }
    }

    /**
     * Checks whether the calling thread has been interrupted, and, if
     * so, throws an exception built using the associated
     * constructor. The interrupted status of the thread is cleared.
     *
     * @param message The message.
     *
     * @throws I18NInterruptedException Thrown if the calling thread
     * was interrupted.
     */

    public static void checkInterruption
        (I18NBoundMessage message)
        throws I18NInterruptedException
    {
        if (Thread.currentThread().isInterrupted()) {
            throw new I18NInterruptedException(message);
        }
    }

    /**
     * Checks whether the calling thread has been interrupted, and, if
     * so, throws an exception built using the associated
     * constructor. The interrupted status of the thread is cleared.
     *
     * @param cause The cause.
     * @param message The message.
     *
     * @throws I18NInterruptedException Thrown if the calling thread
     * was interrupted.
     */

    public static void checkInterruption
        (Throwable cause,
         I18NBoundMessage message)
        throws I18NInterruptedException
    {
        if (Thread.currentThread().isInterrupted()) {
            throw new I18NInterruptedException(cause,message);
        }
    }
}
