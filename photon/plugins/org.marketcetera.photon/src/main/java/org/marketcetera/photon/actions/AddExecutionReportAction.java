package org.marketcetera.photon.actions;

import static org.marketcetera.photon.Messages.ADD_EXECUTION_REPORT_LABEL;
import static org.marketcetera.photon.Messages.ADD_EXECUTION_REPORT_TOOLTIPS;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.AddExecutionReportDialog;

/**
 * Toolbar action that triggers the Add execution report dialog
 * 
 * @author milan
 *
 */
public class AddExecutionReportAction extends Action 
{
    /**
     * unique identifier for this action
     */
    private static final String ID = "org.marketcetera.photon.actions.AddExecutionReportAction"; //$NON-NLS-1$
    
    public AddExecutionReportAction()
    {
        setId(ID);
        setText(ADD_EXECUTION_REPORT_LABEL.getText());
        setToolTipText(ADD_EXECUTION_REPORT_TOOLTIPS.getText());
        setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.SEND_ITEM));
    }
    
    public void run()
    {
    	Dialog addExecutionReportDialog = new AddExecutionReportDialog(Display.getCurrent().getActiveShell());
    	addExecutionReportDialog.open();
    }
}
