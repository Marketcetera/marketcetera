package org.marketcetera.util.l10n;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import org.apache.commons.lang.SystemUtils;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A comparator of two message meta-information holders.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class MessageComparator
{

    // INSTANCE DATA.

    private MessageInfoPair[] mMismatches;
    private MessageInfo[] mExtraSrcInfo;
    private MessageInfo[] mExtraDstInfo;


    // CONSTRUCTORS.

    /**
     * Creates a new comparator for the given meta-information. For
     * both parameters, the order of array elements is unimportant.
     *
     * @param srcInfo The source meta-information.
     * @param dstInfo The destination meta-information.
     */

    public MessageComparator
        (MessageInfo[] srcInfo,
         MessageInfo[] dstInfo)
    {

        // Analyze source and destination.

        HashMap<String,MessageInfo> srcMessages=toHashMap(srcInfo);
        HashMap<String,MessageInfo> dstMessages=toHashMap(dstInfo);

        // Compare.

        LinkedList<MessageInfoPair> mismatches=
            new LinkedList<MessageInfoPair>();
        LinkedList<MessageInfo> extraSrcInfo=new LinkedList<MessageInfo>();
        for (String name:srcMessages.keySet()) {
            MessageInfo srcMessage=srcMessages.get(name);

            // Message missing from destination.

            if (!dstMessages.containsKey(name)) {
                extraSrcInfo.add(srcMessage);
                continue;
            }

            // Message exists in both source and destination, but
            // parameter count differs.

            MessageInfo dstMessage=dstMessages.get(name);
            if ((srcMessage.getParamCount()!=-1) &&
                (dstMessage.getParamCount()!=-1) &&
                (srcMessage.getParamCount()!=dstMessage.getParamCount())) {
                mismatches.add(new MessageInfoPair(srcMessage,dstMessage));
            }
            dstMessages.remove(name);
        }

        // Retain results.

        mMismatches=mismatches.toArray(MessageInfoPair.EMPTY_ARRAY);
        mExtraSrcInfo=extraSrcInfo.toArray(MessageInfo.EMPTY_ARRAY);
        mExtraDstInfo=dstMessages.values().toArray(MessageInfo.EMPTY_ARRAY);
    }

    /**
     * Creates a new comparator for the given meta-information
     * providers.
     *
     * @param srcProvider The source meta-information provider.
     * @param dstProvider The destination meta-information provider.
     */

    public MessageComparator
        (MessageInfoProvider srcProvider,
         MessageInfoProvider dstProvider)
    {
        this(srcProvider.getMessageInfo(),dstProvider.getMessageInfo());
    }

    /**
     * Creates a new comparator for the given container class
     * meta-information and the properties file deduced from the
     * container's message provider and the given locale.
     *
     * @param classInfo The meta-information of a container class.
     * @param locale The locale. Use {@link Locale#ROOT} for the
     * fallback properties file.
     *
     * @throws I18NException Thrown if there is a problem obtaining
     * the meta-information of the properties file.
     */

    private MessageComparator
        (ContainerClassInfo classInfo,
         Locale locale)
        throws I18NException
    {
        this(classInfo,
             new PropertiesFileInfo(classInfo.getProvider(),locale));
    }

    /**
     * Creates a new comparator for the given container class and the
     * properties file deduced from the class's message provider and
     * the given locale.
     *
     * @param container The class.
     * @param locale The locale. Use {@link Locale#ROOT} for the
     * fallback properties file.
     *
     * @throws I18NException Thrown if there is a problem obtaining
     * the meta-information of either the container or the properties
     * file.
     */

    public MessageComparator
        (Class<?> container,
         Locale locale)
        throws I18NException
    {
        this(new ContainerClassInfo(container),locale);
    }

    /**
     * Creates a new comparator for the given container class and the
     * fallback properties file deduced from the class's message
     * provider.
     *
     * @param container The class.
     *
     * @throws I18NException Thrown if there is a problem obtaining
     * the meta-information of either the container or the properties
     * file.
     */

    public MessageComparator
        (Class<?> container)
        throws I18NException
    {
        this(container,Locale.ROOT);
    }


    // INSTANCE METHODS.

    /**
     * Converts the given meta-information array into a map, with the
     * map keys being the message keys.
     *
     * @param infoArray The meta-information in array form.
     *
     * @return The map.
     */

    private HashMap<String,MessageInfo> toHashMap
        (MessageInfo[] infoArray)
    {
        HashMap<String,MessageInfo> result=new HashMap<String,MessageInfo>();
        for (MessageInfo info:infoArray) {
            result.put(info.getKey(),info);
        }
        return result;
    }

    /**
     * Returns the receiver's mismatches. A mismatch occurs when two
     * message keys are present in both source and destination, and
     * both have known but different parameter counts.
     *
     * @return The mismatches.
     */

    public MessageInfoPair[] getMismatches()
    {
        return mMismatches;
    }

    /**
     * Returns the receiver's list of source meta-information that is
     * absent from the destination.
     *
     * @return The list.
     */

    public MessageInfo[] getExtraSrcInfo()
    {
        return mExtraSrcInfo;
    }

    /**
     * Returns the receiver's list of destination meta-information
     * that is absent from the source.
     *
     * @return The list.
     */

    public MessageInfo[] getExtraDstInfo()
    {
        return mExtraDstInfo;
    }

    /**
     * Checks whether the receiver found no differences between source
     * and destination.
     *
     * @return True if so.
     */

    public boolean isMatch()
    {
        return ((getMismatches().length==0) &&
                (getExtraSrcInfo().length==0) &&
                (getExtraDstInfo().length==0));
    }

    /**
     * Returns a textual form of the differences between source and
     * destination.
     *
     * @return The differences. This is the empty string if there are
     * no differences.
     */

    public String getDifferences()
    {
        StringBuilder builder=new StringBuilder();
        for (MessageInfoPair mismatch:getMismatches()) {
            if (builder.length()>0) {
                builder.append(SystemUtils.LINE_SEPARATOR);
            }
            builder.append
                (Messages.PARAM_COUNT_MISMATCH.getText
                 (mismatch.getSrcInfo().getKey(),
                  mismatch.getSrcInfo().getParamCount(),
                  mismatch.getDstInfo().getParamCount()));
        }
        for (MessageInfo info:getExtraSrcInfo()) {
            if (builder.length()>0) {
                builder.append(SystemUtils.LINE_SEPARATOR);
            }
            builder.append(Messages.EXTRA_SRC_MESSAGE.getText(info.getKey()));
        }
        for (MessageInfo info:getExtraDstInfo()) {
            if (builder.length()>0) {
                builder.append(SystemUtils.LINE_SEPARATOR);
            }
            builder.append(Messages.EXTRA_DST_MESSAGE.getText(info.getKey()));
        }
        return builder.toString();
    }
}
