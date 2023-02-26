package org.marketcetera.ui.service.admin;

import java.util.Properties;

import org.marketcetera.admin.UserAttribute;
import org.marketcetera.admin.UserAttributeType;
import org.marketcetera.core.Util;
import org.marketcetera.ui.service.DisplayLayoutService;
import org.marketcetera.ui.service.ServiceManager;
import org.marketcetera.ui.service.SessionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Provides display layout services for the admin client.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class AdminClientDisplayLayoutService
        implements DisplayLayoutService
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.DisplayLayoutService#getDisplayLayout()
     */
    @Override
    public Properties getDisplayLayout()
    {
        AdminClientService adminClientService = serviceManager.getService(AdminClientService.class);
        UserAttribute userAttribute = adminClientService.getUserAttribute(SessionUser.getCurrent().getUsername(),
                                                                          UserAttributeType.DISPLAY_LAYOUT);
        if(userAttribute == null) {
            return new Properties();
        } else {
            return Util.propertiesFromString(userAttribute.getAttribute());
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.DisplayLayoutService#setDisplayLayout(java.util.Properties)
     */
    @Override
    public void setDisplayLayout(Properties inDisplayLayout)
    {
        AdminClientService adminClientService = serviceManager.getService(AdminClientService.class);
        adminClientService.setUserAttribute(SessionUser.getCurrent().getUsername(),
                                            UserAttributeType.DISPLAY_LAYOUT,
                                            Util.propertiesToString(inDisplayLayout));
    }
    /**
     * provides access to client services
     */
    @Autowired
    private ServiceManager serviceManager;
}
