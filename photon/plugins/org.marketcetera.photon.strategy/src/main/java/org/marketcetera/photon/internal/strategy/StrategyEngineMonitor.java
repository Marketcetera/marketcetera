package org.marketcetera.photon.internal.strategy;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.Validate;
import org.eclipse.core.databinding.observable.DisposeEvent;
import org.eclipse.core.databinding.observable.IDisposeListener;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.swt.widgets.Display;
import org.marketcetera.core.publisher.IPublisher;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.PublisherEngine;
import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/* $License$ */

/**
 * Monitors known strategy engines and notifies subscribers of state changes.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: StrategyEngineMonitor.java 83882 2014-08-01 22:31:54Z colin $
 * @since 1.3.1
 */
public class StrategyEngineMonitor
        implements IPublisher
{
    /**
     * Create a new StrategyEngineMonitor instance.
     *
     * @param inContext a <code>BundleContext</code> value
     */
    public StrategyEngineMonitor(BundleContext inContext)
    {
        context = inContext;
        Validate.notNull(context);
    }
    /**
     * Starts monitoring the strategy engines.
     */
    public void start()
    {
        monitorJob = strategyPoller.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run()
            {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run()
                    {
                        try {
                            if(strategyEnginesList != null) {
                                for(Object engineObj : strategyEnginesList) {
                                    StrategyEngine engine = (StrategyEngine)engineObj;
                                    ConnectionState previousState = engineState.getIfPresent(engine.getName());
                                    if(engine.getConnectionState() != previousState) {
                                        engineChangesPublisher.publish(engine);
                                    }
                                    engineState.put(engine.getName(),
                                                    engine.getConnectionState());
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    }
                });
            }
            private final Cache<String,ConnectionState> engineState = CacheBuilder.newBuilder().build();
        },1000,1000,TimeUnit.MILLISECONDS);
        strategyEnginesServiceListener = new ServiceListener() {
            @Override
            public void serviceChanged(ServiceEvent inEvent)
            {
                ServiceReference<?> reference = inEvent.getServiceReference();
                switch(inEvent.getType()) {
                    case ServiceEvent.REGISTERED:
                        final IStrategyEngines strategyEnginesReference = (IStrategyEngines)context.getService(reference);
                        if(strategyEnginesReference != null) {
                            Display.getDefault().asyncExec(new Runnable() {
                                @Override
                                public void run()
                                {
                                    setInput(strategyEnginesReference.getStrategyEngines());
                                }
                            });
                        }
                        break;
                    case ServiceEvent.UNREGISTERING:
                        setInput(null);
                        break;
                    default:
                        SLF4JLoggerProxy.debug(this,
                                               "Ignoring service event {}",
                                               inEvent);
                }
            }
        };
        String filter = "(objectclass=" + IStrategyEngines.class.getName() + ")";
        try {
            context.addServiceListener(strategyEnginesServiceListener,
                                       filter);
            ServiceReference<?>[] srl = context.getServiceReferences((String)null,
                                                                     filter);
            for (int i = 0; srl != null && i < srl.length; i++) {
                strategyEnginesServiceListener.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED,
                                                                               srl[i]));
            }
        } catch (InvalidSyntaxException e) {
            SLF4JLoggerProxy.error(this,
                                   e);
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#subscribe(org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public void subscribe(ISubscriber inSubscriber)
    {
        engineChangesPublisher.subscribe(inSubscriber);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#unsubscribe(org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public void unsubscribe(ISubscriber inSubscriber)
    {
        engineChangesPublisher.unsubscribe(inSubscriber);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#publish(java.lang.Object)
     */
    @Override
    public void publish(Object inData)
    {
        engineChangesPublisher.publish(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#publishAndWait(java.lang.Object)
     */
    @Override
    public void publishAndWait(Object inData)
            throws InterruptedException, ExecutionException
    {
        engineChangesPublisher.publishAndWait(inData);
    }
    /**
     * Stops monitoring the strategy engines.
     */
    public void stop()
    {
        if(monitorJob != null) {
            monitorJob.cancel(true);
            monitorJob = null;
        }
        if(strategyEnginesServiceListener != null) {
            context.removeServiceListener(strategyEnginesServiceListener);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#getSubscriptionCount()
     */
    @Override
    public int getSubscriptionCount()
    {
        return engineChangesPublisher.getSubscriptionCount();
    }
    public IObservableList getStrategyEngineList()
    {
        return strategyEnginesList;
    }
    /**
     * Records the list of strategy engines to observe.
     *
     * @param inStrategyEnginesList an <code>IObservableList</code> value or <code>null</code> to stop monitoring
     */
    private void setInput(final IObservableList inStrategyEnginesList)
    {
        if(strategyEnginesList != null) {
            strategyEnginesList.dispose();
            strategyEnginesList = null;
        }
        if(inStrategyEnginesList != null) {
            strategyEnginesList = Observables.unmodifiableObservableList(inStrategyEnginesList);
            strategyEnginesList.addDisposeListener(new IDisposeListener() {
                @Override
                public void handleDispose(DisposeEvent staleEvent) {
                    setInput(null);
                }
            });
        }
    }
    /**
     * listens to changes in the SE service
     */
    private ServiceListener strategyEnginesServiceListener;
    /**
     * Input to the common viewer, confined to the UI thread.
     */
    private IObservableList strategyEnginesList;
    /**
     * context used to start the plug-in
     */
    private BundleContext context;
    /**
     * polling job used to monitor strategy engine contents
     */
    private ScheduledFuture<?> monitorJob;
    /**
     * tracks subscribers to changes and manages publications of same
     */
    private final PublisherEngine engineChangesPublisher = new PublisherEngine(true);
    /**
     * scheduler used to periodically poll the SE list for changes in strategy state
     */
    private final ScheduledExecutorService strategyPoller = Executors.newSingleThreadScheduledExecutor();
}
