package org.marketcetera.webservices.security.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.dao.PermissionDao;
import org.marketcetera.api.security.SecurityService;
import org.marketcetera.api.security.Subject;
import org.marketcetera.api.security.UsernamePasswordToken;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.webservices.security.AuthenticationService;
import org.marketcetera.webservices.systemmodel.WebServicesPermission;

/* $License$ */

/**
 * Provides web services access to the authentication service.
 *
 * @version $Id$
 * @since $Release$
 */
public class AuthenticationServiceImpl implements AuthenticationService {
// ------------------------------ FIELDS ------------------------------

    private PermissionDao permissionDao;

    /**
     * authentication manager subject
     */
    private SecurityService securityService;

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setPermissionDao(PermissionDao permissionDao) {
        this.permissionDao = permissionDao;
    }

    /**
     * Sets the authenticationManagerService value.
     *
     * @param securityService <code>Subject</code> value
     */
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface AuthenticationService ---------------------

    /* (non-Javadoc)
    * @see org.marketcetera.webservices.security.AuthenticationService#authenticate(java.lang.String, java.lang.String)
    */
    @Override
    public JaxbList<WebServicesPermission> authenticate(String username, String password) {
        SLF4JLoggerProxy.trace(AuthenticationServiceImpl.class, "AuthenticationService authenticate invoked with {}/********", //$NON-NLS-1$
                username);
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = securityService.getSubject();
        subject.login(token);
        List<WebServicesPermission> decoratedPermissions = new ArrayList<WebServicesPermission>();
        for (Permission permission : permissionDao.getAllByUsername(username)) {
            decoratedPermissions.add(new WebServicesPermission(permission));
        }

        return new JaxbList<WebServicesPermission>(decoratedPermissions);
    }

    @XmlRootElement(name = "permissions")
    @XmlSeeAlso(WebServicesPermission.class)
    public static class JaxbList<T> {
        protected List<T> list;

        public JaxbList() {
        }

        public JaxbList(List<T> list) {
            this.list = list;
        }

        @XmlElement(name = "permission")
        public List<T> getList() {
            return list;
        }
    }
}
