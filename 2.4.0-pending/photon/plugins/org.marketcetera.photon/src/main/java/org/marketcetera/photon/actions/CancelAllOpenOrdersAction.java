package org.marketcetera.photon.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * UI for canceling all open orders.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class CancelAllOpenOrdersAction extends Action implements Messages {

	private static final String ID = "org.marketcetera.photon.actions.CancelAllOpenOrdersAction"; //$NON-NLS-1$

	/**
	 * Constructor.
	 */
	public CancelAllOpenOrdersAction() {
		setId(ID);
		setText(CANCEL_ALL_OPEN_ORDERS_ACTION.getText());
		setToolTipText(CANCEL_ALL_OPEN_ORDERS_ACTION_DESCRIPTION.getText());
		setImageDescriptor(PhotonPlugin
				.getImageDescriptor(IImageKeys.CANCEL_ALL_OPEN_ORDERS));
	}

	@Override
	public void run() {
		if (MessageDialog.openConfirm(null,
				CANCEL_ALL_OPEN_ORDERS_ACTION_DESCRIPTION.getText(),
				CANCEL_ALL_OPEN_ORDERS_ACTION_CONFIRMATION.getText())) {
			try {
				new ProgressMonitorDialog(null).run(true, false,
						new IRunnableWithProgress() {

							@Override
							public void run(IProgressMonitor monitor)
									throws InvocationTargetException,
									InterruptedException {
								PhotonPlugin.getDefault().getPhotonController()
										.cancelAllOpenOrders(monitor);
							}
						});
			} catch (InterruptedException e) {
				// Intentionally not restoring the interrupt status since this
				// is the main UI thread where it will be ignored
				reportException(e);
			} catch (InvocationTargetException e) {
				reportException(e);
			}
		}
	}

	private void reportException(Exception e) {
		CANCEL_ALL_OPEN_ORDERS_FAILED.error(this, e);
		ErrorDialog.openError(null, null, null, new Status(IStatus.ERROR,
				PhotonPlugin.ID, CANCEL_ALL_OPEN_ORDERS_FAILED.getText(), e));
	}

}
