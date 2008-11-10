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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.Assert;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;



public class GotoMatchingBracketAction extends Action {

    public final static String GOTO_MATCHING_BRACKET= "GotoMatchingBracket"; //$NON-NLS-1$

    private final RubyEditor fEditor;

    public GotoMatchingBracketAction(RubyEditor editor) {
        super(RubyEditorMessages.GotoMatchingBracket_label);
        Assert.isNotNull(editor);
        fEditor= editor;
        setEnabled(true);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.GOTO_MATCHING_BRACKET_ACTION);
    }

    public void run() {
        fEditor.gotoMatchingBracket();
    }
}
