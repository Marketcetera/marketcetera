package org.marketcetera.server.service;

import java.util.List;

import org.marketcetera.systemmodel.User;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
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
