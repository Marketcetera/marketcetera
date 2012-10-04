package org.marketcetera.core.module;

import javax.management.MXBean;

/* $License$ */
/**
 * ConcurrentTestModuleMXBean
 *
 * @version $Id: ConcurrentTestModuleMXBean.java 82330 2012-04-10 16:29:13Z colin $
 * @since 1.1.0
 */
@MXBean(true)
public interface ConcurrentTestModuleMXBean {
    /**
     * Sets the property value.
     *
     * @param inValue the property value.
     */
    void setValue(String inValue);
}
