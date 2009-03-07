package org.marketcetera.event;

import org.marketcetera.marketdata.MarketDataRequest;

import quickfix.Message;

public class MessageEvent
    extends EventBase
{
    private final MarketDataRequest request;
    private final Message message;
    public MessageEvent()
    {
        this((MarketDataRequest)null);
    }        
    public MessageEvent(MarketDataRequest inRequest)
    {
        super(System.nanoTime(),
              System.currentTimeMillis());
        request = inRequest;
        message = null;
    }
    public MessageEvent(Message inMessage)
    {
        super(System.nanoTime(),
              System.currentTimeMillis());
        message = inMessage;
        request = null;
    }
    public MarketDataRequest getRequest()
    {
        return request;
    }
    public Message getMessage()
    {
        return message;
    }
}