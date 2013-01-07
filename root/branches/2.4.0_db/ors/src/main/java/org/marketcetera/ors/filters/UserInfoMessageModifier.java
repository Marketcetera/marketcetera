package org.marketcetera.ors.filters;

import java.util.Map;

import org.marketcetera.core.CoreException;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.ors.info.SessionInfo;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;

import quickfix.Message;
import quickfix.field.SenderSubID;

/* $License$ */

/**
 * Sets the SenderSubID on outgoing messages according to the Marketcetera user that initiated the order.
 *
 * @author Sameer Patil
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class UserInfoMessageModifier
        implements SessionAwareMessageModifier
{
    /* (non-Javadoc)
     * @see org.marketcetera.ors.filters.MessageModifier#modifyMessage(quickfix.Message, org.marketcetera.ors.history.ReportHistoryServices, org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor)
     */
    @Override
    public boolean modifyMessage(Message inMessage,
                                 ReportHistoryServices inHistoryServices,
                                 FIXMessageAugmentor inAugmentor)
            throws CoreException
    {
        String orsUserName = null;
        if(sessionInfo != null) {
            SimpleUser currentUser = (SimpleUser)sessionInfo.getValue(SessionInfo.ACTOR);
            if(currentUser == null) {
                Messages.NO_SESSION_USERNAME.warn(this);
                return false;
            }
            orsUserName = currentUser.getName();
        }
        String brokerUserName = marketceteraToBrokerUserMap.get(orsUserName);
        if(brokerUserName ==null) {
            Messages.NO_BROKER_USERNAME.warn(this,
                                             orsUserName);
            brokerUserName = orsUserName;
        }
        inMessage.getHeader().setField(new SenderSubID(brokerUserName));
        return true;
    }
    /**
     * Sets the Marketcetera to Broker user map property.
     *
     * @param inMarketceteraToBrokerUserMap a <code>Map&lt;String,String&gt;</code> value
     */
    public void setMarketceteraToBrokerUserMap(Map<String,String> inMarketceteraToBrokerUserMap)
    {
        marketceteraToBrokerUserMap = inMarketceteraToBrokerUserMap;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.filters.SessionAwareMessageModifier#getSessionInfo()
     */
    @Override
    public SessionInfo getSessionInfo()
    {
        return sessionInfo;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.filters.SessionAwareMessageModifier#setSessionInfo(org.marketcetera.ors.info.SessionInfo)
     */
    @Override
    public void setSessionInfo(SessionInfo inSessionInfo)
    {
        sessionInfo = inSessionInfo;
    }
    /**
     * session information value
     */
    private volatile SessionInfo sessionInfo;
    /**
     * stores broker-specific user identifiers keyed by Marketcetera username
     */
    private volatile Map<String,String> marketceteraToBrokerUserMap;
}
