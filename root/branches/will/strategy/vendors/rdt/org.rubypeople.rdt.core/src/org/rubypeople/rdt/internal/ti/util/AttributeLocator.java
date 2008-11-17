package org.rubypeople.rdt.internal.ti.util;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jruby.ast.ArrayNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.Node;
import org.jruby.ast.StrNode;
import org.jruby.ast.SymbolNode;
import org.jruby.evaluator.Instruction;

/**
 * Visitor to find all instance and class attribute declarations (attr_*, cattr_*) within a specific scope.
 * @author Jason Morrison
 */
public class AttributeLocator extends NodeLocator {

	//Singleton pattern
	private AttributeLocator() {}
	private static AttributeLocator staticInstance = new AttributeLocator();
	public static AttributeLocator Instance()
	{
		return staticInstance;
	}
	
	/** Running total of results; is a Set to ensure uniqueness */
	private Set<String> attributes;

	/**
	 * Finds all instance attributes within a given node by looking for attr_* calls
	 * @param rootNode
	 * @return
	 */
	public List<String> findInstanceAttributesInScope(Node rootNode) {
		if ( rootNode == null ) { return new ArrayList<String>(); }
		
		attributes = new HashSet<String>();
		
//		 Traverse to find all matches
		rootNode.accept(this);
		
		// Return the matches
		return new ArrayList<String>(attributes);
	}
	
	/**
	 * Searches via InOrderVisitor for matches
	 */
	public Instruction handleNode(Node node ) {
		// Look for FCallNodes to attr_*
		if ( node instanceof FCallNode ) {
			FCallNode fCallNode = (FCallNode)node;
			
			// Set up the prefix for instance (@) or class (@@) attributes 
			String attrPrefix = null;
			if ( isInstanceAttributeDeclaration(fCallNode.getName()) ) {
				attrPrefix = "@";
			}
			if ( isClassAttributeDeclaration(fCallNode.getName()) ) {
				attrPrefix = "@@";
			}
			if ( attrPrefix != null) {				
				// Look for an array of symbols or strings - these are the instance variables being declared
				Node argsNode = fCallNode.getArgsNode();
				if ( argsNode instanceof ArrayNode ) {
					ArrayNode arrayNode = (ArrayNode)argsNode;
					for (Iterator iter = arrayNode.childNodes().iterator(); iter.hasNext();) {
						Node argNode = (Node) iter.next();

						// The nodes are found - record them!
						if ( argNode instanceof SymbolNode ) {
							attributes.add(attrPrefix + ((SymbolNode)argNode).getName() );
						}
						if ( argNode instanceof StrNode ) {
							attributes.add(attrPrefix + ((StrNode)argNode).getValue() );
						}						
					}
				}
			}
		}
			
		return super.handleNode(node);
	}

	/**
	 * Returns whether the specified method name is an instance attribute declaration
	 * (i.e. attr_* :foo, 'bar', "baz")
	 * @param methodName Method name to test
	 * @return
	 */
	private boolean isInstanceAttributeDeclaration(String methodName) {
		return (
			methodName.equals("attr") ||
			methodName.equals("attr_reader") ||
			methodName.equals("attr_writer") ||
			methodName.equals("attr_accessor") );
	}
	/**
	 * Returns whether the specified method name is a class attribute declaration
	 * (i.e. cattr_* :foo, 'bar', "baz")
	 * Non-standard, but conventional enough to be helpful, I believe.
	 * @param methodName Method name to test
	 * @return
	 */
	private boolean isClassAttributeDeclaration(String methodName) {
		return (
			methodName.equals("cattr") ||
			methodName.equals("cattr_reader") ||
			methodName.equals("cattr_writer") ||
			methodName.equals("cattr_accessor") );
	}
	
	
}
