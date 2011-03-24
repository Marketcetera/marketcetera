package org.marketcetera.server.security;

import java.util.HashMap;
import java.util.Map;

import org.marketcetera.server.service.UserManager;
import org.marketcetera.systemmodel.User;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DatabaseInitializer
        implements InitializingBean
{
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        for(Map.Entry<String,String> entry : users.entrySet()) {
            User user = new User();
            user.setName(entry.getKey());
            user.setActive(true);
            user.setDescription("Autogenerated user: " + entry.getKey());
            // TODO set groups from value
            userManager.write(user);
            user.setHashedPassword(passwordManager.encodePassword(user,
                                                                  entry.getKey()));
            userManager.write(user);
            SLF4JLoggerProxy.info(DatabaseInitializer.class,
                                  "Added user: {}",
                                  user);
        }
    }
    /**
     * 
     *
     *
     * @param inUsers
     */
    public void setUsers(Map<String,String> inUsers)
    {
        users.clear();
        users.putAll(inUsers);
    }
    /**
     * 
     */
    private final Map<String,String> users = new HashMap<String,String>();
    /**
     * 
     */
    @Autowired
    private PasswordManager passwordManager;
    /**
     * 
     */
    @Autowired
    private UserManager userManager;
}