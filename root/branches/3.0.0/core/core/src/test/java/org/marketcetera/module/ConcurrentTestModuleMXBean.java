package org.marketcetera.module;

import org.marketcetera.core.attributes.ClassVersion;

import javax.management.MXBean;

/* $License$ */
/**
 * ConcurrentTestModuleMXBean
 *
 * @author anshul@marketcetera.com
 * @version $Id: ConcurrentTestModuleMXBean.java 82330 2012-04-10 16:29:13Z colin $
 * @since 1.1.0
 */
@ClassVersion("$Id: ConcurrentTestModuleMXBean.java 82330 2012-04-10 16:29:13Z colin $")
@MXBean(true)
public interface ConcurrentTestModuleMXBean {
    /**
     * Sets the property value.
     *
     * @param inValue the property value.
     */
    void setValue(String inValue);
}
