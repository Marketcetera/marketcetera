package org.rubypeople.rdt.internal.core.util;

import java.util.Iterator;

import org.jruby.ast.AliasNode;
import org.jruby.ast.AndNode;
import org.jruby.ast.ArgsCatNode;
import org.jruby.ast.ArgsNode;
import org.jruby.ast.ArgsPushNode;
import org.jruby.ast.ArrayNode;
import org.jruby.ast.AttrAssignNode;
import org.jruby.ast.BackRefNode;
import org.jruby.ast.BeginNode;
import org.jruby.ast.BignumNode;
import org.jruby.ast.BlockArgNode;
import org.jruby.ast.BlockNode;
import org.jruby.ast.BlockPassNode;
import org.jruby.ast.BreakNode;
import org.jruby.ast.CallNode;
import org.jruby.ast.CaseNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.ClassVarDeclNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.Colon2Node;
import org.jruby.ast.Colon3Node;
import org.jruby.ast.ConstDeclNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DAsgnNode;
import org.jruby.ast.DRegexpNode;
import org.jruby.ast.DStrNode;
import org.jruby.ast.DSymbolNode;
import org.jruby.ast.DVarNode;
import org.jruby.ast.DXStrNode;
import org.jruby.ast.DefinedNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.DotNode;
import org.jruby.ast.EnsureNode;
import org.jruby.ast.EvStrNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.FalseNode;
import org.jruby.ast.FixnumNode;
import org.jruby.ast.FlipNode;
import org.jruby.ast.FloatNode;
import org.jruby.ast.ForNode;
import org.jruby.ast.GlobalAsgnNode;
import org.jruby.ast.GlobalVarNode;
import org.jruby.ast.HashNode;
import org.jruby.ast.IfNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.IterNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.Match2Node;
import org.jruby.ast.Match3Node;
import org.jruby.ast.MatchNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.MultipleAsgnNode;
import org.jruby.ast.NewlineNode;
import org.jruby.ast.NextNode;
import org.jruby.ast.NilNode;
import org.jruby.ast.Node;
import org.jruby.ast.NotNode;
import org.jruby.ast.NthRefNode;
import org.jruby.ast.OpAsgnAndNode;
import org.jruby.ast.OpAsgnNode;
import org.jruby.ast.OpAsgnOrNode;
import org.jruby.ast.OpElementAsgnNode;
import org.jruby.ast.OrNode;
import org.jruby.ast.PostExeNode;
import org.jruby.ast.PreExeNode;
import org.jruby.ast.RedoNode;
import org.jruby.ast.RegexpNode;
import org.jruby.ast.RescueBodyNode;
import org.jruby.ast.RescueNode;
import org.jruby.ast.RetryNode;
import org.jruby.ast.ReturnNode;
import org.jruby.ast.RootNode;
import org.jruby.ast.SClassNode;
import org.jruby.ast.SValueNode;
import org.jruby.ast.SelfNode;
import org.jruby.ast.SplatNode;
import org.jruby.ast.StrNode;
import org.jruby.ast.SuperNode;
import org.jruby.ast.SymbolNode;
import org.jruby.ast.ToAryNode;
import org.jruby.ast.TrueNode;
import org.jruby.ast.UndefNode;
import org.jruby.ast.UntilNode;
import org.jruby.ast.VAliasNode;
import org.jruby.ast.VCallNode;
import org.jruby.ast.WhenNode;
import org.jruby.ast.WhileNode;
import org.jruby.ast.XStrNode;
import org.jruby.ast.YieldNode;
import org.jruby.ast.ZArrayNode;
import org.jruby.ast.ZSuperNode;
import org.jruby.ast.visitor.NodeVisitor;
import org.jruby.evaluator.Instruction;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.SourceRefElement;

public class DOMFinder implements NodeVisitor {
	// XXX Refactor out all osrts of common code between this and RubyScriptStructureBuilder
	// XXX Think about abstracting to higher level AST (and visitor) that just deals with class/method/module/etc declarations like the JDT
	public Node foundNode = null;
	
	private Node ast;
	private SourceRefElement element;
	private int rangeStart = -1, rangeLength = 0;
	
