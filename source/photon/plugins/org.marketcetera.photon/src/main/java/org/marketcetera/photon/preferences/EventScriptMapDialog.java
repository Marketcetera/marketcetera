package org.marketcetera.photon.preferences;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.Messages;


/**
 * Dialog for mapping an event to a Ruby script.
 *  
 * @author gmiller 
 * @author andrei@lissovski.org
 */
public class EventScriptMapDialog
    extends Dialog
    implements Messages
{
    private Text scriptText;
    private Button browseButton;

    private String script;

	
    public EventScriptMapDialog(Shell parentShell) {
        super(parentShell);
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(RUBY_TITLE_LABEL.getText());
    }

    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        composite.setLayout(layout);

        Label valueLabel = new Label(composite, SWT.NONE);
        valueLabel.setText(RUBY_SCRIPT_LABEL.getText());
        valueLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
                                              false, false));

        scriptText = new Text(composite, SWT.BORDER);
        GridData scriptTextGridData = new GridData(GridData.FILL, GridData.FILL,
		                                              true, false);
        Point sizeHint = EclipseUtils.getTextAreaSize(composite, null, 25, 1.0);
        scriptTextGridData.widthHint = sizeHint.x;
        scriptTextGridData.heightHint = sizeHint.y;
        scriptText.setLayoutData(scriptTextGridData);
        scriptText.setEditable(false);

        browseButton = new Button(composite, SWT.PUSH);
        browseButton.setText(BROWSE_LABEL.getText());
        GridData browseButtonGridData = new GridData(GridData.FILL, GridData.FILL,
                true, false);
        browseButtonGridData.grabExcessHorizontalSpace = false;
		browseButton.setLayoutData(browseButtonGridData);
        browseButton.addListener(SWT.Selection, 
        		new Listener() {
					public void handleEvent(Event event) {
						RubyScriptSelectionDialog dialog = 
							new RubyScriptSelectionDialog(EventScriptMapDialog.this.getShell());
						if (dialog.open() == Window.OK) {
							Object[] elements = dialog.getResult();
							IFile rubyScriptFile = (IFile) elements[0];  //agl we're guaranteed a single element which is a ruby file
							scriptText.setText(rubyScriptFile.getFullPath().removeFirstSegments(1).toString());  //agl IPath.toString() has a well-defined contract and appears to be the only way to grab a string representation of the relative workspace path
							
							getButton(IDialogConstants.OK_ID).setEnabled(true);
						}
					}
        		});

        return composite;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        Button okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        okButton.setEnabled(false);
        
        createButton(parent, IDialogConstants.CANCEL_ID,
                     IDialogConstants.CANCEL_LABEL, false);
    }

    public String getEntry() {
        return script;
    }

    protected void buttonPressed(int buttonId) {
        script = scriptText.getText();
        super.buttonPressed(buttonId);
    }

}
