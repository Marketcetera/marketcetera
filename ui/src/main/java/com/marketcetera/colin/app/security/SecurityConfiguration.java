package com.marketcetera.colin.app.security;

import java.util.Optional;

import org.marketcetera.admin.AdminClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.marketcetera.colin.app.service.ClientService;
import com.marketcetera.colin.backend.data.Role;
import com.marketcetera.colin.backend.data.entity.User;
import com.marketcetera.colin.backend.repositories.UserRepository;
import com.marketcetera.colin.ui.utils.WebUiConst;

/**
 * Configures spring security, doing the following:
 * <li>Bypass security checks for static resources,</li>
 * <li>Restrict access to the application, allowing only logged in users,</li>
 * <li>Set up the login form,</li>
 * <li>Configures the {@link UserDetailsServiceImpl}.</li>

 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration
        extends WebSecurityConfigurerAdapter
{
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CurrentUser currentUser(UserRepository userRepository,
                                   ClientService inClientService)
    {
        // TODO need to switch this over to using real users
        Optional<AdminClient> adminClient = Optional.empty();
        if(inClientService != null) {
            adminClient = inClientService.getService(AdminClient.class);
        }
        // prototype scope means that a different bean is returned every time this method is invoked
        final String username = SecurityUtils.getUsername();
        User user = username != null ? userRepository.findByEmailIgnoreCase(username) : null;
//        return () -> adminClient.get().getCurrentUser();
        return () -> user;
    }
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CurrentMetcUser currentMetcUser()
    {
//        // TODO need to switch this over to using real users
//        Optional<AdminClient> adminClient = Optional.empty();
//        if(inClientService != null) {
//            adminClient = inClientService.getService(AdminClient.class);
//        }
//        // prototype scope means that a different bean is returned every time this method is invoked
//        final String username = SecurityUtils.getUsername();
//        User user = username != null ? userRepository.findByEmailIgnoreCase(username) : null;
////        return () -> adminClient.get().getCurrentUser();
//        return () -> user;
        return null;
    }
    /**
     * Get the password encoder.
     * 
     * <p>This must match the encoder used on the server.
     * 
     * @return a <code>PasswordEncoder</code> value
     */
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
    /**
     * Registers our UserDetailsService and the password encoder to be used on login attempts.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception
    {
        super.configure(auth);
        auth.authenticationProvider(getCustomAuthenticationProvider());
    }
    @Bean
    public AuthenticationProvider getCustomAuthenticationProvider()
    {
        return new AdminRpcAuthenticationProvider();
    }
    /**
     * Require login to access internal pages and configure login form.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Not using Spring CSRF here to be able to use plain HTML for the login page
        http.csrf().disable()

        // Register our CustomRequestCache, that saves unauthorized access attempts, so
        // the user is redirected after login.
        .requestCache().requestCache(new CustomRequestCache())

        // Restrict access to our application.
        .and().authorizeRequests()

        // Allow all flow internal requests.
        .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()

        // Allow all requests by logged in users.
        .anyRequest().hasAnyAuthority(Role.getAllRoles())

        // Configure the login page.
        .and().formLogin().loginPage(LOGIN_URL).permitAll().loginProcessingUrl(LOGIN_PROCESSING_URL)
        .failureUrl(LOGIN_FAILURE_URL)

        // Register the success handler that redirects users to the page they last tried
        // to access
        .successHandler(new SavedRequestAwareAuthenticationSuccessHandler())

        // Configure logout
        .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
    }

    /**
     * Allows access to static resources, bypassing Spring security.
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                                   // Vaadin Flow static resources
                                   "/VAADIN/**",

                                   // the standard favicon URI
                                   "/favicon.ico",

                                   // the robots exclusion standard
                                   "/robots.txt",

                                   // web application manifest
                                   "/manifest.webmanifest",
                                   "/sw.js",
                                   "/offline-page.html",

                                   // icons and images
                                   "/icons/**",
                                   "/images/**",

                                   // (development mode) static resources
                                   "/frontend/**",

                                   // (development mode) webjars
                                   "/webjars/**",

                                   // (development mode) H2 debugging console
                                   "/h2-console/**",

                                   // (production mode) static resources
                                   "/frontend-es5/**", "/frontend-es6/**");
    }
    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_SUCCESS_URL = "/" + WebUiConst.PAGE_STOREFRONT;
}
