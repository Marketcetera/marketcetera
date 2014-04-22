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
public abstract class CancelOrderActionBase
        extends ActionDelegate
{
    /**
     * Determines whether this action should be enabled, depending on the value of the new selection.
     * 
     * @param inAction an <code>IAction</code> value
     * @param inSelection an <code>ISelection</code> value
     */
    public void selectionChanged(IAction inAction,
                                 ISelection inSelection)
    {
        boolean shouldEnable = false;
        if(inSelection instanceof IStructuredSelection) {
            mSelection = (IStructuredSelection)inSelection;
            if(mSelection.size() == 1) {
                Object firstElement = mSelection.getFirstElement();
                if(firstElement instanceof ReportHolder) {
                    ReportHolder reportHolder = (ReportHolder)firstElement;
                    ReportBase report = reportHolder.getReport();
                    if(report instanceof ExecutionReport) {
                        ExecutionReport eReport = (ExecutionReport)report;
                        if(eReport.getOrderID() != null && eReport.isCancelable() && eReport.getHierarchy().allowCancel()) {
                            shouldEnable = true;
                        }
                    }
                }
            }
        }
        inAction.setEnabled(shouldEnable);
    }
    @Override
    public void runWithEvent(IAction arg0, Event arg1)
    {
        try {
            ExecutionReport report = (ExecutionReport) ((ReportHolder) mSelection.getFirstElement()).getReport();
            processReport(report);
        } catch (Exception e) {
            PhotonPlugin.getMainConsoleLogger().error(Messages.CANNOT_CANCEL.getText(), e);
        }
    }
    /**
     * Hook for subclasses to do the necessary work.
     * 
     * @param report the selected report
     * @throws Exception if an error occurs
     */
    abstract void processReport(ExecutionReport report) throws Exception;
    /**
     * current selection
     */
    protected IStructuredSelection mSelection;
}
