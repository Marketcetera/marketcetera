package org.marketcetera.trade.impl;

import org.marketcetera.trade.AccountUserLookupProvider;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.IdentifyOwnerStrategy;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Identifies the owner of a message using the account value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AccountOwnerStrategy
        implements IdentifyOwnerStrategy
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.outgoingorder.IdentifyOwnerStrategy#getOwnerOf(quickfix.Message, quickfix.SessionID, org.marketcetera.trade.BrokerID)
     */
    @Override
    public UserID getOwnerOf(Message inMessage,
                             SessionID inSessionId,
                             BrokerID inBrokerId)
    {
        SLF4JLoggerProxy.debug(this,
                               "Determing owner of {} using account",
                               inMessage);
        String account = null;
        if(inMessage.isSetField(quickfix.field.Account.FIELD)) {
            try {
                account = inMessage.getString(quickfix.field.Account.FIELD);
            } catch (FieldNotFound e) {
                throw new RuntimeException(e);
            }
        }
        if(account == null) {
            SLF4JLoggerProxy.debug(this,
                                   "{} has no account value, cannot be used to determine owner",
                                   inMessage);
            return null;
        } else {
            UserID user = accountUserLookupProvider.getUserFor(account);
            if(user == null) {
                SLF4JLoggerProxy.warn(this,
                                      "No user for account value ''{}'', cannot assign ownership from account",
                                       account);
                return null;
            }
            SLF4JLoggerProxy.debug(this,
                                   "Assigning ownership to {} based on account value {}",
                                   user,
                                   account);
            return user;
        }
    }
    /**
     * Get the accountUserLookupProvider value.
     *
     * @return an <code>AccountUserLookupProvider</code> value
     */
    public AccountUserLookupProvider getAccountUserLookupProvider()
    {
        return accountUserLookupProvider;
    }
    /**
     * Sets the accountUserLookupProvider value.
     *
     * @param an <code>AccountUserLookupProvider</code> value
     */
    public void setAccountUserLookupProvider(AccountUserLookupProvider inAccountUserLookupProvider)
    {
        accountUserLookupProvider = inAccountUserLookupProvider;
    }
    /**
     * strategy used to lookup users from an account value
     */
    private AccountUserLookupProvider accountUserLookupProvider;
}
