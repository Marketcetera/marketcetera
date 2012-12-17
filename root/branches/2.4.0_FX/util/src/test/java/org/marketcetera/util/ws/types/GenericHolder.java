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

public class GenericHolder<T>
{
    private T mItem;
    private Collection<T> mCollection;
    private List<T> mList;
    private LinkedList<T> mLinkedList;
    private Set<T> mSet;
    private HashSet<T> mHashSet;
    private TreeSet<T> mTreeSet;
    private Map<T,T> mMap;
    private HashMap<T,T> mHashMap;
    private TreeMap<T,T> mTreeMap;


    protected GenericHolder() {}

    public GenericHolder
        (T item,
         Collection<T> collection,
         List<T> list,
         LinkedList<T> linkedList,
         Set<T> set,
         HashSet<T> hashSet,
         TreeSet<T> treeSet,
         Map<T,T> map,
         HashMap<T,T> hashMap,
         TreeMap<T,T> treeMap)
    {
        setItem(item);
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
        (T item)
    {
        mItem=item;
    }

    public T getItem()
    {
        return mItem;
    }

    public void setCollection
        (Collection<T> collection)
    {
        mCollection=collection;
    }

    public Collection<T> getCollection()
    {
        return mCollection;
    }

    public void setList
        (List<T> list)
    {
        mList=list;
    }

    public List<T> getList()
    {
        return mList;
    }

    public void setLinkedList
        (LinkedList<T> linkedList)
    {
        mLinkedList=linkedList;
    }

    public LinkedList<T> getLinkedList()
    {
        return mLinkedList;
    }

    public void setSet
        (Set<T> set)
    {
        mSet=set;
    }

    public Set<T> getSet()
    {
        return mSet;
    }

    public void setHashSet
        (HashSet<T> hashSet)
    {
        mHashSet=hashSet;
    }

    public HashSet<T> getHashSet()
    {
        return mHashSet;
    }

    public void setTreeSet
        (TreeSet<T> treeSet)
    {
        mTreeSet=treeSet;
    }

    public TreeSet<T> getTreeSet()
    {
        return mTreeSet;
    }

    public void setMap
        (Map<T,T> map)
    {
        mMap=map;
    }

    public Map<T,T> getMap()
    {
        return mMap;
    }

    public void setHashMap
        (HashMap<T,T> hashMap)
    {
        mHashMap=hashMap;
    }

    public HashMap<T,T> getHashMap()
    {
        return mHashMap;
    }

    public void setTreeMap
        (TreeMap<T,T> treeMap)
    {
        mTreeMap=treeMap;
    }

    public TreeMap<T,T> getTreeMap()
    {
        return mTreeMap;
    }


    @Override
    public int hashCode()
    {
        return (ArrayUtils.hashCode(getItem())+
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
        GenericHolder<?> o=(GenericHolder<?>)other;
        return (ArrayUtils.isEquals(getItem(),o.getItem()) &&
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
