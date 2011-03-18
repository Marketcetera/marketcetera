package org.marketcetera.systemmodel.persistence;

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
public interface UserDao
{
    /**
     * 
     *
     *
     * @return
     */
    public List<User> getAll();
    /**
     * 
     *
     *
     * @param inUserImpl
     */
    public void write(User inUserImpl);
    /**
     *
     *
     * @param inUsername
     * @return
     */
    public User getByName(String inUsername);
}
