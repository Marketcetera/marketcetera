/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2006 Mirko Stocker <me@misto.ch>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/
package org.jruby.ast;

/**
 * Base class for Defn- and DefsNode 
 * 
 * @author  jpetersen
 */
import org.jruby.ast.types.INameNode;
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.parser.StaticScope;

public abstract class MethodDefNode extends Node implements INameNode {

	protected final ArgumentNode nameNode;
	protected final ArgsNode argsNode;
	protected final StaticScope scope;
	protected final Node bodyNode;

	public MethodDefNode(ISourcePosition position, ArgumentNode nameNode, ArgsNode argsNode, StaticScope scope, Node bodyNode, int id) {
		super(position, id);
		this.nameNode = nameNode;
		this.argsNode = argsNode;
		this.scope = scope;
		this.bodyNode = bodyNode;
	}

	/**
	 * Gets the argsNode.
	 * @return Returns a Node
	 */
	public ArgsNode getArgsNode() {
	    return argsNode;
	}

	/**
	 * Get the static scoping information.
	 * 
	 * @return the scoping info
	 */
	public StaticScope getScope() {
	    return scope;
	}

	/**
	 * Gets the body of this class.
	 * 
	 * @return the contents
	 */
	public Node getBodyNode() {
	    return bodyNode;
	}

	/**
	 * Gets the name's node.
	 * @return Returns an ArgumentNode
	 */
	public ArgumentNode getNameNode() {
	    return nameNode;
	}

	/**
	 * Gets the name.
	 * @return Returns a String
	 */
	public String getName() {
	    return nameNode.getName();
	}
}