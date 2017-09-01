package org.marketcetera.rpc.base;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Maps;

/* $License$ */

/**
 * Provides common behaviors for {@link BaseRpc} services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class BaseUtil
{
    /**
     * Get the value represented by the given qty object.
     *
     * @param inQty a <code>BaseRpc.Qty</code> value
     * @return a <code>BigDecimal</code> value
     */
    public static BigDecimal getScaledQuantity(BaseRpc.Qty inQty)
    {
        BigDecimal base = new BigDecimal(inQty.getQty());
        int scale = inQty.getScale();
        base = base.setScale(scale,
                             RoundingMode.HALF_UP);
        base = base.movePointLeft(scale);
        return base;
    }
    /**
     * Get a qty value from the given input.
     *
     * @param inValue a <code>BigDecimal</code>value
     * @return a <code>BaseRpc.Qty</code> value
     */
    public static BaseRpc.Qty getQtyValueFrom(BigDecimal inValue)
    {
        BigDecimal quantity = inValue.setScale(6,
                                               RoundingMode.HALF_UP);
        quantity = quantity.movePointRight(6);
        BaseRpc.Qty.Builder qtyBuilder = BaseRpc.Qty.newBuilder();
        qtyBuilder.setQty(quantity.longValue());
        qtyBuilder.setScale(6);
        return qtyBuilder.build();
    }
    /**
     * Get an RPC status message with the given values.
     *
     * @param inFailed a <code>boolean</code> value
     * @param inErrorMessage a <code>String</code> value
     * @return a <code>BaseRpc.Status</code> value
     */
    public static BaseRpc.Status getStatus(boolean inFailed,
                                           String inErrorMessage)
    {
        BaseRpc.Status.Builder inStatusBuilder = BaseRpc.Status.newBuilder();
        inStatusBuilder.setFailed(inFailed);
        String value = StringUtils.trimToNull(inErrorMessage);
        if(value != null) {
            inStatusBuilder.setErrorMessage(value);
        }
        return inStatusBuilder.build();
    }
    /**
     * Get an RPC map from the given map.
     *
     * @param inMap a <code>Map&lt;String,String&gt;</code> value
     * @return a <code>BaseRpc.Map</code> value
     */
    public static BaseRpc.Map getRpcMap(Map<String,String> inMap)
    {
        BaseRpc.Map.Builder mapBuilder = BaseRpc.Map.newBuilder();
        if(inMap != null) {
            BaseRpc.KeyValuePair.Builder keyValuePairBuilder = BaseRpc.KeyValuePair.newBuilder();
            for(Map.Entry<String,String> entry : inMap.entrySet()) {
                keyValuePairBuilder.setKey(entry.getKey());
                keyValuePairBuilder.setValue(entry.getValue());
                mapBuilder.addKeyValuePairs(keyValuePairBuilder.build());
                keyValuePairBuilder.clear();
            }
        }
        return mapBuilder.build();
    }
    /**
     * Get a map value from the given RPC map.
     *
     * @param inMap a <code>BaseRpc.Map</code> value
     * @return a <code>Map&lt;String,String&gt;</code> value
     */
    public static Map<String,String> getMap(BaseRpc.Map inMap)
    {
        Map<String,String> map = Maps.newHashMap();
        for(BaseRpc.KeyValuePair rpcKeyValuePair : inMap.getKeyValuePairsList()) {
            map.put(rpcKeyValuePair.getKey(),
                    rpcKeyValuePair.getValue());
        }
        return map;
    }
}
