package org.marketcetera.strategy;

import java.lang.reflect.Constructor;

import org.marketcetera.core.util.misc.ClassVersion;

/* $License$ */

/**
 * Defines the set of strategy languages available.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: Language.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: Language.java 16063 2012-01-31 18:21:55Z colin $")
public enum Language
{
    /**
     * represents a Ruby strategy
     */
    RUBY(RubyExecutor.class),
    /**
     * represents a Java strategy
     */
    JAVA(JavaExecutor.class);
    /**
     * the executor to use to execute strategies of this type
     */
    private final Class<? extends Executor> executorClass;
    /**
     * Returns an executor to use to execute a strategy implemented in this <code>Language</code>.
     *
     * <p>Each invocation of this method is guaranteed to return a unique instance of the given <code>Executor</code>.
     *  
     * @return an <code>Executor</code> value
     */
    Executor getExecutor(Strategy inStrategy)
        throws Exception
    {
        Constructor<? extends Executor> constructor = executorClass.getDeclaredConstructor(Strategy.class);
        constructor.setAccessible(true);
        return constructor.newInstance(inStrategy);
    }
    /**
     * Create a new Language instance.
     *
     * @param inExecutor a <code>Class&lt;? extends Executor&gt;</code> value
     */
    private Language(Class<? extends Executor> inExecutor)
    {
        executorClass = inExecutor;
    }
}
