package org.marketcetera.core.module;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;
import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */
/**
 * A module used for testing JMX integration when the module is a
 * dynamic MBean.
 *
 * @author anshul@marketcetera.com
 * @version $Id: DynamicBeanModule.java 82330 2012-04-10 16:29:13Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: DynamicBeanModule.java 82330 2012-04-10 16:29:13Z colin $")
class DynamicBeanModule extends Module implements DynamicMBean {
    /**
     * Creates an instance.
     *
     * @param inURN the module's URN.
     */
    DynamicBeanModule(ModuleURN inURN) {
        super(inURN, false);
    }

    @Override
    protected void preStart() throws ModuleException {
        //do nothing
    }

    @Override
    protected void preStop() throws ModuleException {
        //do nothing
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

    static final String ATTRIB_NAME = "MyAttrib";
    private final DynamicBeanDelegate mDelegate =
            new DynamicBeanDelegate(ATTRIB_NAME, getURN().instanceName(),
                    DynamicBeanModule.class.getName());
}
