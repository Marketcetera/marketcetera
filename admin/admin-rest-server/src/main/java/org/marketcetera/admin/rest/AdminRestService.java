package org.marketcetera.admin.rest;

import java.security.Principal;
import java.util.Set;

import org.marketcetera.admin.Permission;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/* $License$ */

/**
 * Provides a REST implementation of {@link AdminService}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Controller
@Configuration
@EnableAutoConfiguration
@ConfigurationProperties("admin")
@Tag(name = "Admin", description = "Admin server operations")
public class AdminRestService
{
    /**
     * Finds all <code>Permission</code> values granted to the user with the given username.
     *
     * @return a <code>Set&lt;SimplePermissions&gt;</code> value
     * @param inPrincipal a <code>Principal</code> value
     */
    @ResponseBody
    @RequestMapping(method=RequestMethod.GET,produces={"application/json","application/xml"},value="/admin/getPermissions")
    @Operation(summary = "Gets user permissions", description = "Get the permissions for the user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully returned permissions"),
        @ApiResponse(responseCode = "401", description = "Not logged in")
    })
    Set<Permission> findAllPermissionsByUsername(Principal inPrincipal)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received get permissions for user from {}",
                               inPrincipal);
        return authzService.findAllPermissionsByUsername(inPrincipal.getName());
    }
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authzService;
}