	public DOMFinder(Node ast, SourceRefElement element) {
		this.ast = ast;
		this.element = element;
	}

	public Instruction visitAliasNode(AliasNode arg0) {
		return null;
	}

	public Instruction visitAndNode(AndNode iVisited) {
		visitNode(iVisited.getFirstNode());
		visitNode(iVisited.getSecondNode());
		return null;
	}

	public Instruction visitArgsCatNode(ArgsCatNode iVisited) {
		visitNode(iVisited.getFirstNode());
		visitNode(iVisited.getSecondNode());
		return null;
	}

	public Instruction visitArgsNode(ArgsNode iVisited) {
		visitNode(iVisited.getBlockArgNode());
		if (iVisited.getOptArgs() != null) {
			visitIter(iVisited.getOptArgs().childNodes().iterator());
		}
		return null;
	}
	
	private Instruction visitIter(Iterator iterator) {
		while (iterator.hasNext()) {
			visitNode((Node) iterator.next());
		}
		return null;
	}

	public Instruction visitArrayNode(ArrayNode iVisited) {
		visitIter(iVisited.childNodes().iterator());
		return null;
	}

	public Instruction visitBackRefNode(BackRefNode arg0) {
		return null;
	}

	public Instruction visitBeginNode(BeginNode iVisited) {
		visitNode(iVisited.getBodyNode());
		return null;
	}

	public Instruction visitBignumNode(BignumNode arg0) {
		return null;
	}

	public Instruction visitBlockArgNode(BlockArgNode arg0) {
		return null;
	}

	public Instruction visitBlockNode(BlockNode iVisited) {
		visitIter(iVisited.childNodes().iterator());
		return null;
	}

	public Instruction visitBlockPassNode(BlockPassNode iVisited) {
		visitNode(iVisited.getArgsNode());
		visitNode(iVisited.getBodyNode());
		return null;
	}

	public Instruction visitBreakNode(BreakNode iVisited) {
		visitNode(iVisited.getValueNode());
		return null;
	}

	public Instruction visitCallNode(CallNode iVisited) {
		visitNode(iVisited.getReceiverNode());
		visitNode(iVisited.getArgsNode());
		visitNode(iVisited.getIterNode());
		return null;
	}

	public Instruction visitCaseNode(CaseNode iVisited) {
		visitNode(iVisited.getCaseNode());
		visitNode(iVisited.getFirstWhenNode());
		return null;
	}

	public Instruction visitClassNode(ClassNode node) {
		String name = getFullyQualifiedName(node.getCPath());
		ISourcePosition pos = node.getPosition();
		int nameStart = pos.getStartOffset() + "class".length() + 1;
		if (!found(node, nameStart, name.length())) {
			visitNode(node.getSuperNode());
			visitNode(node.getBodyNode());
		}
		return null;
	}
	
	private Instruction visitNode(Node iVisited) {
		if (iVisited != null)
			iVisited.accept(this);
		return null;
	}
	
	private boolean found(Node node, int start, int length) {
		if (start == this.rangeStart && length == this.rangeLength) {
			this.foundNode = node;
		}
		return false;
	}

	private String getFullyQualifiedName(Node node) {
		if (node == null)
			return "";
		if (node instanceof ConstNode) {
			ConstNode constNode = (ConstNode) node;
			return constNode.getName();
		}
		if (node instanceof Colon2Node) {
			Colon2Node colonNode = (Colon2Node) node;
			String prefix = getFullyQualifiedName(colonNode.getLeftNode());
			if (prefix.length() > 0)
				prefix = prefix + "::";
			return prefix + colonNode.getName();
		}
		return "";
	}
	
