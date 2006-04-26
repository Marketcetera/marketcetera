package org.marketcetera.photon.model;

import org.eclipse.core.runtime.PlatformObject;
import org.marketcetera.core.ClassVersion;

@ClassVersion("$Id$")
public class FilterItem<T> extends PlatformObject{
    T mItem;
    String mName;
    public FilterItem(T item, String name) {
        mItem = item;
        mName = name;
    }
    public T getItem() {
        return mItem;
    }
    public void setItem(T item) {
        mItem = item;
    }
    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }



}
