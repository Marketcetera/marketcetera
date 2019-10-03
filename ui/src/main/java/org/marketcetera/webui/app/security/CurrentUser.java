package org.marketcetera.webui.app.security;

import org.marketcetera.admin.User;

@FunctionalInterface
public interface CurrentUser
{
    /**
     * Get the current user value.
     *
     * @return a <code>User</code> value
     */
    User getUser();
}
