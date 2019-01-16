package org.marketcetera.photon.ui;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Displays the logged in username in the bottom status bar of the Photon client.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class UsernameWidget
        extends WorkbenchWindowControlContribution
{
    /* (non-Javadoc)
     * @see org.eclipse.jface.action.ControlContribution#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createControl(final Composite inParent)
    {
        final Composite composite = new Composite(inParent,
                                                  SWT.NONE);
        usernameValue = new Label(composite,
                                  SWT.NONE);
        GridLayoutFactory.swtDefaults().generateLayout(composite);
        usernameValue.setText(nouser);
        currentUsername = nouser;
        task = new TimerTask() {
            public void run() {
                try {
                    if(!usernameValue.isDisposed()) {
                        usernameValue.getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                if(!usernameValue.isDisposed()) {
                                    String username = PhotonPlugin.getDefault().getCurrentUser();
                                    if(username == null) {
                                        username = nouser;
                                    }
                                    if(username.equals(currentUsername)) {
                                        return;
                                    }
                                    usernameValue.setText(username);
                                    currentUsername = username;
                                    task.cancel();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(PhotonPlugin.MAIN_CONSOLE_LOGGER_NAME,
                                          e);
                }
            }
        };
        timer.schedule(task, 0, 1000);
        return composite;
    }
    /* (non-Javadoc)
     * @see org.eclipse.jface.action.ContributionItem#dispose()
     */
    @Override
    public void dispose()
    {
        if(task != null) {
            task.cancel();
            task = null;
        }
        super.dispose();
    }
    /**
     * holds the current username
     */
    private volatile String currentUsername = "none";
    /**
     * control which displays the username
     */
    private Label usernameValue;
    /**
     * updates logged in username
     */
    private static final Timer timer = new Timer("UserUpdateTimer"); //$NON-NLS-1$
    /**
     * task responsible for updating logged in user
     */
    private TimerTask task;
    /**
     * indicates no authenticated user
     */
    private static final String nouser = "                    ";
}
