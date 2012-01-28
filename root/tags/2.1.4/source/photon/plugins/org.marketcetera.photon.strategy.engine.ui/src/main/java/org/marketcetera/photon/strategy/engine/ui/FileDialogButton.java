package org.marketcetera.photon.strategy.engine.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A {@link ScriptSelectionButton} that opens a native {@link FileDialog} to
 * choose a script file. It works with the native string representation of
 * files.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public final class FileDialogButton extends ScriptSelectionButton {

    /*
     * Note: no unit tests since FileDialog is a native dialog that cannot
     * currently be accessed by SWTBot.
     */

    /**
     * Constructor.
     * 
     * @param text
     *            the button label
     * @throws IllegalArgumentException
     *             if text is null
     */
    public FileDialogButton(String text) {
        super(text);
    }

    @Override
    public String selectScript(Shell shell, String current) {
        FileDialog dialog = new FileDialog(shell, SWT.OPEN | SWT.SHEET);
        dialog.setFileName(current);
        return dialog.open();
    }
}