package org.marketcetera.server.service;

import java.util.List;

import org.marketcetera.systemmodel.User;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface UserManager
{
    /**
     * 
     *
     *
     * @return
     */
    public List<User> getUsers();
    /**
     * 
     *
     *
     * @param inUsername
     * @return
     */
    public User getByName(String inUsername);
    /**
     * 
     *
     *
     * @param inUser
     */
    public void write(User inUser);
}
