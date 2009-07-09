/**
 * 
 */
package org.marketcetera.photon.test;

import java.util.concurrent.CountDownLatch;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;
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
public final class WorkbenchRunner extends AbstractUIRunner {
    
    public WorkbenchRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected void runEventLoop(final Display display, final CountDownLatch ready) {
        PlatformUI.createAndRunWorkbench(display, new WorkbenchAdvisor() {
            
            @Override
            public String getInitialWindowPerspectiveId() {
                return Perspective.ID;
            }
            
            @Override
            public void postStartup() {
                ready.countDown();
            }
        });
    }
    
    @Override
    protected void shutDownUI() {
        PlatformUI.getWorkbench().close();
    }
    
    public static class Perspective implements IPerspectiveFactory {
        
        public static final String ID = "org.marketcetera.photon.test.WorkbenchRunner$Perspective";
        @Override
        public void createInitialLayout(IPageLayout layout) {
            
        }
    }
}