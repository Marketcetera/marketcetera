package org.rubypeople.rdt.internal.core.search.matching;

import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyScript;
import org.rubypeople.rdt.internal.core.util.Util;

public class TypeDeclarationLocator extends PatternLocator {

	private TypeDeclarationPattern pattern;

	public TypeDeclarationLocator(TypeDeclarationPattern pattern) {
		super(pattern);
		this.pattern = pattern;
	}
	
	@Override
	public void reportMatches(RubyScript script, MatchLocator locator) {
		reportMatches((IParent) script, locator);
	}

	private void reportMatches(IParent parent, MatchLocator locator) {
		try {
			IRubyElement[] children = parent.getChildren();
			for (int i = 0; i < children.length; i++) {
				IRubyElement child = children[i];
				if (child.isType(IRubyElement.TYPE) && locator.encloses(child)) {
					int accuracy = getAccuracy((IType) child);
					if (accuracy != IMPOSSIBLE_MATCH) {
						IMember member = (IMember) child;
						ISourceRange range = member.getSourceRange();
						try {
							locator.report(locator.newDeclarationMatch(child, accuracy, range.getOffset(), range.getLength()));
						} catch (CoreException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				if (child instanceof IParent) {
					IParent parentTwo = (IParent) child;
					reportMatches(parentTwo, locator);
				}
			}
		} catch (RubyModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int getAccuracy(IType type) {
		String simpleName = Util.getSimpleName(type.getElementName());
		if (this.pattern.simpleName != null && !matchesName(this.pattern.simpleName, simpleName.toCharArray()))
			return IMPOSSIBLE_MATCH;

		switch (this.pattern.typeSuffix) {
		case CLASS_SUFFIX:
			if (!type.isClass())
				return IMPOSSIBLE_MATCH;
			break;
		case MODULE_SUFFIX:
			if (!type.isModule())
				return IMPOSSIBLE_MATCH;
			break;
		}
		return ACCURATE_MATCH;
	}

}
