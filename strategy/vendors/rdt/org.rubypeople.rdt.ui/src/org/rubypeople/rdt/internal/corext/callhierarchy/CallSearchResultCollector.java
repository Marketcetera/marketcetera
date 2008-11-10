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

import java.util.HashMap;
import java.util.Map;

import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IType;

class CallSearchResultCollector {
    private Map fCalledMembers;

    public CallSearchResultCollector() {
        this.fCalledMembers = createCalledMethodsData();
    }

    public Map getCallers() {
        return fCalledMembers;
    }

    protected void addMember(IMember member, IMember calledMember, int start, int end) {
        addMember(member, calledMember, start, end, CallLocation.UNKNOWN_LINE_NUMBER);
    }

    protected void addMember(IMember member, IMember calledMember, int start, int end, int lineNumber) {
        if ((member != null) && (calledMember != null)) {
            if (!isIgnored(calledMember)) {
                MethodCall methodCall = (MethodCall) fCalledMembers.get(calledMember.getHandleIdentifier());

                if (methodCall == null) {
                    methodCall = new MethodCall(calledMember);
                    fCalledMembers.put(calledMember.getHandleIdentifier(), methodCall);
                }

                methodCall.addCallLocation(new CallLocation(member, calledMember, start,
                        end, lineNumber));
            }
        }
    }

    protected Map createCalledMethodsData() {
        return new HashMap();
    }

    /**
     * Method isIgnored.
     * @param enclosingElement
     * @return boolean
     */
    private boolean isIgnored(IMember enclosingElement) {
    	IType type = getTypeOfElement(enclosingElement);
    	String fullyQualifiedName = "Object";
    	if (type != null) {
    		fullyQualifiedName = type.getFullyQualifiedName();    		
    	}

        return CallHierarchy.getDefault().isIgnored(fullyQualifiedName);
    }

    private IType getTypeOfElement(IMember element) {
        if (element.getElementType() == IRubyElement.TYPE) {
            return (IType) element;
        }

        return element.getDeclaringType();
    }
}
