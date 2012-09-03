package org.marketcetera.dao.impl;

import java.util.Date;

import org.marketcetera.api.dao.PermissionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 7/14/12 3:38 AM
 */

public class StartupBean {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(StartupBean.class);
    private PermissionDao permissionDao;

    public void setPermissionDao(PermissionDao permissionDao) {
        this.permissionDao = permissionDao;
    }

    public void activate() {
        PersistentPermission permission = new PersistentPermission();
        permission.setPermission(new Date().toString());
        permissionDao.save(permission);
    }
}
