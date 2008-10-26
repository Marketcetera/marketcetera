package org.marketcetera.event;

import org.marketcetera.marketdata.DataRequest;

import quickfix.Message;

public class MessageEvent
    extends EventBase
{
    private final DataRequest request;
    private final Message message;
    public MessageEvent()
    {
        this((DataRequest)null);
    }        
    public MessageEvent(DataRequest inRequest)
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
    public DataRequest getRequest()
    {
        return request;
    }
    public Message getMessage()
    {
        return message;
    }
}