package org.rubypeople.rdt.core.parser.warnings;

import java.util.ArrayList;
import java.util.List;

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
import org.jruby.ast.NotNode;
import org.jruby.ast.NthRefNode;
import org.jruby.ast.OpAsgnAndNode;
import org.jruby.ast.OpAsgnNode;
import org.jruby.ast.OpAsgnOrNode;
import org.jruby.ast.OpElementAsgnNode;
import org.jruby.ast.OrNode;
import org.jruby.ast.PostExeNode;
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
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.internal.core.parser.InOrderVisitor;
import org.rubypeople.rdt.internal.core.parser.warnings.ConstantReassignmentVisitor;
import org.rubypeople.rdt.internal.core.parser.warnings.CoreClassReOpening;
import org.rubypeople.rdt.internal.core.parser.warnings.EmptyStatementVisitor;
import org.rubypeople.rdt.internal.core.parser.warnings.Ruby19HashCommaSyntax;
import org.rubypeople.rdt.internal.core.parser.warnings.Ruby19WhenStatements;

/**
 * <p>DelegatingVisitor takes a list of visitors, traverse the AST in order, and at
 * each node calls the correct visitXXXNode method on every visitor. This allows
 * us to traverse the AST only once while having X number of visitors operate on
 * it.</p>
 * 
 * <p>Right now it is customized to RubyLintVisitors, which is the abstract
 * base class for all visitors that do coce analysis for Error/Warning markers.</p>
 * 
 * @author Christopher Williams
 * 
 */
public class DelegatingVisitor extends InOrderVisitor {

	private List<RubyLintVisitor> visitors;
	
	public static List<RubyLintVisitor> createVisitors(IRubyScript script, String contents) {
		List<RubyLintVisitor> visitors = new ArrayList<RubyLintVisitor>();
		visitors.add(new EmptyStatementVisitor(contents));
		visitors.add(new ConstantReassignmentVisitor(contents));
		if (script != null) {
			visitors.add(new CoreClassReOpening(script, contents));
		}
		visitors.add(new Ruby19WhenStatements(contents));
		visitors.add(new Ruby19HashCommaSyntax(contents));
		List<RubyLintVisitor> filtered = new ArrayList<RubyLintVisitor>();
		for (RubyLintVisitor visitor : visitors) {
			if (visitor.isIgnored()) continue;
			filtered.add(visitor);
		}
		return filtered;
	}
	
	public List<CategorizedProblem> getProblems() {
		List<CategorizedProblem> problems = new ArrayList<CategorizedProblem>();
		for (RubyLintVisitor visitor : visitors) {
			problems.addAll(visitor.getProblems());
		}
		return problems;
	}

	public DelegatingVisitor(List<RubyLintVisitor> visitors) {
		this.visitors = visitors;
	}

