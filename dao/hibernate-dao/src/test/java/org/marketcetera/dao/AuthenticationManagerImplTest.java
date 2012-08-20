package org.marketcetera.dao;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.dao.hibernate.HibernateTestBase;
import org.marketcetera.core.systemmodel.User;
import org.marketcetera.core.systemmodel.UserFactory;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;

/* $License$ */

/**
 * Tests the authentication manager service.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AuthenticationManagerImplTest.java 82318 2012-03-22 22:37:06Z colin $
 * @since $Release$
 */
public class AuthenticationManagerImplTest
        extends HibernateTestBase
{
    /**
     * Run before each test.
     * 
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        super.setup();
        userFactory = (UserFactory)getApp().getContext().getBean("persistentUserFactory");
        authenticationManager = (AuthenticationManager)getApp().getContext().getBean("authenticationManager");
        passwordEncoder = (PasswordEncoder)getApp().getContext().getBean("passwordEncoder");
    }
    /**
     * Tests authentication against the user data store.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAuthentication()
            throws Exception
    {
        String rawPassword = "clear-text password";
        String hashedPassword = passwordEncoder.encode(rawPassword);
        User user = userFactory.create("some user",
                                       hashedPassword);
        getDao().getUserDao().add(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(),
                                                                      rawPassword);
        Authentication resultingAuth = authenticationManager.authenticate(auth);
        SecurityContextHolder.getContext().setAuthentication(resultingAuth);
    }
    /**
     * constructs user objects 
     */
    private volatile UserFactory userFactory;
    /**
     * authenticates users
     */
    private volatile AuthenticationManager authenticationManager;
    /**
     * encodes passwords
     */
    private volatile PasswordEncoder passwordEncoder;
}
