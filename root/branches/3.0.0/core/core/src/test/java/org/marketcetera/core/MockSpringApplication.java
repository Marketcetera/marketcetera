package org.marketcetera.core;

import java.util.concurrent.Callable;

import org.marketcetera.core.container.AbstractSpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/* $License$ */

/**
 * Sample application used for unit tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MockSpringApplication.java 82307 2012-03-02 03:13:45Z colin $
 * @since $Release$
 */
public class MockSpringApplication
        extends AbstractSpringApplication
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.ApplicationBase#getLoggerCategory()
     */
    @Override
    protected Class<MockSpringApplication> getLoggerCategory()
    {
        return MockSpringApplication.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ApplicationBase#getName()
     */
    @Override
    protected String getName()
    {
        return "MockSpringApplication";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.container.AbstractSpringApplication#doStartingMessage()
     */
    @Override
    protected void doStartingMessage()
    {
        if(startingMessageException != null) {
            throw startingMessageException;
        }
        super.doStartingMessage();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.container.AbstractSpringApplication#getLoggerFilename()
     */
    @Override
    protected String getLoggerFilename()
    {
        if(loggerFilename != null) {
            return loggerFilename;
        }
        return super.getLoggerFilename();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.container.AbstractSpringApplication#createContext()
     */
    @Override
    protected ConfigurableApplicationContext createContext()
    {
        if(createContextException != null) {
            throw createContextException;
        }
        if(contextToReturn != null) {
            return contextToReturn;
        }
        if(contextCreationBlock != null) {
            try {
                return contextCreationBlock.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return returnNullContext ? null : super.createContext();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.container.AbstractSpringApplication#doStart()
     */
    @Override
    protected void doStart()
    {
        if(doStartException != null) {
            throw doStartException;
        }
        if(doStartBlock != null) {
            doStartBlock.run();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.container.AbstractSpringApplication#isContextAutostarted()
     */
    @Override
    protected boolean isContextAutostarted()
    {
        return autostartedContext;
    }
    /**
     * indicates if the context used autostarts itself or needs to be started
     */
    private volatile boolean autostartedContext = false;
    /**
     * exception which can be thrown during {@link #doStartingMessage()}.
     */
    public volatile RuntimeException startingMessageException;
    /**
     * logger filename to use if non-null
     */
    public volatile String loggerFilename;
    /**
     * context object to return if non-null
     */
    public volatile ConfigurableApplicationContext contextToReturn;
    /**
     * indicates that a <code>null</code> context object should be returned
     */
    public volatile boolean returnNullContext;
    /**
     * used to create the context if non-null
     */
    public volatile Callable<ConfigurableApplicationContext> contextCreationBlock;
    /**
     * exception to throw during {@ink #createContextException} if non-null
     */
    public volatile RuntimeException createContextException;
    /**
     * exception to throw during {@link #doStart()} if non-null
     */
    public volatile RuntimeException doStartException;
    /**
     * block to execute during {@link #doStart()} if non-null
     */
    public volatile Runnable doStartBlock;
}
