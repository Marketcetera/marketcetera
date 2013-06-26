package org.marketcetera.photon.internal.product;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.photon.core.*;
import org.marketcetera.photon.positions.ui.IPositionLabelProvider;
import org.marketcetera.photon.ui.LoginDialog;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/* $License$ */

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class Activator implements BundleActivator {

    /**
     * {@link ICredentialsService} that pops obtains credentials from a
     * {@link LoginDialog}.
     */
    @ClassVersion("$Id$")
    private static class LoginCredentialsService implements ICredentialsService {
        private volatile ICredentials mCredentials;

        @Override
        public synchronized boolean authenticateWithCredentials(
                IAuthenticationHelper helper) {
            ICredentials current = mCredentials;
            if (current == null) {
                Display display = PlatformUI.getWorkbench().getDisplay();
                while (true) {
                    final AtomicBoolean cancelled = new AtomicBoolean();
                    final AtomicReference<Credentials> credentials = new AtomicReference<Credentials>();
                    display.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            IWorkbenchWindow window = PlatformUI.getWorkbench()
                                    .getActiveWorkbenchWindow();
                            Shell parent = window == null ? null : window
                                    .getShell();
                            LoginDialog dialog = new LoginDialog(parent);
                            if (dialog.open() == Window.OK) {
                                credentials.set(new Credentials(dialog
                                        .getConnectionDetails().getUserId(),
                                        dialog.getConnectionDetails()
                                                .getPassword()));
                            } else {
                                cancelled.set(true);
                            }
                        }
                    });
                    if (cancelled.get()) {
                        break;
                    }
                    if (helper.authenticate(credentials.get())) {
                        mCredentials = credentials.get();
                        return true;
                    }
                }
                return false;
            } else {
                return helper.authenticate(current);
            }
        }

        @Override
        public void invalidate() {
            mCredentials = null;
        }
    }

    private LoginCredentialsService mCredentialsService;
    private LogoutService mLogoutService;
    private SymbolResolver mSymbolResolver;
    private static Activator sInstance;

    @Override
    public void start(BundleContext context) throws Exception {
        context.registerService(IPositionLabelProvider.class.getName(),
                new PhotonPositionLabelProvider(), null);
        mCredentialsService = new LoginCredentialsService();
        context.registerService(ICredentialsService.class.getName(),
                mCredentialsService, null);
        mLogoutService = new LogoutService() {
            @Override
            protected void doLogout() {
                mCredentialsService.invalidate();
            }
        };
        mSymbolResolver = new SymbolResolver();
        context.registerService(ILogoutService.class.getName(), mLogoutService,
                null);
        context.registerService(ISymbolResolver.class.getName(),
                                mSymbolResolver,
                                null);
        sInstance = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        sInstance = null;
        mCredentialsService = null;
    }

    /**
     * @return the logout service
     */
    static ILogoutService getLogoutService() {
        return sInstance.mLogoutService;
    }

}
