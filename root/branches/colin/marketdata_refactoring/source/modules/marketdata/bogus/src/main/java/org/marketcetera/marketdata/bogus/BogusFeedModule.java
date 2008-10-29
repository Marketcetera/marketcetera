package org.marketcetera.marketdata.bogus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.EventBase;
import org.marketcetera.marketdata.MarketDataFeedTokenSpec;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.IllegalRequestParameterValue;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.RequestID;
import org.marketcetera.module.UnsupportedRequestParameterType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * StrategyAgent module for {@link BogusFeed}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
@ClassVersion("$Id:$")  //$NON-NLS-1$
public final class BogusFeedModule
        extends Module
        implements DataEmitter
{
    /**
     * Create a new BogusFeedEmitter instance.
     * 
     * @throws CoreException 
     */
    BogusFeedModule()
        throws CoreException
    {
        super(BogusFeedModuleFactory.INSTANCE_URN,
              false);
        feed = BogusFeedFactory.getInstance().getMarketDataFeed();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected final void preStart()
            throws ModuleException
    {
        feed.start();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected final void preStop()
            throws ModuleException
    {
        feed.stop();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.RequestID)
     */
    @Override
    public final void cancel(RequestID inRequestID)
    {
        synchronized(tokens) {
            BogusFeedToken token = tokens.remove(inRequestID);
            assert(token != null);
            token.cancel();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#requestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    public final void requestData(DataRequest inRequest,
                                  final DataEmitterSupport inSupport)
            throws UnsupportedRequestParameterType, IllegalRequestParameterValue
    {
        Object requestPayload = inRequest.getData();
        org.marketcetera.marketdata.DataRequest request = null;
        if(requestPayload == null) {
            throw new IllegalRequestParameterValue(getURN(),
                                                   null);
        }
        if(requestPayload instanceof String) {
            try {
                request = org.marketcetera.marketdata.DataRequest.newRequestFromString((String)requestPayload);
            } catch (Exception e) {
                throw new IllegalRequestParameterValue(getURN(),
                                                       requestPayload);
            }
        } else if (requestPayload instanceof org.marketcetera.marketdata.DataRequest) {
            request = (org.marketcetera.marketdata.DataRequest)requestPayload;
        } else {
            throw new UnsupportedRequestParameterType(getURN(),
                                                      requestPayload);
        }
        try {
            ISubscriber subscriber = new ISubscriber() {
                @Override
                public boolean isInteresting(Object inData)
                {
                    return inData instanceof EventBase;
                }
                @Override
                public void publishTo(Object inEvent)
                {
                    inSupport.send(inEvent);
                }
            };
            MarketDataFeedTokenSpec<BogusFeedCredentials> spec = MarketDataFeedTokenSpec.generateTokenSpec(BogusFeedCredentials.getInstance(),
                                                                                                           request,
                                                                                                           Arrays.asList(new ISubscriber[] { subscriber }));
            synchronized(tokens) {
                tokens.put(inSupport.getRequestID(),
                           feed.execute(spec));
            }
        } catch (CoreException e) {
            inSupport.dataEmitError(e.getI18NBoundMessage(),
                                    true);
        }
    }
    /**
     * tokens for active data requests
     */
    private final Map<RequestID,BogusFeedToken> tokens = new HashMap<RequestID,BogusFeedToken>();
    /**
     * actual market data provider
     */
    private final BogusFeed feed;
}
