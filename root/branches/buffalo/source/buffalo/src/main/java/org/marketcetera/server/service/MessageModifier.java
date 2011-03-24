package org.marketcetera.server.service;

import org.marketcetera.util.misc.ClassVersion;

import quickfix.Message;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface MessageModifier
{
    /**
     * 
     *
     *
     * @param inMessage
     */
    public void modify(Message inMessage);
}
