package org.marketcetera.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import static org.junit.Assert.*;

/* $License$ */

/**
 * Tests {@link org.marketcetera.core.container.AbstractSpringApplication}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AbstractSpringApplicationTest.java 82306 2012-02-29 23:18:25Z colin $
 * @since $Release$
 */
@Ignore
public class AbstractSpringApplicationTest
{
    /**
     * Run before each test. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void before()
            throws Exception
    {
        beanName = "someName-" + System.nanoTime();
        beanValue = "some value " + System.nanoTime();
        mockApplication = new MockSpringApplication();
        mockApplication.start();
    }
    /**
     * Run after each test. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void after()
            throws Exception
    {
        mockApplication.stop();
    }
    /**
     * Tests the start and stop methods.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testStartAndStop()
            throws Exception
    {
        assertTrue(mockApplication.isRunning());
        mockApplication.start();
        assertTrue(mockApplication.isRunning());
        mockApplication.stop();
        assertFalse(mockApplication.isRunning());
        mockApplication.stop();
        assertFalse(mockApplication.isRunning());
    }
    /**
     * Tests what happens if an exception is thrown by an
     * overriden call to {@link org.marketcetera.core.container.AbstractSpringApplication#doStartingMessage()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDoStartingMessageThrows()
            throws Exception
    {
        mockApplication.stop();
        mockApplication.startingMessageException = new NullPointerException("This exception is expected");
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                mockApplication.start();
            }
        };
        assertFalse(mockApplication.isRunning());
    }
    /**
     * Tests what happens if a subclass specifies a logger file that does not exist. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testMissingLoggerFile()
            throws Exception
    {
        mockApplication.stop();
        String badFilename = "this-file-does-not-exist";
        File testBadFilename = new File(badFilename);
        assertFalse(testBadFilename.exists());
        mockApplication.loggerFilename = badFilename;
        mockApplication.start();
        assertTrue(mockApplication.isRunning());
    }
    /**
     * Tests what happens if {@link org.marketcetera.core.container.AbstractSpringApplication#createContext()} throws
     * an exception. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testCreateContextThrows()
            throws Exception
    {
        mockApplication.stop();
        mockApplication.createContextException = new NullPointerException("This exception is expected");
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                mockApplication.start();
            }
        };
        assertFalse(mockApplication.isRunning());
    }
    /**
     * Tests what happens if {@link org.marketcetera.core.container.AbstractSpringApplication#createContext()} returns <code>null</code>.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testCreateContextReturnsNull()
            throws Exception
    {
        mockApplication.stop();
        mockApplication.returnNullContext = true;
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_CONTEXT.getText(mockApplication.getName())) {
            @Override
            protected void run()
                    throws Exception
            {
                mockApplication.start();
            }
        };
        assertFalse(mockApplication.isRunning());
    }
    /**
     * Tests {@link org.marketcetera.core.container.AbstractSpringApplication#createContext()} returning a custom context.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testCreateCustomContext()
            throws Exception
    {
        mockApplication.stop();
        GenericApplicationContext customContext = new StaticApplicationContext();
        modifyContext(customContext);
        mockApplication.contextToReturn = customContext;
        mockApplication.start();
        assertTrue(mockApplication.isRunning());
        ConfigurableApplicationContext actualContext = mockApplication.getContext();
        assertNotNull(actualContext);
        assertSame(customContext,
                   actualContext);
        verifyContext(customContext);
        verifyContext((AbstractApplicationContext)actualContext);
    }
    /**
     * Tests {@link org.marketcetera.core.container.AbstractSpringApplication#doStart()} if it throws an exception.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDoStartThrowsException()
            throws Exception
    {
        mockApplication.stop();
        mockApplication.doStartException = new NullPointerException("This exception is expected");
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                mockApplication.start();
            }
        };
        assertFalse(mockApplication.isRunning());
    }
    /**
     * Tests {@link org.marketcetera.core.container.AbstractSpringApplication#doStart()} executing custom code during start.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDoStartExecutesCustom()
            throws Exception
    {
        mockApplication.stop();
        final AtomicBoolean executed = new AtomicBoolean(false);
        assertFalse(executed.get());
        mockApplication.doStartBlock = new Runnable() {
            @Override
            public void run()
            {
                executed.set(true);
            }
        };
        mockApplication.start();
        assertTrue(mockApplication.isRunning());
        assertTrue(executed.get());
    }
    /**
     * Tests that the application context can be modified during start by
     * {@link org.marketcetera.core.container.AbstractSpringApplication#doStart()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDoStartModifiesContext()
            throws Exception
    {
        mockApplication.stop();
        final List<Exception> exceptions = new ArrayList<Exception>();
        mockApplication.doStartBlock = new Runnable() {
            @Override
            public void run()
            {
                ConfigurableApplicationContext context = mockApplication.getContext();
                GenericApplicationContext customContext = new StaticApplicationContext();
                try {
                    modifyContext(customContext);
                } catch (Exception e) {
                    e.printStackTrace();
                    exceptions.add(e);
                }
                customContext.refresh();
                context.setParent(customContext);
            }
        };
        mockApplication.start();
        assertTrue(mockApplication.isRunning());
        assertTrue(exceptions.toString(),
                   exceptions.isEmpty());
        verifyContext((AbstractApplicationContext)mockApplication.getContext());
    }
    /**
     * Tests start behavior if a custom context has a problem. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testBadContext()
            throws Exception
    {
        mockApplication.stop();
        mockApplication.contextCreationBlock = new Callable<ConfigurableApplicationContext>() {
            @Override
            public ConfigurableApplicationContext call()
                    throws Exception
            {
                return new ClassPathXmlApplicationContext(new String[] { "bad_context.xml" } );
            }
        };
        new ExpectedFailure<RuntimeException>() {
            @Override
            protected void run()
                    throws Exception
            {
                mockApplication.start();
            }
        };
        assertFalse(mockApplication.isRunning());
    }
    /**
     * Modifies the given context in a way that can be subsequently measured.
     *
     * @param inContext a <code>GenericApplicationContext</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void modifyContext(final GenericApplicationContext inContext)
            throws Exception
    {
        new ExpectedFailure<NoSuchBeanDefinitionException>() {
            @Override
            protected void run()
                    throws Exception
            {
                inContext.getBean(beanName);
            }
        };
        RootBeanDefinition bean = new RootBeanDefinition(String.class);
        bean.setResourceDescription(AbstractSpringApplicationTest.class.getName());
        ConstructorArgumentValues values = new ConstructorArgumentValues();
        values.addGenericArgumentValue(beanValue);
        bean.setConstructorArgumentValues(values);
        inContext.registerBeanDefinition(beanName,
                                             bean);
        assertNotNull(inContext.getBean(beanName));
    }
    /**
     * Verifies that the given context has been modified by {@link #modifyContext(GenericApplicationContext)}. 
     *
     * @param inContext an <code>AbstractApplicationContext</code> value
     */
    private void verifyContext(AbstractApplicationContext inContext)
    {
        assertNotNull(inContext.getBean(beanName));
    }
    /**
     * test application
     */
    private volatile MockSpringApplication mockApplication;
    /**
     * test spring bean name
     */
    private volatile String beanName;
    /**
     * test spring bean value
     */
    private volatile String beanValue;
}
