/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.ui.IWorkingCopyManager;
import org.rubypeople.rdt.ui.PreferenceConstants;



/**
 * A tool bar action which toggles the presentation model of the
 * connected text editor. The editor shows either the highlight range
 * only or always the whole document.
 */
public class TogglePresentationAction extends TextEditorAction implements IPropertyChangeListener {

    private IPreferenceStore fStore;

    /**
     * Constructs and updates the action.
     */
    public TogglePresentationAction() {
        super(RubyEditorMessages.getBundleForConstructedKeys(), "TogglePresentation.", null, IAction.AS_CHECK_BOX); //$NON-NLS-1$
        RubyPluginImages.setToolImageDescriptors(this, "segment_edit.gif"); //$NON-NLS-1$
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.TOGGLE_PRESENTATION_ACTION);
        update();
    }

    /*
     * @see IAction#actionPerformed
     */
    public void run() {

        ITextEditor editor= getTextEditor();
        if (editor == null)
            return;

        IRegion remembered= editor.getHighlightRange();
        editor.resetHighlightRange();

        boolean showAll= !editor.showsHighlightRangeOnly();
        setChecked(showAll);

        editor.showHighlightRangeOnly(showAll);
        if (remembered != null)
            editor.setHighlightRange(remembered.getOffset(), remembered.getLength(), true);

        fStore.removePropertyChangeListener(this);
        fStore.setValue(PreferenceConstants.EDITOR_SHOW_SEGMENTS, showAll);
        fStore.addPropertyChangeListener(this);
    }

    /*
     * @see TextEditorAction#update
     */
    public void update() {
        ITextEditor editor= getTextEditor();
        boolean checked= (editor != null && editor.showsHighlightRangeOnly());
        setChecked(checked);
        if (editor instanceof RubyEditor) {
            IWorkingCopyManager manager= RubyPlugin.getDefault().getWorkingCopyManager();
            setEnabled(manager.getWorkingCopy(editor.getEditorInput()) != null);
        } else
            setEnabled(editor != null);
    }

    /*
     * @see TextEditorAction#setEditor(ITextEditor)
     */
    public void setEditor(ITextEditor editor) {

        super.setEditor(editor);

        if (editor != null) {

            if (fStore == null) {
                fStore= RubyPlugin.getDefault().getPreferenceStore();
                fStore.addPropertyChangeListener(this);
            }
            synchronizeWithPreference(editor);

        } else if (fStore != null) {
            fStore.removePropertyChangeListener(this);
            fStore= null;
        }

        update();
    }

    /**
     * Synchronizes the appearance of the editor with what the preference store tells him.
     *
     * @param editor the text editor
     */
    private void synchronizeWithPreference(ITextEditor editor) {

        if (editor == null)
            return;

        boolean showSegments= fStore.getBoolean(PreferenceConstants.EDITOR_SHOW_SEGMENTS);
        setChecked(showSegments);

        if (editor.showsHighlightRangeOnly() != showSegments) {
            IRegion remembered= editor.getHighlightRange();
            editor.resetHighlightRange();
            editor.showHighlightRangeOnly(showSegments);
            if (remembered != null)
                editor.setHighlightRange(remembered.getOffset(), remembered.getLength(), true);
        }
    }

    /*
     * @see IPropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().equals(PreferenceConstants.EDITOR_SHOW_SEGMENTS))
            synchronizeWithPreference(getTextEditor());
    }
}
