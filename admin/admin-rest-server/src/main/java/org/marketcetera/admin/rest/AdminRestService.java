package org.marketcetera.admin.rest;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;

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
//@Api(value="Admin server operations")
public class AdminRestService
{
//    /**
//     * Finds all <code>Permission</code> values granted to the user with the given username.
//     *
//     * @return a <code>Set&lt;SimplePermissions&gt;</code> value
//     * @param inPrincipal a <code>Principal</code> value
//     */
//    @ResponseBody
//    @RequestMapping(method=RequestMethod.GET,produces={"application/json","application/xml"},value="/matp/admin/getPermissions")
//    @ApiOperation(value="Gets user permissions",response=Set.class,protocols= "http,https",
//                  notes="Get the permissions for the user")
//    @ApiResponses(value={ @ApiResponse(code=200,message="Successfully returned permissions"),
//                          @ApiResponse(code=401,message="Not logged in") })
//    public Set<Permission> findAllPermissionsByUsername(Principal inPrincipal)
//    {
//        SLF4JLoggerProxy.debug(this,
//                               "Received get permissions for user from {}",
//                               inPrincipal);
//        return authzService.findAllPermissionsByUsername(inPrincipal.getName());
//    }
//    /**
//     * provides access to authorization services
//     */
//    @Autowired
//    private AuthorizationService authzService;
}
