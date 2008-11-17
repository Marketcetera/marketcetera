package org.rubypeople.rdt.internal.ui.dialogs;

import java.util.Arrays;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * An abstract base class for dialogs with a status bar and ok/cancel buttons.
 * The status message must be passed over as StatusInfo object and can be
 * an error, warning or ok. The OK button is enabled or disabled depending
 * on the status.
 */ 
public abstract class SelectionStatusDialog extends SelectionDialog {
	
	private MessageLine fStatusLine;
	private IStatus fLastStatus;
	private Image fImage;
	private boolean fStatusLineAboveButtons= false;	

	/**
	 * Creates an instance of a <code>SelectionStatusDialog</code>.
	 */	
	public SelectionStatusDialog(Shell parent) {
		super(parent);
	}
	
	/**
	 * Controls whether status line appears to the left of the buttons (default)
	 * or above them.
	 *
	 * @param aboveButtons if <code>true</code> status line is placed above buttons; if
	 * 	<code>false</code> to the right
	 */
	public void setStatusLineAboveButtons(boolean aboveButtons) {
		fStatusLineAboveButtons= aboveButtons;
	}
	
	/**
	 * Sets the image for this dialog.
	 * @param image the image.
	 */
	public void setImage(Image image) {
		fImage= image;
	}
	
	/**
	 * Returns the first element from the list of results. Returns <code>null</code>
	 * if no element has been selected.
	 *
	 * @return the first result element if one exists. Otherwise <code>null</code> is
	 *  returned.
	 */	
	public Object getFirstResult() {
		Object[] result= getResult();
		if (result == null || result.length == 0)
			return null;
		return result[0];	
	}
	
	/**
	 * Sets a result element at the given position.
	 */
	protected void setResult(int position, Object element) {
		Object[] result= getResult();
		result[position]= element;
		setResult(Arrays.asList(result));
	}

	/**
	 * Compute the result and return it.
	 */
	protected abstract void computeResult();
	  
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (fImage != null)
			shell.setImage(fImage);
	}
	
	/**
	 * Update the dialog's status line to reflect the given status. It is safe to call
	 * this method before the dialog has been opened.
	 */
	protected void updateStatus(IStatus status) {
		fLastStatus= status;
		if (fStatusLine != null && !fStatusLine.isDisposed()) {
		    updateButtonsEnableState(status);
		    fStatusLine.setErrorStatus(status);
		}
	}	

	/**
	 * Update the status of the ok button to reflect the given status. Subclasses
	 * may override this method to update additional buttons.
	 */
	protected void updateButtonsEnableState(IStatus status) {
		Button okButton= getOkButton();
		if (okButton != null && !okButton.isDisposed())
			okButton.setEnabled(!status.matches(IStatus.ERROR));
	}
	
	protected void okPressed() {
		computeResult();
		super.okPressed();
	}

	public void create() {
		super.create();
		if (fLastStatus != null)
			updateStatus(fLastStatus);
	}

	protected Control createButtonBar(Composite parent) {
		Composite composite= new Composite(parent, SWT.NULL);
		GridLayout layout= new GridLayout();
		if (fStatusLineAboveButtons) {
			layout.marginWidth= 5;
		} else {
			layout.numColumns= 2;
		}
		layout.marginHeight= 0; layout.marginWidth= 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		fStatusLine= new MessageLine(composite);
		fStatusLine.setAlignment(SWT.LEFT);
		fStatusLine.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fStatusLine.setErrorStatus(null);
		
		GridData gd= new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent= convertWidthInCharsToPixels(1);
		fStatusLine.setLayoutData(gd);
		
		super.createButtonBar(composite);
		return composite;
	}
	
}