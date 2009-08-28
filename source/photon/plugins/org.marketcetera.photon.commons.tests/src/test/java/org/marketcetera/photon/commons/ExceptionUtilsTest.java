package org.marketcetera.photon.commons;

import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.PhotonTestBase;

/* $License$ */

/**
 * Tests {@link ExceptionUtils}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class ExceptionUtilsTest extends PhotonTestBase {

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
    
    @Test
    public void testLaunderedGet() throws Exception {
        new ExpectedFailure<RuntimeException>("xyz") {
            @Override
            protected void run() throws Exception {
                ExceptionUtils.launderedGet(new ImmediateFuture<Void>(new RuntimeException("xyz")));
            }
        };
    }
    
    public class ImmediateFuture<T> implements Future<T> {

        private final T result;
        private final Exception exception;

        public ImmediateFuture(T result) {
            this.result = result;
            this.exception = null;
        }

        public ImmediateFuture(Exception ex) {
            this.result = null;
            this.exception = ex;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public T get() throws InterruptedException, ExecutionException {
            if (exception != null){
                throw new ExecutionException(exception);
            }
            return result;
        }

        @Override
        public T get(long timeout, TimeUnit unit) throws InterruptedException,
                ExecutionException, TimeoutException {
            return get();
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }
    }
}
