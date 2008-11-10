package org.rubypeople.rdt.internal.ui.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.rubypeople.rdt.core.IMember;

public class AllMembersFilter extends ViewerFilter {

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return (!(element instanceof IMember));
	}

}
