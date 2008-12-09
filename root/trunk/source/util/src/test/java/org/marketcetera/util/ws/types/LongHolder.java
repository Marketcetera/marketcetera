package org.marketcetera.util.ws.types;

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

public class LongHolder
    extends GenericHolder<Long>
{
    private long mItemP;
    private long[] mArrayP;
    private Long[] mArray;


    private LongHolder() {}

    public LongHolder
        (long itemP,
         Long item,
         long[] arrayP,
         Long[] array,
         Collection<Long> collection,
         List<Long> list,
         LinkedList<Long> linkedList,
         Set<Long> set,
         HashSet<Long> hashSet,
         TreeSet<Long> treeSet,
         Map<Long,Long> map,
         HashMap<Long,Long> hashMap,
         TreeMap<Long,Long> treeMap)
    {
        super(item,collection,list,linkedList,
              set,hashSet,treeSet,map,hashMap,treeMap);
        setItemP(itemP);        
        setArrayP(arrayP);
        setArray(array);
    }


    public void setItemP
        (long itemP)
    {
        mItemP=itemP;
    }

    public long getItemP()
    {
        return mItemP;
    }

    public void setArrayP
        (long[] arrayP)
    {
        mArrayP=arrayP;
    }

    public long[] getArrayP()
    {
        return mArrayP;
    }

    public void setArray
        (Long[] array)
    {
        mArray=array;
    }

    public Long[] getArray()
    {
        return mArray;
    }


    @Override
    public int hashCode()
    {
        return (super.hashCode()+
                ArrayUtils.hashCode(getItemP())+
                ArrayUtils.hashCode(getArrayP())+
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
        LongHolder o=(LongHolder)other;
        return (super.equals(o) &&
                ArrayUtils.isEquals(getItemP(),o.getItemP()) &&
                ArrayUtils.isEquals(getArrayP(),o.getArrayP()) &&
                ArrayUtils.isEquals(getArray(),o.getArray()));
    }
}
