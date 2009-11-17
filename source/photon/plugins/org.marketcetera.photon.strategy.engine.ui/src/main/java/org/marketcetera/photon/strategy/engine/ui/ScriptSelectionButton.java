package org.marketcetera.photon.strategy.engine.ui;

import org.eclipse.swt.widgets.Shell;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Abstraction for configuring a button that selects a script, e.g. from a file
 * chooser.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public abstract class ScriptSelectionButton {

    private final String mText;

    /**
     * Constructor.
     * 
     * @param text
     *            the button label
     * @throws IllegalArgumentException
     *             if text is null
     */
    public ScriptSelectionButton(String text) {
        Validate.notNull(text, "text"); //$NON-NLS-1$
        mText = text;
    }

    /**
     * Returns the button label.
     * 
     * @return the label for the button
     */
    public final String getText() {
        return mText;
    }

    /**
     * Provides a string representation of the script's location.
     * 
     * @param shell
     *            the current UI shell to support popup dialogs
     * @param current
     *            the current value for the script's location, may be null if
     *            none exists
     * @return the selected value, or null if none was selected
     */
    public abstract String selectScript(Shell shell, String current);
}