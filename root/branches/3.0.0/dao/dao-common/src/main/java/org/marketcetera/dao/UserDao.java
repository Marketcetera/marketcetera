package org.marketcetera.dao;

import java.util.List;

import org.marketcetera.core.systemmodel.User;
import org.marketcetera.core.attributes.ClassVersion;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;

/* $License$ */

/**
 * Provides data access for {@link User} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: UserDao.java 82354 2012-05-11 17:46:11Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: UserDao.java 82354 2012-05-11 17:46:11Z colin $")
public interface UserDao
        extends UserDetailsService
{
    /**
     * Gets the <code>User</code> corresponding to the given username.
     *
     * @param inUsername a <code>String</code> value
     * @return a <code>User</code> value
     */
    public User getByName(String inUsername);
    /**
     * Adds the given <code>User</code> to the database.
     *
     * @param inData a <code>User</code> value
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void add(User inData);
    /**
     * Saves the given <code>User</code> to the database.
     *
     * @param inData a <code>User</code> value
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void save(User inData);
    /**
     * Deletes the given <code>User</code> from the database.
     *
     * @param inData a <code>User</code> value
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(User inData);
    /**
     * Gets the <code>User</code> corresponding to the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>User</code> value
     */
    public User getById(long inId);
    /**
     * Gets all <code>User</code> values.
     *
     * @return a <code>List&lt;User&gt;</code> value
     */
    public List<User> getAll();
}
