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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.jruby.ast.Node;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.ASTProvider;
import org.rubypeople.rdt.internal.ui.util.StringMatcher;

public class CallHierarchy {
    private static final String PREF_USE_IMPLEMENTORS= "PREF_USE_IMPLEMENTORS"; //$NON-NLS-1$
    private static final String PREF_USE_FILTERS = "PREF_USE_FILTERS"; //$NON-NLS-1$
    private static final String PREF_FILTERS_LIST = "PREF_FILTERS_LIST"; //$NON-NLS-1$

    private static final String DEFAULT_IGNORE_FILTERS = "java.*,javax.*"; //$NON-NLS-1$
    private static CallHierarchy fgInstance;
    private IRubySearchScope fSearchScope;
    private StringMatcher[] fFilters;

    public static CallHierarchy getDefault() {
        if (fgInstance == null) {
            fgInstance = new CallHierarchy();
        }

        return fgInstance;
    }

    public boolean isSearchUsingImplementorsEnabled() {
        IPreferenceStore settings = RubyPlugin.getDefault().getPreferenceStore();

        return settings.getBoolean(PREF_USE_IMPLEMENTORS);
    }

    public void setSearchUsingImplementorsEnabled(boolean enabled) {
        IPreferenceStore settings = RubyPlugin.getDefault().getPreferenceStore();

        settings.setValue(PREF_USE_IMPLEMENTORS, enabled);
    }

    public Collection getImplementingMethods(IMethod method) {
        if (isSearchUsingImplementorsEnabled()) {
            IRubyElement[] result = Implementors.getInstance().searchForImplementors(new IRubyElement[] {
                        method
                    }, new NullProgressMonitor());

            if ((result != null) && (result.length > 0)) {
                return Arrays.asList(result);
            }
        }

        return new ArrayList(0);
    }

    public Collection getInterfaceMethods(IMethod method) {
        if (isSearchUsingImplementorsEnabled()) {
            IRubyElement[] result = Implementors.getInstance().searchForInterfaces(new IRubyElement[] {
                        method
                    }, new NullProgressMonitor());

            if ((result != null) && (result.length > 0)) {
                return Arrays.asList(result);
            }
        }

        return new ArrayList(0);
    }

    public MethodWrapper getCallerRoot(IMethod method) {
        return new CallerMethodWrapper(null, new MethodCall(method));
    }

    public MethodWrapper getCalleeRoot(IMethod method) {
        return new CalleeMethodWrapper(null, new MethodCall(method));
    }

    public static CallLocation getCallLocation(Object element) {
        CallLocation callLocation = null;

        if (element instanceof MethodWrapper) {
            MethodWrapper methodWrapper = (MethodWrapper) element;
            MethodCall methodCall = methodWrapper.getMethodCall();

            if (methodCall != null) {
                callLocation = methodCall.getFirstCallLocation();
            }
        } else if (element instanceof CallLocation) {
            callLocation = (CallLocation) element;
        }

        return callLocation;
    }

    public IRubySearchScope getSearchScope() {
        if (fSearchScope == null) {
            fSearchScope= SearchEngine.createWorkspaceScope();
        }

        return fSearchScope;
    }

    public void setSearchScope(IRubySearchScope searchScope) {
        this.fSearchScope = searchScope;
    }

    /**
     * Checks whether the fully qualified name is ignored by the set filters.
     *
     * @param fullyQualifiedName
     *
     * @return True if the fully qualified name is ignored.
     */
    public boolean isIgnored(String fullyQualifiedName) {
        if ((getIgnoreFilters() != null) && (getIgnoreFilters().length > 0)) {
            for (int i = 0; i < getIgnoreFilters().length; i++) {
                String fullyQualifiedName1 = fullyQualifiedName;

                if (getIgnoreFilters()[i].match(fullyQualifiedName1)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isFilterEnabled() {
        IPreferenceStore settings = RubyPlugin.getDefault().getPreferenceStore();
        return settings.getBoolean(PREF_USE_FILTERS);
    }

    public void setFilterEnabled(boolean filterEnabled) {
        IPreferenceStore settings = RubyPlugin.getDefault().getPreferenceStore();
        settings.setValue(PREF_USE_FILTERS, filterEnabled);
    }
    
    /**
     * Returns the current filters as a string.
     */
    public String getFilters() {
        IPreferenceStore settings = RubyPlugin.getDefault().getPreferenceStore();

        return settings.getString(PREF_FILTERS_LIST);
    }

    public void setFilters(String filters) {
        fFilters = null;

        IPreferenceStore settings = RubyPlugin.getDefault().getPreferenceStore();
        settings.setValue(PREF_FILTERS_LIST, filters);
    }

    /**
     * Returns filters for packages which should not be included in the search results.
     *
     * @return StringMatcher[]
     */
    private StringMatcher[] getIgnoreFilters() {
        if (fFilters == null) {
            String filterString = null;

            if (isFilterEnabled()) {
                filterString = getFilters();

                if (filterString == null) {
                    filterString = DEFAULT_IGNORE_FILTERS;
                }
            }

            if (filterString != null) {
                fFilters = parseList(filterString);
            } else {
                fFilters = null;
            }
        }

        return fFilters;
    }

    /**
     * Parses the comma separated string into an array of StringMatcher objects
     *
     * @return list
     */
    private static StringMatcher[] parseList(String listString) {
        List list = new ArrayList(10);
        StringTokenizer tokenizer = new StringTokenizer(listString, ","); //$NON-NLS-1$

        while (tokenizer.hasMoreTokens()) {
            String textFilter = tokenizer.nextToken().trim();
            list.add(new StringMatcher(textFilter, false, false));
        }

        return (StringMatcher[]) list.toArray(new StringMatcher[list.size()]);
    }
    
    static Node getRubyScriptNode(IMember member, boolean resolveBindings) {
    	IRubyScript icu= member.getRubyScript();
        if (icu != null && icu.exists()) {
        	return ASTProvider.getASTProvider().getAST(icu, ASTProvider.WAIT_YES, null);
        }
        return null;
    }
}
