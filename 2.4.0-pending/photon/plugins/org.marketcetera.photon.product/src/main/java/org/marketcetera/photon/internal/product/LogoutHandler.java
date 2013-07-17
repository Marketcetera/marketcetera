package org.marketcetera.photon.internal.product;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Handler for the {@code org.marketcetera.photon.product.commands.logout}
 * command.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public final class LogoutHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Activator.getLogoutService().logout();
        return null;
    }
}
