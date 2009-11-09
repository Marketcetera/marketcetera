package org.marketcetera.photon.test;

import java.util.concurrent.CountDownLatch;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.junit.runners.model.InitializationError;

/* $License$ */

/**
 * Test runner that launches a simple workbench with an empty perspective. Use
 * this if you need to test views or other code that requires workbench
 * services.
 * <p>
 * For basic SWT/JFace tests, use {@link SimpleUIRunner}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public final class WorkbenchRunner extends AbstractUIRunner {

    /**
     * Constructor. Should only be called by the JUnit framework.
     * 
     * @param klass
     *            test class
     * @throws InitializationError
     *             if the test class is malformed.
     */
    public WorkbenchRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected void runEventLoop(final Display display,
            final CountDownLatch ready) {
        PlatformUI.createAndRunWorkbench(display, new WorkbenchAdvisor() {

            @Override
            public String getInitialWindowPerspectiveId() {
                return Perspective.ID;
            }

            @Override
            public void postStartup() {
                ready.countDown();
            }

            @Override
            public void eventLoopException(Throwable exception) {
                setAsyncThrowable(exception);
                super.eventLoopException(exception);
            }
        });
    }

    @Override
    protected void shutDownUI(Display display) {
        display.syncExec(new Runnable() {
            @Override
            public void run() {
                PlatformUI.getWorkbench().close();
            }
        });
    }

    public static class Perspective implements IPerspectiveFactory {

        public static final String ID = "org.marketcetera.photon.test.WorkbenchRunner$Perspective";

        @Override
        public void createInitialLayout(IPageLayout layout) {

        }
    }
}