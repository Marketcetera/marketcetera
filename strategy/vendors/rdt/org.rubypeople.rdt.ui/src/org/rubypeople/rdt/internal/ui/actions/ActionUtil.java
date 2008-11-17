package org.rubypeople.rdt.internal.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;

public class ActionUtil {
	
	private ActionUtil() {}

	public static boolean isProcessable(Shell shell, IEditorPart editor) {
		if (editor == null)
			return true;
		IRubyElement input= SelectionConverter.getInput(editor);
		// if a Ruby editor doesn't have an input of type Ruby element
		// then it is for sure not on the build path
		if (input == null) {
			MessageDialog.openInformation(shell, 
				ActionMessages.ActionUtil_notOnBuildPath_title,  
				ActionMessages.ActionUtil_notOnBuildPath_message); 
			return false;
		}
		return isProcessable(shell, input);
	}
	
	public static boolean isProcessable(Shell shell, Object element) {
		if (!(element instanceof IRubyElement))
			return true;
			
		if (isOnBuildPath((IRubyElement)element))
			return true;
		MessageDialog.openInformation(shell, 
			ActionMessages.ActionUtil_notOnBuildPath_title,  
			ActionMessages.ActionUtil_notOnBuildPath_message); 
		return false;
	}
	
	public static boolean isOnBuildPath(IRubyElement element) {	
        //fix for bug http://dev.eclipse.org/bugs/show_bug.cgi?id=20051
        if (element.getElementType() == IRubyElement.RUBY_PROJECT)
            return true;
		IRubyProject project= element.getRubyProject();
		try {
			// TODO When we handle loadpaths correctly, uncomment
//			if (!project.isOnLoadpath(element))
//				return false;
			IProject resourceProject= project.getProject();
			if (resourceProject == null)
				return false;
			IProjectNature nature= resourceProject.getNature(RubyCore.NATURE_ID);
			// We have a Ruby project
			if (nature != null)
				return true;
		} catch (CoreException e) {
		}
		return false;
	}

}
