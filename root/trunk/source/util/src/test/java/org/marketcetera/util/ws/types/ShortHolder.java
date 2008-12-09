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

public class ShortHolder
    extends GenericHolder<Short>
{
    private short mItemP;
    private short[] mArrayP;
    private Short[] mArray;


    private ShortHolder() {}

    public ShortHolder
        (short itemP,
         Short item,
         short[] arrayP,
         Short[] array,
         Collection<Short> collection,
         List<Short> list,
         LinkedList<Short> linkedList,
         Set<Short> set,
         HashSet<Short> hashSet,
         TreeSet<Short> treeSet,
         Map<Short,Short> map,
         HashMap<Short,Short> hashMap,
         TreeMap<Short,Short> treeMap)
    {
        super(item,collection,list,linkedList,
              set,hashSet,treeSet,map,hashMap,treeMap);
        setItemP(itemP);        
        setArrayP(arrayP);
        setArray(array);
    }


    public void setItemP
        (short itemP)
    {
        mItemP=itemP;
    }

    public short getItemP()
    {
        return mItemP;
    }

    public void setArrayP
        (short[] arrayP)
    {
        mArrayP=arrayP;
    }

    public short[] getArrayP()
    {
        return mArrayP;
    }

    public void setArray
        (Short[] array)
    {
        mArray=array;
    }

    public Short[] getArray()
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
        ShortHolder o=(ShortHolder)other;
        return (super.equals(o) &&
                ArrayUtils.isEquals(getItemP(),o.getItemP()) &&
                ArrayUtils.isEquals(getArrayP(),o.getArrayP()) &&
                ArrayUtils.isEquals(getArray(),o.getArray()));
    }
}
