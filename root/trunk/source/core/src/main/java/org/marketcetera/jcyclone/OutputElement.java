package org.marketcetera.jcyclone;

import org.marketcetera.core.MarketceteraException;

/**
 * Abstract wrapper for an output version of the message that is
 * passed through the JCyclone stages
 * Subclasses overwrite the {@link #output} method with the concrete
 * implementation - could be out to a JMS Queue/Topic or an
 * outgoing FIX connection
 * @author Toli Kuznets
 * @version $Id$
 */
public abstract class OutputElement extends StageElement{
    public OutputElement(Object elem) {
        super(elem);
    }

    public abstract void output() throws MarketceteraException;
}
