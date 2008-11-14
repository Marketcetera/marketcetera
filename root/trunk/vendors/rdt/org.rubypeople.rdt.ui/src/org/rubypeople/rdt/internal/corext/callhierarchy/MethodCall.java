/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jesper Kamstrup Linnet (eclipse@kamstrup-linnet.dk) - initial API and implementation 
 *          (report 36180: Callers/Callees view)
 *******************************************************************************/
package org.rubypeople.rdt.internal.corext.callhierarchy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.rubypeople.rdt.core.IMember;

public class MethodCall {
    private IMember fMember;
    private List fCallLocations;

    /**
     * @param enclosingElement
     */
    public MethodCall(IMember enclosingElement) {
        this.fMember = enclosingElement;
    }

    /**
     *
     */
    public Collection getCallLocations() {
        return fCallLocations;
    }

    public CallLocation getFirstCallLocation() {
        if ((fCallLocations != null) && !fCallLocations.isEmpty()) {
            return (CallLocation) fCallLocations.get(0);
        } else {
            return null;
        }
    }

    public boolean hasCallLocations() {
        return fCallLocations != null && fCallLocations.size() > 0;
    }
    
    /**
     * @return Object
     */
    public Object getKey() {
        return getMember().getHandleIdentifier();
    }

    /**
     *
     */
    public IMember getMember() {
        return fMember;
    }

    /**
     * @param location
     */
    public void addCallLocation(CallLocation location) {
        if (fCallLocations == null) {
            fCallLocations = new ArrayList();
        }

        fCallLocations.add(location);
    }
}
