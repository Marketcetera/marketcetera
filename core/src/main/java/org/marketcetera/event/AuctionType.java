package org.marketcetera.event;

import java.util.Map;

import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Maps;

/* $License$ */

/**
 * Indicates the type of imbalance auction.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public enum AuctionType
{
    INVALID(0),
    OPEN(1),
    CLOSE(2),
    HALT(3),
    MARKET(4),
    IPO(5),
    INTRADAY(6);
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
     * Gets the <code>AuctionType</code> for the given code.
     *
     * @param inCode a <code>byte</code> value
     * @return an <code>AuctionType</code> value
     * @throws IllegalArgumentException if the code does not correspond to an <code>AuctionType</code>
     */
    public static AuctionType getFor(byte inCode)
    {
        AuctionType type = typesByCode.get(inCode);
        if(type == null) {
            throw new IllegalArgumentException();
        }
        return type;
    }
    /**
     * Create a new AuctionType instance.
     *
     * @param inCode an <code>int</code> value
     */
    private AuctionType(int inCode)
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
    private static final Map<Byte,AuctionType> typesByCode = Maps.newHashMap();
    /**
     * performs static initialization
     */
    static {
        for(AuctionType type : AuctionType.values()) {
            typesByCode.put(type.getCode(),
                            type);
        }
    }
}
