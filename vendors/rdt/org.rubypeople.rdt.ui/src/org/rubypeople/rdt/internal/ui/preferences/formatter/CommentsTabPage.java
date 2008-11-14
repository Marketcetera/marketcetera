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
package org.rubypeople.rdt.internal.ui.preferences.formatter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.rubypeople.rdt.core.formatter.DefaultCodeFormatterConstants;

/**
 * Tab page for the comment formatter settings.
 */
public class CommentsTabPage extends ModifyDialogTabPage {

    private final static class Controller implements Observer {

        private final Collection fMasters;
        private final Collection fSlaves;

        public Controller(Collection masters, Collection slaves) {
            fMasters = masters;
            fSlaves = slaves;
            for (final Iterator iter = fMasters.iterator(); iter.hasNext();) {
                ((CheckboxPreference) iter.next()).addObserver(this);
            }
            update(null, null);
        }

        public void update(Observable o, Object arg) {
            boolean enabled = true;

            for (final Iterator iter = fMasters.iterator(); iter.hasNext();) {
                enabled &= ((CheckboxPreference) iter.next()).getChecked();
            }

            for (final Iterator iter = fSlaves.iterator(); iter.hasNext();) {
                final Object obj = iter.next();
                if (obj instanceof Preference) {
                    ((Preference) obj).setEnabled(enabled);
                } else if (obj instanceof Control) {
                    ((Group) obj).setEnabled(enabled);
                }
            }
        }
    }

    private final static String PREVIEW = createPreviewHeader("An example for comment formatting. This example is meant to illustrate the various possibilities offered by <i>Eclipse</i> in order to format comments.") + //$NON-NLS-1$
            "require 'open-uri'\n"
            + //$NON-NLS-1$
            "#\n"
            + //$NON-NLS-1$
            "# This is the comment for the example module.\n"
            + //$NON-NLS-1$
            "#\n"
            + //$NON-NLS-1$
            " module Example\n"
            + //$NON-NLS-1$
            " #\n"
            + //$NON-NLS-1$
            " #\n"
            + //$NON-NLS-1$
            " # These possibilities include:\n"
            + //$NON-NLS-1$
            " # * Formatting of header comments. * Formatting of Rdoc tags\n"
            + //$NON-NLS-1$
            " #\n"
            + //$NON-NLS-1$
            " def self.bar; puts 'hello bar'; end\n"
            + //$NON-NLS-1$
            " #\n"
            + //$NON-NLS-1$
            " # The following is some sample code which illustrates source formatting within Rdoc comments:\n"
            + //$NON-NLS-1$
            " # <tt>class Example\n# @a= 1\n# @b= true\n# end</tt>\n" + //$NON-NLS-1$ 
            " #\n" + //$NON-NLS-1$
            " def foo(a, b); puts a + b; end\n" + //$NON-NLS-1$
            "end"; //$NON-NLS-1$

    private RubyScriptPreview fPreview;

    public CommentsTabPage(ModifyDialog modifyDialog, Map workingValues) {
        super(modifyDialog, workingValues);
    }

    protected void doCreatePreferences(Composite composite, int numColumns) {

        // global group
        final Group globalGroup = createGroup(numColumns, composite,
                FormatterMessages.CommentsTabPage_group1_title);
        final CheckboxPreference global = createPrefTrueFalse(globalGroup, numColumns,
                FormatterMessages.CommentsTabPage_enable_comment_formatting,
                DefaultCodeFormatterConstants.FORMATTER_COMMENT_FORMAT);
//        final CheckboxPreference header = createPrefTrueFalse(globalGroup, numColumns,
//                FormatterMessages.CommentsTabPage_format_header,
//                DefaultCodeFormatterConstants.FORMATTER_COMMENT_FORMAT_HEADER);

        // blank lines group
        final Group settingsGroup = createGroup(numColumns, composite,
                FormatterMessages.CommentsTabPage_group2_title);
        final CheckboxPreference blankComments = createPrefTrueFalse(settingsGroup, numColumns,
                FormatterMessages.CommentsTabPage_clear_blank_lines,
                DefaultCodeFormatterConstants.FORMATTER_COMMENT_CLEAR_BLANK_LINES);

        final Group widthGroup = createGroup(numColumns, composite,
                FormatterMessages.CommentsTabPage_group3_title);
        final NumberPreference lineWidth = createNumberPref(widthGroup, numColumns,
                FormatterMessages.CommentsTabPage_line_width,
                DefaultCodeFormatterConstants.FORMATTER_COMMENT_LINE_LENGTH, 0, 9999);

        Collection masters, slaves;

        masters = new ArrayList();
        masters.add(global);

        slaves = new ArrayList();
        slaves.add(settingsGroup);
//        slaves.add(header);
        slaves.add(blankComments);
        slaves.add(lineWidth);

        new Controller(masters, slaves);

        masters = new ArrayList();
        masters.add(global);

        slaves = new ArrayList();

        new Controller(masters, slaves);
    }

    protected void initializePage() {
        fPreview.setPreviewText(PREVIEW);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jdt.internal.ui.preferences.formatter.ModifyDialogTabPage#doCreateJavaPreview(org.eclipse.swt.widgets.Composite)
     */
    protected RubyPreview doCreateRubyPreview(Composite parent) {
        fPreview = new RubyScriptPreview(fWorkingValues, parent);
        return fPreview;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jdt.internal.ui.preferences.formatter.ModifyDialogTabPage#doUpdatePreview()
     */
    protected void doUpdatePreview() {
        fPreview.update();
    }

    private CheckboxPreference createPrefTrueFalse(Composite composite, int numColumns,
            String text, String key) {
        return createCheckboxPref(composite, numColumns, text, key, FALSE_TRUE);
    }
}
