package org.marketcetera.fix.dao;

import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.fix.FixSessionAttributeDescriptorFactory;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Creates {@link FixSessionAttributeDescriptor} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class PersistentFixSessionAttributeDescriptorFactory
        implements FixSessionAttributeDescriptorFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSessionAttributeDescriptorFactory#create(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public PersistentFixSessionAttributeDescriptor create(String inName,
                                                          String inDescription,
                                                          String inDefaultValue,
                                                          String inPattern,
                                                          boolean inIsRequired)
    {
        PersistentFixSessionAttributeDescriptor descriptor = new PersistentFixSessionAttributeDescriptor();
        descriptor.setName(inName);
        descriptor.setDescription(inDescription);
        descriptor.setDefaultValue(inDefaultValue);
        descriptor.setPattern(inPattern);
        descriptor.setIsRequired(inIsRequired);
        return descriptor;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSessionAttributeDescriptorFactory#create(com.marketcetera.ors.brokers.FixSessionAttributeDescriptor)
     */
    @Override
    public PersistentFixSessionAttributeDescriptor create(FixSessionAttributeDescriptor inFixSessionAttributeDescriptor)
    {
        PersistentFixSessionAttributeDescriptor descriptor = new PersistentFixSessionAttributeDescriptor();
        descriptor.setName(inFixSessionAttributeDescriptor.getName());
        descriptor.setDescription(inFixSessionAttributeDescriptor.getDescription());
        descriptor.setDefaultValue(inFixSessionAttributeDescriptor.getDefaultValue());
        descriptor.setPattern(inFixSessionAttributeDescriptor.getPattern());
        descriptor.setIsRequired(inFixSessionAttributeDescriptor.isRequired());
        return descriptor;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSessionAttributeDescriptorFactory#create()
     */
    @Override
    public FixSessionAttributeDescriptor create()
    {
        return new PersistentFixSessionAttributeDescriptor();
    }
}
