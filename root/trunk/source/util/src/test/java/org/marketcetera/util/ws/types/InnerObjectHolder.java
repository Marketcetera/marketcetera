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

public class InnerObjectHolder
    extends GenericHolder<InnerObject>
{
    private InnerObject[] mArray;


    private InnerObjectHolder() {}

    public InnerObjectHolder
        (InnerObject item,
         InnerObject[] array,
         Collection<InnerObject> collection,
         List<InnerObject> list,
         LinkedList<InnerObject> linkedList,
         Set<InnerObject> set,
         HashSet<InnerObject> hashSet,
         TreeSet<InnerObject> treeSet,
         Map<InnerObject,InnerObject> map,
         HashMap<InnerObject,InnerObject> hashMap,
         TreeMap<InnerObject,InnerObject> treeMap)
    {
        super(item,collection,list,linkedList,
              set,hashSet,treeSet,map,hashMap,treeMap);
        setArray(array);
    }


    public void setArray
        (InnerObject[] array)
    {
        mArray=array;
    }

    public InnerObject[] getArray()
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
        InnerObjectHolder o=(InnerObjectHolder)other;
        return (super.equals(o) &&
                ArrayUtils.isEquals(getArray(),o.getArray()));
    }
}
