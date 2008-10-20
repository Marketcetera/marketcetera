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

import quickfix.InvalidMessage;
import quickfix.Message;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
public final class BogusFeedModule
        extends Module
        implements DataEmitter
{
    /**
     * Create a new BogusFeedEmitter instance.
     * @throws CoreException 
     */
    BogusFeedModule()
        throws CoreException
    {
        super(BogusFeedModuleFactory.INSTANCE_URN,
              true);
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
            if(token != null) {
                token.cancel();
            } else {
                // TODO this should not happen
            }
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
        Object obj = inRequest.getData();
        Message query = null;
        if(obj == null) {
            throw new IllegalRequestParameterValue(getURN(),
                                                   null);
        }
        if(obj instanceof String) {
            try {
                query = new Message((String)obj);
            } catch (InvalidMessage e) {
                throw new IllegalRequestParameterValue(getURN(),
                                                       obj,
                                                       e);
            }
        } else if (obj instanceof Message) {
            query = (Message)obj;
        } else {
            throw new UnsupportedRequestParameterType(getURN(),
                                                      obj);
        }
        //Submit the market data request to active API and arrange
        // for it to invoke inSupport.send(market_data_event);
        // whenever it has market data.
        //
        //invoke inSupport.dataEmitError(error_false, false);
        // to indicate errors that do not interrupt the data flow
        //
        //invoke inSupport.dataEmitError(error_false, true);
        // to indicate errors that interrupt the data flow and request
        // the framework to stop that data flow.
        System.out.println("Query is " + query);
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
                                                                                                           query,
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
    private final Map<RequestID,BogusFeedToken> tokens = new HashMap<RequestID,BogusFeedToken>();
    private final BogusFeed feed;
}
