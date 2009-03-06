package org.marketcetera.util.ws.types;

import java.util.Collection;
import java.util.Date;
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

public class DateHolder
{
    private Date mItem;
    private Date[] mArray;
    private Collection<Date> mCollection;
    private List<Date> mList;
    private LinkedList<Date> mLinkedList;
    private Set<Date> mSet;
    private HashSet<Date> mHashSet;
    private TreeSet<Date> mTreeSet;
    private Map<Date,Date> mMap;
    private HashMap<Date,Date> mHashMap;
    private TreeMap<Date,Date> mTreeMap;


    private DateHolder() {}

    public DateHolder
        (Date item,
         Date[] array,
         Collection<Date> collection,
         List<Date> list,
         LinkedList<Date> linkedList,
         Set<Date> set,
         HashSet<Date> hashSet,
         TreeSet<Date> treeSet,
         Map<Date,Date> map,
         HashMap<Date,Date> hashMap,
         TreeMap<Date,Date> treeMap)
    {
        setItem(item);
        setArray(array);
        setCollection(collection);
        setList(list);
        setLinkedList(linkedList);
        setSet(set);
        setHashSet(hashSet);
        setTreeSet(treeSet);
        setMap(map);
        setHashMap(hashMap);
        setTreeMap(treeMap);
    }


    public void setItem
        (Date item)
    {
        mItem=item;
    }

    public Date getItem()
    {
        return mItem;
    }

    public void setArray
        (Date[] array)
    {
        mArray=array;
    }

    public Date[] getArray()
    {
        return mArray;
    }

    public void setCollection
        (Collection<Date> collection)
    {
        mCollection=collection;
    }

    public Collection<Date> getCollection()
    {
        return mCollection;
    }

    public void setList
        (List<Date> list)
    {
        mList=list;
    }

    public List<Date> getList()
    {
        return mList;
    }

    public void setLinkedList
        (LinkedList<Date> linkedList)
    {
        mLinkedList=linkedList;
    }

    public LinkedList<Date> getLinkedList()
    {
        return mLinkedList;
    }

    public void setSet
        (Set<Date> set)
    {
        mSet=set;
    }

    public Set<Date> getSet()
    {
        return mSet;
    }

    public void setHashSet
        (HashSet<Date> hashSet)
    {
        mHashSet=hashSet;
    }

    public HashSet<Date> getHashSet()
    {
        return mHashSet;
    }

    public void setTreeSet
        (TreeSet<Date> treeSet)
    {
        mTreeSet=treeSet;
    }

    public TreeSet<Date> getTreeSet()
    {
        return mTreeSet;
    }

    public void setMap
        (Map<Date,Date> map)
    {
        mMap=map;
    }

    public Map<Date,Date> getMap()
    {
        return mMap;
    }

    public void setHashMap
        (HashMap<Date,Date> hashMap)
    {
        mHashMap=hashMap;
    }

    public HashMap<Date,Date> getHashMap()
    {
        return mHashMap;
    }

    public void setTreeMap
        (TreeMap<Date,Date> treeMap)
    {
        mTreeMap=treeMap;
    }

    public TreeMap<Date,Date> getTreeMap()
    {
        return mTreeMap;
    }


    @Override
    public int hashCode()
    {
        return (ArrayUtils.hashCode(getItem())+
                ArrayUtils.hashCode(getArray())+
                ArrayUtils.hashCode(getCollection())+
                ArrayUtils.hashCode(getList())+
                ArrayUtils.hashCode(getLinkedList())+
                ArrayUtils.hashCode(getSet())+
                ArrayUtils.hashCode(getHashSet())+
                ArrayUtils.hashCode(getTreeSet())+
                ArrayUtils.hashCode(getMap())+
                ArrayUtils.hashCode(getHashMap())+
                ArrayUtils.hashCode(getTreeMap()));
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
        DateHolder o=(DateHolder)other;
        return (ArrayUtils.isEquals(getItem(),o.getItem()) &&
                ArrayUtils.isEquals(getArray(),o.getArray()) &&
                ArrayUtils.isEquals(getCollection(),o.getCollection()) &&
                ArrayUtils.isEquals(getList(),o.getList()) &&
                ArrayUtils.isEquals(getLinkedList(),o.getLinkedList()) &&
                ArrayUtils.isEquals(getSet(),o.getSet()) &&
                ArrayUtils.isEquals(getHashSet(),o.getHashSet()) &&
                ArrayUtils.isEquals(getTreeSet(),o.getTreeSet()) &&
                ArrayUtils.isEquals(getMap(),o.getMap()) &&
                ArrayUtils.isEquals(getHashMap(),o.getHashMap()) &&
                ArrayUtils.isEquals(getTreeMap(),o.getTreeMap()));
    }
}
