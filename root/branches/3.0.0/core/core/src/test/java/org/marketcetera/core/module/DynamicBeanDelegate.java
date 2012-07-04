package org.marketcetera.core.module;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;
import org.marketcetera.core.attributes.ClassVersion;

/**
 * A dynamic mbean delegate that provides a dynamic mbean with a single string
 * attribute. This class is meant to help out with testing.
 *
 * @author anshul@marketcetera.com
 * @version $Id: DynamicBeanDelegate.java 82330 2012-04-10 16:29:13Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: DynamicBeanDelegate.java 82330 2012-04-10 16:29:13Z colin $")
class DynamicBeanDelegate implements DynamicMBean {
    /**
     * Creates an instance.
     *
     * @param inAttribName  the attribute name
     * @param inAttribValue the attribute's default value.
     * @param inClassName the bean's class name.
     */
    DynamicBeanDelegate(String inAttribName,
                        String inAttribValue,
                        String inClassName) {
        mAttribValue = inAttribValue;
        mAttribName = inAttribName;
        mClassName = inClassName;
    }

    @Override
    public Object getAttribute(String attribute)
            throws AttributeNotFoundException {
        if (mAttribName.equals(attribute)) {
            return mAttribValue;
        }
        throw new AttributeNotFoundException(attribute);
    }

    @Override
    public void setAttribute(Attribute attribute)
            throws AttributeNotFoundException {
        if (mAttribName.equals(attribute.getName())) {
            mAttribValue = (String) attribute.getValue();
            return;
        }
        throw new AttributeNotFoundException(attribute.getName());
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        AttributeList list = new AttributeList();
        for (String attrib : attributes) {
            if (mAttribName.equals(attrib)) {
                list.add(new Attribute(attrib, mAttribValue));
            }
        }
        return list;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        AttributeList list = new AttributeList();
        for (Attribute attrib : attributes.asList()) {
            if (mAttribName.equals(attrib.getName())) {
                mAttribValue = (String) attrib.getValue();
                list.add(new Attribute(mAttribName, mAttribValue));
            }
        }
        return list;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature)
            throws ReflectionException {
        throw new ReflectionException(new NoSuchMethodException(actionName));
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return new MBeanInfo(mClassName, "dynamic bean",
                new MBeanAttributeInfo[]{
                        new MBeanAttributeInfo(mAttribName,
                                String.class.getName(),
                                "What part of the name do you not get?",
                                true, true, false)
                }, null, null, null);
    }

    private volatile String mAttribValue;
    private final String mAttribName;
    public final String mClassName;
}
