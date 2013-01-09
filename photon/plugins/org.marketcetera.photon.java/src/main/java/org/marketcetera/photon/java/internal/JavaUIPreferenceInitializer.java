package org.marketcetera.photon.java.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlightings;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Preferences used by {@link JavaEditor}. Based on
 * org.eclipse.jdt.internal.ui.JavaUIPreferenceInitializer.
 * <p>
 * Preferences related to the basic Java editing functionality provided by
 * {@link JavaEditor} were retained.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class JavaUIPreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getCurrent().getPreferenceStore();
        EditorsUI.useAnnotationsPreferencePage(store);
        EditorsUI.useQuickDiffPreferencePage(store);
        store
                .setToDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER);
        store
                .setToDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER_COLOR);

        store.setDefault(PreferenceConstants.EDITOR_MULTI_LINE_COMMENT_BOLD,
                false);
        store.setDefault(PreferenceConstants.EDITOR_MULTI_LINE_COMMENT_ITALIC,
                false);

        store.setDefault(PreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_BOLD,
                false);
        store.setDefault(PreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_ITALIC,
                false);

        store.setDefault(PreferenceConstants.EDITOR_JAVA_KEYWORD_BOLD, true);
        store.setDefault(PreferenceConstants.EDITOR_JAVA_KEYWORD_ITALIC, false);

        store.setDefault(PreferenceConstants.EDITOR_STRING_BOLD, false);
        store.setDefault(PreferenceConstants.EDITOR_STRING_ITALIC, false);

        store.setDefault(PreferenceConstants.EDITOR_JAVA_DEFAULT_BOLD, false);
        store.setDefault(PreferenceConstants.EDITOR_JAVA_DEFAULT_ITALIC, false);

        store.setDefault(PreferenceConstants.EDITOR_JAVA_KEYWORD_RETURN_BOLD,
                true);
        store.setDefault(PreferenceConstants.EDITOR_JAVA_KEYWORD_RETURN_ITALIC,
                false);

        store.setDefault(PreferenceConstants.EDITOR_JAVA_OPERATOR_BOLD, false);
        store
                .setDefault(PreferenceConstants.EDITOR_JAVA_OPERATOR_ITALIC,
                        false);

        store.setDefault(PreferenceConstants.EDITOR_JAVA_BRACKET_BOLD, false);
        store.setDefault(PreferenceConstants.EDITOR_JAVA_BRACKET_ITALIC, false);

        store.setDefault(PreferenceConstants.EDITOR_TASK_TAG_BOLD, true);
        store.setDefault(PreferenceConstants.EDITOR_TASK_TAG_ITALIC, false);

        store.setDefault(PreferenceConstants.EDITOR_JAVADOC_KEYWORD_BOLD, true);
        store.setDefault(PreferenceConstants.EDITOR_JAVADOC_KEYWORD_ITALIC,
                false);

        store.setDefault(PreferenceConstants.EDITOR_JAVADOC_TAG_BOLD, false);
        store.setDefault(PreferenceConstants.EDITOR_JAVADOC_TAG_ITALIC, false);

        store.setDefault(PreferenceConstants.EDITOR_JAVADOC_LINKS_BOLD, false);
        store
                .setDefault(PreferenceConstants.EDITOR_JAVADOC_LINKS_ITALIC,
                        false);

        store
                .setDefault(PreferenceConstants.EDITOR_JAVADOC_DEFAULT_BOLD,
                        false);
        store.setDefault(PreferenceConstants.EDITOR_JAVADOC_DEFAULT_ITALIC,
                false);

        SemanticHighlightings.initDefaults(store);

        org.eclipse.jdt.internal.ui.JavaUIPreferenceInitializer
                .setThemeBasedPreferences(store, false);

    }

}
