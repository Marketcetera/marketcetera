package org.marketcetera.util.except;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.log.I18NMessageProvider;

/**
 * An internationalized exception indicating interruption.
 * 
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NInterruptedException
	extends I18NException
{

    // CONSTRUCTORS.

    /**
     * Constructs a new throwable with the default interruption
     * message and no underlying cause.
     */

	public I18NInterruptedException()
    {
    	super(Messages.PROVIDER,Messages.THREAD_INTERRUPTED);
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
		super(cause,Messages.PROVIDER,Messages.THREAD_INTERRUPTED);
	}

    /**
     * Constructs a new throwable with the given message, but without
     * an underlying cause.
     *
     * @param provider The message provider.
     * @param message The message.
     * @param params The message parameters.
     */

    public I18NInterruptedException
        (I18NMessageProvider provider,
         I18NMessage message,
         Object... params)
	{
    	super(provider,message,params);
	}

    /**
     * Constructs a new throwable with the given message and
     * underlying cause.
     *
     * @param cause The cause.
     * @param provider The message provider.
     * @param message The message.
     * @param params The message parameters.
     */

	public I18NInterruptedException
        (Throwable cause,
         I18NMessageProvider provider,
         I18NMessage message,
         Object... params)
	{
    	super(cause,provider,message,params);
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
        if (Thread.interrupted()) {
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
        if (Thread.interrupted()) {
            throw new I18NInterruptedException(cause);
        }
    }

    /**
     * Checks whether the calling thread has been interrupted, and, if
     * so, throws an exception built using the associated
     * constructor. The interrupted status of the thread is cleared.
     *
     * @param provider The message provider.
     * @param message The message.
     * @param params The message parameters.
     *
     * @throws I18NInterruptedException Thrown if the calling thread
     * was interrupted.
     */

    public static void checkInterruption
        (I18NMessageProvider provider,
         I18NMessage message,
         Object... params)
        throws I18NInterruptedException
    {
        if (Thread.interrupted()) {
            throw new I18NInterruptedException(provider,message,params);
        }
    }

    /**
     * Checks whether the calling thread has been interrupted, and, if
     * so, throws an exception built using the associated
     * constructor. The interrupted status of the thread is cleared.
     *
     * @param cause The cause.
     * @param provider The message provider.
     * @param message The message.
     * @param params The message parameters.
     *
     * @throws I18NInterruptedException Thrown if the calling thread
     * was interrupted.
     */

    public static void checkInterruption
        (Throwable cause,
         I18NMessageProvider provider,
         I18NMessage message,
         Object... params)
        throws I18NInterruptedException
    {
        if (Thread.interrupted()) {
            throw new I18NInterruptedException
                (cause,provider,message,params);
        }
    }
}
