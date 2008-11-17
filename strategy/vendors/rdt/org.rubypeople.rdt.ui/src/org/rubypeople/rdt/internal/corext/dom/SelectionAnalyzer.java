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
package org.rubypeople.rdt.internal.corext.dom;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.jruby.ast.Node;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.internal.core.parser.InOrderVisitor;

import com.sun.org.apache.xpath.internal.Expression;


/**
 * Maps a selection to a set of AST nodes.
 */
public class SelectionAnalyzer extends InOrderVisitor {
	
	private Selection fSelection;
	private boolean fTraverseSelectedNode;
	private Node fLastCoveringNode;
	
	// Selected nodes
	private List fSelectedNodes;
	
	public SelectionAnalyzer(Selection selection, boolean traverseSelectedNode) {
		Assert.isNotNull(selection);
		fSelection= selection;
		fTraverseSelectedNode= traverseSelectedNode;
	}
	
	public boolean hasSelectedNodes() {
		return fSelectedNodes != null && !fSelectedNodes.isEmpty();
	}
	
	public Node[] getSelectedNodes() {
		if (fSelectedNodes == null || fSelectedNodes.isEmpty())
			return new Node[0];
		return (Node[]) fSelectedNodes.toArray(new Node[fSelectedNodes.size()]);
	}
	
	public Node getFirstSelectedNode() {
		if (fSelectedNodes == null || fSelectedNodes.isEmpty())
			return null;
		return (Node)fSelectedNodes.get(0);
	}
	
	public Node getLastSelectedNode() {
		if (fSelectedNodes == null || fSelectedNodes.isEmpty())
			return null;
		return (Node)fSelectedNodes.get(fSelectedNodes.size() - 1);
	}
	
	public boolean isExpressionSelected() {
		if (!hasSelectedNodes())
			return false;
		return fSelectedNodes.get(0) instanceof Expression;
	}
	
	public IRegion getSelectedNodeRange() {
		if (fSelectedNodes == null || fSelectedNodes.isEmpty())
			return null;
		Node firstNode= (Node)fSelectedNodes.get(0);
		Node lastNode= (Node)fSelectedNodes.get(fSelectedNodes.size() - 1);
		int start= firstNode.getPosition().getStartOffset();
		return new Region(start, lastNode.getPosition().getEndOffset() - start);
	}
	
	public Node getLastCoveringNode() {
		return fLastCoveringNode;
	}
	
	protected Selection getSelection() {
		return fSelection;
	}
	
	//--- node management ---------------------------------------------------------
	
	protected Instruction visitNode(Node node) {
		// The selection lies behind the node.
		if (fSelection.liesOutside(node)) {
			return null;
		} else if (fSelection.covers(node)) {
			if (isFirstNode()) {
				handleFirstSelectedNode(node);
			} else {
				handleNextSelectedNode(node);
			}
			return null;
		} else if (fSelection.coveredBy(node)) {
			fLastCoveringNode= node;
			return null;
		} else if (fSelection.endsIn(node)) {
			handleSelectionEndsIn(node);
			return null;
		}
		// There is a possibility that the user has selected trailing semicolons that don't belong
		// to the statement. So dive into it to check if sub nodes are fully covered.
		return null;
	}
	
	protected void reset() {
		fSelectedNodes= null;
	}
	
	protected void handleFirstSelectedNode(Node node) {
		fSelectedNodes= new ArrayList(5);
		fSelectedNodes.add(node);
	}
	
	protected void handleNextSelectedNode(Node node) {
//		if (getFirstSelectedNode().getParent() == node.getParent()) { // FIXME If the selected node shares common parent, with previosu selected node, then add it
			fSelectedNodes.add(node);
//		}
	}

	protected boolean handleSelectionEndsIn(Node node) {
		return false;
	}
	
	protected List internalGetSelectedNodes() {
		return fSelectedNodes;
	}
	
	private boolean isFirstNode() {
		return fSelectedNodes == null;
	}	
}
