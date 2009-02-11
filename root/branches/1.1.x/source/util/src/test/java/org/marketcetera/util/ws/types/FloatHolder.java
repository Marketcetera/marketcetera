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

public class FloatHolder
    extends GenericHolder<Float>
{
    private float mItemP;
    private float[] mArrayP;
    private Float[] mArray;


    private FloatHolder() {}

    public FloatHolder
        (float itemP,
         Float item,
         float[] arrayP,
         Float[] array,
         Collection<Float> collection,
         List<Float> list,
         LinkedList<Float> linkedList,
         Set<Float> set,
         HashSet<Float> hashSet,
         TreeSet<Float> treeSet,
         Map<Float,Float> map,
         HashMap<Float,Float> hashMap,
         TreeMap<Float,Float> treeMap)
    {
        super(item,collection,list,linkedList,
              set,hashSet,treeSet,map,hashMap,treeMap);
        setItemP(itemP);        
        setArrayP(arrayP);
        setArray(array);
    }


    public void setItemP
        (float itemP)
    {
        mItemP=itemP;
    }

    public float getItemP()
    {
        return mItemP;
    }

    public void setArrayP
        (float[] arrayP)
    {
        mArrayP=arrayP;
    }

    public float[] getArrayP()
    {
        return mArrayP;
    }

    public void setArray
        (Float[] array)
    {
        mArray=array;
    }

    public Float[] getArray()
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
        FloatHolder o=(FloatHolder)other;
        return (super.equals(o) &&
                ArrayUtils.isEquals(getItemP(),o.getItemP()) &&
                ArrayUtils.isEquals(getArrayP(),o.getArrayP()) &&
                ArrayUtils.isEquals(getArray(),o.getArray()));
    }
}
