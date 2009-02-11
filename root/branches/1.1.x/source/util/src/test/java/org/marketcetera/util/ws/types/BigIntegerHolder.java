package org.marketcetera.util.ws.types;

import java.math.BigInteger;
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

public class BigIntegerHolder
    extends GenericHolder<BigInteger>
{
    private BigInteger[] mArray;


    private BigIntegerHolder() {}

    public BigIntegerHolder
        (BigInteger item,
         BigInteger[] array,
         Collection<BigInteger> collection,
         List<BigInteger> list,
         LinkedList<BigInteger> linkedList,
         Set<BigInteger> set,
         HashSet<BigInteger> hashSet,
         TreeSet<BigInteger> treeSet,
         Map<BigInteger,BigInteger> map,
         HashMap<BigInteger,BigInteger> hashMap,
         TreeMap<BigInteger,BigInteger> treeMap)
    {
        super(item,collection,list,linkedList,
              set,hashSet,treeSet,map,hashMap,treeMap);
        setArray(array);
    }


    public void setArray
        (BigInteger[] array)
    {
        mArray=array;
    }

    public BigInteger[] getArray()
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
        BigIntegerHolder o=(BigIntegerHolder)other;
        return (super.equals(o) &&
                ArrayUtils.isEquals(getArray(),o.getArray()));
    }
}
