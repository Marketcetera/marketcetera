package org.marketcetera.fix;

import java.util.Collection;

import org.marketcetera.brokers.Broker;
import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.Message;

/* $License$ */

/**
 * Manages a set of {@link MessageModifier} objects and applies them to messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MessageModifierManager
{
    /**
     * Applies the given message modifiers to the given message under the aegis of the given broker.
     *
     * @param inMessage a <code>Message</code> value
     * @param inBroker a <code>Broker</code> value
     * @param inModifiers a <code>Collection&lt;MessageModifier&gt;</code> value
     * @return a <code>boolean</code> value if the message was modified
     * @throw OrderIntercepted if the order was intercepted by a modifier
     */
    public static boolean applyMessageModifiers(Message inMessage,
                                                Broker inBroker,
                                                Collection<MessageModifier> inModifiers)
    {
        boolean modified = false;
        for(MessageModifier messageModifier : inModifiers) {
            try {
                modified |= messageModifier.modify(inBroker,
                                                   inMessage);
            } catch (OrderIntercepted e) {
                SLF4JLoggerProxy.debug(MessageModifierManager.class,
                                       "{} has been intercepted",
                                       inMessage);
                throw e;
            } catch (Exception e) {
                Messages.MODIFICATION_FAILED.warn(MessageModifierManager.class,
                                                  e,
                                                  inMessage,
                                                  inBroker.toString());
            }
        }
        return modified;
    }
}
