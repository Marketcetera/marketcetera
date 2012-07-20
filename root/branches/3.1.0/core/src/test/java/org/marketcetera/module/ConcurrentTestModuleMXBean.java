package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import javax.management.MXBean;

/* $License$ */
/**
 * ConcurrentTestModuleMXBean
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.1.0
 */
@ClassVersion("$Id$")
@MXBean(true)
public interface ConcurrentTestModuleMXBean {
    /**
     * Sets the property value.
     *
     * @param inValue the property value.
     */
    void setValue(String inValue);
}
