package org.marketcetera.trade.event;

import org.marketcetera.admin.User;
import org.marketcetera.trade.BrokerID;

/* $License$ */

/**
 * Indicates that an injected FIX message has been received.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleInjectedFixMessageEvent
        extends AbstractFixMessageEvent
        implements InjectedFixMessageEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.admin.HasUser#getUser()
     */
    @Override
    public User getUser()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("InjectedFixAppMessageEvent [user=").append(user).append(", brokerId=")
                .append(getBrokerId()).append(", message=").append(getMessage()).append("]");
        return builder.toString();
    }
    /**
     * Create a new SimpleInjectedFixAppMessageEvent instance.
     *
     * @param inUser a <code>User</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inMessage a <code>quickfix.Message</code> value
     */
    public SimpleInjectedFixMessageEvent(User inUser,
                                         BrokerID inBrokerId,
                                         quickfix.Message inMessage)
    {
        super(inBrokerId,
              inMessage);
        user = inUser;
    }
    /**
     * user value
     */
    private final User user;
}
