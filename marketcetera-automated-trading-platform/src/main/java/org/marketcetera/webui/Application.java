package org.marketcetera.webui;

import org.marketcetera.admin.AdminRpcClientFactory;
import org.marketcetera.admin.PermissionFactory;
import org.marketcetera.admin.RoleFactory;
import org.marketcetera.admin.UserAttributeFactory;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.admin.impl.SimplePermissionFactory;
import org.marketcetera.admin.impl.SimpleRoleFactory;
import org.marketcetera.admin.impl.SimpleUserAttributeFactory;
import org.marketcetera.admin.impl.SimpleUserFactory;
import org.marketcetera.symbol.IterativeSymbolResolver;
import org.marketcetera.symbol.PatternSymbolResolver;
import org.marketcetera.symbol.SymbolResolverService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages={"org.marketcetera","com.marketcetera"})
@Theme(value = "marketceteraautomatedtradingplatform", variant = Lumo.DARK)
@PWA(name = "Marketcetera Automated Trading Platform", shortName = "Marketcetera Automated Trading Platform", offlineResources = {})
@EntityScan(basePackages={"org.marketcetera","com.marketcetera"})
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class Application
        implements AppShellConfigurator
{
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    /**
     * Get the admin client factory value.
     *
     * @return an <code>AdminClientFactory</code> value
     */
    @Bean
    public AdminRpcClientFactory getAdminClientFactory()
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
     * Get the symbol resolver service value.
     *
     * @return a <code>SymbolResolverService</code> value
     */
    @Bean
    public SymbolResolverService getSymbolResolverService()
    {
        IterativeSymbolResolver symbolResolverService = new IterativeSymbolResolver();
        symbolResolverService.getSymbolResolvers().add(new PatternSymbolResolver());
        return symbolResolverService;
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
    private static final long serialVersionUID = 5355962292846525698L;
}
