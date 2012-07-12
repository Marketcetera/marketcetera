package org.marketcetera.photon.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.ActionDelegate;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Base class for actions that are enabled on cancelable execution reports.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public abstract class CancelOrderActionBase extends ActionDelegate {

	protected IStructuredSelection mSelection;

	/**
	 * Determines whether this action should be enabled, depending on the value of the new
	 * selection. Checks the selection to see if it is non-trivial, and if it contains an
	 * appropriate execution report type as its first element.
	 */
	public void selectionChanged(IAction action, ISelection incoming) {
		boolean shouldEnable = false;
		if (incoming instanceof IStructuredSelection) {
			mSelection = (IStructuredSelection) incoming;
			if (mSelection.size() == 1) {
				Object firstElement = mSelection.getFirstElement();
				if (firstElement instanceof ReportHolder) {
					ReportHolder reportHolder = (ReportHolder) firstElement;
					ReportBase report = reportHolder.getReport();
					if (report instanceof ExecutionReport) {
						ExecutionReport ereport = (ExecutionReport) report;
						if (ereport.getOrderID() != null && ereport.isCancelable()) {
							shouldEnable = true;
						}
					}
				}
			}
		}
		action.setEnabled(shouldEnable);
	}

	@Override
	public void runWithEvent(IAction arg0, Event arg1) {
		try {
			ExecutionReport report = (ExecutionReport) ((ReportHolder) mSelection.getFirstElement())
					.getReport();
			processReport(report);
		} catch (Exception e) {
			PhotonPlugin.getMainConsoleLogger().error(Messages.CANNOT_CANCEL.getText(), e);
		}
	}

	/**
	 * Hook for subclasses to do the necessary work.
	 * 
	 * @param report
	 *            the selected report
	 * @throws Exception
	 *             if an error occurs
	 */
	abstract void processReport(ExecutionReport report) throws Exception;

}