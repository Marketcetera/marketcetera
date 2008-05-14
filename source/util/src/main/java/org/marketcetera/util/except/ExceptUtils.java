package org.marketcetera.util.except;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage;

/**
 * General-purpose utilities.
 * 
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public final class ExceptUtils
{

    // CLASS METHODS.

    /**
     * Checks whether the calling thread has been interrupted, and, if
     * so, throws an exception built using the {@link
     * InterruptedException#InterruptedException()} constructor. The
     * interrupted status of the thread is cleared.
     *
     * @throws InterruptedException Thrown if the calling thread
     * was interrupted.
     */

    public static void checkInterruption()
        throws InterruptedException
    {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    /**
     * Checks whether the calling thread has been interrupted, and, if
     * so, throws an exception built using the {@link
     * InterruptedException#InterruptedException(String)}
     * constructor. The interrupted status of the thread is cleared.
     *
     * @param message The message.
     *
     * @throws InterruptedException Thrown if the calling thread
     * was interrupted.
     */

    public static void checkInterruption
        (String message)
        throws InterruptedException
    {
        if (Thread.interrupted()) {
            throw new InterruptedException(message);
        }
    }

    /**
     * Checks whether the calling thread has been interrupted, and, if
     * so, throws an exception built using the {@link
     * InterruptedException#InterruptedException()} constructor. The
     * given underlying cause is set on the thrown exception. The
     * interrupted status of the thread is cleared.
     *
     * @param cause The cause.
     *
     * @throws InterruptedException Thrown if the calling thread
     * was interrupted.
     */

    public static void checkInterruption
        (Throwable cause)
        throws InterruptedException
    {
        if (Thread.interrupted()) {
            InterruptedException ex=new InterruptedException();
            ex.initCause(cause);
            throw ex;
        }
    }

    /**
     * Checks whether the calling thread has been interrupted, and, if
     * so, throws an exception built using the {@link
     * InterruptedException#InterruptedException(String)}
     * constructor. The given underlying cause is set on the thrown
     * exception. The interrupted status of the thread is cleared.
     *
     * @param cause The cause.
     * @param message The message.
     *
     * @throws InterruptedException Thrown if the calling thread
     * was interrupted.
     */

    public static void checkInterruption
        (Throwable cause,
         String message)
        throws InterruptedException
    {
        if (Thread.interrupted()) {
            InterruptedException ex=new InterruptedException(message);
            ex.initCause(cause);
            throw ex;
        }
    }

    /**
     * Swallows the given throwable. It logs the given parameterized
     * message and throwable under the given logging category at the
     * warning level via the given logger proxy. Also, if the given
     * throwable is an instance of {@link InterruptedException}, then
     * the calling thread is interrupted.
     * 
     * @param throwable The throwable.
     * @param logger The logger proxy.
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     *
     * @throws InterruptedException Thrown if the calling thread
     * was interrupted.
     */

    public static void swallow
        (Throwable throwable,
         I18NLoggerProxy logger,
         Object category,
         I18NMessage message,
         Object... params)
    {
        logger.warn(category,throwable,message,params);
        if (throwable instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Swallows the given throwable. It logs a standard message
     * alongside the throwable at the warning level. Also, if the
     * given throwable is an instance of {@link InterruptedException},
     * then the calling thread is interrupted.
     * 
     * @param throwable The throwable.
     *
     * @throws InterruptedException Thrown if the calling thread
     * was interrupted.
     */

    public static void swallow
        (Throwable throwable)
    {
        swallow(throwable,Messages.LOGGER,ExceptUtils.class,
                Messages.THROWABLE_IGNORED);
    }


    // CONSTRUCTORS.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private ExceptUtils() {}
}
