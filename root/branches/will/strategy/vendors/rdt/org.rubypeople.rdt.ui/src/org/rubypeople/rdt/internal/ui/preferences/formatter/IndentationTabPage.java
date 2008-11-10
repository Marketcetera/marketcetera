/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     istvan@benedek-home.de
 *       - 103706 [formatter] indent empty lines
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.preferences.formatter;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.text.Assert;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.formatter.DefaultCodeFormatterConstants;


public class IndentationTabPage extends ModifyDialogTabPage {
    
    private final String PREVIEW=
    createPreviewHeader(FormatterMessages.IndentationTabPage_preview_header) + 
    "class Example\n" + //$NON-NLS-1$
    "  @@my_hash= {1 => 'one', 2 => 'two',3 => 'three'}\n" + //$NON-NLS-1$
    "  @@my_array= [1,2,3,4,5,6]\n" + //$NON-NLS-1$
    "  MY_CONST= 1\n" + //$NON-NLS-1$
    "  $some_string= \"Hello\"\n" + //$NON-NLS-1$
    "  a_float= 3.0\n" + //$NON-NLS-1$
    "  def foo(a, b, c, d, e, f)\n" + //$NON-NLS-1$  
    "    case a\n" + //$NON-NLS-1$
    "    when 0\n" + //$NON-NLS-1$
    "      Other.foo\n" + //$NON-NLS-1$
    "    else\n" + //$NON-NLS-1$
    "      Other.baz\n" + //$NON-NLS-1$
    "    end\n" + //$NON-NLS-1$
    "  end\n" + //$NON-NLS-1$
    "  def bar(v)\n" + //$NON-NLS-1$   
    "    for i in 0...10 do\n" + //$NON-NLS-1$
    "      v << i\n" + //$NON-NLS-1$
    "    end\n" + //$NON-NLS-1$
    "  end\n" + //$NON-NLS-1$
    "end\n" + //$NON-NLS-1$
    "\n" + //$NON-NLS-1$
    "module MyModule\n" + //$NON-NLS-1$
    "end";//$NON-NLS-1$
    
    private RubyScriptPreview fPreview;
    private String fOldTabChar= null;
    
    public IndentationTabPage(ModifyDialog modifyDialog, Map workingValues) {
        super(modifyDialog, workingValues);
    }

    protected void doCreatePreferences(Composite composite, int numColumns) {

        final Group generalGroup= createGroup(numColumns, composite, FormatterMessages.IndentationTabPage_general_group_title); 
        
        final String[] tabPolicyValues= new String[] {RubyCore.SPACE, RubyCore.TAB, DefaultCodeFormatterConstants.MIXED};
        final String[] tabPolicyLabels= new String[] {
                FormatterMessages.IndentationTabPage_general_group_option_tab_policy_SPACE, 
                FormatterMessages.IndentationTabPage_general_group_option_tab_policy_TAB, 
                FormatterMessages.IndentationTabPage_general_group_option_tab_policy_MIXED
        };
        final ComboPreference tabPolicy= createComboPref(generalGroup, numColumns, FormatterMessages.IndentationTabPage_general_group_option_tab_policy, DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, tabPolicyValues, tabPolicyLabels);
        final CheckboxPreference onlyForLeading= createCheckboxPref(generalGroup, numColumns, FormatterMessages.IndentationTabPage_use_tabs_only_for_leading_indentations, DefaultCodeFormatterConstants.FORMATTER_USE_TABS_ONLY_FOR_LEADING_INDENTATIONS, FALSE_TRUE);
        final NumberPreference indentSize= createNumberPref(generalGroup, numColumns, FormatterMessages.IndentationTabPage_general_group_option_indent_size, DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, 0, 32); 
        final NumberPreference tabSize= createNumberPref(generalGroup, numColumns, FormatterMessages.IndentationTabPage_general_group_option_tab_size, DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, 0, 32);
        
        String tabchar= (String) fWorkingValues.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
        updateTabPreferences(tabchar, tabSize, indentSize, onlyForLeading);
        tabPolicy.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                updateTabPreferences((String) arg, tabSize, indentSize, onlyForLeading);
            }
        });
        tabSize.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                indentSize.updateWidget();
            }
        });
        
        final Group classGroup = createGroup(numColumns, composite, FormatterMessages.IndentationTabPage_indent_group_title); 
        createCheckboxPref(classGroup, numColumns, FormatterMessages.IndentationTabPage_switch_group_option_indent_statements_within_case_body, DefaultCodeFormatterConstants.FORMATTER_INDENT_CASE_BODY, FALSE_TRUE); 
        createCheckboxPref(classGroup, numColumns, FormatterMessages.IndentationTabPage_indent_empty_lines, DefaultCodeFormatterConstants.FORMATTER_INDENT_EMPTY_LINES, FALSE_TRUE); 
    }
    
    public void initializePage() {
        fPreview.setPreviewText(PREVIEW);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.preferences.formatter.ModifyDialogTabPage#doCreateRubyPreview(org.eclipse.swt.widgets.Composite)
     */
    protected RubyPreview doCreateRubyPreview(Composite parent) {
        fPreview= new RubyScriptPreview(fWorkingValues, parent);
        return fPreview;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.preferences.formatter.ModifyDialogTabPage#doUpdatePreview()
     */
    protected void doUpdatePreview() {
        fPreview.update();
    }

    private void updateTabPreferences(String tabPolicy, NumberPreference tabPreference, NumberPreference indentPreference, CheckboxPreference onlyForLeading) {
        /*
         * If the tab-char is SPACE (or TAB), INDENTATION_SIZE
         * preference is not used by the core formatter. We piggy back the
         * visual tab length setting in that preference in that case. If the
         * user selects MIXED, we use the previous TAB_SIZE preference as the
         * new INDENTATION_SIZE (as this is what it really is) and set the 
         * visual tab size to the value piggy backed in the INDENTATION_SIZE
         * preference. See also CodeFormatterUtil. 
         */
        if (DefaultCodeFormatterConstants.MIXED.equals(tabPolicy)) {
            if (RubyCore.SPACE.equals(fOldTabChar) || RubyCore.TAB.equals(fOldTabChar))
                swapTabValues();
            tabPreference.setEnabled(true);
            tabPreference.setKey(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
            indentPreference.setEnabled(true);
            indentPreference.setKey(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE);
            onlyForLeading.setEnabled(true);
        } else if (RubyCore.SPACE.equals(tabPolicy)) {
            if (DefaultCodeFormatterConstants.MIXED.equals(fOldTabChar))
                swapTabValues();
            tabPreference.setEnabled(true);
            tabPreference.setKey(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE);
            indentPreference.setEnabled(true);
            indentPreference.setKey(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
            onlyForLeading.setEnabled(false);
        } else if (RubyCore.TAB.equals(tabPolicy)) {
            if (DefaultCodeFormatterConstants.MIXED.equals(fOldTabChar))
                swapTabValues();
            tabPreference.setEnabled(true);
            tabPreference.setKey(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
            indentPreference.setEnabled(false);
            indentPreference.setKey(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
            onlyForLeading.setEnabled(true);
        } else {
            Assert.isTrue(false);
        }
        fOldTabChar= tabPolicy;
    }

    private void swapTabValues() {
        Object tabSize= fWorkingValues.get(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
        Object indentSize= fWorkingValues.get(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE);
        fWorkingValues.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, indentSize);
        fWorkingValues.put(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE, tabSize);
    }
}
