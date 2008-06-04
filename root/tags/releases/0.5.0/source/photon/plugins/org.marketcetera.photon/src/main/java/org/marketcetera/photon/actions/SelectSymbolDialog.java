package org.marketcetera.photon.actions;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.marketcetera.photon.EclipseUtils;

public class SelectSymbolDialog extends Dialog {
	private String title;

	private String prompt;

	private Text symbolText;

	private String targetSymbol;

	public SelectSymbolDialog(IWorkbenchWindow window, String title,
			String prompt) {
		super(window.getShell());
		this.title = title;
		this.prompt = prompt;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);

		Label promptLabel = new Label(composite, SWT.NONE);
		promptLabel.setText(prompt);
		promptLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		symbolText = new Text(composite, SWT.BORDER);
		{
			GridData gridData = new GridData(SWT.CENTER, SWT.FILL, true, true);
			Point sizeHint = EclipseUtils.getTextAreaSize(symbolText, "METC",
					8, 1.0);
			gridData.widthHint = sizeHint.x;
			gridData.heightHint = sizeHint.y;
			symbolText.setLayoutData(gridData);
		}

		return composite;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);

	}

	protected void okPressed() {
		String symbol = symbolText.getText();
		if (symbol != null && symbol.trim().length() > 0) {
			targetSymbol = symbol;
		}
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		targetSymbol = null;
		super.cancelPressed();
	}
	
	public String getTargetSymbol() {
		return targetSymbol;
	}
}
