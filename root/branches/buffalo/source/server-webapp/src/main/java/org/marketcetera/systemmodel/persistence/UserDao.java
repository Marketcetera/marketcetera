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
    public List<User> getAll();
    public void write(User inUser);
}
