package org.marketcetera.photon.test;

import java.util.concurrent.CountDownLatch;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Display;
import org.junit.runners.model.InitializationError;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Test runner that runs UI events in the Display's default {@link Realm}.
 * <p>
 * Any framework method on the test class can be annotated with {@link UI} to
 * indicate it should be run on the UI thread. For example:
 * 
 * <pre>
 * &#064;RunWith(SimpleUIRunner.class)
 * public class MyTest {
 * 
 *     &#064;Before
 *     &#064;UI
 *     public void before() {
 *         // on the UI thread
 *         ...
 *     }
 * 
 *     &#064;Test
 *     &#064;UI
 *     public void testText() throws Exception {
 *         // on the UI thread
 *         ...
 *     }
 * 
 *     &#064;Test
 *     public void testText() throws Exception {
 *         // NOT on the UI thread
 *         ...
 *     }
 * }
 * </pre>
 * <p>
 * Use {@link WorkbenchRunner} if you need a full workbench.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public final class SimpleUIRunner extends AbstractUIRunner {

    private volatile boolean mShutDown;

    /**
     * Constructor. Should only be called by the JUnit framework.
     * 
     * @param klass
     *            test class
     * @throws InitializationError
     *             if the test class is malformed.
     */
    public SimpleUIRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected void runEventLoop(final Display display,
            final CountDownLatch ready) {
        ready.countDown();
        Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
            @Override
            public void run() {
                while (!mShutDown) {
                    try {
                        if (!display.readAndDispatch()) {
                            display.sleep();
                        }
                    } catch (Throwable t) {
                        setAsyncThrowable(t);
                    }
                }
            }
        });
    }
    
    @Override
    protected void shutDownUI(Display display) {
        mShutDown = true;
        display.wake();
    }
}