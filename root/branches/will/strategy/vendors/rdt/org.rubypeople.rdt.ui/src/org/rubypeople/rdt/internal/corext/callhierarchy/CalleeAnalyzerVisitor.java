package org.rubypeople.rdt.internal.corext.callhierarchy;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.jruby.ast.CallNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.ast.VCallNode;
import org.jruby.evaluator.Instruction;
import org.jruby.lexer.yacc.IDESourcePosition;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.parser.InOrderVisitor;
import org.rubypeople.rdt.internal.core.util.ASTUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

class CalleeAnalyzerVisitor extends InOrderVisitor {

	private IMethod fMethod;
	private CallSearchResultCollector fSearchResults;
	private IProgressMonitor fProgressMonitor;
	private int fMethodStartPosition;
	private int fMethodEndPosition;

	public CalleeAnalyzerVisitor(IMethod method, IProgressMonitor progressMonitor) {
        fSearchResults = new CallSearchResultCollector();
        this.fMethod = method;
        this.fProgressMonitor = progressMonitor;

        try {
            ISourceRange sourceRange = method.getSourceRange();
            this.fMethodStartPosition = sourceRange.getOffset();
            this.fMethodEndPosition = fMethodStartPosition + sourceRange.getLength();
        } catch (RubyModelException jme) {
            RubyPlugin.log(jme);
        }
	}
	
	private void addMethodCall(ISourcePosition pos) {
		int offset = pos.getStartOffset();
		int endOffset = pos.getEndOffset();
		int length = endOffset - offset;
		try {					
			IRubyElement[] elements = fMethod.getRubyScript().codeSelect(offset, length);
			if (elements == null) return; // FIXME Only take first, what do we do?
			for (int i = 0; i < elements.length; i++) {
				if (elements[i] instanceof IMember) {
					IMember member = (IMember) elements[i];
					fSearchResults.addMember(fMethod, member, offset, endOffset, pos.getStartLine());
				}
			}
		} catch (RubyModelException e) {
			RubyPlugin.log(e);
		}
	}

    /**
     * Method getCallees.
     *
     * @return CallerElement
     */
    public Map getCallees() {
        return fSearchResults.getCallers();
    }
    
    // FIXME When visiting types, check to see if we even need to traverse into the type...
    
    @Override
    public Instruction visitVCallNode(VCallNode iVisited) {
    	if (isNodeWithinMethod(iVisited)) {
    		addMethodCall(iVisited.getPosition());
    	}
    	return super.visitVCallNode(iVisited);
    }
    
    @Override
    public Instruction visitFCallNode(FCallNode iVisited) {
    	if (isNodeWithinMethod(iVisited)) {
    		addMethodCall(iVisited.getPosition());// FIXME Only look up the hierarchy for the resolution
    	}
    	return super.visitFCallNode(iVisited);
    }
    
    @Override
    public Instruction visitCallNode(CallNode iVisited) {
    	if (isNodeWithinMethod(iVisited)) {
    		if (iVisited.getName().equals("[]"))return super.visitCallNode(iVisited);
    		String receiver = ASTUtil.stringRepresentation(iVisited.getReceiverNode());
    		ISourcePosition original = iVisited.getPosition();
    		int start = original.getStartOffset() + receiver.length() + 1;
    		ISourcePosition pos = new IDESourcePosition(original.getFile(), original.getStartLine(), original.getEndLine(), start, original.getEndOffset());
    		addMethodCall(pos);
    	}
    	return super.visitCallNode(iVisited);
    }
    
    private boolean isNodeWithinMethod(Node node) {
        int nodeStartPosition = node.getPosition().getStartOffset();
        int nodeEndPosition = node.getPosition().getEndOffset();

        if (nodeStartPosition < fMethodStartPosition) {
            return false;
        }

        if (nodeEndPosition > fMethodEndPosition) {
            return false;
        }

        return true;
    }
    
    @Override
    public Instruction visitDefnNode(DefnNode iVisited) {
    	 progressMonitorWorked(1);
    	return super.visitDefnNode(iVisited);
    }
    
    @Override
    public Instruction visitDefsNode(DefsNode iVisited) {
    	 progressMonitorWorked(1);
    	return super.visitDefsNode(iVisited);
    }
    
    @Override
    public Instruction visitClassNode(ClassNode iVisited) {
    	 progressMonitorWorked(1);
    	return super.visitClassNode(iVisited);
    }
    
    @Override
    public Instruction visitModuleNode(ModuleNode iVisited) {
    	 progressMonitorWorked(1);
    	return super.visitModuleNode(iVisited);
    }
    
    private void progressMonitorWorked(int work) {
        if (fProgressMonitor != null) {
            fProgressMonitor.worked(work);
            if (fProgressMonitor.isCanceled()) {
                throw new OperationCanceledException();
            }
        }
    }

}
