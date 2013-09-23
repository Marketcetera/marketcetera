package org.marketcetera.ors.filters;

import java.util.Map;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.InitializingBean;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.SecurityType;

/* $License$ */

/**
 * Filters orders by asset class.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id: OrderFilter.java 16154 2012-07-14 16:34:05Z colin $")
public class AssetClassFilter
        implements OrderFilter, InitializingBean
{
    /* (non-Javadoc)
     * @see org.marketcetera.ors.filters.OrderFilter#isAccepted(org.marketcetera.ors.filters.OrderFilter.MessageInfo, quickfix.Message)
     */
    @Override
    public boolean isAccepted(MessageInfo inMessageInfo,
                              Message inMessage)
            throws CoreException
    {
        SecurityType securityType = new SecurityType();
        if(inMessage.isSetField(securityType)) {
            try {
                inMessage.getField(securityType);
            } catch (FieldNotFound e) {
                SLF4JLoggerProxy.debug(this,
                                       "No asset class on {}, accepted by default", //$NON-NLS-1$
                                       inMessage);
                return true;
            }
            String assetClass = securityType.getValue();
            UserList userlist = userlists.get(assetClass);
            if(userlist != null) {
                if(userlist.getBlacklist() != null && userlist.getBlacklist().contains(inMessageInfo.getUser().getName())) {
                    SLF4JLoggerProxy.debug(this,
                                           "User {} blacklisted for {}", //$NON-NLS-1$
                                           inMessageInfo.getUser().getName(),
                                           inMessage);
                    throw new CoreException(new I18NBoundMessage2P(Messages.ASSET_CLASS_RESTRICTED,
                                                                   securityType.getValue(),
                                                                   inMessageInfo.getUser().getName()));
                }
                if(userlist.getWhitelist() != null && !userlist.getWhitelist().contains(inMessageInfo.getUser().getName())) {
                    SLF4JLoggerProxy.debug(this,
                                           "User {} not in whitelist for {}", //$NON-NLS-1$
                                           inMessageInfo.getUser().getName(),
                                           inMessage);
                    throw new CoreException(new I18NBoundMessage2P(Messages.ASSET_CLASS_RESTRICTED,
                                                                   securityType.getValue(),
                                                                   inMessageInfo.getUser().getName()));
                }
            }
        }
        return true;
    }
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        if(userlists != null) {
            for(String securityTypeValue : userlists.keySet()) {
                Validate.isTrue(org.marketcetera.trade.SecurityType.getInstanceForFIXValue(securityTypeValue) != org.marketcetera.trade.SecurityType.Unknown,
                        Messages.UNKNOWN_ASSET_CLASS.getText(securityTypeValue));
            }
        }
    }
    /**
     * Get the userlists value.
     *
     * @return a <code>Map&lt;String,UserList&gt;</code> value
     */
    public Map<String,UserList> getUserlists()
    {
        return userlists;
    }
    /**
     * Sets the userlists value.
     *
     * @param inUserlists a <code>Map&lt;String,UserList&gt;</code> value
     */
    public void setUserlists(Map<String,UserList> inUserlists)
    {
        userlists = inUserlists;
    }
    /**
     * allowed and disallowed users by asset class representation
     */
    private Map<String,UserList> userlists;
}
