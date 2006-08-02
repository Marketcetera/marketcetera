package org.marketcetera.oms.jcycloneSample;

import org.jcyclone.core.cfg.IConfigData;
import org.jcyclone.core.handler.EventHandlerException;
import org.jcyclone.core.handler.ISingleThreadedEventHandler;
import org.jcyclone.core.queue.IElement;
import org.jcyclone.core.queue.ISink;
import org.jcyclone.core.queue.SinkException;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.ClassVersion;

import java.util.List;

/**
 * First stage of the 3-stage sample Jcyclone HellowWorld app
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class InitialStage implements ISingleThreadedEventHandler
{
    private ISink nextHandler;

    public void init(IConfigData iConfigData) throws Exception {
        nextHandler = iConfigData.getManager().getStage(iConfigData.getString("nextHandler")).getSink();
        if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("got next handler "+nextHandler, this); }

        // send 3 events on next stage
        nextHandler.enqueue(new SampleEvent("event 1"));
        nextHandler.enqueue(new SampleEvent("event 2"));
        nextHandler.enqueue(new SampleEvent("event 3"));
    }

    public void handleEvent(IElement iElement) throws EventHandlerException {
        // noop - we only generate events
        if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("In handleEvent", this); }
    }

    public void handleEvents(List list) throws EventHandlerException {
        // noop - we only generate events
        if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("In handleEvent-s", this); }
    }

    public void destroy() throws Exception {
        if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("in destroy", this); }
    }

    public void sendOneEvent(String inMsg) throws SinkException
    {
        nextHandler.enqueue(new SampleEvent(inMsg));
    }
}
