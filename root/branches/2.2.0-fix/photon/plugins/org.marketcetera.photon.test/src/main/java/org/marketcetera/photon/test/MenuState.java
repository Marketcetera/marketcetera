package org.marketcetera.photon.test;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.swt.widgets.MenuItem;

/* $License$ */

/**
 * Captures the state of a menu. This is necessary since SWTBotMenu sometimes
 * fails with "widget disposed" errors.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public final class MenuState {

    private final String mText;
    private final boolean mEnabled;
    private final boolean mChecked;

    public MenuState(MenuItem item) {
        mText = item.getText();
        mEnabled = item.isEnabled();
        mChecked = item.getSelection();
    }

    public String getText() {
        return mText;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mText).append(mEnabled).append(
                mChecked).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MenuState other = (MenuState) obj;
        return new EqualsBuilder().append(mText, other.getText()).append(
                mEnabled, other.isEnabled())
                .append(mChecked, other.isChecked()).isEquals();
    }

}
