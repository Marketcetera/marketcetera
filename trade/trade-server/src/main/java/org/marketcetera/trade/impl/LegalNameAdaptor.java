package org.marketcetera.trade.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marketcetera.admin.User;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.trade.AccountUserLookupProvider;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Looks up users based on interpreting the account name to valid username characters.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class LegalNameAdaptor
        implements AccountUserLookupProvider
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.outgoingorder.AccountUserLookupProvider#getUserFor(java.lang.String)
     */
    @Override
    public UserID getUserFor(String inAccount)
    {
        Matcher m = namePattern.matcher(inAccount);
        StringBuffer buffer = new StringBuffer(inAccount.length());
        while(m.find()) {
            buffer.append(m.group(0));
        }
        String username = buffer.toString();
        SLF4JLoggerProxy.debug(this,
                               "{} translated to {}",
                               inAccount,
                               username);
        User user = userService.findByName(username);
        if(user == null) {
            return null;
        }
        return user.getUserID();
    }
    /**
     * provides access to user services
     */
    @Autowired
    private UserService userService;
    /**
     * translation of the regex used for name validation
     */
    private static final Pattern namePattern = Pattern.compile("[\\p{L}\\p{N}- ]{1,255}"); //$NON-NLS-1$
}
