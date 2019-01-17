package org.marketcetera.photon.ui;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.event.LoginEvent;
import org.marketcetera.photon.event.LogoutEvent;

import com.google.common.eventbus.Subscribe;

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
        PhotonPlugin.getDefault().register(this);
        return composite;
    }
    /* (non-Javadoc)
     * @see org.eclipse.jface.action.ContributionItem#dispose()
     */
    @Override
    public void dispose()
    {
        PhotonPlugin.getDefault().unregister(this);
        super.dispose();
    }
    /**
     * Receive a logon event.
     *
     * @param inLogonEvent a <code>LogonEvent</code> value
     */
    @Subscribe
    public void receiveLogon(LoginEvent inLogonEvent)
    {
        if(usernameValue == null || usernameValue.isDisposed()) {
            return;
        }
        String newUsername = inLogonEvent.getUsername();
        if(currentUsername.equals(newUsername)) {
            return;
        }
        currentUsername = newUsername;
        if(currentUsername == null) {
            currentUsername = nouser;
        }
        updateWidget();
    }
    /**
     * Receive a logout event.
     *
     * @param inLogoutEvent a <code>LogoutEvent</code> value
     */
    @Subscribe
    public void receiveLogout(LogoutEvent inLogoutEvent)
    {
        currentUsername = nouser;
        updateWidget();
    }
    private void updateWidget()
    {
        usernameValue.getDisplay().asyncExec(new Runnable() {
            @Override
            public void run()
            {
                usernameValue.setText(currentUsername);
            }}
        );
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
     * indicates no authenticated user
     */
    private static final String nouser = "                    ";
}
