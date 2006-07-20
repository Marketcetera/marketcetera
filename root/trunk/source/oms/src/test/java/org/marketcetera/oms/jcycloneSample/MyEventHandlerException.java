package org.marketcetera.oms.jcycloneSample;

import org.jcyclone.core.handler.EventHandlerException;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
public class MyEventHandlerException extends EventHandlerException {
    public MyEventHandlerException(Exception nested) {
        super(nested.getMessage());
    }
}
