package org.rubypeople.rdt.internal.ui.text.ruby.hover;

import java.util.ArrayList;
import java.util.List;

import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.internal.ui.infoviews.RiUtility;

public class RiDocHoverProvider extends AbstractRubyEditorTextHover {		
	
	protected String getHoverInfo(IRubyElement[] rubyElements) {
		if (rubyElements == null || rubyElements.length == 0) return null;
		String symbol = getRICompatibleName(rubyElements[0]);
		if (symbol == null) return null;
		return getRIResult(symbol);
	}
 
	private String getRIResult(String symbol) {
		if (symbol == null || symbol.trim().length() == 0) return null;		
    	List<String> args = new ArrayList<String>();  
    	args.add("--no-pager");
    	args.add(symbol);
		String content = RiUtility.getRIHTMLContents(args);
		if (content == null) return null;	
		if (content.indexOf("More than one method matched your request") > -1) return null;
		// Change ugly colors!
		content = content.replace("color: #00ffff", "font-weight: bold");
		content = content.replace("color: #ffff00", "font-weight: italic");
		return content;			
	}

	private String getRICompatibleName(IRubyElement element) {
		switch (element.getElementType()) {
		case IRubyElement.TYPE:
			return ((IType) element).getFullyQualifiedName();
		case IRubyElement.METHOD:
			IMethod method = (IMethod) element;
			String delimeter = method.isSingleton() ? "::" : "#";
			return method.getDeclaringType().getFullyQualifiedName() + delimeter + element.getElementName();

		default:
			return null;
		}
	}
}