	public Instruction visitClassVarAsgnNode(ClassVarAsgnNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitClassVarDeclNode(ClassVarDeclNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitClassVarNode(ClassVarNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitColon2Node(Colon2Node arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitColon3Node(Colon3Node arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitConstDeclNode(ConstDeclNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitConstNode(ConstNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitDAsgnNode(DAsgnNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitDRegxNode(DRegexpNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitDStrNode(DStrNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitDSymbolNode(DSymbolNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitDVarNode(DVarNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitDXStrNode(DXStrNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitDefinedNode(DefinedNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitDefnNode(DefnNode iVisited) {
		String name = iVisited.getName();
		ISourcePosition pos = iVisited.getPosition();
		int nameStart = pos.getStartOffset() + "def".length() + 1; 
		if (!found(iVisited, nameStart, name.length()) ) {
			visitNode(iVisited.getArgsNode());
			visitNode(iVisited.getBodyNode());
		}
		return null;
	}

	public Instruction visitDefsNode(DefsNode iVisited) {
		String name;
		String receiver = ASTUtil.stringRepresentation(iVisited.getReceiverNode());
		if (receiver != null && receiver.trim().length() > 0) {
			name = receiver + "." + iVisited.getName();
		} else {
			name = iVisited.getName();
		}
		ISourcePosition pos = iVisited.getPosition();
		int nameStart = pos.getStartOffset() + "def".length() + 1; 
		if (!found(iVisited, nameStart, name.length())) {
		  visitNode(iVisited.getReceiverNode());
		  visitNode(iVisited.getArgsNode());
		  visitNode(iVisited.getBodyNode());
		}		
		return null;
	}

	public Instruction visitDotNode(DotNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitEnsureNode(EnsureNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitEvStrNode(EvStrNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitFCallNode(FCallNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitFalseNode(FalseNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitFixnumNode(FixnumNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitFlipNode(FlipNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitFloatNode(FloatNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitForNode(ForNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitGlobalAsgnNode(GlobalAsgnNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitGlobalVarNode(GlobalVarNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitHashNode(HashNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitIfNode(IfNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitInstAsgnNode(InstAsgnNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitInstVarNode(InstVarNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitIterNode(IterNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitLocalAsgnNode(LocalAsgnNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitLocalVarNode(LocalVarNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitMatch2Node(Match2Node arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitMatch3Node(Match3Node arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitMatchNode(MatchNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitModuleNode(ModuleNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitMultipleAsgnNode(MultipleAsgnNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitNewlineNode(NewlineNode iVisited) {
		visitNode(iVisited.getNextNode());
		return null;
	}

	public Instruction visitNextNode(NextNode iVisited) {
		visitNode(iVisited.getValueNode());
		return null;
	}

	public Instruction visitNilNode(NilNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitNotNode(NotNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitNthRefNode(NthRefNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitOpAsgnAndNode(OpAsgnAndNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitOpAsgnNode(OpAsgnNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitOpAsgnOrNode(OpAsgnOrNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitOpElementAsgnNode(OpElementAsgnNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitOrNode(OrNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Instruction visitPreExeNode(PreExeNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitPostExeNode(PostExeNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitRedoNode(RedoNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitRegexpNode(RegexpNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitRescueBodyNode(RescueBodyNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitRescueNode(RescueNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitRetryNode(RetryNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitReturnNode(ReturnNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitSClassNode(SClassNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitSValueNode(SValueNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitSelfNode(SelfNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitSplatNode(SplatNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitStrNode(StrNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitSuperNode(SuperNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitSymbolNode(SymbolNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitToAryNode(ToAryNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitTrueNode(TrueNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitUndefNode(UndefNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitUntilNode(UntilNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitVAliasNode(VAliasNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitVCallNode(VCallNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitWhenNode(WhenNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitWhileNode(WhileNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitXStrNode(XStrNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitYieldNode(YieldNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitZArrayNode(ZArrayNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitZSuperNode(ZSuperNode node) {
		return null;
	}

	public Node search() throws RubyModelException {		
		ISourceRange range = null;
		if (this.element instanceof IMember)
			range = ((IMember) this.element).getNameRange();
		else
			range = this.element.getSourceRange();
		this.rangeStart = range.getOffset();
		this.rangeLength = range.getLength();
		this.ast.accept(this);
		return this.foundNode;
	}

	public Instruction visitArgsPushNode(ArgsPushNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitAttrAssignNode(AttrAssignNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitRootNode(RootNode iVisited) {
		visitNode(iVisited.getBodyNode());
		return null;
	}

}
