package org.marketcetera.photon.commons;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.marketcetera.photon.test.ExpectedFailure;

import com.google.common.collect.Lists;


/* $License$ */

/**
 * Tests {@link SynchronizedProxy}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class SynchronizedProxyTest {

    @Test
    public void testProxy() throws Exception {
        List<String> list = Lists.newArrayList();
        List<String> proxy = SynchronizedProxy.proxy(list, List.class);
        proxy.add("ABC");
        assertThat(list.get(0), is("ABC"));
    }
    
    @Test
    public void testRuntimeException() throws Exception {
        List<String> list = Lists.newArrayList();
        final List<String> proxy = SynchronizedProxy.proxy(list, List.class);
        new ExpectedFailure<IndexOutOfBoundsException>(null) {
            @Override
            protected void run() throws Exception {
                proxy.get(1);
            }
        };
    }
    
    @Test
    public void testCheckedException() throws Exception {
        Callable<Void> delegate = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                throw new IOException();
            }
        };
        final Callable<Void> proxy = SynchronizedProxy.proxy(delegate, Callable.class);
        new ExpectedFailure<IOException>(null) {
            @Override
            protected void run() throws Exception {
                proxy.call();
            }
        };
    }
}
