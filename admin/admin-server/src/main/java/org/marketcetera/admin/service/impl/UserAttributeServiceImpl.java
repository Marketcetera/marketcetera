package org.marketcetera.admin.service.impl;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.Validate;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserAttribute;
import org.marketcetera.admin.UserAttributeFactory;
import org.marketcetera.admin.UserAttributeType;
import org.marketcetera.admin.dao.PersistentUserAttribute;
import org.marketcetera.admin.dao.PersistentUserAttributeDao;
import org.marketcetera.admin.service.UserAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.collect.Lists;

/* $License$ */

/**
 * Provides services for <code>UserAttribute</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.2.0
 */
@Transactional(readOnly=true,propagation=Propagation.REQUIRED)
public class UserAttributeServiceImpl
        implements UserAttributeService
{
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(userAttributeDao);
        Validate.notNull(userAttributeFactory);
    }
    /**
     * Stops the object.
     */
    @PreDestroy
    public void stop()
    {
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.irouter.service.UserAttributeService#save(com.marketcetera.tiaacref.systemmodel.UserAttribute)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public UserAttribute save(UserAttribute inUserAttribute)
    {
        PersistentUserAttribute pUserAttribute;
        if(inUserAttribute instanceof PersistentUserAttribute) {
            pUserAttribute = (PersistentUserAttribute)inUserAttribute;
        } else {
            pUserAttribute = (PersistentUserAttribute)userAttributeFactory.create(inUserAttribute);
        }
        return userAttributeDao.save(pUserAttribute);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.service.UserAttributeService#delete(org.marketcetera.admin.UserAttribute)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void delete(UserAttribute inUserAttribute)
    {
        PersistentUserAttribute pUserAttribute = userAttributeDao.findByUserAndUserAttributeType(inUserAttribute.getUser(),
                                                                                                 inUserAttribute.getAttributeType());
        if(pUserAttribute != null) {
            userAttributeDao.delete(pUserAttribute);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.service.UserAttributeService#getUserAttribute(com.marketcetera.admin.User, com.marketcetera.admin.UserAttributeType)
     */
    @Override
    public UserAttribute getUserAttribute(User inUser,
                                          UserAttributeType inUserAttributeType)
    {
        return userAttributeDao.findByUserAndUserAttributeType(inUser,
                                                               inUserAttributeType);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.service.UserAttributeService#getAllByUser(org.marketcetera.admin.User)
     */
    @Override
    public Collection<? extends UserAttribute> getAllByUser(User inUser)
    {
        return userAttributeDao.findByUser(inUser);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.service.UserAttributeService#deleteAllByUser(org.marketcetera.admin.User)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void deleteAllByUser(User inUser)
    {
        userAttributeDao.deleteAll(userAttributeDao.findByUser(inUser));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.service.UserAttributeService#saveAll(org.marketcetera.admin.User, java.util.Collection)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public Collection<? extends UserAttribute> saveAll(User inUser,
                                                       Collection<UserAttribute> inUserAttributes)
    {
        Collection<PersistentUserAttribute> entities = Lists.newArrayList();
        for(UserAttribute userAttribute : inUserAttributes) {
            PersistentUserAttribute pUserAttribute;
            if(userAttribute instanceof PersistentUserAttribute) {
                pUserAttribute = (PersistentUserAttribute)userAttribute;
            } else {
                pUserAttribute = new PersistentUserAttribute(userAttribute);
            }
            entities.add(pUserAttribute);
        }
        return userAttributeDao.saveAll(entities);
    }
    /**
     * Get the userAttributeDao value.
     *
     * @return a <code>PersistentUserAttributeDao</code> value
     */
    public PersistentUserAttributeDao getUserAttributeDao()
    {
        return userAttributeDao;
    }
    /**
     * Sets the userAttributeDao value.
     *
     * @param inUserAttributeDao a <code>PersistentUserAttributeDao</code> value
     */
    public void setUserAttributeDao(PersistentUserAttributeDao inUserAttributeDao)
    {
        userAttributeDao = inUserAttributeDao;
    }
    /**
     * Get the userAttributeFactory value.
     *
     * @return a <code>UserAttributeFactory</code> value
     */
    public UserAttributeFactory getUserAttributeFactory()
    {
        return userAttributeFactory;
    }
    /**
     * Sets the userAttributeFactory value.
     *
     * @param inUserAttributeFactory a <code>UserAttributeFactory</code> value
     */
    public void setUserAttributeFactory(UserAttributeFactory inUserAttributeFactory)
    {
        userAttributeFactory = inUserAttributeFactory;
    }
    /**
     * provides access to the user attribute data store
     */
    @Autowired
    private PersistentUserAttributeDao userAttributeDao;
    /**
     * creates user attribute objects
     */
    @Autowired
    private UserAttributeFactory userAttributeFactory;
}
