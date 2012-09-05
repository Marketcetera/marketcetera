package org.marketcetera.dao.impl;

import java.util.Date;

import org.marketcetera.api.dao.PermissionDao;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.dao.domain.PersistentPermission;

/**
 * @version $Id$
 * @date 7/14/12 3:38 AM
 */

public class StartupBean {
    private PermissionDao permissionDao;
    public void setPermissionDao(PermissionDao permissionDao) {
        this.permissionDao = permissionDao;
    }
    public void activate() {
        PersistentPermission permission = new PersistentPermission();
        permission.setPermission(new Date().toString());
        SLF4JLoggerProxy.info(this,
                              "Creating {}",
                              permission);
        permissionDao.save(permission);
    }
}
