package org.marketcetera.executioncontainer;

import org.marketcetera.core.AbstractSpringApplication;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides a base application for Marketcetera.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ExecutionContainer.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: ExecutionContainer.java 82384 2012-07-20 19:09:59Z colin $")
public class ExecutionContainer
        extends AbstractSpringApplication
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.AbstractSpringApplication#getLoggerCategory()
     */
    @Override
    protected Class<? extends AbstractSpringApplication> getLoggerCategory()
    {
        return ExecutionContainer.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.AbstractSpringApplication#getName()
     */
    @Override
    protected String getName()
    {
        return "Execution Container";
    }
    /**
     * Main routine.
     *
     * @param inArgs a <code>String[]</code> value
     */
    public static void main(String[] inArgs)
    {
        ExecutionContainer app = new ExecutionContainer();
        app.start();
        try {
            app.waitForever();
        } catch (InterruptedException e) {
            app.stop();
        }
    }
}
