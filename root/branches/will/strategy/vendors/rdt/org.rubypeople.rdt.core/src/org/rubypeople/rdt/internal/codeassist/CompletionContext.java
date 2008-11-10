package org.rubypeople.rdt.internal.codeassist;

import org.jruby.ast.ClassNode;
import org.jruby.ast.MethodDefNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyScript;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.ti.util.ClosestSpanningNodeLocator;
import org.rubypeople.rdt.internal.ti.util.INodeAcceptor;

public class CompletionContext {
	
	private IRubyScript script;
	private int offset;
	private boolean isMethodInvokation = false;
	private String correctedSource;
	private String partialPrefix;
	private String fullPrefix;
	private int replaceStart;
	private boolean isAfterDoubleSemiColon = false;
	private Node fRootNode;

	public CompletionContext(IRubyScript script, int offset) throws RubyModelException {
		this.script = script;
		if (offset < 0)
			offset = 0;
		this.offset = offset;
		replaceStart = offset + 1;
		try {
			run();
		} catch (RuntimeException e) {
			RubyCore.log(e);
		}
	}
	
	private void run() throws RubyModelException {
		StringBuffer source = new StringBuffer(script.getSource());
		if (offset >= source.length()) {
			offset = source.length() - 1;
			replaceStart = offset + 1;
		}
		// Read from offset back until we hit a: space, period
		// if we hit a period, use character before period as offset for
		// inferrer
		StringBuffer tmpPrefix = new StringBuffer();
		boolean setOffset = false;
		for (int i = offset; i >= 0; i--) {			
			char curChar = source.charAt(i);
			if (offset == i) { // check the first character
				switch (curChar) {
				case '.':
				case '$': // if it breaks syntax, lets fix it
				case '@':
				case ',':
					// TODO What if there is a valid character after this, so syntax isn't broken?
					source.deleteCharAt(i);
//					i--;
					break;
				case ':':
					if (i > 0) {						
						// Check character before this for :
						char previous = source.charAt(i - 1);
						if (previous == ':') {
							isAfterDoubleSemiColon = true;
							source.deleteCharAt(i);
							source.deleteCharAt(i - 1);
							tmpPrefix.insert(0, "::");
							partialPrefix = "";
							i--;
							continue;
						}
					}					
					break;
				}
			}
			if (curChar == '.') {				
				isMethodInvokation = true;				
				if (partialPrefix == null) this.partialPrefix = tmpPrefix.toString();
				if (offset - 1 == i) {				
					offset = i;
				} else {
					offset = i - 1;
				}
				setOffset = true;
			} else if (curChar == ':') {
				if (i > 0) {						
					// Check character before this for :
					char previous = source.charAt(i - 1);
					if (previous == ':') {
						isAfterDoubleSemiColon = true;
						if (partialPrefix == null) partialPrefix = tmpPrefix.toString();
						tmpPrefix.insert(0, ":");							
						i--;
					}
				}
			}
			// FIXME This logic is very much like RubyWordDetector in the UI!
			if (Character.isWhitespace(curChar) || curChar == ',' || curChar == '(' || curChar == '[' || curChar == '{') {
				if (!setOffset) {
					offset = i + 1;
					setOffset = true;
				}				
				break;
			}
			tmpPrefix.insert(0, curChar);
		}
		this.fullPrefix = tmpPrefix.toString();
		if (partialPrefix == null)
			partialPrefix = fullPrefix;
		if (partialPrefix != null)
			replaceStart -= partialPrefix.length();
		this.correctedSource = source.toString();
	}
	
	/**
	 * This is when we have a receiver and a period in the prefix
	 * @return
	 */
	public boolean isExplicitMethodInvokation() {
	  return isMethodInvokation;	
	}
	
	/**
	 * This is when it could be a method call with an implicit self, or when it may just be a local
	 * @return
	 */
	public boolean isMethodInvokationOrLocal() {
		  return !isExplicitMethodInvokation() && (emptyPrefix() || (getPartialPrefix().length() > 0 && Character.isLowerCase(getPartialPrefix().charAt(0))));	
	}
	
	/**
	 * The last portion of prefix is not null, not empty and starts with an uppercase letter
	 * @return
	 */
	public boolean isConstant() {
		return getPartialPrefix() != null && getPartialPrefix().length() > 0 && Character.isUpperCase(getPartialPrefix().charAt(0));
	}
	
	public int getReplaceStart() {
		return replaceStart;
	}
	
	/**
	 * Modified source which should not fail parsing.
	 * @return
	 */
	public String getCorrectedSource() {
		return correctedSource;
	}
	
	public boolean isBroken() {
		try {
			return !getCorrectedSource().equals(script.getSource());
		} catch (RubyModelException e) {
			return true;
		}
	}
	
	public boolean hasReceiver() {
		return getFullPrefix().indexOf('.') > 1;
	}
	
	/**
	 * The original source
	 * @return
	 */
	public String getSource() {
		try {
			return getScript().getSource();
		} catch (RubyModelException e) {
			return "";
		}
	}
	
	public String getFullPrefix() {
		return fullPrefix;
	}
	
	public String getPartialPrefix() {
		return partialPrefix;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public IRubyScript getScript() {
		return script;
	}

	public boolean emptyPrefix() {
		return getFullPrefix() == null || getFullPrefix().length() == 0;
	}

	public boolean prefixStartsWith(String name) {
		return name != null && getPartialPrefix() != null && name.startsWith(getPartialPrefix());
	}

	public boolean isGlobal() {
		return !emptyPrefix() && !isExplicitMethodInvokation() && getPartialPrefix().startsWith("$");
	}
	
	public boolean isDoubleSemiColon() {
		return isAfterDoubleSemiColon && !isMethodInvokation;
	}

	public boolean fullPrefixIsConstant() {
		if (getFullPrefix() == null || getFullPrefix().length() == 0) return false;
		if (getFullPrefix().endsWith("\".") || getFullPrefix().endsWith("'.")) return false;
		return Character.isUpperCase(getFullPrefix().charAt(0));
	}

	/**
	 * Returns whether we're inside a type definition and not inside a method definition (used to determine if we should only show class level methods)
	 * @return
	 */
	public boolean inTypeDefinition() {
		if (getRootNode() == null) return false;
		Node spanner = ClosestSpanningNodeLocator.Instance().findClosestSpanner(getRootNode(), getOffset(), new INodeAcceptor() {
			
			public boolean doesAccept(Node node) {
				return node instanceof MethodDefNode || node instanceof ClassNode || node instanceof ModuleNode;
			}
		
		});
		return spanner instanceof ClassNode || spanner instanceof ModuleNode;
	}
	
	Node getRootNode() {
		if (fRootNode != null) return fRootNode;
		RubyParser parser = new RubyParser();
		if (!isBroken()) {
			try {
				fRootNode = parser.parse(getScript().getElementName(), getSource()).getAST();
			} catch (RuntimeException e) {
				// ignore
			}
		}
		if (fRootNode == null) {
			try {
				fRootNode = parser.parse(getCorrectedSource()).getAST();
			} catch (RuntimeException e) {
				// ignore
			}
		}
		if (fRootNode == null) {
			fRootNode = ((RubyScript) getScript()).lastGoodAST;
		}
		return fRootNode;
	}

}
