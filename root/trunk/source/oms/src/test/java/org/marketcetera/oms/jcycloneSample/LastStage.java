package org.marketcetera.oms.jcycloneSample;

import org.jcyclone.core.cfg.IConfigData;
import org.jcyclone.core.handler.EventHandlerException;
import org.jcyclone.core.handler.ISingleThreadedEventHandler;
import org.jcyclone.core.queue.IElement;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.ClassVersion;

import java.util.List;
import java.util.Vector;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class LastStage implements ISingleThreadedEventHandler {
    private String callSign = "last";
    private Vector<String> allMsgs;

    public LastStage()
    {
        allMsgs = new Vector<String>();
    }

    public void init(IConfigData iConfigData) throws Exception {
        if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("intializing LastStage", this); }
    }

    public void handleEvent(IElement iElement) throws EventHandlerException {
        if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("LastStage: incoming event is "+iElement, this); }
        if(iElement instanceof SampleEvent) {
            String msg = ((SampleEvent)iElement).getData();
            String outgoing = msg + "--"+callSign + "--";
            allMsgs.add(outgoing);
        }
    }

    public void handleEvents(List list) throws EventHandlerException {
        if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("LastStage: In handleEvent-s", this); }
        for (Object o : list) {
            if(o instanceof SampleEvent) {
                String msg = ((SampleEvent)o).getData();
                String outgoing = msg + "--"+callSign;
                allMsgs.add(outgoing);
            }
        }
    }

    public void destroy() throws Exception {
        if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("LastStage: in destroy", this); }
        for (String s : allMsgs) {
            System.out.println(s);
        }
    }

    public Vector<String> getAllMessages() {return allMsgs; }
}

