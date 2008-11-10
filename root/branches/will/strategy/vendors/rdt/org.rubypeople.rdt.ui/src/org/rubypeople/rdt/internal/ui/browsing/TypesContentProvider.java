package org.rubypeople.rdt.internal.ui.browsing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;

public class TypesContentProvider extends RubyBrowsingContentProvider {
	
	TypesContentProvider(RubyBrowsingPart browsingPart) {
		super(false, browsingPart);
	}

	/* (non-Rubydoc)
	 * Method declared on ITreeContentProvider.
	 */
	public Object[] getChildren(Object element) {
		if (!exists(element))
			return NO_CHILDREN;

		try {
			startReadInDisplayThread();
			if (element instanceof IStructuredSelection) {
				Assert.isLegal(false);
				Object[] result= new Object[0];
				Class clazz= null;
				Iterator iter= ((IStructuredSelection)element).iterator();
				while (iter.hasNext()) {
					Object item=  iter.next();
					if (clazz == null)
						clazz= item.getClass();
					if (clazz == item.getClass())
						result= concatenate(result, getChildren(item));
					else
						return NO_CHILDREN;
				}
				return result;
			}
			if (element instanceof IStructuredSelection) {
				Assert.isLegal(false);
				Object[] result= new Object[0];
				Iterator iter= ((IStructuredSelection)element).iterator();
				while (iter.hasNext())
					result= concatenate(result, getChildren(iter.next()));
				return result;
			}
			if (element instanceof ISourceFolderRoot)  {
				ISourceFolderRoot root = (ISourceFolderRoot) element;
				IRubyElement[] children = root.getChildren();
				Object[] result= new Object[0];
				for (int i = 0; i < children.length; i++) {
					result = concatenate(result, getChildren(children[i]));
				}
				return result;
			}
			if (element instanceof ISourceFolder)
				return getFolderContents((ISourceFolder)element);
			if (element instanceof IRubyScript)
				return getTopLevelTypes((IRubyScript)element);
			if (element instanceof IType)
				return getSubTypes((IType) element);

			return super.getChildren(element);

		} catch (RubyModelException e) {
			return NO_CHILDREN;
		} finally {
			finishedReadInDisplayThread();
		}
	}

	private Object[] getTopLevelTypes(IRubyScript script) throws RubyModelException {
		return script.getTypes();
	}

	private Object[] getSubTypes(IType type) throws RubyModelException {
		return type.getTypes();
	}

	/*
	 *
	 * @see ITreeContentProvider
	 */
	public boolean hasChildren(Object element) {
		return element instanceof ISourceFolder || (element instanceof IType && super.hasChildren(element)); // FIXME the and part should be "and has subtypes"
	}
}
