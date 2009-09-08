package org.marketcetera.photon.commons.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import org.marketcetera.photon.commons.Validate;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.l10n.MessageInfo;
import org.marketcetera.util.l10n.MessageInfoProvider;
import org.marketcetera.util.l10n.Messages;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.misc.ReflectUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/* $License$ */

/**
 * Helper class for testing Messages classes that use {@link LocalizedLabel}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class LocalizedLabelMessageInfoProvider implements MessageInfoProvider {

    private final ImmutableList<MessageInfo> mMessageInfo;

    /**
     * Constructor.
     * 
     * @param clazz
     *            the messages class
     * @throws I18NException
     *             if introspection fails
     * @throws IllegalArgumentException
     *             if clazz is null
     */
    public LocalizedLabelMessageInfoProvider(Class<?> clazz)
            throws I18NException {
        Validate.notNull(clazz, "clazz"); //$NON-NLS-1$
        List<MessageInfo> info = Lists.newLinkedList();
        try {
            for (Field field : ReflectUtils.getAllFields(clazz)) {
                String name = field.getName();
                if ((field.getModifiers() & Modifier.STATIC) == 0) {
                    Messages.NONSTATIC_FIELD_IGNORED.info(this, name);
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
                    String messageId = field.getName().toLowerCase();
                    info.add(new MessageInfo(messageId + ".label", 0) {
                    });
                    info.add(new MessageInfo(messageId + ".tooltip", 0) {
                    });
                }
            }
            mMessageInfo = ImmutableList.copyOf(info);
        } catch (IllegalAccessException ex) {
            throw new I18NException(ex, new I18NBoundMessage1P(
                    Messages.INTROSPECTION_FAILED, clazz.getName()));
        }
    }

    @Override
    public List<MessageInfo> getMessageInfo() {
        return mMessageInfo;
    }

}
