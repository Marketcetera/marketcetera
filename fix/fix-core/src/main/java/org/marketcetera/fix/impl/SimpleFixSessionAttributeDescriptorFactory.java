package org.marketcetera.fix.impl;

import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.fix.FixSessionAttributeDescriptorFactory;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleFixSessionAttributeDescriptorFactory
        implements FixSessionAttributeDescriptorFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSessionAttributeDescriptorFactory#create(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public SimpleFixSessionAttributeDescriptor create(String inName,
                                                      String inDescription,
                                                      String inDefaultValue,
                                                      String inPattern,
                                                      boolean inIsRequired)
    {
        SimpleFixSessionAttributeDescriptor descriptor = new SimpleFixSessionAttributeDescriptor();
        descriptor.setDefaultValue(inDefaultValue);
        descriptor.setDescription(inDescription);
        descriptor.setName(inName);
        descriptor.setPattern(inPattern);
        descriptor.setRequired(inIsRequired);
        return descriptor;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSessionAttributeDescriptorFactory#create(com.marketcetera.fix.FixSessionAttributeDescriptor)
     */
    @Override
    public SimpleFixSessionAttributeDescriptor create(FixSessionAttributeDescriptor inDescriptor)
    {
        if(inDescriptor instanceof SimpleFixSessionAttributeDescriptor) {
            return (SimpleFixSessionAttributeDescriptor)inDescriptor;
        }
        SimpleFixSessionAttributeDescriptor descriptor = new SimpleFixSessionAttributeDescriptor();
        descriptor.setAdvice(inDescriptor.getAdvice());
        descriptor.setDefaultValue(inDescriptor.getDefaultValue());
        descriptor.setDescription(inDescriptor.getDescription());
        descriptor.setName(inDescriptor.getName());
        descriptor.setPattern(inDescriptor.getPattern());
        descriptor.setRequired(inDescriptor.isRequired());
        return descriptor;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSessionAttributeDescriptorFactory#create()
     */
    @Override
    public FixSessionAttributeDescriptor create()
    {
        return new SimpleFixSessionAttributeDescriptor();
    }
}
