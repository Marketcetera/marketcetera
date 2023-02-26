package org.marketcetera.ui.service.admin;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.marketcetera.admin.Permission;
import org.marketcetera.ui.service.AuthorizationHelperService;
import org.marketcetera.ui.service.ServiceManager;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/* $License$ */

/**
 * Provides authorization resolution services as a remote service.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class WebAuthorizationHelperService
        implements AuthorizationHelperService
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.AuthorizationHelperService#hasPermission(org.springframework.security.core.GrantedAuthority)
     */
    @Override
    public boolean hasPermission(GrantedAuthority inGrantedAuthority)
    {
        SessionUser sessionUser = SessionUser.getCurrent();
        if(sessionUser == null) {
            SLF4JLoggerProxy.trace(this,
                                   "No current user, permission {} is denied",
                                   inGrantedAuthority);
            return false;
        }
        return permissionMapsByUsername.getUnchecked(sessionUser.getUsername()).getUnchecked(inGrantedAuthority.getAuthority());
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        permissionMapsByUsername = CacheBuilder.newBuilder().expireAfterAccess(userPermissionCacheTtl,TimeUnit.MILLISECONDS).build(new CacheLoader<String,LoadingCache<String,Boolean>>() {
            @Override
            public LoadingCache<String,Boolean> load(String inUsername)
                    throws Exception
            {
                AdminClientService adminClientService = serviceManager.getService(AdminClientService.class);
                Set<Permission> permissions = adminClientService.getPermissionsForUser();
                LoadingCache<String,Boolean> permissionsByPermissionName = CacheBuilder.newBuilder().build(new CacheLoader<String,Boolean>() {
                    @Override
                    public Boolean load(String inPermissionName)
                            throws Exception
                    {
                        SLF4JLoggerProxy.trace(WebAuthorizationHelperService.this,
                                               "Checking to see if {} has permission {} in {}",
                                               inUsername,
                                               inPermissionName,
                                               permissions);
                        if(permissions == null) {
                            return false;
                        }
                        for(Permission permission : permissions) {
                            if(permission.getAuthority().equals(inPermissionName)) {
                                return true;
                            }
                        }
                        return false;
                    }});
                return permissionsByPermissionName;
            }}
        );
    }
    /**
     * length of time to cache user permissions
     */
    @Value("${metc.user.authorization.permission.cache.ttl:300000}")
    private long userPermissionCacheTtl = 1000 * 60 * 5;
    /**
     * caches permissions by username and permission name
     */
    private LoadingCache<String,LoadingCache<String,Boolean>> permissionMapsByUsername;
    /**
     * service manager value
     */
    @Autowired
    private ServiceManager serviceManager;
}
