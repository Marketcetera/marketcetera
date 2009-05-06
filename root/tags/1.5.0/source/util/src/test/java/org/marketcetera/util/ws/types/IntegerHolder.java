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

public class IntegerHolder
    extends GenericHolder<Integer>
{
    private int mItemP;
    private int[] mArrayP;
    private Integer[] mArray;


    private IntegerHolder() {}

    public IntegerHolder
        (int itemP,
         Integer item,
         int[] arrayP,
         Integer[] array,
         Collection<Integer> collection,
         List<Integer> list,
         LinkedList<Integer> linkedList,
         Set<Integer> set,
         HashSet<Integer> hashSet,
         TreeSet<Integer> treeSet,
         Map<Integer,Integer> map,
         HashMap<Integer,Integer> hashMap,
         TreeMap<Integer,Integer> treeMap)
    {
        super(item,collection,list,linkedList,
              set,hashSet,treeSet,map,hashMap,treeMap);
        setItemP(itemP);        
        setArrayP(arrayP);
        setArray(array);
    }


    public void setItemP
        (int itemP)
    {
        mItemP=itemP;
    }

    public int getItemP()
    {
        return mItemP;
    }

    public void setArrayP
        (int[] arrayP)
    {
        mArrayP=arrayP;
    }

    public int[] getArrayP()
    {
        return mArrayP;
    }

    public void setArray
        (Integer[] array)
    {
        mArray=array;
    }

    public Integer[] getArray()
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
        IntegerHolder o=(IntegerHolder)other;
        return (super.equals(o) &&
                ArrayUtils.isEquals(getItemP(),o.getItemP()) &&
                ArrayUtils.isEquals(getArrayP(),o.getArrayP()) &&
                ArrayUtils.isEquals(getArray(),o.getArray()));
    }
}
