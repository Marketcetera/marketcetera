package org.marketcetera.core.systemmodel.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.core.systemmodel.Authority;
import org.marketcetera.core.systemmodel.AuthorityDao;
import org.marketcetera.core.systemmodel.AuthorityManagerService;

/* $License$ */

/**
 * Provides Authority manager services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AuthorityManagerServiceImpl
        implements AuthorityManagerService
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.AuthorityManagerService#getAuthorityByName(java.lang.String)
     */
    @Override
    public Authority getAuthorityByName(String inName)
    {
        inName = StringUtils.trimToNull(inName);
        Validate.notNull(inName);
        return authorityDao.getByName(inName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.AuthorityManagerService#getAuthorityById(long)
     */
    @Override
    public Authority getAuthorityById(long inId)
    {
        return authorityDao.getById(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.AuthorityManagerService#getAllAuthorities()
     */
    @Override
    public List<Authority> getAllAuthorities()
    {
        return authorityDao.getAll();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.AuthorityManagerService#addAuthority(org.marketcetera.core.systemmodel.Authority)
     */
    @Override
    public void addAuthority(Authority inAuthority)
    {
        Validate.notNull(inAuthority);
        authorityDao.add(inAuthority);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.AuthorityManagerService#saveAuthority(org.marketcetera.core.systemmodel.Authority)
     */
    @Override
    public void saveAuthority(Authority inAuthority)
    {
        Validate.notNull(inAuthority);
        authorityDao.save(inAuthority);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.AuthorityManagerService#deleteAuthority(org.marketcetera.core.systemmodel.Authority)
     */
    @Override
    public void deleteAuthority(Authority inAuthority)
    {
        Validate.notNull(inAuthority);
        authorityDao.delete(inAuthority);
    }
    /**
     * Sets the authorityDao value.
     *
     * @param an <code>AuthorityDao</code> value
     */
    public void setAuthorityDao(AuthorityDao inAuthorityDao)
    {
        authorityDao = inAuthorityDao;
    }
    /**
     * authority DAO value
     */
    private AuthorityDao authorityDao;
}
