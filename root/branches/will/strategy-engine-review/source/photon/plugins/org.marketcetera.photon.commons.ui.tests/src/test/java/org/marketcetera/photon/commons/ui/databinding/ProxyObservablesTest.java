package org.marketcetera.photon.commons.ui.databinding;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.ExpectedIllegalStateException;
import org.marketcetera.photon.test.LockRealm;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.SWTTestUtil;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.ThreadRealm;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;

/* $License$ */

/**
 * Test {@link ProxyObservables}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class ProxyObservablesTest extends PhotonTestBase {

    @Test
    public void testPreconditions() throws Exception {
        new ExpectedIllegalStateException(
                "this method requires a default realm") {
            @Override
            protected void run() throws Exception {
                ProxyObservables.proxyList(null);
            }
        };
        new ExpectedNullArgumentFailure("originalList") {
            @Override
            protected void run() throws Exception {
                Realm.runWithDefault(new LockRealm(), new Runnable() {
                    @Override
                    public void run() {
                        ProxyObservables.proxyList(null);
                    }
                });
            }
        };
        new ExpectedIllegalStateException("must be called from the proxy realm") {
            @Override
            protected void run() throws Exception {
                Realm.runWithDefault(new LockRealm(), new Runnable() {
                    @Override
                    public void run() {
                        ProxyObservables.proxyList(new WritableList());
                    }
                });
            }
        };
        new ExpectedNullArgumentFailure("realm") {
            @Override
            protected void run() throws Exception {
                ProxyObservables.proxyList(null, mock(IObservableList.class));
            }
        };
        new ExpectedNullArgumentFailure("originalList") {
            @Override
            protected void run() throws Exception {
                ProxyObservables.proxyList(new LockRealm(), null);
            }
        };
        new ExpectedIllegalStateException("must be called from the proxy realm") {
            @Override
            protected void run() throws Exception {
                LockRealm realm = new LockRealm();
                ProxyObservables.proxyList(realm, new WritableList(realm));
            }
        };
    }

    @Test
    public void testPassthrough() throws Exception {
        ThreadRealm realm = new ThreadRealm();
        realm.init(Thread.currentThread());
        IObservableList original = new WritableList(realm);
        assertThat(ProxyObservables.proxyList(realm, original),
                sameInstance(original));
    }

    final Exchanger<IObservableList> mModelExchanger = new Exchanger<IObservableList>();
    final CountDownLatch mProxyReady = new CountDownLatch(1);
    final CountDownLatch mModelUpdated = new CountDownLatch(1);
    final CountDownLatch mModelReadyToDispose = new CountDownLatch(1);

    private final class ModelRunnable implements Runnable {
        @Override
        public void run() {
            try {
                ThreadRealm realm = new ThreadRealm();
                realm.init(Thread.currentThread());
                WritableList model = new WritableList(realm,
                        new ArrayList<Object>(), String.class);
                mModelExchanger.exchange(model);
                mProxyReady.await();
                model.add("ABC");
                mModelUpdated.countDown();
                mModelReadyToDispose.await();
                model.dispose();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testProxyList() throws Throwable {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(new ModelRunnable());

        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                final IObservableList proxy = ProxyObservables
                        .proxyList(mModelExchanger.exchange(null));
                assertThat(proxy.getElementType(),
                        equalTo((Object) String.class));
                assertThat(proxy.getRealm(), is(Realm.getDefault()));
                mProxyReady.countDown();
                mModelUpdated.await();
                // update should not happen immediately since the UI thread is
                // in use
                assertThat(proxy.size(), is(0));
                // wait for the update
                SWTTestUtil.conditionalDelay(1, TimeUnit.SECONDS,
                        new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return proxy.size() == 1;
                            }
                        });
                assertThat(proxy.get(0), is((Object) "ABC"));
                dispose(proxy);
            }
        });

        future.get();
        executor.shutdownNow();
    }

    @Test
    public void testProxyListWithRealm() throws Throwable {
        ExecutorService executor1 = Executors.newSingleThreadExecutor();
        ExecutorService executor2 = Executors.newSingleThreadExecutor();
        Future<?> future1 = executor1.submit(new ModelRunnable());
        Future<?> future2 = executor2.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    ThreadRealm realm = new ThreadRealm();
                    realm.init(Thread.currentThread());
                    final IObservableList proxy = ProxyObservables.proxyList(
                            realm, mModelExchanger.exchange(null));
                    assertThat(proxy.getElementType(),
                            equalTo((Object) String.class));
                    assertThat(proxy.getRealm(), is((Realm) realm));
                    mProxyReady.countDown();
                    mModelUpdated.await();
                    // update should not happen immediately since the
                    // ThreadRealm is not blocking
                    assertThat(proxy.size(), is(0));
                    // wait for the update
                    realm.processQueue();
                    assertThat(proxy.size(), is(1));
                    assertThat(proxy.get(0), is((Object) "ABC"));
                    dispose(proxy);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        future1.get();
        future2.get();
        executor1.shutdownNow();
        executor2.shutdownNow();
    }

    @Test
    public void testDisposeOnProxyThread() throws Throwable {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    ThreadRealm realm = new ThreadRealm();
                    realm.init(Thread.currentThread());
                    WritableList model = new WritableList(realm,
                            new ArrayList<Object>(), String.class);
                    mModelExchanger.exchange(model);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                final IObservableList model = mModelExchanger.exchange(null);
                final IObservableList proxy = ProxyObservables.proxyList(model);
                // proxy is automatically disposed if the model is disposed on
                // the proxy realm
                model.dispose();
                assertTrue(proxy.isDisposed());
            }
        });

        future.get();
        executor.shutdownNow();
    }

    private void dispose(final IObservableList proxy) {
        /*
         * Ideally, the proxy would be automatically disposed with the original,
         * but due to DataBindingContext limitations this is not possible (see
         * ProxyObservables javadocs for details). Manually disposing here
         * prevents exceptions.
         */
        proxy.dispose();
        mModelReadyToDispose.countDown();
    }
}
