package org.marketcetera.api.security;

import java.util.List;

import org.marketcetera.api.dao.Authority;

/* $License$ */

/**
 * Provides access to and management of <code>Authority</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface AuthorityManagerService
{
    /**
     * Gets the <code>Authority</code> with the given name. 
     *
     * @param inName a <code>String</code> value
     * @return an <code>Authority</code> value
     */
    public Authority getAuthorityByName(String inName);
    /**
     * Gets the <code>Authority</code> with the given id.
     *
     * @param inId a <code>long</code> value
     * @return an <code>Authority</code> value
     */
    public Authority getAuthorityById(long inId);
    /**
     * Gets all <code>Authority</code> values.
     *
     * @return a <code>List&lt;Authority&gt;</code> value
     */
    public List<Authority> getAllAuthorities();
    /**
     * Adds the given <code>Authority</code> value.
     *
     * @param inAuthority an <code>Authority</code> value
     */
    public void addAuthority(Authority inAuthority);
    /**
     * Saves the given <code>Authority</code> value.
     *
     * @param inAuthority an <code>Authority</code> value
     */
    public void saveAuthority(Authority inAuthority);
    /**
     * Deletes the given <code>Authority</code> value.
     *
     * @param inAuthority an <code>Authority</code> value
     */
    public void deleteAuthority(Authority inAuthority);
}
