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

public class EnumObjectHolder
    extends GenericHolder<EnumObject>
{
    private EnumObject[] mArray;


    private EnumObjectHolder() {}

    public EnumObjectHolder
        (EnumObject item,
         EnumObject[] array,
         Collection<EnumObject> collection,
         List<EnumObject> list,
         LinkedList<EnumObject> linkedList,
         Set<EnumObject> set,
         HashSet<EnumObject> hashSet,
         TreeSet<EnumObject> treeSet,
         Map<EnumObject,EnumObject> map,
         HashMap<EnumObject,EnumObject> hashMap,
         TreeMap<EnumObject,EnumObject> treeMap)
    {
        super(item,collection,list,linkedList,
              set,hashSet,treeSet,map,hashMap,treeMap);
        setArray(array);
    }


    public void setArray
        (EnumObject[] array)
    {
        mArray=array;
    }

    public EnumObject[] getArray()
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
        EnumObjectHolder o=(EnumObjectHolder)other;
        return (super.equals(o) &&
                ArrayUtils.isEquals(getArray(),o.getArray()));
    }
}
