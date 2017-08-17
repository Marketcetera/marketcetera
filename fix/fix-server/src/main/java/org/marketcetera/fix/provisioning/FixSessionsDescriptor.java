package org.marketcetera.fix.provisioning;

import java.util.Collection;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Contains a collection of {@link FixSessionDescriptor} and global settings to apply to them.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixSessionsDescriptor
{
    /**
     * Get the sessionSettings value.
     *
     * @return a <code>SessionSettingsDescriptor</code> value
     */
    public SessionSettingsDescriptor getSessionSettings()
    {
        return sessionSettings;
    }
    /**
     * Sets the sessionSettings value.
     *
     * @param inSessionSettings a <code>SessionSettingsDescriptor</code> value
     */
    public void setSessionSettings(SessionSettingsDescriptor inSessionSettings)
    {
        sessionSettings = inSessionSettings;
    }
    /**
     * Get the fixSessions value.
     *
     * @return a <code>Collection&lt;FixSessionDescriptor&gt;</code> value
     */
    public Collection<FixSessionDescriptor> getFixSessions()
    {
        return fixSessions;
    }
    /**
     * Sets the fixSessions value.
     *
     * @param inFixSessions a <code>Collection&lt;FixSessionDescriptor&gt;</code> value
     */
    public void setFixSessions(Collection<FixSessionDescriptor> inFixSessions)
    {
        fixSessions = inFixSessions;
    }
    /**
     * global session settings
     */
    private SessionSettingsDescriptor sessionSettings;
    /**
     * fix sessions
     */
    private Collection<FixSessionDescriptor> fixSessions = Lists.newArrayList();
}
