package org.rubypeople.rdt.internal.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public abstract class ResourceSelector {
	protected final static String EMPTY_STRING = "";
	protected Composite composite;
	protected Button browseButton;
	protected Text textField;
	protected String browseDialogMessage = EMPTY_STRING;
	protected String browseDialogTitle = EMPTY_STRING;
	protected String validatedSelectionText = EMPTY_STRING;

	public ResourceSelector(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		GridLayout compositeLayout = new GridLayout();
		compositeLayout.marginWidth = 0;
		compositeLayout.marginHeight = 0;
		compositeLayout.numColumns = 2;
		composite.setLayout(compositeLayout);

		textField = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatedSelectionText = validateResourceSelection();
			}
		});

		browseButton = new Button(composite, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowseSelected();
			}
		});
	}

	protected abstract void handleBrowseSelected();
	protected abstract String validateResourceSelection();

	protected Shell getShell() {
		return composite.getShell();
	}

	public void setLayoutData(Object layoutData) {
		composite.setLayoutData(layoutData);
	}

	public void addModifyListener(ModifyListener aListener) {
		textField.addModifyListener(aListener);
	}

	public void setBrowseDialogMessage(String aMessage) {
		browseDialogMessage = aMessage;
	}

	public void setBrowseDialogTitle(String aTitle) {
		browseDialogTitle = aTitle;
	}

	public void setEnabled(boolean enabled) {
		composite.setEnabled(enabled);
		textField.setEnabled(enabled);
		browseButton.setEnabled(enabled);
	}

	public String getSelectionText() {
		return textField.getText();
	}

	public String getValidatedSelectionText() {
		return validatedSelectionText;
	}

	public void setSelectionText(String newText) {
		textField.setText(newText);
	}
}
