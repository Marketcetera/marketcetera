package org.marketcetera.photon.commons;

import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.test.ExpectedFailure;

/* $License$ */

/**
 * Tests {@link ExceptionUtils}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class ExceptionUtilsTest {

    @Test
    public void testLaunderThrowable() throws Exception {
        new ExpectedNullArgumentFailure("throwable") {
            @Override
            protected void run() throws Exception {
                ExceptionUtils.launderThrowable(null);
            }
        };
        final Error error = new Error();
        try {
            ExceptionUtils.launderThrowable(error);
        } catch (Error e) {
            assertThat(e, sameInstance(e));
        }
        new ExpectedFailure<IllegalStateException>("Not unchecked") {
            @Override
            protected void run() throws Exception {
                ExceptionUtils.launderThrowable(new IOException("test"));
            }
        };
        RuntimeException runtime = new RuntimeException();
        assertThat(ExceptionUtils.launderThrowable(runtime),
                sameInstance(runtime));
    }

}
