package org.rubypeople.rdt.internal.ui.util;

import java.io.File;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

public class FileSelector extends ResourceSelector {

	public FileSelector(Composite parent) {
		super(parent);
	}

	@Override
	protected void handleBrowseSelected() {
		FileDialog dialog = new FileDialog(getShell());
		dialog.setText(browseDialogMessage);
		setFilterPath(dialog);
		
		String selectedFile = dialog.open();
		if (selectedFile != null) {
			setText(selectedFile);
		}		

	}

	protected void setText(String selectedFile) {
		textField.setText(selectedFile);
	}

	protected boolean setFilterPath(FileDialog dialog) {
		String currentWorkingDir = textField.getText();
		if (!currentWorkingDir.trim().equals("")) {
			File path = new File(currentWorkingDir);
			if (path.exists()) {
				dialog.setFilterPath(currentWorkingDir);
				return true;
			}			
		}
		return false;
	}

	@Override
	protected String validateResourceSelection() {
		String file = textField.getText();
		File directoryFile = new File(file);
		if (directoryFile.exists() && directoryFile.isFile())
			return file;
		return EMPTY_STRING;
	}

	public File getSelection() {
		return new File(getSelectionText());
	}

}
