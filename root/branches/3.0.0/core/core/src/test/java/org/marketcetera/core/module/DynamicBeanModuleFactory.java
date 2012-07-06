package org.marketcetera.core.module;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;
import org.marketcetera.core.attributes.ClassVersion;
import org.marketcetera.core.util.log.I18NMessage0P;

/* $License$ */
/**
 * Factory for testing JMX integration when the module factory is a
 * dynamic mbean.
 *
 * @author anshul@marketcetera.com
 * @version $Id: DynamicBeanModuleFactory.java 82330 2012-04-10 16:29:13Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: DynamicBeanModuleFactory.java 82330 2012-04-10 16:29:13Z colin $")
public class DynamicBeanModuleFactory extends ModuleFactory implements DynamicMBean {
    /**
     * Creates an instance.
     */
    public DynamicBeanModuleFactory() {
        super(PROVIDER_URN, new I18NMessage0P(Messages.LOGGER,
                "dynamicBeanFactory"), true, false, String.class);
    }

    @Override
    public Module create(Object... inParameters) throws ModuleCreationException {
        return new DynamicBeanModule(new ModuleURN(PROVIDER_URN,
                (String)inParameters[0]));
    }

    @Override
    public Object getAttribute(String attribute)
            throws AttributeNotFoundException {
        return mDelegate.getAttribute(attribute);
    }

    @Override
    public void setAttribute(Attribute attribute)
            throws AttributeNotFoundException {
        mDelegate.setAttribute(attribute);
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        return mDelegate.getAttributes(attributes);
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        return mDelegate.setAttributes(attributes);
    }

    @Override
    public Object invoke(String actionName, Object[] params,
                         String[] signature) throws ReflectionException {
        return mDelegate.invoke(actionName, params, signature);
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return mDelegate.getMBeanInfo();
    }
    static final ModuleURN PROVIDER_URN = new ModuleURN("metc:test:dynamicbean");
    static final String ATTRIB_NAME = "whyAttrib";
    private final DynamicBeanDelegate mDelegate =
            new DynamicBeanDelegate(ATTRIB_NAME, PROVIDER_URN.providerName(),
                    DynamicBeanModuleFactory.class.getName());
}
