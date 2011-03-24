package org.marketcetera.server.service;

import java.util.List;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Has {@link MessageModifier} filters that will be applied to incoming and outgoing messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface HasFixMessageModifiers
{
    /**
     * 
     *
     *
     * @return
     */
    public List<MessageModifier> getPreSendMessageModifiers();
    /**
     * 
     *
     *
     * @return
     */
    public List<MessageModifier> getResponseMessageModifiers();
}
