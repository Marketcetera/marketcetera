package org.marketcetera.core.ws.types;

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
import org.marketcetera.core.ws.wrappers.SerWrapper;

/**
 * @since 1.0.0
 * @version $Id: SerWrapperHolder.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

@SuppressWarnings("rawtypes")
public class SerWrapperHolder
    extends GenericHolder<SerWrapper>
{
    private SerWrapper[] mArray;


    @SuppressWarnings("unused")
    private SerWrapperHolder() {}

    public SerWrapperHolder
        (SerWrapper item,
         SerWrapper[] array,
         Collection<SerWrapper> collection,
         List<SerWrapper> list,
         LinkedList<SerWrapper> linkedList,
         Set<SerWrapper> set,
         HashSet<SerWrapper> hashSet,
         TreeSet<SerWrapper> treeSet,
         Map<SerWrapper,SerWrapper> map,
         HashMap<SerWrapper,SerWrapper> hashMap,
         TreeMap<SerWrapper,SerWrapper> treeMap)
    {
        super(item,collection,list,linkedList,
              set,hashSet,treeSet,map,hashMap,treeMap);
        setArray(array);
    }


    public void setArray
        (SerWrapper[] array)
    {
        mArray=array;
    }

    public SerWrapper[] getArray()
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
        SerWrapperHolder o=(SerWrapperHolder)other;
        return (super.equals(o) &&
                ArrayUtils.isEquals(getArray(),o.getArray()));
    }
}
