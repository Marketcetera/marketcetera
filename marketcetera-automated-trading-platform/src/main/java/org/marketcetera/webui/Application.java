package org.marketcetera.webui;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import org.marketcetera.admin.PermissionFactory;
import org.marketcetera.admin.RoleFactory;
import org.marketcetera.admin.dao.PersistentPermissionFactory;
import org.marketcetera.admin.dao.PersistentRoleFactory;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.admin.service.impl.UserServiceImpl;
import org.marketcetera.symbol.IterativeSymbolResolver;
import org.marketcetera.symbol.PatternSymbolResolver;
import org.marketcetera.symbol.SymbolResolverService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication(scanBasePackages={"org.marketcetera","com.marketcetera"})
@EnableJpaRepositories(basePackages={"org.marketcetera","com.marketcetera"})
@Theme(value = "marketceteraautomatedtradingplatform", variant = Lumo.DARK)
@PWA(name = "Marketcetera Automated Trading Platform", shortName = "Marketcetera Automated Trading Platform", offlineResources = {})
@EntityScan(basePackages={"org.marketcetera","com.marketcetera"})
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
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
        return new PersistentRoleFactory();
    }
    /**
     * Get the permission factory value.
     *
     * @return a <code>PermissionFactory</code> value
     */
    @Bean
    public PermissionFactory getPermissionFactory()
    {
        return new PersistentPermissionFactory();
    }
    /**
     * Get the user service bean.
     *
     * @return a <code>UserService</code> value
     */
    @Bean
    public UserService getUserService()
    {
        return new UserServiceImpl();
    }

}
