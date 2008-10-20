package org.marketcetera.marketdata.marketcetera;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.EventBase;
import org.marketcetera.marketdata.FeedStatus;
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
public class MarketceteraFeedModule
        extends Module
        implements DataEmitter, MarketceteraFeedMXBean
{
    /**
     * Create a new MarketceteraFeedEmitter instance.
     * @throws CoreException 
     */
    public MarketceteraFeedModule()
        throws CoreException
    {
        super(MarketceteraFeedModuleFactory.INSTANCE_URN,
              true);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        try {
            credentials = MarketceteraFeedCredentials.getInstance(getURL(),
                                                                  getSenderCompID(),
                                                                  getTargetCompID());
            feed = MarketceteraFeedFactory.getInstance().getMarketDataFeed(credentials);
        } catch (CoreException e) {
            throw new ModuleException(e.getI18NBoundMessage());
        }
        feed.start();
        while(!feed.getFeedStatus().equals(FeedStatus.AVAILABLE)) {
            System.out.println("Sleeping...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println("Feed is at status " + feed.getFeedStatus());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        feed.stop();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(RequestID inRequestID)
    {
        synchronized(tokens) {
            MarketceteraFeedToken token = tokens.remove(inRequestID);
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
    public void requestData(DataRequest inRequest,
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
            MarketDataFeedTokenSpec<MarketceteraFeedCredentials> spec = MarketDataFeedTokenSpec.generateTokenSpec(MarketceteraFeedCredentials.getInstance(getURL(),
                                                                                                                                                          getSenderCompID(),
                                                                                                                                                          getTargetCompID()),
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
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.marketcetera.MarketceteraFeedMXBean#getSenderCompID()
     */
    @Override
    public String getSenderCompID()
    {
        return senderCompID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.marketcetera.MarketceteraFeedMXBean#getTargetCompID()
     */
    @Override
    public String getTargetCompID()
    {
        return targetCompID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.marketcetera.MarketceteraFeedMXBean#getURL()
     */
    @Override
    public String getURL()
    {
        return url;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.marketcetera.MarketceteraFeedMXBean#setSenderCompID(java.lang.String)
     */
    @Override
    public void setSenderCompID(String inSenderCompID)
    {
        senderCompID = inSenderCompID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.marketcetera.MarketceteraFeedMXBean#setTargetCompID(java.lang.String)
     */
    @Override
    public void setTargetCompID(String inTargetCompID)
    {
        targetCompID = inTargetCompID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.marketcetera.MarketceteraFeedMXBean#setURL(java.lang.String)
     */
    @Override
    public void setURL(String inURL)
    {
        url = inURL;
    }
    private String url;
    private String senderCompID;
    private String targetCompID;
    private final Map<RequestID,MarketceteraFeedToken> tokens = new HashMap<RequestID,MarketceteraFeedToken>();
    private MarketceteraFeed feed;
    private MarketceteraFeedCredentials credentials;
}
