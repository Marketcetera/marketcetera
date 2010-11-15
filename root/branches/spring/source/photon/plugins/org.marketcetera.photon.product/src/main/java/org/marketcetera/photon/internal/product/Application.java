package org.marketcetera.photon.internal.product;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.ApplicationWorkbenchAdvisor;
import org.marketcetera.photon.ApplicationWorkbenchWindowAdvisor;
import org.marketcetera.photon.strategy.StrategyUI;

/* $License$ */

/**
 * The Photon application.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public final class Application implements IApplication {

    @Override
    public Object start(IApplicationContext context) throws Exception {
        Display display = PlatformUI.createDisplay();
        try {
            /*
             * The advisors are in the org.marketcetera.photon plugin for legacy
             * reasons.
             */
            int returnCode = PlatformUI.createAndRunWorkbench(display,
                    new ApplicationWorkbenchAdvisor() {
                        @Override
                        public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
                                IWorkbenchWindowConfigurer configurer) {
                            return new ApplicationWorkbenchWindowAdvisor(
                                    configurer) {
                                @Override
                                public void postWindowOpen() {
                                    StrategyUI.initializeStrategyEngines();
                                    super.postWindowOpen();
                                }
                            };
                        }
                    });
            if (returnCode == PlatformUI.RETURN_RESTART) {
                return IApplication.EXIT_RESTART;
            }
            return IApplication.EXIT_OK;
        } finally {
            display.dispose();
        }
    }

    @Override
    public void stop() {
        // Do nothing
    }
}
