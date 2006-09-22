package org.marketcetera.datamodel.helpers;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.datamodel.TableBase;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Date;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class AccessTimeModificationInterceptor extends EmptyInterceptor  {
    public AccessTimeModificationInterceptor() {
    }

    /** Update the modifiedOn column */
    public boolean onFlushDirty(Object entity, Serializable serializable, Object[] currentState, Object[] previousState,
                                String[] propertyNames, Type[] types) {
        return modifyUpdatedOn(entity, currentState, propertyNames);
    }

    /** Update the createdOn and updatedOn columns */
    public boolean onSave(Object entity, Serializable serializable, Object[] currentState, String[] propertyNames, Type[] types) {
        return modifyUpdatedOn(entity, currentState, propertyNames);
    }

    private boolean modifyUpdatedOn(Object entity, Object[] currentState, String[] propertyNames) {
        if ( entity instanceof TableBase ) {
            for ( int i=0; i < propertyNames.length; i++ ) {
                if ( TableBase.MODIFIED_ON_COL_NAME.equals( propertyNames[i] ) ) {
                    currentState[i] = new Date();
                    return true;
                }
            }
        }
        return false;
    }
}
