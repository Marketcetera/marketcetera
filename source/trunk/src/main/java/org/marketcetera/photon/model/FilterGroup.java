/**
 * 
 */
package org.marketcetera.photon.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.IFiltersListener;


@ClassVersion("$Id$")
public class FilterGroup extends PlatformObject{
    Object mParent;

    ArrayList<Object> mChildren = new ArrayList<Object>();

    String mLabel;

    List<IFiltersListener> mListeners = new ArrayList<IFiltersListener>();

    public FilterGroup(Object parent, String name) {
        super();
        mParent = parent;
        mLabel = name;
    }

    public Object[] getChildren() {
        return mChildren.toArray();
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String name) {
        mLabel = name;
    }

    public Object getParent() {
        return mParent;
    }

    public void setParent(Object parent) {
        mParent = parent;
    }

    public void addChild(Object child) {
        mChildren.add(child);
        for (IFiltersListener listener : mListeners) {
            listener.filtersChanged(this, child);
        }
    }

    public void addFiltersListener(IFiltersListener listener) {
        mListeners.add(listener);
    }

    public void removeFiltersListener(IFiltersListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public ImageDescriptor getImageDescriptor(Object object) {
        // TODO Auto-generated method stub
        return null;
    }

}