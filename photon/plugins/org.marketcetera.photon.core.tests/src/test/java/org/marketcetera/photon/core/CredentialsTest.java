package org.marketcetera.photon.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;

/* $License$ */

/**
 * Tests {@link Credentials}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class CredentialsTest {

    @Test
    public void test() throws Exception {
        new ExpectedNullArgumentFailure("username") {
            @Override
            protected void run() throws Exception {
                new Credentials(null, "asdf");
            }
        };
        new ExpectedNullArgumentFailure("password") {
            @Override
            protected void run() throws Exception {
                new Credentials("asdf", null);
            }
        };
        ICredentials fixture = new Credentials("xyz", "abc");
        assertThat(fixture.getUsername(), is("xyz"));
        assertThat(fixture.getPassword(), is("abc"));
        fixture = new Credentials("abc", "xyz");
        assertThat(fixture.getUsername(), is("abc"));
        assertThat(fixture.getPassword(), is("xyz"));
        fixture = new Credentials("", "");
        assertThat(fixture.getUsername(), is(""));
        assertThat(fixture.getPassword(), is(""));
    }

}
