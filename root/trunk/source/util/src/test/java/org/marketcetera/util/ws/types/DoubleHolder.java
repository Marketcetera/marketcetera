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

public class DoubleHolder
    extends GenericHolder<Double>
{
    private double mItemP;
    private double[] mArrayP;
    private Double[] mArray;


    private DoubleHolder() {}

    public DoubleHolder
        (double itemP,
         Double item,
         double[] arrayP,
         Double[] array,
         Collection<Double> collection,
         List<Double> list,
         LinkedList<Double> linkedList,
         Set<Double> set,
         HashSet<Double> hashSet,
         TreeSet<Double> treeSet,
         Map<Double,Double> map,
         HashMap<Double,Double> hashMap,
         TreeMap<Double,Double> treeMap)
    {
        super(item,collection,list,linkedList,
              set,hashSet,treeSet,map,hashMap,treeMap);
        setItemP(itemP);        
        setArrayP(arrayP);
        setArray(array);
    }


    public void setItemP
        (double itemP)
    {
        mItemP=itemP;
    }

    public double getItemP()
    {
        return mItemP;
    }

    public void setArrayP
        (double[] arrayP)
    {
        mArrayP=arrayP;
    }

    public double[] getArrayP()
    {
        return mArrayP;
    }

    public void setArray
        (Double[] array)
    {
        mArray=array;
    }

    public Double[] getArray()
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
        DoubleHolder o=(DoubleHolder)other;
        return (super.equals(o) &&
                ArrayUtils.isEquals(getItemP(),o.getItemP()) &&
                ArrayUtils.isEquals(getArrayP(),o.getArrayP()) &&
                ArrayUtils.isEquals(getArray(),o.getArray()));
    }
}
