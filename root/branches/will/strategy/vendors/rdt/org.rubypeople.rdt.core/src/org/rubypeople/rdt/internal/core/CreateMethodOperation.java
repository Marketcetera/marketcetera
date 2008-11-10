package org.rubypeople.rdt.internal.core;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.NewlineNode;
import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModelStatus;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.util.ASTRewrite;
import org.rubypeople.rdt.internal.core.util.ASTUtil;
import org.rubypeople.rdt.internal.core.util.Messages;

public class CreateMethodOperation extends RubyModelOperation {

	/**
	 * A constant meaning to position the new element
	 * as the last child of its parent element.
	 */
	protected static final int INSERT_LAST = 1;
	/**
	 * A constant meaning to position the new element
	 * after the element defined by <code>fAnchorElement</code>.
	 */
	protected static final int INSERT_AFTER = 2;	
	/**
	 * A constant meaning to position the new element
	 * before the element defined by <code>fAnchorElement</code>.
	 */
	protected static final int INSERT_BEFORE = 3;
	/**
	 * One of the position constants, describing where
	 * to position the newly created element.
	 */
	protected int insertionPolicy = INSERT_LAST;
	/**
	 * The element that the newly created element is
	 * positioned relative to, as described by
	 * <code>fInsertPosition</code>, or <code>null</code>
	 * if the newly created element will be positioned
	 * last.
	 */
	protected IRubyElement anchorElement = null;
	/**
	 * A flag indicating whether creation of a new element occurred.
	 * A request for creating a duplicate element would request in this
	 * flag being set to <code>false</code>. Ensures that no deltas are generated
	 * when creation does not occur.
	 */
	protected boolean creationOccurred = true;
	
	private String source;
	private Node cuAST;
	private Node createdNode;
	private String[] parameters;

	public CreateMethodOperation(IType parentElement, String source, boolean force) {
		super(null, new IRubyElement[]{parentElement}, force);
		this.source = source;
	}

