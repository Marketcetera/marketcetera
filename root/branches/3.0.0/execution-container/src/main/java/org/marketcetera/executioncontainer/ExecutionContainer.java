package org.marketcetera.executioncontainer;

import org.marketcetera.core.container.AbstractSpringApplication;
import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * Provides a base application for Marketcetera.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ExecutionContainer.java 82351 2012-05-04 21:46:58Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: ExecutionContainer.java 82351 2012-05-04 21:46:58Z colin $")
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
