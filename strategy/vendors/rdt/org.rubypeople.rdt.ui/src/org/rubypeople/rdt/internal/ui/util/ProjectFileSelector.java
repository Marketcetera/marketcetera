package org.rubypeople.rdt.internal.ui.util;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

// XXX Collapse this into FileSelector!
public class ProjectFileSelector extends FileSelector {

	private RubyProjectSelector selector;

	public ProjectFileSelector(Composite parent, RubyProjectSelector selector) {
		super(parent);
		this.selector = selector;
	}
	
	@Override
	protected boolean setFilterPath(FileDialog dialog) {
		boolean set = super.setFilterPath(dialog);
		if (!set) {
			IProject project = selector.getSelection();
			if (project == null) return false;
			String filename = textField.getText();
			if (filename != null && filename.trim().length() != 0) {
				File projectFile = project.getLocation().append(filename).toFile();
				if (projectFile.exists()) {
					dialog.setFilterPath(projectFile.getParent());
					return true;
				}
			}
			dialog.setFilterPath(project.getLocation().toOSString());
			return true;
		}
		return set;
	}
	
	@Override
	protected void setText(String selectedFile) {
		IProject project = selector.getSelection();
		if (project != null) {
			String projectAbsolutePath = project.getLocation().toFile().toString();
			if (selectedFile.startsWith(projectAbsolutePath)) {
				selectedFile = selectedFile.substring(projectAbsolutePath.length() + 1); // +1 is for the path separator 
			}
		}
		super.setText(selectedFile);
	}

}
