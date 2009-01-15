package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage1P;

import javax.management.ObjectName;
import javax.management.MalformedObjectNameException;
import java.io.Serializable;
import java.util.Arrays;
import java.beans.ConstructorProperties;

/* $License$ */
/**
 * Represents a URN that can be used within the module
 * framework system. Instances of this class maintain a normalized
 * version of a URN. This ensures that equivalent representations
 * of the URN evaluate as equal.
 * <p>
 * The URNs have the following format
 * <br/>
 * <code>metc:provType:provider:instance</code>
 *<br/>
 * <b>where</b>:
 * <ul>
 *  <li><b>metc</b>: The URN prefix <code>metc</code> indicating a marketcetera URN</li>
 *  <li><b>provType</b>: Identifies a data provider type, for example, <code>mdata</code>
 *      for market data providers, <code>cep</code> for complex event processors,
 *      <code>news</code> for news providers, <code>system</code> for system data
 *      providers for data like trade suggestions, execution reports, etc.
 *      This field can be omitted if the provider type is unknown or if the provider
 *      type can be safely omitted for the particular usage of the URN</li>
 * <li><b>provider</b>: the name of the provider of data. For example, for
 *      market data, this can be <code>opentick</code> or <code>activ</code>.
 *      In case, the name of the provider is omitted, the request may be routed
 *      to any provider for the specified <i>provType</i>, if one is specified,
 *      in the URN and if a provider is available for that provider type.
 *      If more than one provider is available, system may arbitrarily choose
 *      any of the available providers. </li>
 *  <li><b>instance</b>: identifies the particular instance of the module. This
 *      field can be omitted for singleton module instances. For modules that
 *      have multiple instances, like strategy, this may refer to the module name
 *      which, for example, may be the name of the strategy.</li>
 * </ul>
 *
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public final class ModuleURN implements Serializable {
    /**
     * Creates a new instance given the string representation.
     * Each component of the URN is trimmed. Any trailing empty
     * elements of the URN are pruned.
     *
     * @param inString the string representation of the URN,
     * cannot be null or an empty string.
     *
     * @throws IllegalArgumentException if the URN string is empty
     * or the URN has no non-empty fields.
     */
    @ConstructorProperties({"value"})  //$NON-NLS-1$
    public ModuleURN(String inString) {
        String[] urnPieces = inString.split(URN_SEPARATOR_CHAR, MAX_ELEMENTS);
        if(urnPieces.length == MAX_ELEMENTS) {
            //The last element can have separators if the supplied URN
            //has extra fields, prune them.
            int idx = urnPieces[MAX_ELEMENTS - 1].indexOf(URN_SEPARATOR_CHAR);
            if(idx >= 0) {
                urnPieces[MAX_ELEMENTS - 1] =
                        urnPieces[MAX_ELEMENTS - 1].substring(0,idx);
            }
        }
        int lastEmpty = urnPieces.length;
        boolean lookForEmpty = true;
        //Remove spaces and prune empty trailing fields
        for(int i = urnPieces.length - 1; i >= 0; i--) {
            urnPieces[i] = urnPieces[i].trim();
            if(urnPieces[i].isEmpty()) {
                urnPieces[i] = null;
                if(lookForEmpty) {
                    lastEmpty = i;
                }
            } else {
                lookForEmpty = false;
            }
        }
        if(lastEmpty == 0) {
            throw new IllegalArgumentException(
                    Messages.EMPTY_URN.getText(inString));
        }
        if(lastEmpty < urnPieces.length) {
            urnPieces = Arrays.copyOfRange(urnPieces,0,lastEmpty);
        }
        mURNPieces = urnPieces;
    }

    /**
     * Creates a child URN of the supplied parent URN, appending
     * the <code>inInstance</code> value to it.
     *
     * If the supplied URN is a provider type URN, the returned
     * URN will be a provider URN.
     *
     * If the supplied URN is a provider URN, the returned URN
     * will be an instance URN.
     *
     * If the supplied URN is an instance URN, the returned
     * URN will be a copy of the supplied instance URN. The
     * <code>inInstance</code> value will be ignored.
     *
     * if <code>inInstance</code> value is null, only has
     * whitespaces, is empty or has ':' as its first non-whitespace
     * character, it will be ignored and the returned URN will
     * be a copy of the supplied <code>inParent</code> URN. 
     *
     * if <code>inInstance</code> has any ':' characters, the first
     * instance of ':' character and any characters following it
     * are pruned before appending <code>inInstance</code> to
     * the supplied <code>inParent</code>.
     *
     * @param inParent the parent URN
     * @param inInstance the text element to add to the parent URN 
     */
    public ModuleURN(ModuleURN inParent, String inInstance) {
        if(inParent.mURNPieces.length == MAX_ELEMENTS ||
                inInstance == null ||
                inInstance.trim().isEmpty()) {
            //make it a copy of the supplied parent URN.
            mURNPieces = Arrays.copyOf(inParent.mURNPieces,
                    inParent.mURNPieces.length);
            return;
        }
        //if inInstance has any separators remove them.
        int idx = inInstance.indexOf(URN_SEPARATOR_CHAR);
        if(idx >= 0) {
            inInstance = inInstance.substring(0, idx);
        }
        inInstance = inInstance.trim();
        if(inInstance.isEmpty()) {
            //make it a copy of the supplied parent URN.
            mURNPieces = Arrays.copyOf(inParent.mURNPieces,
                    inParent.mURNPieces.length);
            return;
        }
        mURNPieces = Arrays.copyOf(inParent.mURNPieces,
                inParent.mURNPieces.length + 1);
        mURNPieces[mURNPieces.length - 1] = inInstance;
    }

    /**
     * Returns the URN scheme.
     *
     * @return the URN scheme.
     */
    public String scheme() {
        return mURNPieces.length > 0? mURNPieces[0]: null;
    }

    /**
     * Returns the provider type.
     *
     * @return the provider type.
     */
    public String providerType() {
        return mURNPieces.length > 1? mURNPieces[1]: null;
    }

    /**
     * Returns the provider name.
     *
     * @return the provider name.
     */
    public String providerName() {
        return mURNPieces.length > 2? mURNPieces[2]: null;
    }

    /**
     * Returns the instance name.
     *
     * @return the instance name.
     */
    public String instanceName() {
        return mURNPieces.length > 3? mURNPieces[3]: null;
    }

    /**
     * Returns true if this is a instance URN.
     *
     * @return if this is a instance URN.
     */
    public boolean instanceURN() {
        return instanceName() != null;
    }

    /**
     * Returns true if this URN is a parent of the supplied URN.
     *
     * @param inChild if the URN that needs to be compared with
     * this URN.
     *
     * @return if this URN is a parent of the supplied URN.
     */
    public boolean parentOf(ModuleURN inChild) {
        if(mURNPieces.length == (inChild.mURNPieces.length - 1)) {
            for(int i = 0; i < mURNPieces.length; i++) {
                if(mURNPieces[i] == null) {
                    if(inChild.mURNPieces[i] != null) {
                        return false;
                    }
                } else {
                    if(!mURNPieces[i].equals(inChild.mURNPieces[i])) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Returns the parent URN of this URN, if this is a provider
     * or an instance URN, null otherwise.
     *
     * @return the parent URN if this is a provider or an instance URN.
     */
    public ModuleURN parent() {
        if(mURNPieces.length > 2) {
            return new ModuleURN(Arrays.copyOfRange(mURNPieces,
                    0, mURNPieces.length - 1));
        }
        return null;
    }

    /**
     * The value of this URN as string.
     *
     * @return the string value of this URN.
     */
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < mURNPieces.length; i++) {
            if(mURNPieces[i] != null) {
                sb.append(mURNPieces[i]);
            }
            if(i < (mURNPieces.length - 1)) {
                sb.append(URN_SEPARATOR_CHAR);
            }
        }
        return sb.toString();
    }

    /**
     * Converts this URN to the object name that can be used to lookup
     * instances of the MXBean for the object indentified by this URN
     * in the MBean server.
     *
     * @return the object name for this URN.
     *
     * @throws MXBeanOperationException if there were errors creating
     * the object name.
     */
    public ObjectName toObjectName() throws MXBeanOperationException {
        StringBuilder sb = new StringBuilder(
                ModuleManager.MBEAN_DOMAIN_NAME).append(":");  //$NON-NLS-1$
        // Each URN field is the value of the corresponding key
        // from MBEAN_NAME_KEYS
        boolean addComma = false;
        for(int i = 1; i < mURNPieces.length;i++) {
            if(mURNPieces[i] != null) {
                if(addComma) {
                    sb.append(",");  //$NON-NLS-1$
                }
                sb.append(MBEAN_NAME_KEYS[i]).append('=').append(mURNPieces[i]);  //$NON-NLS-1$
                addComma = true;
            }
        }
        try {
            return new ObjectName(sb.toString());
        } catch (MalformedObjectNameException e) {
            throw new MXBeanOperationException(e,
                    new I18NBoundMessage1P(
                            Messages.BEAN_OBJECT_NAME_ERROR,toString()));
        }
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModuleURN moduleURN = (ModuleURN) o;

        return Arrays.equals(mURNPieces, moduleURN.mURNPieces);

    }

    @Override
    public int hashCode() {
        return (mURNPieces != null ? Arrays.hashCode(mURNPieces) : 0);
    }
    /**
     * The scheme for all system URNs
     */
    public static final String SCHEME = "metc";  //$NON-NLS-1$

    /**
     * Creates an instance.
     *
     * @param providerType the provider type
     * @param providerName the provider name
     * @param instanceName the instance name
     */
    ModuleURN (String providerType, String providerName, String instanceName) {
        String[] urnPieces = null;
        if(instanceName != null) {
            urnPieces = new String[MAX_ELEMENTS];
            urnPieces[MAX_ELEMENTS - 1] = instanceName;
        }
        if(providerName != null) {
            if(urnPieces == null) {
                urnPieces = new String[MAX_ELEMENTS - 1];
            }
            urnPieces[MAX_ELEMENTS - 2] = providerName;
        }
        if(providerType != null) {
            if(urnPieces == null) {
                urnPieces = new String[MAX_ELEMENTS - 2];
            }
            urnPieces[MAX_ELEMENTS - 3] = providerType;
        }
        if(urnPieces == null) {
            urnPieces = new String[MAX_ELEMENTS - 3];
        }
        urnPieces[0] = SCHEME;
        mURNPieces = urnPieces;
    }

    /**
     * Creates an instance.
     *
     * @param inPieces The individual pieces of the URN.
     */
    private ModuleURN(String[] inPieces) {
        mURNPieces = inPieces;
    }

    private final String[] mURNPieces;
    static final String URN_SEPARATOR_CHAR = ":";  //$NON-NLS-1$
    /**
     * MBean object name keys corresponding to fields in the
     * provider / instance URN.
     */
    private static final String [] MBEAN_NAME_KEYS = {
            "", //This value is not used, its a placeholder for scheme //$NON-NLS-1$ 
            "type",  //$NON-NLS-1$
            "provider",  //$NON-NLS-1$
            "name"};  //$NON-NLS-1$
    private static final long serialVersionUID = -4563867950366932693L;
    private static final int MAX_ELEMENTS = 4;
}