	@Override
	public Instruction visitAliasNode(AliasNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitAliasNode(iVisited);
		}
		return super.visitAliasNode(iVisited);
	}
	
	@Override
	public Instruction visitAndNode(AndNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitAndNode(iVisited);
		}
		return super.visitAndNode(iVisited);
	}

	@Override
	public Instruction visitArgsCatNode(ArgsCatNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitArgsCatNode(iVisited);
		}
		return super.visitArgsCatNode(iVisited);
	}

	@Override
	public Instruction visitArgsNode(ArgsNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitArgsNode(iVisited);
		}
		Instruction ins = super.visitArgsNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitArgsNode(iVisited);
		}
		return ins;
	}

	@Override
	public Instruction visitArgsPushNode(ArgsPushNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitArgsPushNode(iVisited);
		}
		return super.visitArgsPushNode(iVisited);
	}

	@Override
	public Instruction visitArrayNode(ArrayNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitArrayNode(iVisited);
		}
		return super.visitArrayNode(iVisited);
	}

	@Override
	public Instruction visitAttrAssignNode(AttrAssignNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitAttrAssignNode(iVisited);
		}
		return super.visitAttrAssignNode(iVisited);
	}

	@Override
	public Instruction visitBackRefNode(BackRefNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitBackRefNode(iVisited);
		}
		return super.visitBackRefNode(iVisited);
	}

	@Override
	public Instruction visitBeginNode(BeginNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitBeginNode(iVisited);
		}
		return super.visitBeginNode(iVisited);
	}

	@Override
	public Instruction visitBignumNode(BignumNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitBignumNode(iVisited);
		}
		return super.visitBignumNode(iVisited);
	}

	@Override
	public Instruction visitBlockArgNode(BlockArgNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitBlockArgNode(iVisited);
		}
		return super.visitBlockArgNode(iVisited);
	}

	@Override
	public Instruction visitBlockNode(BlockNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitBlockNode(iVisited);
		}
		Instruction ins = super.visitBlockNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitBlockNode(iVisited);
		}
		return ins;
	}

	@Override
	public Instruction visitBlockPassNode(BlockPassNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitBlockPassNode(iVisited);
		}
		return super.visitBlockPassNode(iVisited);
	}

	@Override
	public Instruction visitBreakNode(BreakNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitBreakNode(iVisited);
		}
		return super.visitBreakNode(iVisited);
	}

	@Override
	public Instruction visitCallNode(CallNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitCallNode(iVisited);
		}
		return super.visitCallNode(iVisited);
	}

	@Override
	public Instruction visitCaseNode(CaseNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitCaseNode(iVisited);
		}
		return super.visitCaseNode(iVisited);
	}

	@Override
	public Instruction visitClassNode(ClassNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitClassNode(iVisited);
		}
		Instruction ins = super.visitClassNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitClassNode(iVisited);
		}
		return ins;
	}

	@Override
	public Instruction visitClassVarAsgnNode(ClassVarAsgnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitClassVarAsgnNode(iVisited);
		}
		return super.visitClassVarAsgnNode(iVisited);
	}

	@Override
	public Instruction visitClassVarDeclNode(ClassVarDeclNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitClassVarDeclNode(iVisited);
		}
		return super.visitClassVarDeclNode(iVisited);
	}

	@Override
	public Instruction visitClassVarNode(ClassVarNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitClassVarNode(iVisited);
		}
		return super.visitClassVarNode(iVisited);
	}

	@Override
	public Instruction visitColon2Node(Colon2Node iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitColon2Node(iVisited);
		}
		return super.visitColon2Node(iVisited);
	}

	@Override
	public Instruction visitColon3Node(Colon3Node iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitColon3Node(iVisited);
		}
		return super.visitColon3Node(iVisited);
	}

	@Override
	public Instruction visitConstDeclNode(ConstDeclNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitConstDeclNode(iVisited);
		}
		return super.visitConstDeclNode(iVisited);
	}

	@Override
	public Instruction visitConstNode(ConstNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitConstNode(iVisited);
		}
		return super.visitConstNode(iVisited);
	}

	@Override
	public Instruction visitDAsgnNode(DAsgnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDAsgnNode(iVisited);
		}
		return super.visitDAsgnNode(iVisited);
	}

	@Override
	public Instruction visitDefinedNode(DefinedNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDefinedNode(iVisited);
		}
		return super.visitDefinedNode(iVisited);
	}

	@Override
	public Instruction visitDefnNode(DefnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDefnNode(iVisited);
		}
		Instruction ins = super.visitDefnNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitDefnNode(iVisited);
		}
		return ins;
	}

	@Override
	public Instruction visitDefsNode(DefsNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDefsNode(iVisited);
		}
		Instruction ins = super.visitDefsNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitDefsNode(iVisited);
		}
		return ins;
	}


	@Override
	public Instruction visitDotNode(DotNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDotNode(iVisited);
		}
		return super.visitDotNode(iVisited);
	}

	@Override
	public Instruction visitDRegxNode(DRegexpNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDRegxNode(iVisited);
		}
		return super.visitDRegxNode(iVisited);
	}

	@Override
	public Instruction visitDStrNode(DStrNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDStrNode(iVisited);
		}
		return super.visitDStrNode(iVisited);
	}

	@Override
	public Instruction visitDSymbolNode(DSymbolNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDSymbolNode(iVisited);
		}
		return super.visitDSymbolNode(iVisited);
	}

	@Override
	public Instruction visitDVarNode(DVarNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDVarNode(iVisited);
		}
		return super.visitDVarNode(iVisited);
	}

	@Override
	public Instruction visitDXStrNode(DXStrNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDXStrNode(iVisited);
		}
		return super.visitDXStrNode(iVisited);
	}

	@Override
	public Instruction visitEnsureNode(EnsureNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitEnsureNode(iVisited);
		}
		return super.visitEnsureNode(iVisited);
	}

	@Override
	public Instruction visitEvStrNode(EvStrNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitEvStrNode(iVisited);
		}
		return super.visitEvStrNode(iVisited);
	}

	@Override
	public Instruction visitFalseNode(FalseNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitFalseNode(iVisited);
		}
		return super.visitFalseNode(iVisited);
	}

	@Override
	public Instruction visitFCallNode(FCallNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitFCallNode(iVisited);
		}
		return super.visitFCallNode(iVisited);
	}

	@Override
	public Instruction visitFixnumNode(FixnumNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitFixnumNode(iVisited);
		}
		return super.visitFixnumNode(iVisited);
	}

	@Override
	public Instruction visitFlipNode(FlipNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitFlipNode(iVisited);
		}
		return super.visitFlipNode(iVisited);
	}

	@Override
	public Instruction visitFloatNode(FloatNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitFloatNode(iVisited);
		}
		return super.visitFloatNode(iVisited);
	}

	@Override
	public Instruction visitForNode(ForNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitForNode(iVisited);
		}
		return super.visitForNode(iVisited);
	}

	@Override
	public Instruction visitGlobalAsgnNode(GlobalAsgnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitGlobalAsgnNode(iVisited);
		}
		return super.visitGlobalAsgnNode(iVisited);
	}

	@Override
	public Instruction visitGlobalVarNode(GlobalVarNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitGlobalVarNode(iVisited);
		}
		return super.visitGlobalVarNode(iVisited);
	}

	@Override
	public Instruction visitHashNode(HashNode iVisited) {	
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitHashNode(iVisited);
		}
		Instruction ins = super.visitHashNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitHashNode(iVisited);
		}
		return ins;
	}

	@Override
	public Instruction visitIfNode(IfNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitIfNode(iVisited);
		}
		Instruction ins = super.visitIfNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitIfNode(iVisited);
		}
		return ins;
	}

	@Override
	public Instruction visitInstAsgnNode(InstAsgnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitInstAsgnNode(iVisited);
		}
		return super.visitInstAsgnNode(iVisited);
	}

	@Override
	public Instruction visitInstVarNode(InstVarNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitInstVarNode(iVisited);
		}
		return super.visitInstVarNode(iVisited);
	}

	@Override
	public Instruction visitIterNode(IterNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitIterNode(iVisited);
		}
		return super.visitIterNode(iVisited);
	}

	@Override
	public Instruction visitLocalAsgnNode(LocalAsgnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitLocalAsgnNode(iVisited);
		}
		return super.visitLocalAsgnNode(iVisited);
	}

	@Override
	public Instruction visitLocalVarNode(LocalVarNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitLocalVarNode(iVisited);
		}
		return super.visitLocalVarNode(iVisited);
	}

	@Override
	public Instruction visitMatch2Node(Match2Node iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitMatch2Node(iVisited);
		}
		return super.visitMatch2Node(iVisited);
	}

	@Override
	public Instruction visitMatch3Node(Match3Node iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitMatch3Node(iVisited);
		}
		return super.visitMatch3Node(iVisited);
	}

	@Override
	public Instruction visitMatchNode(MatchNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitMatchNode(iVisited);
		}
		return super.visitMatchNode(iVisited);
	}

	@Override
	public Instruction visitModuleNode(ModuleNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitModuleNode(iVisited);
		}
		Instruction ins = super.visitModuleNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitModuleNode(iVisited);
		}
		return ins;
	}

	@Override
	public Instruction visitMultipleAsgnNode(MultipleAsgnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitMultipleAsgnNode(iVisited);
		}
		return super.visitMultipleAsgnNode(iVisited);
	}

	@Override
	public Instruction visitNewlineNode(NewlineNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitNewlineNode(iVisited);
		}
		return super.visitNewlineNode(iVisited);
	}

	@Override
	public Instruction visitNextNode(NextNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitNextNode(iVisited);
		}
		return super.visitNextNode(iVisited);
	}

	@Override
	public Instruction visitNilNode(NilNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitNilNode(iVisited);
		}
		return super.visitNilNode(iVisited);
	}

	@Override
	public Instruction visitNotNode(NotNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitNotNode(iVisited);
		}
		return super.visitNotNode(iVisited);
	}

	@Override
	public Instruction visitNthRefNode(NthRefNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitNthRefNode(iVisited);
		}
		return super.visitNthRefNode(iVisited);
	}

	@Override
	public Instruction visitOpAsgnAndNode(OpAsgnAndNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitOpAsgnAndNode(iVisited);
		}
		return super.visitOpAsgnAndNode(iVisited);
	}

	@Override
	public Instruction visitOpAsgnNode(OpAsgnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitOpAsgnNode(iVisited);
		}
		return super.visitOpAsgnNode(iVisited);
	}

	@Override
	public Instruction visitOpAsgnOrNode(OpAsgnOrNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitOpAsgnOrNode(iVisited);
		}
		return super.visitOpAsgnOrNode(iVisited);
	}

	@Override
	public Instruction visitOpElementAsgnNode(OpElementAsgnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitOpElementAsgnNode(iVisited);
		}
		return super.visitOpElementAsgnNode(iVisited);
	}

	@Override
	public Instruction visitOrNode(OrNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitOrNode(iVisited);
		}
		return super.visitOrNode(iVisited);
	}

	@Override
	public Instruction visitPostExeNode(PostExeNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitPostExeNode(iVisited);
		}
		return super.visitPostExeNode(iVisited);
	}

	@Override
	public Instruction visitRedoNode(RedoNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitRedoNode(iVisited);
		}
		return super.visitRedoNode(iVisited);
	}

	@Override
	public Instruction visitRegexpNode(RegexpNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitRegexpNode(iVisited);
		}
		return super.visitRegexpNode(iVisited);
	}

	@Override
	public Instruction visitRescueBodyNode(RescueBodyNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitRescueBodyNode(iVisited);
		}
		Instruction ins = super.visitRescueBodyNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitRescueBodyNode(iVisited);
		}
		return ins;
	}

	@Override
	public Instruction visitRescueNode(RescueNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitRescueNode(iVisited);
		}
		return super.visitRescueNode(iVisited);
	}

	@Override
	public Instruction visitRetryNode(RetryNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitRetryNode(iVisited);
		}
		return super.visitRetryNode(iVisited);
	}

	@Override
	public Instruction visitReturnNode(ReturnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitReturnNode(iVisited);
		}
		return super.visitReturnNode(iVisited);
	}

	@Override
	public Instruction visitRootNode(RootNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitRootNode(iVisited);
		}
		return super.visitRootNode(iVisited);
	}

	@Override
	public Instruction visitSClassNode(SClassNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitSClassNode(iVisited);
		}
		Instruction ins = super.visitSClassNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitSClassNode(iVisited);
		}
		return ins;
	}

	@Override
	public Instruction visitSelfNode(SelfNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitSelfNode(iVisited);
		}
		return super.visitSelfNode(iVisited);
	}

	@Override
	public Instruction visitSplatNode(SplatNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitSplatNode(iVisited);
		}
		return super.visitSplatNode(iVisited);
	}

	@Override
	public Instruction visitStrNode(StrNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitStrNode(iVisited);
		}
		return super.visitStrNode(iVisited);
	}

	@Override
	public Instruction visitSuperNode(SuperNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitSuperNode(iVisited);
		}
		return super.visitSuperNode(iVisited);
	}

	@Override
	public Instruction visitSValueNode(SValueNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitSValueNode(iVisited);
		}
		return super.visitSValueNode(iVisited);
	}

	@Override
	public Instruction visitSymbolNode(SymbolNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitSymbolNode(iVisited);
		}
		return super.visitSymbolNode(iVisited);
	}

	@Override
	public Instruction visitToAryNode(ToAryNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitToAryNode(iVisited);
		}
		return super.visitToAryNode(iVisited);
	}

	@Override
	public Instruction visitTrueNode(TrueNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitTrueNode(iVisited);
		}
		return super.visitTrueNode(iVisited);
	}

	@Override
	public Instruction visitUndefNode(UndefNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitUndefNode(iVisited);
		}
		return super.visitUndefNode(iVisited);
	}

	@Override
	public Instruction visitUntilNode(UntilNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitUntilNode(iVisited);
		}
		return super.visitUntilNode(iVisited);
	}

	@Override
	public Instruction visitVAliasNode(VAliasNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitVAliasNode(iVisited);
		}
		return super.visitVAliasNode(iVisited);
	}

	@Override
	public Instruction visitVCallNode(VCallNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitVCallNode(iVisited);
		}
		return super.visitVCallNode(iVisited);
	}

	@Override
	public Instruction visitWhenNode(WhenNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitWhenNode(iVisited);
		}
		Instruction ins = super.visitWhenNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitWhenNode(iVisited);
		}
		return ins;
	}

	@Override
	public Instruction visitWhileNode(WhileNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitWhileNode(iVisited);
		}
		return super.visitWhileNode(iVisited);
	}

	@Override
	public Instruction visitXStrNode(XStrNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitXStrNode(iVisited);
		}
		return super.visitXStrNode(iVisited);
	}

	@Override
	public Instruction visitYieldNode(YieldNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitYieldNode(iVisited);
		}
		return super.visitYieldNode(iVisited);
	}

	@Override
	public Instruction visitZArrayNode(ZArrayNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitZArrayNode(iVisited);
		}
		return super.visitZArrayNode(iVisited);
	}

	@Override
	public Instruction visitZSuperNode(ZSuperNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitZSuperNode(iVisited);
		}
		return super.visitZSuperNode(iVisited);
	}
}