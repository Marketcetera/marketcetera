package org.marketcetera.photon.preferences;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.core.MMapEntry;


/**
 * Dialog for mapping an event to a Ruby script.
 *  
 * @author gmiller 
 * @author andrei@lissovski.org
 */
public class EventScriptMapDialog extends Dialog {

	private static final String TITLE = "Register script";
    
    private static final String EVENT_PROMPT = "Event";
    private static final String SCRIPT_PROMPT = "Script";
    private static final String BROWSE_PROMPT = "Browse...";
    
	private Combo eventCombo;
    private Text scriptText;
    private Button browseButton;

    private String event;
    private String script;

	
    public EventScriptMapDialog(Shell parentShell) {
        super(parentShell);
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(TITLE);
    }

    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        composite.setLayout(layout);

        Label keyLabel = new Label(composite, SWT.NONE);
        keyLabel.setText(EVENT_PROMPT);
        keyLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
                                                     false, false));

        eventCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
        //agl todo:revisit we need canonical event types in the model and have a mapping between those and the combo items
        eventCombo.add("Trade", 0);  
        eventCombo.add("Quote", 1);
        eventCombo.select(0);
        GridData eventComboGridData = new GridData(GridData.FILL, GridData.FILL, true,
                                         false);
        eventComboGridData.horizontalSpan = 2;
        eventComboGridData.widthHint = convertHeightInCharsToPixels(20);
        eventCombo.setLayoutData(eventComboGridData);

        Label valueLabel = new Label(composite, SWT.NONE);
        valueLabel.setText(SCRIPT_PROMPT);
        valueLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
                                              false, false));

        scriptText = new Text(composite, SWT.BORDER);
        scriptText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
                                              true, false));
        scriptText.setEditable(false);

        browseButton = new Button(composite, SWT.PUSH);
        browseButton.setText(BROWSE_PROMPT);
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
							scriptText.setText(rubyScriptFile.getFullPath().toString());  //agl IPath.toString() has a well-defined contract and appears to be the only way to grab a string representation of the relative workspace path
							
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

    public Map.Entry<String, String> getEntry() {
        return new MMapEntry<String, String>(event, script);
    }

    protected void buttonPressed(int buttonId) {
        event = eventCombo.getText();
        script = scriptText.getText();
        super.buttonPressed(buttonId);
    }

}
