package org.marketcetera.photon.java.internal;

import org.eclipse.jdt.internal.ui.text.JavaColorManager;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A simple Java Editor that uses as much as it can from the JDT one without
 * engaging the core JDT engine.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class JavaEditor extends TextEditor {

    @Override
    protected void initializeEditor() {
        super.initializeEditor();
        setPreferenceStore(new ChainedPreferenceStore(new IPreferenceStore[] {
                Activator.getCurrent().getPreferenceStore(),
                EditorsUI.getPreferenceStore() }));
        JavaColorManager colorManager = Activator.getCurrent()
                .getColorManager();
        JavaSourceViewerConfiguration configuration = new JavaSourceViewerConfiguration(
                colorManager, getPreferenceStore(), this,
                IJavaPartitions.JAVA_PARTITIONING);
        setSourceViewerConfiguration(configuration);
    }

    @Override
    protected void editorContextMenuAboutToShow(IMenuManager menu) {
        super.editorContextMenuAboutToShow(menu);
        // remove editor actions that don't work in Photon
        menu.remove(ITextEditorActionConstants.CONTEXT_PREFERENCES);
        for (IContributionItem item : menu.getItems()) {
            if (item instanceof MenuManager) {
                MenuManager subMenu = (MenuManager) item;
                // Warning: this is a best effort since the menu doesn't have an
                // id
                if (subMenu.getMenuText().startsWith("Sho&w In")) { //$NON-NLS-1$
                    menu.remove(subMenu);
                }
            }
        }
    }

    @Override
    protected void rulerContextMenuAboutToShow(IMenuManager menu) {
        super.rulerContextMenuAboutToShow(menu);
        // remove preferences action that doesn't work
        menu.remove(ITextEditorActionConstants.RULER_PREFERENCES);
    }
}