	@Override
	protected void executeOperation() throws RubyModelException {
		try {
			beginTask(getMainTaskName(), getMainAmountOfWork());
			RubyElementDelta delta = newRubyElementDelta();
			IRubyScript unit = getRubyScript();
			generateNewRubyScriptAST(unit);
			if (this.creationOccurred) {
				//a change has really occurred
				unit.save(null, false);
				boolean isWorkingCopy = unit.isWorkingCopy();
				if (!isWorkingCopy)
					this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE);
				worked(1);
				resultElements = generateResultHandles();
				if (!isWorkingCopy // if unit is working copy, then save will have already fired the delta
						&& unit.getParent().exists()) {
					for (int i = 0; i < resultElements.length; i++) {
						delta.added(resultElements[i]);
					}
					addDelta(delta);
				} // else unit is created outside classpath
				  // non-ruby resource delta will be notified by delta processor
			}
		} finally {
			done();
		}

	}
	
	/**
	 * Returns the IType the member is to be created in.
	 */
	protected IType getType() {
		return (IType)getParentElement();
	}
	
	/**
	 * @see CreateElementInCUOperation#generateResultHandle
	 */
	protected IRubyElement generateResultHandle() {
		String[] types = convertASTMethodTypesToSignatures();
		String name = getASTNodeName();
		return getType().getMethod(name, types);
	}
	
	private String getASTNodeName() {
		if (this.createdNode instanceof DefsNode)
		  return ((DefsNode) this.createdNode).getName();
		return ((DefnNode) this.createdNode).getName();
	}
	
	/**
	 * Returns the type signatures of the parameter types of the
	 * current <code>MethodDeclaration</code>
	 */
	protected String[] convertASTMethodTypesToSignatures() {
		if (this.parameters == null) {
			if (this.createdNode != null) {
				DefnNode methodDeclaration = (DefnNode) this.createdNode;
				this.parameters = ASTUtil.getArgs(methodDeclaration.getArgsNode(), methodDeclaration.getScope());
			}
		}
		return this.parameters;
	}
	

	
	/**
	 * Creates and returns the handles for the elements this operation created.
	 */
	protected IRubyElement[] generateResultHandles() {
		return new IRubyElement[]{generateResultHandle()};
	}
	
	/**
	 * Returns the ruby script in which the new element is being created.
	 */
	protected IRubyScript getRubyScript() {
		return getRubyScriptFor(getParentElement());
	}
	
	/**
	 * @see CreateElementInCUOperation#getMainTaskName()
	 */
	public String getMainTaskName(){
		return Messages.operation_createMethodProgress; 
	}
	
	/**
	 * Returns the amount of work for the main task of this operation for
	 * progress reporting.
	 */
	protected int getMainAmountOfWork(){
		return 2;
	}

	/**
	 * Instructs this operation to position the new element before
	 * the given sibling, or to add the new element as the last child
	 * of its parent if <code>null</code>.
	 */
	public void createBefore(IRubyElement sibling) {
		setRelativePosition(sibling, INSERT_BEFORE);
	}

	/**
	 * Instructs this operation to position the new element relative
	 * to the given sibling, or to add the new element as the last child
	 * of its parent if <code>null</code>. The <code>position</code>
	 * must be one of the position constants.
	 */
	protected void setRelativePosition(IRubyElement sibling, int policy) throws IllegalArgumentException {
		if (sibling == null) {
			this.anchorElement = null;
			this.insertionPolicy = INSERT_LAST;
		} else {
			this.anchorElement = sibling;
			this.insertionPolicy = policy;
		}
	}
	
	/*
	 * Generates a new AST for this operation and applies it to the given cu
	 */
	protected void generateNewRubyScriptAST(IRubyScript cu) throws RubyModelException {
		this.cuAST = parse(cu);
		IDocument document = getDocument(cu);
		ASTRewrite rewriter = ASTRewrite.create(this.cuAST, document);
		
		Node child = generateElementAST(document, cu);
		if (child != null) {
			Node parent = ((RubyElement) getParentElement()).findNode(this.cuAST);
			if (parent == null)
				parent = this.cuAST;
			insertASTNode(rewriter, parent, child);
			apply(rewriter, document);
		}
		worked(1);
	}
	
	private void insertASTNode(ASTRewrite rewriter, Node parent, Node child) {
		switch (this.insertionPolicy) {
			case INSERT_BEFORE:
				Node element = ((RubyElement) this.anchorElement).findNode(this.cuAST);
				rewriter.insertBefore(source, child, element, null);
			case INSERT_AFTER:
				element = ((RubyElement) this.anchorElement).findNode(this.cuAST);
				rewriter.insertAfter(source, child, element, null);
			case INSERT_LAST:
				rewriter.insertLast(source, child, null);
				break;
		}		
	}

	private Node generateElementAST(IDocument document, IRubyScript cu) {
		RubyParser parser = new RubyParser();		
		Node root = parser.parse(this.source).getAST();
		this.createdNode = ((NewlineNode) ((RootNode) root).getBodyNode()).getNextNode(); // Grab node from our parsed source!
		return this.createdNode;
	}

	protected Node parse(IRubyScript cu) throws RubyModelException {
		// ensure cu is consistent (noop if already consistent)
		cu.makeConsistent(this.progressMonitor);
		// create an AST for the ruby script
		RubyParser parser = new RubyParser();
		return parser.parse(cu.getSource()).getAST();
	}
	
	protected void apply(ASTRewrite rewriter, IDocument document) throws RubyModelException {
		TextEdit edits = rewriter.rewriteAST(document, null);
 		try {
	 		edits.apply(document);
 		} catch (BadLocationException e) {
 			throw new RubyModelException(e, IRubyModelStatusConstants.INVALID_CONTENTS);
 		}
	}
	
	/**
	 * Possible failures: <ul>
	 *  <li>NO_ELEMENTS_TO_PROCESS - the compilation unit supplied to the operation is
	 * 		<code>null</code>.
	 *  <li>INVALID_NAME - no name, a name was null or not a valid
	 * 		import declaration name.
	 *  <li>INVALID_SIBLING - the sibling provided for positioning is not valid.
	 * </ul>
	 * @see IRubyModelStatus
	 */
	public IRubyModelStatus verify() {
		if (getParentElement() == null) {
			return new RubyModelStatus(IRubyModelStatusConstants.NO_ELEMENTS_TO_PROCESS);
		}
		if (this.anchorElement != null) {
			IRubyElement domPresentParent = this.anchorElement.getParent();
			if (!domPresentParent.equals(getParentElement())) {
				return new RubyModelStatus(IRubyModelStatusConstants.INVALID_SIBLING, this.anchorElement);
			}
		}
		return RubyModelStatus.VERIFIED_OK;
	}
	
}
