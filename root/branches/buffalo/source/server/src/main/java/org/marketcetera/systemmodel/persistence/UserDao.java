package org.marketcetera.systemmodel.persistence;

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
     * @param inUser
     */
    public void write(User inUser);
    /**
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
     * @param inUserID
     * @return
     */
    public User getById(long inUserID);
}
