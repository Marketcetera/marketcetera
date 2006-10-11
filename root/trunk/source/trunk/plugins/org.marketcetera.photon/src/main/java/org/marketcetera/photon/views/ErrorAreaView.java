package org.marketcetera.photon.views;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.RCPUtils;
import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.custom.CCombo;

@Deprecated
@ClassVersion("$Id$")
public abstract class ErrorAreaView extends ViewPart {

	private Composite top = null;
	private FormToolkit formToolkit = null;   //  @jve:decl-index=0:visual-constraint=""
	private Form form = null;
	private CLabel messageLabel = null;

	@Override
	public void createPartControl(Composite parent) {
        GridData messageLabelGridData = new GridData();
        messageLabelGridData.horizontalAlignment = GridData.FILL;
        messageLabelGridData.grabExcessHorizontalSpace = true;
        messageLabelGridData.verticalAlignment = GridData.END;
        top = new Composite(parent, SWT.NONE);
        top.setLayout(new GridLayout());
		form = getFormToolkit().createForm(top);

		top.setBackground(form.getBackground());
        top.setForeground(form.getForeground());
        messageLabel = new CLabel(top, SWT.NONE);
        messageLabel.setLayoutData(messageLabelGridData);
        messageLabel.setBackground(form.getBackground());
        messageLabel.setForeground(form.getForeground());
	

        createWorkArea(form.getBody());
   	}

    protected abstract void createWorkArea(Composite parent);

	/**
     * Sets the message for this dialog with an indication of what type of
     * message it is.
     * <p>
     * The valid message types are one of <code>NONE</code>,
     * <code>INFORMATION</code>,<code>WARNING</code>, or
     * <code>ERROR</code>, members of the {@link IMessageProvider}
     * interface.
     * </p>
     * 
     * @param newMessage
     *            the message, or <code>null</code> to clear the message
     * @param newType
     *            the message type
     * @since 2.0
     */
    public void setMessage(String newMessage, int newType) {
        Image newImage = null;
        if (newMessage != null) {
            switch (newType) {
            case IMessageProvider.NONE:
                break;
            case IMessageProvider.INFORMATION:
                newImage = JFaceResources.getImage(TitleAreaDialog.DLG_IMG_MESSAGE_INFO);
                break;
            case IMessageProvider.WARNING:
                newImage = JFaceResources.getImage(TitleAreaDialog.DLG_IMG_MESSAGE_WARNING);
                break;
            case IMessageProvider.ERROR:
                newImage = JFaceResources.getImage(TitleAreaDialog.DLG_IMG_MESSAGE_ERROR);
                break;
            }
        }
        showMessage(newMessage, newImage);
    }
    /**
     * Show the new message and image.
     * @param newMessage 
     * @param newImage
     */
    public void showMessage(String newMessage, Image newImage) {
        // Any change?
        if (newMessage.equals(messageLabel.getText()) && messageLabel.getImage() == newImage) {
			return;
		}
        messageLabel.setText(RCPUtils.escapeAmpersands(newMessage));
        messageLabel.setImage(newImage);
    }

    public void clearMessage(){
    	messageLabel.setText("");
    	messageLabel.setImage(null);
    }
    
    @Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * This method initializes formToolkit	
	 * 	
	 * @return org.eclipse.ui.forms.widgets.FormToolkit	
	 */
	protected FormToolkit getFormToolkit() {
		if (formToolkit == null) {
			formToolkit = new FormToolkit(Display.getCurrent());
		}
		return formToolkit;
	}

	
}
