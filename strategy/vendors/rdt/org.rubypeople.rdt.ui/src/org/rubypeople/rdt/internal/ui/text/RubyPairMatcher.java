/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.jruby.ast.BeginNode;
import org.jruby.ast.CaseNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.ForNode;
import org.jruby.ast.IfNode;
import org.jruby.ast.IterNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.ast.SClassNode;
import org.jruby.ast.WhenNode;
import org.jruby.ast.WhileNode;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.ti.util.ClosestSpanningNodeLocator;
import org.rubypeople.rdt.internal.ti.util.INodeAcceptor;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

/**
 * Helper class for match pairs of characters.
 */
public class RubyPairMatcher implements ICharacterPairMatcher {

    protected char[] fPairs;
    protected IDocument fDocument;
    protected int fOffset;

    protected int fStartPos;
    protected int fEndPos;
    protected int fAnchor;


    public RubyPairMatcher(char[] pairs) {
        fPairs= pairs;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.source.ICharacterPairMatcher#match(org.eclipse.jface.text.IDocument, int)
     */
    public IRegion match(IDocument document, int offset) {

        fOffset= offset;

        if (fOffset < 0)
            return null;

        fDocument= document;
        
        if (fDocument != null && matchPairsAt() && fStartPos != fEndPos) // match parens
            return new Region(fStartPos, fEndPos - fStartPos + 1);

        if (fDocument != null && matchBlocksAt() && fStartPos != fEndPos) // match code blocks
            return new Region(fStartPos, fEndPos - fStartPos + 1);
        
        return null;
    }

    private boolean matchBlocksAt() {
    	fStartPos= -1;
        fEndPos= -1;
		String src = fDocument.get();
		if (src.length() == 0) {
			return false;
		}
		Node root;
		try {
			RubyParser parser = new RubyParser();
			root = parser.parse(src).getAST();
		} catch (SyntaxException e) {
			// ignore
			return false;
		} catch (RuntimeException e) {
			RubyPlugin.log(e);
			return false;
		}
		Node spanning = ClosestSpanningNodeLocator.Instance().findClosestSpanner(root, fOffset, new INodeAcceptor() {
		
			public boolean doesAccept(Node node) {
				if (node instanceof IfNode) {
					IfNode ifNode = (IfNode) node;
					// FIXME Only grab IfNode if it isn't a modifier!
				}
				return node instanceof ModuleNode || node instanceof SClassNode || node instanceof ClassNode || 
				node instanceof DefnNode || node instanceof DefsNode || node instanceof BeginNode || node instanceof WhileNode || 
				node instanceof CaseNode || node instanceof ForNode || node instanceof IfNode || node instanceof IterNode;
			}
		
		});
		if (spanning == null) return false;
		if (!isOnEnd(spanning) && !isOnBeginning(spanning)) {
			return false;
		}
		if (isOnEnd(spanning)) {
			fAnchor = RIGHT;
		} else {
			fAnchor = LEFT;
		}
		fStartPos = spanning.getPosition().getStartOffset();
		fEndPos = spanning.getPosition().getEndOffset();
		if (src.length() == fEndPos) {
			fEndPos -= 1;
		}
		return true;
	}

	private boolean isOnBeginning(Node spanning) {
		return (fOffset >= spanning.getPosition().getStartOffset()) && (fOffset <= spanning.getPosition().getStartOffset() + getKeywordLength(spanning));
	}

	private boolean isOnEnd(Node spanning) {
		return (fOffset >= spanning.getPosition().getEndOffset() - 4) && (fOffset <= spanning.getPosition().getEndOffset());
	}

	private int getKeywordLength(Node spanning) {
		if ((spanning instanceof ClassNode) || (spanning instanceof BeginNode) || (spanning instanceof WhileNode)) {
			return 5;
		}
		if ((spanning instanceof DefnNode) || (spanning instanceof DefsNode) || (spanning instanceof ForNode)) {
			return 3;
		}
		if (spanning instanceof WhenNode) {
			return 4;
		}
		if (spanning instanceof ModuleNode) {
			return 6;
		}
		if ((spanning instanceof IfNode) || (spanning instanceof IterNode)) {
			return 2;
		}
		return 1;
	}

	/* (non-Javadoc)
     * @see org.eclipse.jface.text.source.ICharacterPairMatcher#getAnchor()
     */
    public int getAnchor() {
        return fAnchor;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.source.ICharacterPairMatcher#dispose()
     */
    public void dispose() {
        clear();
        fDocument= null;
    }

    /*
     * @see org.eclipse.jface.text.source.ICharacterPairMatcher#clear()
     */
    public void clear() {
    }

    protected boolean matchPairsAt() {

        int i;
        int pairIndex1= fPairs.length;
        int pairIndex2= fPairs.length;

        fStartPos= -1;
        fEndPos= -1;

        // get the char preceding the start position
        try {

            char prevChar= fDocument.getChar(Math.max(fOffset - 1, 0));
            // search for opening peer character next to the activation point
            for (i= 0; i < fPairs.length; i= i + 2) {
                if (prevChar == fPairs[i]) {
                    fStartPos= fOffset - 1;
                    pairIndex1= i;
                }
            }

            // search for closing peer character next to the activation point
            for (i= 1; i < fPairs.length; i= i + 2) {
                if (prevChar == fPairs[i]) {
                    fEndPos= fOffset - 1;
                    pairIndex2= i;
                }
            }

            if (fEndPos > -1) {
                fAnchor= RIGHT;
                fStartPos= searchForOpeningPeer(fEndPos, fPairs[pairIndex2 - 1], fPairs[pairIndex2], fDocument);
                if (fStartPos > -1)
                    return true;
                else
                    fEndPos= -1;
            }   else if (fStartPos > -1) {
                fAnchor= LEFT;
                fEndPos= searchForClosingPeer(fStartPos, fPairs[pairIndex1], fPairs[pairIndex1 + 1], fDocument);
                if (fEndPos > -1)
                    return true;
                else
                    fStartPos= -1;
            }

        } catch (BadLocationException x) {
        }

        return false;
    }

    protected int searchForClosingPeer(int offset, char openingPeer, char closingPeer, IDocument document) throws BadLocationException {
        RubyHeuristicScanner scanner= new RubyHeuristicScanner(document, IRubyPartitions.RUBY_PARTITIONING, TextUtilities.getContentType(document, IRubyPartitions.RUBY_PARTITIONING, offset, false));
        return scanner.findClosingPeer(offset + 1, openingPeer, closingPeer);
    }


    protected int searchForOpeningPeer(int offset, char openingPeer, char closingPeer, IDocument document) throws BadLocationException {
        RubyHeuristicScanner scanner= new RubyHeuristicScanner(document, IRubyPartitions.RUBY_PARTITIONING, TextUtilities.getContentType(document, IRubyPartitions.RUBY_PARTITIONING, offset, false));
        int peer= scanner.findOpeningPeer(offset - 1, openingPeer, closingPeer);
        if (peer == RubyHeuristicScanner.NOT_FOUND)
            return -1;
        return peer;
    }


}
