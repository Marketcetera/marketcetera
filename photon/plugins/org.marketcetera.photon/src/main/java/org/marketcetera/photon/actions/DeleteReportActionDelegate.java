package org.marketcetera.photon.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.actions.ActionDelegate;
import org.marketcetera.client.ClientManager;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ExecutionReportImpl;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Deletes an <code>ExecutionReport</code> from the server.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class DeleteReportActionDelegate
        extends ActionDelegate
{
    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.ActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(IAction inAction,
                                 ISelection inSelection)
    {
        boolean shouldEnable = false;
        currentSelection = inSelection;
        ReportBase report = getReportFromSelection(inSelection);
        if(report != null) {
            shouldEnable = true;
        }
        inAction.setEnabled(shouldEnable);
    }
    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.ActionDelegate#runWithEvent(org.eclipse.jface.action.IAction, org.eclipse.swt.widgets.Event)
     */
    @Override
    public void runWithEvent(IAction inAction,
                             Event inEvent)
    {
        ReportBase report = getReportFromSelection(currentSelection);
        if(report != null && report instanceof ExecutionReport) {
            try {
                MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell(),SWT.YES | SWT.NO); 
                messageBox.setText(Messages.DEL_EXECUTION_REPORT_MXBOX_TITLE_WARNING.getText());
                messageBox.setMessage(Messages.DEL_EXECUTION_REPORT_MXBOX_MESSAGE.getText());
                if(messageBox.open() == SWT.YES) {
                    ClientManager.getInstance().deleteReport(((ExecutionReportImpl)report));
                }
            } catch (Exception anyException) {
                PhotonPlugin.getMainConsoleLogger().error(Messages.DEL_EXECUTION_REPORT_ERROR.getText(), 
                                                          anyException);
            }
        }
    }
    /**
     * Gets the report object from the given selection.
     *
     * @param inSelection an <code>ISelection</code> value
     * @return a <code>ReportBase</code> value
     */
    private ReportBase getReportFromSelection(ISelection inSelection) {
        ReportBase report = null;
        if(inSelection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection)inSelection;
            if(structuredSelection.size() == 1) {
                Object firstElement = structuredSelection.getFirstElement();
                if(firstElement instanceof ReportHolder) {
                    report = ((ReportHolder)firstElement).getReport();
                }
            }
        }
        return report;
    }
    /**
     * action ID
     */
    public final static String ID = "org.marketcetera.photon.actions.DeleteReportActionDelegate"; //$NON-NLS-1$
    /**
     * current selection value
     */
    private ISelection currentSelection;
}
