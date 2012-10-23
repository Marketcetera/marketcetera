package org.marketcetera.api.dao;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "permissionAttribute")
public enum PermissionAttribute {
    // note: do not change the order of these attributes as it will invalidate the calculated bit flag values (it is ok to add to the list)
    Create,
    Read,
    Update,
    Delete;
    /**
     * Returns a bit flag composite value representing the given attributes. 
     *
     * @param inAttributes a <code>Set&lt;PermissionAttribute&gt;</code> value
     * @return an <code>int</code> value
     */
    public static int getBitFlagValueFor(Set<PermissionAttribute> inAttributes)
    {
        int flagValue = 0;
        if(inAttributes != null) {
            for(PermissionAttribute attribute : inAttributes) {
                flagValue += calculateFlagValue(attribute);
            }
        }
        return flagValue;
    }
    /**
     * Gets the attributes that correspond to the given composite bit flag value.
     *
     * @param inBitFlagValue an <code>int</code> value
     * @return a <code>Set&lt;PermissionAttribute&gt;</code> value
     */
    public static Set<PermissionAttribute> getAttributesFor(int inBitFlagValue)
    {
        Set<PermissionAttribute> attributes = new HashSet<PermissionAttribute>();
        for(int index=PermissionAttribute.values().length-1;index>=0;index--) {
            PermissionAttribute candidate = PermissionAttribute.values()[index];
            int flagValue = calculateFlagValue(candidate);
            if(flagValue <= inBitFlagValue) {
                attributes.add(candidate);
                inBitFlagValue -= flagValue;
            } else if(inBitFlagValue == 0) {
                break;
            }
        }
        assert(inBitFlagValue == 0);
        return attributes;
    }
    /**
     * Calculates the bit flag value associated with the given attribute.
     *
     * @param inAttribute a <code>PermissionAttribute</code> value
     * @return an <code>int</code> value
     */
    private static int calculateFlagValue(PermissionAttribute inAttribute)
    {
        return (int)Math.pow(2,
                             inAttribute.ordinal());
    }
}