package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage3P;

import javax.management.*;

/* $License$ */
/**
 * A helper class that is used to set the values of mbean
 * attributes during their initialization. This setter
 * uses {@link StringToTypeConverter} to convert string values to the
 * appropriate types. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
class MBeanAttributeSetterHelper {
    /**
     * Returns true if the attributes of the type as indicated
     * by the supplied attribute info are supported.
     *
     * @param inInfo the attribute info.
     *
     * @return true if the attributes of the supplied type are supported.
     */
    static boolean isSupported(MBeanAttributeInfo inInfo) {
        return StringToTypeConverter.isSupported(inInfo.getType());
    }

    /**
     * Sets the value of the specified bean attribute to the supplied
     * string value after converting it to the same type as the bean
     * attribute.
     *
     * This method should only be invoked for attributes for which
     * {@link #isSupported(javax.management.MBeanAttributeInfo)} returns
     * true. Otherwise, this method will throw
     * <code>IllegalArgumentException</code>.
     *
     * @param inServer the mbean server
     * @param inName the name of the mbean whose attribute
     * needs to be modified.
     * @param inInfo the attribute info of the attribute whose value
     * needs to be updated.
     * @param inValue the value of the attribute as a string. The string
     * value is converted to the appropriate type for the attribute.
     *
     * @throws BeanAttributeSetException if there were errors converting
     * the supplied string value to the correct type for the bean attribute
     * or if there were MBean errors when performing this operation.
     */
    static void setValue(MBeanServer inServer, ObjectName inName,
                  MBeanAttributeInfo inInfo, String inValue)
            throws BeanAttributeSetException {
        try {
            Object value = StringToTypeConverter.convert(
                    inInfo.getType(),inValue);
            inServer.setAttribute(inName,
                    new Attribute(inInfo.getName(), value));
        } catch (JMException e) {
            throw new BeanAttributeSetException(e, new I18NBoundMessage3P(
                    Messages.UNABLE_SET_ATTRIBUTE, inInfo.getName(),
                    inValue, inName));
        } catch (IllegalArgumentException e) {
            throw new BeanAttributeSetException(e, new I18NBoundMessage3P(
                    Messages.UNABLE_SET_ATTRIBUTE, inInfo.getName(),
                    inValue, inName));
        }
    }
}
