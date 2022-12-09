package org.marketcetera.webui.security;

import org.marketcetera.webui.views.login.LoginView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration
        extends VaadinWebSecurity
{

    public static final String LOGOUT_URL = "/";

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authManager(HttpSecurity http)
            throws Exception
    {
        System.out.println("COCO: SecurityConfiguration.authManager");
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider);
        return authenticationManagerBuilder.build();
    }
    @Override
    protected void configure(HttpSecurity inHttpSecurity)
            throws Exception
    {
        System.out.println("COCO: SecurityConfiguration.configure");
        inHttpSecurity.authorizeRequests().requestMatchers(new AntPathRequestMatcher("/images/*.png")).permitAll();
        super.configure(inHttpSecurity);
        setLoginView(inHttpSecurity,
                     LoginView.class,
                     LOGOUT_URL);
    }
    @Autowired
    private RpcAuthenticationProvider authProvider;

}
