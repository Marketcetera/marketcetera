package org.marketcetera.jcyclone;

import org.jcyclone.core.handler.EventHandlerException;
import org.jcyclone.core.queue.IElement;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MessageKey;

import java.util.List;
import java.util.Vector;

/**
 * Final stage that sends output to:
 * 1. JMS
 * 2. FIX
 * 3. DB (potentially)
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class OutputStage extends JCycloneStageBase {
    public void handleEvent(IElement elem) throws EventHandlerException
    {
        if(elem instanceof OutputElement) {
            try {
                ((OutputElement) elem).output();
            } catch (MarketceteraException ex) {
                throw new MarketceteraEventHandlerException(ex.getMessage(), ex);
            }
        } else if(elem instanceof StageElement) {
            LoggerAdapter.error(MessageKey.JCYCLONE_UNEXPECTED_ELEM.getLocalizedMessage(((StageElement)elem).getElement()), this);
        }
    }

    public void handleEvents(List events) throws EventHandlerException {
        Vector<Exception> allExceptions = new Vector<Exception>();
        for (Object o : events) {
            try {
                handleEvent((IElement) o);
            } catch (Exception ex) {
                allExceptions.add(ex);
                LoggerAdapter.error(MessageKey.JCYCLONE_ERROR_OUTPUT_SEND.getLocalizedMessage(), ex, this);
            }
        }
        if(allExceptions.size() > 0) {
            LoggerAdapter.error(MessageKey.JCYCLONE_SEND_NUM_ERRORS.getLocalizedMessage(allExceptions.size()), this);
            throw new MarketceteraEventHandlerException(MessageKey.JCYCLONE_SEND_NUM_ERRORS.getLocalizedMessage(allExceptions.size()));
        }
    }
}
