package org.marketcetera.oms.jcycloneSample;

import org.jcyclone.core.cfg.IConfigData;
import org.jcyclone.core.handler.EventHandlerException;
import org.jcyclone.core.handler.ISingleThreadedEventHandler;
import org.jcyclone.core.queue.IElement;
import org.jcyclone.core.queue.ISink;
import org.jcyclone.core.queue.SinkException;
import org.marketcetera.core.LoggerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
public class MiddleStage implements ISingleThreadedEventHandler {
    private ISink nextHandler;
    private String callSign = "middle";

    public void init(IConfigData iConfigData) throws Exception {
        String nextHandlerName = iConfigData.getString("nextHandler");
        nextHandler = iConfigData.getManager().getStage(nextHandlerName).getSink();
        if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("MiddleStage: got next handler: "+nextHandlerName, this); }
    }

    public void handleEvent(IElement iElement) throws EventHandlerException {
        if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("incoming event is "+iElement, this); }

        if(iElement instanceof SampleEvent) {
            nextHandler.enqueueLossy(getNextEvent((SampleEvent)iElement));
        }
    }

    public void handleEvents(List list) throws EventHandlerException {
        if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("In handleEvent-s", this); }
        ArrayList<SampleEvent> outList = new ArrayList<SampleEvent>();
        try {
            for (Object o : list) {
                if(o instanceof SampleEvent) {
                    outList.add(getNextEvent((SampleEvent)o));
                }

            }
            nextHandler.enqueueMany(outList);
        } catch (SinkException ex) {
            throw new MyEventHandlerException(ex);
        }
    }

    public void destroy() throws Exception {
        if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("in destroy", this); }
    }

    // append stage2 data
    private SampleEvent getNextEvent(SampleEvent in)
    {
        String msg = in.getData();
        return new SampleEvent(msg + "--"+callSign);
    }
}
