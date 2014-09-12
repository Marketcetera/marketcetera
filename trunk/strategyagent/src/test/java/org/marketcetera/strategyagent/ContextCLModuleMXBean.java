package org.marketcetera.strategyagent;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.DisplayName;

import javax.management.MXBean;

/* $License$ */
/**
 * Management interface of a module to verify that the context
 * classloader is correctly setup when the methods are invoked.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
@DisplayName("Management Interface for the module")
@MXBean(true)
public interface ContextCLModuleMXBean {
    @DisplayName("An Attribute")
    public String getAttribute();
    @DisplayName("An Attribute")
    public void setAttribute(
            @DisplayName("An Attribute")
            String inValue);
    @DisplayName("An Operation")
    public void operation();
}