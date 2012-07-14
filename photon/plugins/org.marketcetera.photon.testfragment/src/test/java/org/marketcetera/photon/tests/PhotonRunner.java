package org.marketcetera.photon.tests;

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.testing.ITestHarness;
import org.eclipse.ui.testing.TestableObject;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.AllTests;
import org.marketcetera.photon.ApplicationWorkbenchAdvisor;
import org.marketcetera.photon.test.AbstractUIRunner;

/* $License$ */

/**
 * JUnit 3.8 suite runner that starts up the Photon application.
 * <p>
 * Note, to run Photon properly (with branding and default plug-in preferences),
 * it is necessary to add <code>-product org.marketcetera.photon.Photon</code>
 * to the launch configuration.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class PhotonRunner extends Runner implements ITestHarness {

    private final Runner mDelegatedRunner;

    private volatile RunNotifier mNotifier;

    private volatile TestableObject mTestableObject;

    /**
     * Constructor called by JUnit runtime.
     */
    public PhotonRunner(Class<?> klass) throws Throwable {
        mDelegatedRunner = new AllTests(klass);
    }

    @Override
    public void run(RunNotifier notifier) {
        mNotifier = notifier;
        mTestableObject = PlatformUI.getTestableObject();
        mTestableObject.setTestHarness(this);
        try {
            AbstractUIRunner.sTestUIThreadExecutor.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    PlatformUI.createAndRunWorkbench(Display.getCurrent(),
                            new ApplicationWorkbenchAdvisor());
                    return null;
                }
            }).get();
        } catch (Exception e) {
            notifier.fireTestFailure(new Failure(mDelegatedRunner
                    .getDescription(), e));
        }
    }

    @Override
    public Description getDescription() {
        return mDelegatedRunner.getDescription();
    }

    @Override
    public void runTests() {
        mTestableObject.testingStarting();
        mTestableObject.runTest(new Runnable() {
            public void run() {
                mDelegatedRunner.run(mNotifier);
            }
        });
        mTestableObject.testingFinished();
    }

}
