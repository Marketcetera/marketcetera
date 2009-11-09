package org.marketcetera.photon.commons.ui;

import org.eclipse.jface.viewers.IElementComparer;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * An IElementComparer that compares objects by identity. 
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class IdentityComparer implements IElementComparer {

    @Override
    public boolean equals(Object a, Object b) {
        return a == b;
    }

    @Override
    public int hashCode(Object element) {
        return System.identityHashCode(element);
    }
}
