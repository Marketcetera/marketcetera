package org.marketcetera.webui.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * $License$ */

/**
 * Provides Spring Security configuration.
 * 
 * <li>Bypass security checks for static resources,</li>
 * <li>Restrict access to the application, allowing only logged in users,</li>
 * <li>Set up the login form</li>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration
        extends WebSecurityConfigurerAdapter
{
    /* (non-Javadoc)
     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
     */
    @Override
    protected void configure(HttpSecurity inHttp)
            throws Exception
    {
        // Not using Spring CSRF here to be able to use plain HTML for the login page
        inHttp.csrf().disable()
            // Register our CustomRequestCache, that saves unauthorized access attempts, so
            // the user is redirected after login.
            .requestCache().requestCache(new CustomRequestCache())
            // Restrict access to our application.
            .and().authorizeRequests()
            // Allow all flow internal requests.
            .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()
            // Allow all requests by logged in users.
            .anyRequest().authenticated()
            // Configure the login page.
            .and().formLogin().loginPage(LOGIN_URL).permitAll().loginProcessingUrl(LOGIN_PROCESSING_URL)
            .failureUrl(LOGIN_FAILURE_URL)
            // Configure logout
            .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
    }
    /* (non-Javadoc)
     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#userDetailsService()
     */
    @Bean
    @Override
    protected UserDetailsService userDetailsService()
    {
        // TODO this needs to tie into our Admin RPC client
        UserDetails user = User.withUsername("user")
                .password("{noop}password")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
    /* (non-Javadoc)
     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.WebSecurity)
     */
    @Override
    public void configure(WebSecurity web)
            throws Exception
    {
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
    /**
     * login in-process URL
     */
    private static final String LOGIN_PROCESSING_URL = "/login";
    /**
     * login failed URL
     */
    private static final String LOGIN_FAILURE_URL = "/login";
    /**
     * login URL
     */
    private static final String LOGIN_URL = "/login";
    /**
     * login success URL
     */
    private static final String LOGOUT_SUCCESS_URL = "/login";
}
