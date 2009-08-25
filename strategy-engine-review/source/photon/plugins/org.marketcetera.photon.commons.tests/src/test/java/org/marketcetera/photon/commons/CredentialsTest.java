package org.marketcetera.photon.commons;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.test.PhotonTestBase;

/* $License$ */

/**
 * Tests {@link Credentials}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class CredentialsTest extends PhotonTestBase {

    @Test
    public void test() throws Exception {
        new ExpectedNullArgumentFailure("username") {
            @Override
            protected void run() throws Exception {
                new Credentials(null, null);
            }
        };
        new ExpectedNullArgumentFailure("password") {
            @Override
            protected void run() throws Exception {
                new Credentials("asdf", null);
            }
        };
        Credentials fixture = new Credentials("xyz", "abc");
        assertThat(fixture.getUsername(), is("xyz"));
        assertThat(fixture.getPassword(), is("abc"));
        fixture = new Credentials("abc", "xyz");
        assertThat(fixture.getUsername(), is("abc"));
        assertThat(fixture.getPassword(), is("xyz"));
    }

}
