/**
 * 
 */
package org.marketcetera.photon;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.model.FilterGroup;

@ClassVersion("$Id$")
public interface IFiltersListener {
    public void filtersChanged(FilterGroup filters, Object entry);
}