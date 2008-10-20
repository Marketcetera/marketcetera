package org.marketcetera.strategy;

import static org.marketcetera.strategy.Messages.STRATEGY_ALREADY_REGISTERED;
import static org.marketcetera.strategy.Messages.STRATEGY_NOT_FOUND;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Singleton object that manages <code>Strategy</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
@ClassVersion("$Id:$") //$NON-NLS-1$
public class StrategyManager
{
    private static final StrategyManager sInstance = new StrategyManager();
    private final Map<String,StrategyMetaData> sRunningStrategiesByName = new HashMap<String,StrategyMetaData>();
    private final Map<Integer,StrategyMetaData> sAllStrategiesByID = new HashMap<Integer,StrategyMetaData>();
    private final Object sLock = new Object(); 
    private final ExecutorService mExecutorPool = Executors.newCachedThreadPool();
    private final Map<String,StrategyMetaData> mRegisteredStrategies = new HashMap<String,StrategyMetaData>();
    public static StrategyManager getInstance()
    {
        return sInstance;
    }
    public final Set<String> getRegisteredStrategyNames()
    {
        Set<String> result = new HashSet<String>();
        synchronized(mRegisteredStrategies) {
            result.addAll(mRegisteredStrategies.keySet());
        }
        return result;
    }
    public final Set<String> getRunningStrategyNames()
    {
        Set<String> result = new HashSet<String>();
        synchronized(sLock) {
            result.addAll(sRunningStrategiesByName.keySet());
        }
        return result;
    }
    /**
     * Registers a <code>Strategy</code> with the <code>StrategyManager</code>.
     * 
     * <p>To be registered, a <code>Strategy</code> must compile.  The new <code>Strategy</code>
     * will always be in a non-running state. If a <code>Strategy</code> with the given name is already registered, 
     * the new <code>Strategy</code> replaces the existing one, but only if the new <code>Strategy</code>
     * compiles successfully.
     *
     * @param inName a <code>String</code> value containing the name of the <code>Strategy</code>
     * @param inType an <code>int</code> value indicating the source language of the <code>Strategy</code>
     * @param inStrategy a <code>File</code> value indicating the location of the <code>Strategy</code> to execute
     * @throws NullPointerException if <code>inName</code> or <code>inStategy</code> is null
     * @throws IllegalArgumentException if <code>inType</code> does not correspond to a valid language type
     * @throws NoExecutorException if the given type does not have a matching <code>StategyExecutor</code>
     * @throws StrategyAlreadyRegistedException if the given strategy name is already in use by a registered strategy
     * @throws StrategyNotFoundException 
     */
    public final void register(String inName,
                               int inType,
                               File inStrategy)
        throws NoExecutorException, StrategyAlreadyRegistedException, StrategyNotFoundException
    {
        SLF4JLoggerProxy.debug(this,
                               "Registering strategy {} of type {}",
                               inName,
                               inType);
        if(inName == null) {
            throw new NullPointerException();
        }
        if(inStrategy == null) {
            throw new NullPointerException();
        }
        // name and strategy are non-null
        // check type
        StrategyLanguage language;
        try {
            language = StrategyLanguage.values()[inType];
        } catch (ArrayIndexOutOfBoundsException e) {
            // invalid type
            throw new IllegalArgumentException();
        }
        // make sure the strategy can be loaded
        String strategy;
        try {
            strategy = readStrategyFromFile(inStrategy);
        } catch (IOException e) {
            throw new StrategyNotFoundException(e);
        }
        // make sure there isn't already a strategy by this name
        StrategyMetaData strategyMetaData = findData(inName);
        if(strategyMetaData != null) {
            throw new StrategyAlreadyRegistedException(new I18NBoundMessage1P(STRATEGY_ALREADY_REGISTERED,
                                                                              inName));
        }
        // name, strategy, and type are valid, complete the registration process
        doRegister(StrategyMetaData.createStrategyData(inName,
                                                       language,
                                                       strategy));
    }
    /**
     * Unregisters the <code>Strategy</code> with the given name.
     * 
     * <p>If the given name does not correspond to a registered <code>strategy</code>, this call will
     * fail.  If the <code>strategy</code> is registered, it will be halted, if necessary, before it
     * is unregistered.
     *
     * @param inName a <code>String</code> value
     * @throws StrategyNotFoundException if the <code>strategy</code> is not registered 
     */
    public final void unregister(String inName)
        throws StrategyNotFoundException
    {
        if(inName == null) {
            throw new NullPointerException();
        }
        StrategyMetaData data = findData(inName);
        if(data == null) {
            // there is no strategy by this name registered
            throw new StrategyNotFoundException(new I18NBoundMessage1P(STRATEGY_NOT_FOUND,
                                                                       inName));
        }
        doUnregister(data);
    }
    /**
     * Executes the <code>Strategy</code> with the given name.
     *
     * <p>If the given name does not correspond to a registered <code>strategy</code>, this call will
     * fail.  If the <code>strategy</code> is already running, this call does nothing.
     * 
     * @param inName a <code>String</code> value
     * @throws StrategyNotFoundException if the <code>strategy</code> is not registered 
     * @throws StrategyExecutionException 
     */
    public final void execute(String inName)
        throws StrategyNotFoundException, StrategyExecutionException
    {
        if(inName == null) {
            throw new NullPointerException();
        }
        StrategyMetaData data = findData(inName);
        if(data == null) {
            // there is no strategy by this name registered
            throw new StrategyNotFoundException(new I18NBoundMessage1P(STRATEGY_NOT_FOUND,
                                                                       inName));
        }
        if(data.isRunning()) {
            // strategy already running - do nothing
            return;
        }
        StrategyRunner runner = new StrategyRunner(data);
        try {
            mExecutorPool.submit(runner).get();
        } catch (Throwable t) {
            if(t.getCause() != null &&
               t.getCause() instanceof StrategyExecutionException) {
                throw (StrategyExecutionException)t.getCause();
            }
            throw new StrategyExecutionException(t);
        }
    }
    final void reportStrategyRunning(IStrategy inStrategy)
    {
        SLF4JLoggerProxy.debug(StrategyExecutor.class,
                               "{} reports as running",
                               inStrategy);
        synchronized(sLock) {
            int id = inStrategy.getStrategyID();
            StrategyMetaData data = sAllStrategiesByID.get(id);
            assert(data != null);
            assert(!sRunningStrategiesByName.containsKey(data.getName()));
            SLF4JLoggerProxy.debug(StrategyExecutor.class,
                                   "StrategyData is {}",
                                   data);
            sRunningStrategiesByName.put(data.getName(),
                                         data);
        }
    }
    final void reportStrategyStopping(IStrategy inStrategy)
    {
        SLF4JLoggerProxy.debug(StrategyExecutor.class,
                               "{} reports as stopping",
                               inStrategy);
        synchronized(sLock) {
            int id = inStrategy.getStrategyID();
            StrategyMetaData data = sAllStrategiesByID.get(id);
            assert(data != null);
            assert(sRunningStrategiesByName.containsKey(data.getName()));
            sRunningStrategiesByName.remove(data.getName());
        }
    }
    final void reportStrategyUnregistering(StrategyMetaData inStrategy)
    {
        assert(!inStrategy.isRunning());
        synchronized(sLock) {
            if(inStrategy.getStrategy() != null) {
                sAllStrategiesByID.remove(inStrategy.getStrategy().getStrategyID());
                sRunningStrategiesByName.remove(inStrategy.getName());
            } else {
                assert(!sRunningStrategiesByName.containsKey(inStrategy.getName()));
            }
        }
    }
    final void reportStrategyCreation(StrategyMetaData inStrategy)
    {
        assert(inStrategy.getStrategy() != null);
        synchronized(sLock) {
            assert(!sAllStrategiesByID.containsKey(inStrategy.getStrategy().getStrategyID()));
            SLF4JLoggerProxy.debug(StrategyManager.class,
                                   "Registering {}",
                                   inStrategy);
            sAllStrategiesByID.put(inStrategy.getStrategy().getStrategyID(),
                                   inStrategy);
        }
    }
    private StrategyManager()
    {
    }
    final Set<StrategyMetaData> getRunningStrategies()
    {
        Set<StrategyMetaData> result = new HashSet<StrategyMetaData>();
        synchronized(sLock) {
            result.addAll(sRunningStrategiesByName.values());
        }
        return result;
    }
    private void doRegister(StrategyMetaData inData)
        throws NoExecutorException
    {
        synchronized(mRegisteredStrategies) {
            mRegisteredStrategies.put(inData.getName(),
                                      inData);
        }
        // strategy is registered and not running
    }
    private void doUnregister(StrategyMetaData inData)
    {
        synchronized(mRegisteredStrategies) {
            // strategy is registered
            // check to see if the strategy is running
            if(getRunningStrategies().contains(inData)) {
                inData.stop();
            }
            mRegisteredStrategies.remove(inData.getName());
        }
    }
    private StrategyMetaData findData(String inName)
    {
        synchronized(mRegisteredStrategies) {
            return mRegisteredStrategies.get(inName); 
        }
    }
    /**
     * Reads the contents of the given file in the default encoding.
     *
     * @param inStrategy a <code>File</code> value
     * @return a <code>String</code> value containing the contents of the given <code>File</code>
     * @throws IOException if the file cannot be read
     */
    private String readStrategyFromFile(File inStrategy)
        throws IOException
    {
        return FileUtils.readFileToString(inStrategy);
    }
    private static class StrategyRunner
        implements Callable<IStrategy>
    {
        private final StrategyMetaData mStrategyData;
        private StrategyRunner(StrategyMetaData inStrategy)
        {
            mStrategyData = inStrategy;
        }
        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public IStrategy call()
                throws Exception
        {
            mStrategyData.getExecutor().execute();
            return null; // TODO
        }
    }
}
