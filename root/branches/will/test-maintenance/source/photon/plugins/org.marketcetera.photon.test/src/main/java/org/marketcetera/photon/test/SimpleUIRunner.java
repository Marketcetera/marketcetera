/**
 * 
 */
package org.marketcetera.photon.test;

import java.util.concurrent.CountDownLatch;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Display;
import org.junit.runners.model.InitializationError;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Test runner that starts a separate UI thread. The UI thread initializes a
 * {@link Display} and runs the event loop in the Display's default
 * {@link Realm}.
 * <p>
 * Any framework method on the test class can be annotated with {@link UI} to
 * indicate it should be run on the UI thread. For example:
 * 
 * <pre>
 * &#064;RunWith(UIRunner.class)
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
 * Currently, a new thread is created for each test.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public final class SimpleUIRunner extends AbstractUIRunner {
    
    public SimpleUIRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected void runEventLoop(final Display display, final CountDownLatch ready) {
        ready.countDown();
        Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
            public void run() {
                while (!display.isDisposed()) {
                    if (!display.readAndDispatch()) {
                        display.sleep();
                    }
                }
            }
        });
    }
}