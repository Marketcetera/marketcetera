package org.marketcetera.util.ws.types;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.lang.ArrayUtils;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class BigDecimalHolder
    extends GenericHolder<BigDecimal>
{
    private BigDecimal[] mArray;


    private BigDecimalHolder() {}

    public BigDecimalHolder
        (BigDecimal item,
         BigDecimal[] array,
         Collection<BigDecimal> collection,
         List<BigDecimal> list,
         LinkedList<BigDecimal> linkedList,
         Set<BigDecimal> set,
         HashSet<BigDecimal> hashSet,
         TreeSet<BigDecimal> treeSet,
         Map<BigDecimal,BigDecimal> map,
         HashMap<BigDecimal,BigDecimal> hashMap,
         TreeMap<BigDecimal,BigDecimal> treeMap)
    {
        super(item,collection,list,linkedList,
              set,hashSet,treeSet,map,hashMap,treeMap);
        setArray(array);
    }


    public void setArray
        (BigDecimal[] array)
    {
        mArray=array;
    }

    public BigDecimal[] getArray()
    {
        return mArray;
    }


    @Override
    public int hashCode()
    {
        return (super.hashCode()+
                ArrayUtils.hashCode(getArray()));
    }

    @Override
    public boolean equals
        (Object other)
    {
        if (this==other) {
            return true;
        }
        if ((other==null) || !getClass().equals(other.getClass())) {
            return false;
        }
        BigDecimalHolder o=(BigDecimalHolder)other;
        return (super.equals(o) &&
                ArrayUtils.isEquals(getArray(),o.getArray()));
    }
}
