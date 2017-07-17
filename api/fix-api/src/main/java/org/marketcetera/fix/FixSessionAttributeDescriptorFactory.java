package org.marketcetera.fix;



/* $License$ */

/**
 * Creates {@link FixSessionAttributeDescriptor} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixSessionAttributeDescriptorFactory
{
    /**
     * Create a FixSessionAttributeDescriptor object.
     *
     * @param inName a <code>String</code> value
     * @param inDescription a <code>String</code> value or <code>null</code>
     * @param inDefaultValue a <code>String</code> value or <code>null</code>
     * @param inPattern a <code>String</code> value or <code>null</code>
     * @param isRequired a <code>boolean</code> value
     * @return a <code>FixSessionAttributeDescriptor</code> value
     */
    FixSessionAttributeDescriptor create(String inName,
                                         String inDescription,
                                         String inDefaultValue,
                                         String inPattern,
                                         boolean isRequired);
    /**
     * Create a FixSessionAttributeDescriptor object.
     *
     * @param inFixSessionAttributeDescriptor a <code>FixSessionAttributeDescriptor</code> value
     * @return a <code>FixSessionAttributeDescriptor</code> value
     */
    FixSessionAttributeDescriptor create(FixSessionAttributeDescriptor inFixSessionAttributeDescriptor);
    /**
     * Create a FixSessionAttributeDescriptor object.
     *
     * @return a <code>FixSessionAttributeDescriptor</code> value
     */
    FixSessionAttributeDescriptor create();
}
