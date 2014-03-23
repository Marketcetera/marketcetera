package org.marketcetera.event;

import java.util.Map;

import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Maps;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public enum ImbalanceType
{
    INVALID(0),
    NONE(1),
    BUY(2),
    SELL(3);
    /**
     * Get the code value.
     *
     * @return a <code>byte</code> value
     */
    public byte getCode()
    {
        return code;
    }
    /**
     * Gets the <code>ImbalanceType</code> for the given code.
     *
     * @param inCode a <code>byte</code> value
     * @return an <code>ImbalanceType</code> value
     * @throws IllegalArgumentException if the code does not correspond to an <code>ImbalanceType</code>
     */
    public static ImbalanceType getFor(byte inCode)
    {
        ImbalanceType type = typesByCode.get(inCode);
        if(type == null) {
            throw new IllegalArgumentException();
        }
        return type;
    }
    /**
     * Create a new ImbalanceType instance.
     *
     * @param inCode an <code>int</code> value
     */
    private ImbalanceType(int inCode)
    {
        code = (byte)inCode;
    }
    /**
     * code value
     */
    private final byte code;
    /**
     * all types by their code value
     */
    private static final Map<Byte,ImbalanceType> typesByCode = Maps.newHashMap();
    /**
     * performs static initialization
     */
    static {
        for(ImbalanceType type : ImbalanceType.values()) {
            typesByCode.put(type.getCode(),
                            type);
        }
    }
}
