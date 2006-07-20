package org.marketcetera.jcyclone;

import org.jcyclone.core.handler.EventHandlerException;
import org.jcyclone.core.handler.ISingleThreadedEventHandler;
import org.jcyclone.core.cfg.IConfigData;
import org.jcyclone.core.queue.IElement;
import org.marketcetera.core.LoggerAdapter;

import java.util.List;

/**
 * Abstract superclass for all JCyclone stages
 * @author Toli Kuznets
 * @version $Id$
 */
public abstract class JCycloneStageBase extends JCycloneSource implements ISingleThreadedEventHandler  {
    public void init(IConfigData config) throws Exception {
        if(LoggerAdapter.isInfoEnabled(this)) {
            LoggerAdapter.info("Initializing stage "+config.getStage().getName(), this);
        }
        String nextHandlerName = config.getString(JCycloneConstants.NEXT_STAGE);
        if(nextHandlerName!=null) {
            setNextStage(config.getManager().getStage(nextHandlerName).getSink(), nextHandlerName);
        } else {
            if(LoggerAdapter.isDebugEnabled(this)) {
                LoggerAdapter.debug("No next stage for "+config.getStage().getName(), this);
            }
        }
    }

    /** Basic implementation that just calls through to {@link #handleEvent} */
    public void handleEvents(List events) throws EventHandlerException
    {
        for (Object o : events) {
            handleEvent((IElement)o);
        }
    }


}
