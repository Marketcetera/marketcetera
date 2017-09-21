package org.marketcetera.modules.ticktock;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.RequestDataException;
import org.marketcetera.module.RequestID;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/* $License$ */

/**
 * Emits time data at regular intervals.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TickTockModule
        extends Module
        implements DataEmitter
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#requestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws RequestDataException
    {
        dataRequests.put(inSupport.getFlowID(),
                         inSupport);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.DataFlowID, org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(DataFlowID inFlowID,
                       RequestID inRequestID)
    {
        dataRequests.invalidate(inFlowID);
    }
    /**
     * Create a new TickTockModule instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     */
    TickTockModule(ModuleURN inURN)
    {
        super(inURN,
              false);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        tickTockService = Executors.newScheduledThreadPool(1);
        token = tickTockService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run()
            {
                DateTime data = new DateTime();
                SLF4JLoggerProxy.trace(TickTockModule.this,
                                       "Sending {}",
                                       data);
                try {
                    for(DataEmitterSupport dataEmitter : dataRequests.asMap().values()) {
                        dataEmitter.send(data);
                    }
                } catch (Exception e) {
                    PlatformServices.handleException(TickTockModule.this,
                                                     "Emitting timestamp",
                                                     e);
                }
            }},1,1,TimeUnit.SECONDS);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        try {
            if(token != null) {
                token.cancel(true);
            }
            token = null;
        } catch (Exception ignored) {}
        try {
            if(tickTockService != null) {
                tickTockService.shutdownNow();
            }
            tickTockService = null;
        } catch (Exception ignored) {}
    }
    /**
     * provides a scheduled service
     */
    private ScheduledExecutorService tickTockService;
    /**
     * holds the scheduled token value
     */
    private ScheduledFuture<?> token;
    /**
     * holds data requests
     */
    private final Cache<DataFlowID,DataEmitterSupport> dataRequests = CacheBuilder.newBuilder().build();
}
