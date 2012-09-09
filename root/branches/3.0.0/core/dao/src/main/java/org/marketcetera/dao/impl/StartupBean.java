package org.marketcetera.dao.impl;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.marketcetera.api.dao.*;
import org.marketcetera.api.security.User;
import org.marketcetera.api.systemmodel.SystemObject;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.dao.domain.PersistentPermission;
import org.marketcetera.dao.domain.PersistentRole;
import org.marketcetera.dao.domain.PersistentUser;
import org.marketcetera.dao.domain.SystemObjectList;


/**
 * @version $Id$
 * @date 7/14/12 3:38 AM
 */

public class StartupBean {

    // --------------------- GETTER / SETTER METHODS ---------------------
    /**
     * Sets the permissionDao value.
     *
     * @param a <code>PermissionDao</code> value
     */
    public void setPermissionDao(PermissionDao inPermissionDao)
    {
        permissionDao = inPermissionDao;
    }
    /**
     * Sets the roleDao value.
     *
     * @param inRoleDao a <code>RoleDao</code> value
     */
    public void setRoleDao(RoleDao inRoleDao)
    {
        roleDao = inRoleDao;
    }
    /**
     * Sets the userDao value.
     *
     * @param inUserDao a <code>UserDao</code> value
     */
    public void setUserDao(UserDao inUserDao)
    {
        userDao = inUserDao;
    }
    // -------------------------- OTHER METHODS --------------------------
    public void activate() {
        SystemObjectList initialObjectList;
        try {
            InputStream inputStream = getClass().getResourceAsStream("/initialdata.xml");
            JAXBContext context = JAXBContext.newInstance(PersistentPermission.class,PersistentRole.class,PersistentUser.class,SystemObjectList.class);
            Unmarshaller m = context.createUnmarshaller();
            Object rawDeserialized = m.unmarshal(new InputStreamReader(inputStream));
            if(rawDeserialized instanceof SystemObjectList) {
                initialObjectList = (SystemObjectList)rawDeserialized;
            } else {
                throw new IllegalArgumentException("Expected an element of type objectList instead of " + rawDeserialized.getClass().getCanonicalName());
            }
        } catch (JAXBException e) {
            SLF4JLoggerProxy.error(this,
                                   e);
            throw new RuntimeException("Could not initialize data from initialdata.xml");
        }
        for(SystemObject o : initialObjectList.getObjects()) {
            SLF4JLoggerProxy.debug(this,
                                   "Examining initial data {}",
                                   o);
            try {
                if(o instanceof Permission) {
                    SLF4JLoggerProxy.info(this,
                                          "Writing {}",
                                          o);
                    permissionDao.add((Permission)o);
                } else if(o instanceof Role) {
                    SLF4JLoggerProxy.info(this,
                                          "Writing {}",
                                          o);
                    roleDao.add((Role) o);
                } else if(o instanceof User) {
                    SLF4JLoggerProxy.info(this,
                                          "Writing {}",
                                          o);
                    userDao.add((User) o);
                } else {
                    SLF4JLoggerProxy.warn(this,
                                          "Skipping unknown initial data object {}",
                                          o);
                }
            } catch (RuntimeException e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Error writing {}, skipping",
                                      o);
            }
        }
    }
    // ------------------------------ FIELDS ------------------------------
    private PermissionDao permissionDao;
    private RoleDao roleDao;
    private UserDao userDao;
}
