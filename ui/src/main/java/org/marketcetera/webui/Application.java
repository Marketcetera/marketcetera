package org.marketcetera.webui;

import org.marketcetera.admin.AdminClientFactory;
import org.marketcetera.admin.AdminRpcClientFactory;
import org.marketcetera.admin.AdminRpcClientParameters;
import org.marketcetera.admin.PermissionFactory;
import org.marketcetera.admin.RoleFactory;
import org.marketcetera.admin.UserAttributeFactory;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.admin.impl.SimplePermissionFactory;
import org.marketcetera.admin.impl.SimpleRoleFactory;
import org.marketcetera.admin.impl.SimpleUserAttributeFactory;
import org.marketcetera.admin.impl.SimpleUserFactory;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.webui.app.MainView;
import org.marketcetera.webui.app.security.SecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

/**
 * The entry point of the Spring Boot application.
 */
@EnableAutoConfiguration
@SpringBootConfiguration
@SpringBootApplication(scanBasePackageClasses = { SecurityConfiguration.class, MainView.class, Application.class,UserService.class }, exclude = ErrorMvcAutoConfiguration.class)
public class Application
        extends SpringBootServletInitializer
{
    /**
     * Main application function.
     *
     * @param inArgs a <code>String[]</code> value
     */
    public static void main(String[] inArgs)
    {
        SpringApplication.run(Application.class,
                              inArgs);
    }
    /**
     * Get the admin client factory value.
     *
     * @return an <code>AdminClientFactory&lt;AdminRpcClientParameters&gt;</code> value
     */
    @Bean
    public AdminClientFactory<AdminRpcClientParameters> getAdminClientFactory()
    {
        return new AdminRpcClientFactory();
    }
    /**
     * Get the user attribute factory value.
     *
     * @return a <code>UserAttribute</code> value
     */
    @Bean
    public UserAttributeFactory getUserAttributeFactory()
    {
        return new SimpleUserAttributeFactory();
    }
    /**
     * Get the permission factory value.
     *
     * @return a <code>PermissionFactory</code> value
     */
    @Bean
    public PermissionFactory getPermissionFactory()
    {
        return new SimplePermissionFactory();
    }
    /**
     * Get the user factory value.
     *
     * @return a <code>UserFactory</code> value
     */
    @Bean
    public UserFactory getUserFactory()
    {
        return new SimpleUserFactory();
    }
    /**
     * Get the role factory value.
     *
     * @return a <code>RoleFactory</code> value
     */
    @Bean
    public RoleFactory getRoleFactory()
    {
        return new SimpleRoleFactory();
    }
}
