package org.marketcetera.photon.commons.ui.databinding;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Display;
import org.junit.Test;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.ThreadRealm;

/* $License$ */

/**
 * Verifies the caveat/"feature" of {@link DataBindingContext} that prevents
 * observables from being disposed except in the data binding context's
 * validation realm. If this test fails, it can be removed and
 * {@link ProxyObservables} documentation/implementation should be updated
 * accordingly.
 * <p>
 * See <a
 * href="http://bugs.eclipse.org/281723">http://bugs.eclipse.org/281723</a>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class DataBindingContextCaveatTest extends PhotonTestBase {
    @Test
    public void test() throws Exception {
        new ExpectedFailure<AssertionFailedException>(
                "assertion failed: This operation must be run within the observable's realm") {
            @Override
            protected void run() throws Exception {
                final Exchanger<IObservableList> mModelExchanger = new Exchanger<IObservableList>();
                final CountDownLatch mModelReadyToDispose = new CountDownLatch(
                        1);
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
                            mModelReadyToDispose.await();
                            model.dispose(); // throws AssertionFailedException
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                Display d = new Display();
                Realm.runWithDefault(SWTObservables.getRealm(d),
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    WritableList target = WritableList
                                            .withElementType(String.class);
                                    DataBindingContext dbc = new DataBindingContext();
                                    dbc.bindList(target, mModelExchanger
                                            .exchange(null));
                                    mModelReadyToDispose.countDown();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });

                try {
                    future.get(); // propagates the AssertionFailedException
                } catch (ExecutionException e) {
                    throw (Exception) e.getCause();
                } finally {
                    executor.shutdownNow();
                    d.dispose();
                }
            }
        };
    }
}
