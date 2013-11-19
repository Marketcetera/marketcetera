package org.marketcetera.ors.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.marketcetera.persist.NDEntityBase;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides a system info entry.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Entity
@Table(name="system_info")
@ClassVersion("$Id$")
public class PersistentSystemInfo
        extends NDEntityBase
{
    /**
     * Get the value value.
     *
     * @return a <code>String</code> value
     */
    public String getValue()
    {
        return value;
    }
    /**
     * Sets the value value.
     *
     * @param inValue a <code>String</code> value
     */
    public void setValue(String inValue)
    {
        value = inValue;
    }
    /**
     * system info value
     */
    @Column(name="value",nullable=true)
    private String value;
    private static final long serialVersionUID = -3387267566769523065L;
}
