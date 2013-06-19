package org.marketcetera.strategy;

import org.marketcetera.core.util.misc.ClassVersion;

/* $License$ */

/**
 * Executes a strategy.
 *
 * @version $Id: Executor.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
interface Executor
{
    /**
     * Starts execution of the {@link Strategy}.
     *
     * @return a <code>RunningStrategy</code> value
     * @throws Exception if an error occurs
     */
    RunningStrategy start()
        throws Exception;
    /**
     * Stops execution of a {@link Strategy}.
     *
     * @throws Exception if an error occurs
     */
    void stop()
        throws Exception;
    /**
     * Returns an interpretation of the given exception.
     *
     * @param inE an <code>Exception</code> value
     * @return a <code>String</code> value containing a description of the exception
     */
    String interpretRuntimeException(Exception inE);
    /**
     * Returns the name of the method rendered for the appropriate language.
     *
     * @param inMethodName a <code>String</code> value
     * @return a <code>String</code> value
     */
    String translateMethodName(String inMethodName);
}
