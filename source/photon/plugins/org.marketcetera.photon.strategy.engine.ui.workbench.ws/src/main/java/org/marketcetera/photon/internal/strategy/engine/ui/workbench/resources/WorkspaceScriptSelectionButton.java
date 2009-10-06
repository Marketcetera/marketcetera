package org.marketcetera.photon.internal.strategy.engine.ui.workbench.resources;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.marketcetera.photon.strategy.engine.ui.ScriptSelectionButton;
import org.marketcetera.photon.strategy.engine.ui.workbench.resources.StrategyEngineWorkspaceUI;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A {@link ScriptSelectionButton} that selects scripts from the workspace and
 * returns the result as a platform resource URL.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class WorkspaceScriptSelectionButton extends ScriptSelectionButton {

    /**
     * The prefix used to convert an IFile path to a platform resource URL.
     */
    private static final String PLATFORM_RESOURCE_URL_PREFIX = "platform:/resource"; //$NON-NLS-1$

    /**
     * Constructor.
     */
    public WorkspaceScriptSelectionButton() {
        super(Messages.WORKSPACE_SCRIPT_SELECTION_BUTTON__LABEL.getText());
    }

    @Override
    public String selectScript(Shell shell, String current) {
        final ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
                shell, new WorkbenchLabelProvider(),
                new BaseWorkbenchContentProvider());
        dialog
                .setTitle(Messages.WORKSPACE_SCRIPT_SELECTION_BUTTON_DIALOG__TITLE
                        .getText());
        dialog
                .setMessage(Messages.WORKSPACE_SCRIPT_SELECTION_BUTTON_DIALOG_PROMPT
                        .getText());
        dialog
                .setEmptyListMessage(Messages.WORKSPACE_SCRIPT_SELECTION_BUTTON_EMPTY_WORKSPACE
                        .getText());
        dialog.setAllowMultiple(false);
        dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
        if (current != null) {
            if (current.startsWith(PLATFORM_RESOURCE_URL_PREFIX)) {
                dialog.setInitialSelection(ResourcesPlugin.getWorkspace()
                        .getRoot().getFile(
                                new Path(current
                                        .substring(PLATFORM_RESOURCE_URL_PREFIX
                                                .length()))));
            }
        }
        dialog.addFilter(new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement,
                    Object element) {
                if (element instanceof IFile) {
                    // filter dotfiles
                    return !(((IFile) element).getName()).startsWith("."); //$NON-NLS-1$
                }
                return true;
            }
        });
        dialog.setValidator(new ISelectionStatusValidator() {
            @Override
            public IStatus validate(Object[] selection) {
                // only accept files
                if (selection.length > 0 && selection[0] instanceof IFile) {
                    return new Status(IStatus.OK,
                            StrategyEngineWorkspaceUI.PLUGIN_ID, null, null);
                } else {
                    return new Status(IStatus.ERROR,
                            StrategyEngineWorkspaceUI.PLUGIN_ID, null, null);
                }
            }
        });
        dialog.open();
        IFile result = (IFile) dialog.getFirstResult();
        if (result == null) {
            return null;
        } else {
            return PLATFORM_RESOURCE_URL_PREFIX
                    + result.getFullPath().toString();
        }
    }

}