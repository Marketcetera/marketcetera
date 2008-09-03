package org.marketcetera.photon.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.update.ui.UpdateJob;
import org.eclipse.update.ui.UpdateManagerUI;
import org.marketcetera.photon.Messages;


/**
 * "Check for updates" action.
 *
 * @author alissovski
 */
public class CheckForUpdatesAction
    extends Action
    implements IAction, Messages
{

       public static final String ID = "org.marketcetera.photon.actions.CheckForUpdatesAction";  //$NON-NLS-1$

       private IWorkbenchWindow window;


       public CheckForUpdatesAction(IWorkbenchWindow window) {
               this.window = window;

               setId(ID);
               setText(CHECK_FOR_UPDATES_ACTION.getText());
               setToolTipText(CHECK_FOR_UPDATES_ACTION_DESCRIPTION.getText());
       }

       /* (non-Javadoc)
        * @see org.eclipse.jface.action.Action#run()
        */
       @Override
       public void run() {
               BusyIndicator.showWhile(window.getShell().getDisplay(),
                               new Runnable() {
                                       public void run() {
                                               final boolean automatic = false;
                                               final boolean download = false;
                                               UpdateJob job = new UpdateJob(CHECK_FOR_UPDATES_JOB_ACTION.getText(),
                                                                             automatic,
                                                                             download);

                                               UpdateManagerUI.openInstaller(window.getShell(), job);
                                       }
               });
       }

}
