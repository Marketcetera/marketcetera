package org.marketcetera.webui.app.security;

import org.marketcetera.webui.ui.utils.UiConst;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

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
    /**
     * The password encoder to use when encrypting passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        // TODO we probably need to replace this with our own
        return new BCryptPasswordEncoder();
    }
    // TODO I don't know what this does
//    @Bean
//    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//    public CurrentUser currentUser()
//    {
//        final String username = SecurityUtils.getUsername();
//        User user = username != null ? userRepository.findByEmailIgnoreCase(username) : null;
//        return () -> user;
//    }
    /**
     * Registers our UserDetailsService and the password encoder to be used on login attempts.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception
    {
        super.configure(auth);
//        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        auth.authenticationProvider(getCustomAuthenticationProvider());
    }
    @Bean
    public AuthenticationProvider getCustomAuthenticationProvider()
    {
        return new AdminRpcAuthenticationProvider();
    }
    /* (non-Javadoc)
     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
     */
    @Override
    protected void configure(HttpSecurity http)
            throws Exception
    {
        // Not using Spring CSRF here to be able to use plain HTML for the login page
        http.csrf().disable()
        // Register our CustomRequestCache, that saves unauthorized access attempts, so the user is redirected after login.
        .requestCache().requestCache(new CustomRequestCache())
        // Restrict access to our application.
        .and().authorizeRequests()
        // Allow all flow internal requests.
        .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()
        // Allow all requests by logged in users.
//        .anyRequest().hasAnyAuthority(Role.getAllRoles())
        // Configure the login page.
        .and().formLogin().loginPage(LOGIN_URL).permitAll().loginProcessingUrl(LOGIN_PROCESSING_URL)
        .failureUrl(LOGIN_FAILURE_URL)
        // Register the success handler that redirects users to the page they last tried to access
        .successHandler(new SavedRequestAwareAuthenticationSuccessHandler())
        // Configure logout
        .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
    }
    /**
     * Allows access to static resources, bypassing Spring security.
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
    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_SUCCESS_URL = "/" + UiConst.PAGE_STOREFRONT;
}
