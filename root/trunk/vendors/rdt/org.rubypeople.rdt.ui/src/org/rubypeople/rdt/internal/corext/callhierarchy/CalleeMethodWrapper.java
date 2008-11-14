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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.jruby.ast.Node;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;

class CalleeMethodWrapper extends MethodWrapper {
    private Comparator fMethodWrapperComparator = new MethodWrapperComparator();

    private static class MethodWrapperComparator implements Comparator {
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {
            MethodWrapper m1 = (MethodWrapper) o1;
            MethodWrapper m2 = (MethodWrapper) o2;

            CallLocation callLocation1 = m1.getMethodCall().getFirstCallLocation();
            CallLocation callLocation2 = m2.getMethodCall().getFirstCallLocation();

            if ((callLocation1 != null) && (callLocation2 != null)) {
                if (callLocation1.getStart() == callLocation2.getStart()) {
                    return callLocation1.getEnd() - callLocation2.getEnd();
                }

                return callLocation1.getStart() - callLocation2.getStart();
            }

            return 0;
        }
    }

    /**
     * Constructor for CalleeMethodWrapper.
     */
    public CalleeMethodWrapper(MethodWrapper parent, MethodCall methodCall) {
        super(parent, methodCall);
    }

	/* Returns the calls sorted after the call location
	 * @see org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper#getCalls()
     */
    public MethodWrapper[] getCalls(IProgressMonitor progressMonitor) {
        MethodWrapper[] result = super.getCalls(progressMonitor);
        Arrays.sort(result, fMethodWrapperComparator);

        return result;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper#getTaskName()
     */
    protected String getTaskName() {
        return CallHierarchyMessages.CalleeMethodWrapper_taskname; 
    }

	/*
	 * @see org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper#createMethodWrapper(org.eclipse.jdt.internal.corext.callhierarchy.MethodCall)
     */
    protected MethodWrapper createMethodWrapper(MethodCall methodCall) {
        return new CalleeMethodWrapper(this, methodCall);
    }

	/**
     * Find callees called from the current method.
	 * @see org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper#findChildren(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected Map findChildren(IProgressMonitor progressMonitor) {
    	if (getMember().exists() && getMember().getElementType() == IRubyElement.METHOD) {
        	Node cu= CallHierarchy.getRubyScriptNode(getMember(), true);
            if (progressMonitor != null) {
                progressMonitor.worked(5);
            }

        	if (cu != null) {
        		CalleeAnalyzerVisitor visitor = new CalleeAnalyzerVisitor((IMethod) getMember(), progressMonitor);
        
        		cu.accept(visitor);
        		return visitor.getCallees();
        	}
        }
        return new HashMap(0);
    }
}
