package org.marketcetera.photon.commons.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.marketcetera.photon.commons.ReflectiveMessages;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.l10n.ContainerClassInfo;
import org.marketcetera.util.l10n.Messages;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.misc.ReflectUtils;

/* $License$ */

/**
 * Holder of meta-information about a message container class. This class
 * extends {@link ContainerClassInfo} to support {@link ReflectiveMessages}
 * classes that use {@link LocalizedLabel}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class LocalizedLabelContainerClassInfo extends ContainerClassInfo {

    /**
     * Constructor.
     * 
     * @param clazz
     *            the messages class
     * @throws I18NException
     *             if introspection fails
     */
    public LocalizedLabelContainerClassInfo(Class<?> clazz)
            throws I18NException {
        super(clazz);
        try {
            for (Field field : ReflectUtils.getAllFields(clazz)) {
                String name = field.getName();
                if ((field.getModifiers() & Modifier.STATIC) == 0) {
                    // no need to log again, superclass handled it
                    continue;
                }
                Class<?> type = field.getType();
                if (LocalizedLabel.class.isAssignableFrom(type)) {
                    field.setAccessible(true);
                    LocalizedLabel label = ((LocalizedLabel) (field.get(clazz)));
                    if (label == null) {
                        Messages.NULL_FIELD_IGNORED.info(this, name);
                        continue;
                    }
                    addMessage(label.getLabelMessage());
                    addMessage(label.getTooltipMessage());
                }
            }
        } catch (IllegalAccessException ex) {
            throw new I18NException(ex, new I18NBoundMessage1P(
                    Messages.INTROSPECTION_FAILED, clazz.getName()));
        }
    }
}
