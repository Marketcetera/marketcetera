package org.marketcetera.photon.preferences;

import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MMapEntry;
import org.marketcetera.photon.Messages;

@ClassVersion("$Id$") //$NON-NLS-1$
public class MapDialog
    extends Dialog
    implements Messages
{

    private Text keyText;

    private Text valueText;

    private String key;
    private String value;

	private final String keyPrompt;

	private final String valuePrompt;

	private final String title;

    public MapDialog(IShellProvider parentShell, String title, String keyPrompt, String valuePrompt) {
        super(parentShell);
		this.title = title;
		this.keyPrompt = keyPrompt;
		this.valuePrompt = valuePrompt;
    }

    public MapDialog(Shell parentShell, String title, String keyPrompt, String valuePrompt) {
        super(parentShell);
		this.title = title;
		this.keyPrompt = keyPrompt;
		this.valuePrompt = valuePrompt;
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(title);
    }

    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        composite.setLayout(layout);


        Label keyLabel = new Label(composite, SWT.NONE);
        keyLabel.setText(keyPrompt);
        keyLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
                                                     false, false));

        keyText = new Text(composite, SWT.BORDER);
        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
                                         false);
        gridData.widthHint = convertHeightInCharsToPixels(20);
        keyText.setLayoutData(gridData);

        Label valueLabel = new Label(composite, SWT.NONE);
        valueLabel.setText(valuePrompt);
        valueLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
                                              false, false));

        valueText = new Text(composite, SWT.BORDER);
        valueText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
                                              true, false));

        return composite;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                     IDialogConstants.CANCEL_LABEL, false);

    }

    public Map.Entry<String, String> getEntry() {
        if (key == null) return null;
        return new MMapEntry<String, String>(key, value);
    }

    protected void buttonPressed(int buttonId) {
        key = keyText.getText();
        value = valueText.getText();
        super.buttonPressed(buttonId);
    }

    protected void okPressed() {

        if (key == null || key.equals("")) { //$NON-NLS-1$
            MessageDialog.openError(getShell(),
                                    INVALID_PROMPT.getText(keyPrompt),
                                    MUST_NOT_BE_BLANK.getText(keyPrompt));
            return;
        }
        if (value == null || value.equals("")) { //$NON-NLS-1$
            MessageDialog.openError(getShell(),
                                    INVALID_PROMPT.getText(valuePrompt),
                                    MUST_NOT_BE_BLANK.getText(valuePrompt));
            return;
        }
        super.okPressed();
    }


}
