package org.marketcetera.fix;

/* $License$ */

/**
 * Describes a Fix session attribute.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixSessionAttributeDescriptor
{
    /**
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName();
    /**
     * Get the defaultValue value.
     *
     * @return a <code>String</code> value
     */
    public String getDefaultValue();
    /**
     * Get the description value.
     *
     * @return a <code>String</code> value
     */
    public String getDescription();
    /**
     * Get the pattern value.
     *
     * @return a <code>String</code> value
     */
    public String getPattern();
    /**
     * Get the required value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isRequired();
    /**
     * Get the advice value.
     *
     * @return a <code>String</code> value
     */
    public String getAdvice();
}
