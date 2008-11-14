/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.jruby.ast.MethodDefNode;
import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.dom.Selection;
import org.rubypeople.rdt.internal.corext.dom.SelectionAnalyzer;
import org.rubypeople.rdt.internal.ti.util.ClosestSpanningNodeLocator;
import org.rubypeople.rdt.internal.ti.util.INodeAcceptor;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.actions.SelectionConverter;

/**
 * A special text selection that gives access to the resolved and
 * enclosing element.
 */
public class RubyTextSelection extends TextSelection {

	private IRubyElement fElement;
	private IRubyElement[] fResolvedElements;

	private boolean fEnclosingElementRequested;
	private IRubyElement fEnclosingElement;

	private boolean fPartialASTRequested;
	private RootNode fPartialAST;

	private boolean fNodesRequested;
	private Node[] fSelectedNodes;
	private Node fCoveringNode;

	private boolean fInMethodBodyRequested;
	private boolean fInMethodBody;

	/**
	 * Creates a new text selection at the given offset and length.
	 */
	public RubyTextSelection(IRubyElement element, IDocument document, int offset, int length) {
		super(document, offset, length);
		fElement= element;
	}

	/**
	 * Resolves the <code>IRubyElement</code>s at the current offset. Returns
	 * an empty array if the string under the offset doesn't resolve to a
	 * <code>IRubyElement</code>.
	 *
	 * @return the resolved ruby elements at the current offset
	 * @throws RubyModelException passed from the underlying code resolve API
	 */
	public IRubyElement[] resolveElementAtOffset() throws RubyModelException {
		if (fResolvedElements != null)
			return fResolvedElements;
		// long start= System.currentTimeMillis();
		fResolvedElements= SelectionConverter.codeResolve(fElement, this);
		// System.out.println("Time resolving element: " + (System.currentTimeMillis() - start));
		return fResolvedElements;
	}

	public IRubyElement resolveEnclosingElement() throws RubyModelException {
		if (fEnclosingElementRequested)
			return fEnclosingElement;
		fEnclosingElementRequested= true;
		fEnclosingElement= SelectionConverter.resolveEnclosingElement(fElement, this);
		return fEnclosingElement;
	}

	public RootNode resolvePartialAstAtOffset() {
		if (fPartialASTRequested)
			return fPartialAST;
		fPartialASTRequested= true;
		if (! (fElement instanceof IRubyScript))
			return null;
		// long start= System.currentTimeMillis();
		fPartialAST= (RootNode) RubyPlugin.getDefault().getASTProvider().getAST(fElement, ASTProvider.WAIT_YES, null);
		// System.out.println("Time requesting partial AST: " + (System.currentTimeMillis() - start));
		return fPartialAST;
	}

	public Node[] resolveSelectedNodes() {
		if (fNodesRequested)
			return fSelectedNodes;
		fNodesRequested= true;
		RootNode root= resolvePartialAstAtOffset();
		if (root == null)
			return null;
		Selection ds= Selection.createFromStartLength(getOffset(), getLength());
		SelectionAnalyzer analyzer= new SelectionAnalyzer(ds, false);
		root.accept(analyzer);
		fSelectedNodes= analyzer.getSelectedNodes();
		fCoveringNode= analyzer.getLastCoveringNode();
		return fSelectedNodes;
	}

	public Node resolveCoveringNode() {
		if (fNodesRequested)
			return fCoveringNode;
		resolveSelectedNodes();
		return fCoveringNode;
	}

	public boolean resolveInMethodBody() {
		if (fInMethodBodyRequested)
			return fInMethodBody;
		fInMethodBodyRequested= true;
		resolveSelectedNodes();
		Node node= getStartNode();
		if (node == null) {
			fInMethodBody= true;
		} else {
			Node spanner = ClosestSpanningNodeLocator.Instance().findClosestSpanner(resolvePartialAstAtOffset(), node.getPosition().getStartOffset(), new INodeAcceptor() {
				
				public boolean doesAccept(Node node) {
					return node instanceof MethodDefNode;
				}
			
			});
			if (spanner != null) fInMethodBody = true;
		}
		return fInMethodBody;
	}

	private Node getStartNode() {
		if (fSelectedNodes != null && fSelectedNodes.length > 0)
			return fSelectedNodes[0];
		else
			return fCoveringNode;
	}
}
