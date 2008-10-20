package org.marketcetera.strategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.marketcetera.strategy.BSFStrategyExecutor.JRubyStrategyExecutor;
import org.marketcetera.strategy.BSFStrategyExecutor.JavaStrategyExecutor;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since $Release$
 */
enum StrategyLanguage
{
    JRUBY(JRubyStrategyExecutor.class),
    CPP(CPPStrategyExecutor.class),
    JAVA(JavaStrategyExecutor.class);
//    JAVASCRIPT(JavaStrategyExecutor.class),
//    JACL(JavaStrategyExecutor.class),
//    JLOG(JavaStrategyExecutor.class),
//    JLISP(JavaStrategyExecutor.class),
//    JYTHON(JavaStrategyExecutor.class);

    private Class<? extends StrategyExecutor> mExecutorClass;
    private StrategyLanguage(Class<? extends StrategyExecutor> inClass)
    {
        mExecutorClass = inClass;
    }
    /**
     * 
     *
     *
     * @param inStrategy
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    IStrategyExecutor getExecutor(StrategyMetaData inStrategy)
        throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
    {
        Constructor<? extends IStrategyExecutor> executorConstructor = mExecutorClass.getDeclaredConstructor(StrategyMetaData.class);
        executorConstructor.setAccessible(true);
        return executorConstructor.newInstance(inStrategy);
    }
}
