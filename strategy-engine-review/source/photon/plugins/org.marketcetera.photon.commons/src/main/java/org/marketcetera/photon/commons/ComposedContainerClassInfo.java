package org.marketcetera.photon.commons;

import java.util.List;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.l10n.ContainerClassInfo;
import org.marketcetera.util.l10n.MessageInfo;
import org.marketcetera.util.l10n.MessageInfoProvider;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/* $License$ */

/**
 * Helper class that allows additional {@link MessageInfoProvider} instances to
 * be composed with {@link ContainerClassInfo} for testing messages classes.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class ComposedContainerClassInfo extends ContainerClassInfo {

    private final ImmutableList<MessageInfo> mMessageInfo;

    /**
     * Constructor.
     * 
     * @param clazz
     *            the messages class
     * @throws I18NException
     *             if introspection of the given class fails, or the class does
     *             not contain a message provider
     * @throws IllegalArgumentException
     *             if additionalProviders is null, empty, or has null elements
     */
    public ComposedContainerClassInfo(Class<?> clazz,
            MessageInfoProvider... additionalProviders) throws I18NException {
        super(clazz);
        Validate.nonNullElements(additionalProviders, "additionalProviders"); //$NON-NLS-1$
        List<MessageInfo> combined = Lists.newLinkedList();
        combined.addAll(super.getMessageInfo());
        for (MessageInfoProvider provider : additionalProviders) {
            combined.addAll(provider.getMessageInfo());
        }
        mMessageInfo = ImmutableList.copyOf(combined);
    }

    @Override
    public List<MessageInfo> getMessageInfo() {
        return mMessageInfo;
    }

}
