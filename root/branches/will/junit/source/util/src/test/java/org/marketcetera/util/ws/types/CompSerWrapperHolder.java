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
import org.marketcetera.util.ws.wrappers.CompSerWrapper;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class CompSerWrapperHolder
    extends GenericHolder<CompSerWrapper>
{
    private CompSerWrapper[] mArray;


    private CompSerWrapperHolder() {}
    
    public CompSerWrapperHolder
        (CompSerWrapper item,
         CompSerWrapper[] array,
         Collection<CompSerWrapper> collection,
         List<CompSerWrapper> list,
         LinkedList<CompSerWrapper> linkedList,
         Set<CompSerWrapper> set,
         HashSet<CompSerWrapper> hashSet,
         TreeSet<CompSerWrapper> treeSet,
         Map<CompSerWrapper,CompSerWrapper> map,
         HashMap<CompSerWrapper,CompSerWrapper> hashMap,
         TreeMap<CompSerWrapper,CompSerWrapper> treeMap)
    {
        super(item,collection,list,linkedList,
              set,hashSet,treeSet,map,hashMap,treeMap);
        setArray(array);
    }


    public void setArray
        (CompSerWrapper[] array)
    {
        mArray=array;
    }

    public CompSerWrapper[] getArray()
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
        CompSerWrapperHolder o=(CompSerWrapperHolder)other;
        return (super.equals(o) &&
                ArrayUtils.isEquals(getArray(),o.getArray()));
    }
}
