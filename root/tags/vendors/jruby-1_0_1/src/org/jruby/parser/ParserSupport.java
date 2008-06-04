/*
 ***** BEGIN LICENSE BLOCK *****
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
 * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
 * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
 * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
 * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
 * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
 * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
 * Copyright (C) 2006-2007 Mirko Stocker <me@misto.ch>
 * Copyright (C) 2006 Thomas Corbat <tcorbat@hsr.ch>
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
package org.jruby.parser;

import org.jruby.ast.AndNode;
import org.jruby.ast.ArgsCatNode;
import org.jruby.ast.ArgsPushNode;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.ArrayNode;
import org.jruby.ast.AssignableNode;
import org.jruby.ast.AttrAssignNode;
import org.jruby.ast.BackRefNode;
import org.jruby.ast.BeginNode;
import org.jruby.ast.BignumNode;
import org.jruby.ast.BlockNode;
import org.jruby.ast.BlockPassNode;
import org.jruby.ast.BreakNode;
import org.jruby.ast.CallNode;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.ClassVarDeclNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.ConstDeclNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DAsgnNode;
import org.jruby.ast.DRegexpNode;
import org.jruby.ast.DStrNode;
import org.jruby.ast.DotNode;
import org.jruby.ast.EvStrNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.FalseNode;
import org.jruby.ast.FixnumNode;
import org.jruby.ast.FlipNode;
import org.jruby.ast.FloatNode;
import org.jruby.ast.GlobalAsgnNode;
import org.jruby.ast.GlobalVarNode;
import org.jruby.ast.IArgumentNode;
import org.jruby.ast.IfNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.ListNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.Match2Node;
import org.jruby.ast.Match3Node;
import org.jruby.ast.MatchNode;
import org.jruby.ast.MultipleAsgnNode;
import org.jruby.ast.NewlineNode;
import org.jruby.ast.NilNode;
import org.jruby.ast.Node;
import org.jruby.ast.NodeTypes;
import org.jruby.ast.NthRefNode;
import org.jruby.ast.OptNNode;
import org.jruby.ast.OrNode;
import org.jruby.ast.RegexpNode;
import org.jruby.ast.RootNode;
import org.jruby.ast.SValueNode;
import org.jruby.ast.SelfNode;
import org.jruby.ast.SplatNode;
import org.jruby.ast.StrNode;
import org.jruby.ast.SuperNode;
import org.jruby.ast.SymbolNode;
import org.jruby.ast.TrueNode;
import org.jruby.ast.YieldNode;
import org.jruby.ast.types.ILiteralNode;
import org.jruby.common.IRubyWarnings;
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.lexer.yacc.ISourcePositionHolder;
import org.jruby.lexer.yacc.IDESourcePosition;
import org.jruby.lexer.yacc.SyntaxException;
import org.jruby.lexer.yacc.Token;
import org.jruby.runtime.DynamicScope;
import org.jruby.util.ByteList;
import org.jruby.util.IdUtil;

/** 
 *
 */
public class ParserSupport {
    // Parser states:
    private StaticScope currentScope;
    
    // Is the parser current within a singleton (value is number of nested singletons)
    private int inSingleton;
    
    // Is the parser currently within a method definition
    private boolean inDefinition;

    private IRubyWarnings warnings;

    private RubyParserConfiguration configuration;
    private RubyParserResult result;

    public void reset() {
        inSingleton = 0;
        inDefinition = false;
    }
    
    public StaticScope getCurrentScope() {
        return currentScope;
    }
    
    public void popCurrentScope() {
        currentScope = currentScope.getEnclosingScope();
    }
    
    public void pushBlockScope() {
        currentScope = new BlockStaticScope(currentScope);
    }
    
    public void pushLocalScope() {
        currentScope = new LocalStaticScope(currentScope);
    }
    
    public Node arg_concat(ISourcePosition position, Node node1, Node node2) {
        return node2 == null ? node1 : new ArgsCatNode(position, node1, node2);
    }

    public Node arg_blk_pass(Node firstNode, BlockPassNode secondNode) {
        if (secondNode != null) {
            secondNode.setArgsNode(firstNode);
            return secondNode;
        }
        return firstNode;
    }

    public Node appendPrintToBlock(Node block) {
    	ISourcePosition position = block.getPosition();
        return appendToBlock(block, new FCallNode(position, "print", 
            new ArrayNode(position).add(new GlobalVarNode(position, "$_"))));
    }

    public Node appendWhileLoopToBlock(Node block, boolean chop, boolean split) {
    	ISourcePosition position = block.getPosition();
        if (split) {
            block = appendToBlock(new GlobalAsgnNode(position, "$F", 
                new CallNode(position, new GlobalVarNode(position, "$_"), "split", null)), block);
        }
        if (chop) {
            block = appendToBlock(new CallNode(position, new GlobalVarNode(position, "$_"), "chop!", null), block);
        }
        return new OptNNode(position, block);
    }

    /**
     * We know for callers of this that it cannot be any of the specials checked in gettable.
     * 
     * @param id to check its variable type
     * @param position location of this position
     * @return an AST node representing this new variable
     */
    public Node gettable2(String id, ISourcePosition position) {
        switch (IdUtil.getVarType(id)) {
        case IdUtil.LOCAL_VAR:
            return currentScope.declare(position, id);
        case IdUtil.CONSTANT:
            return new ConstNode(position, id);
        case IdUtil.INSTANCE_VAR:
            return new InstVarNode(position, id);
        case IdUtil.CLASS_VAR:
            return new ClassVarNode(position, id);
        case IdUtil.GLOBAL_VAR:
            return new GlobalVarNode(position, id);                
        }
        
        throw new SyntaxException(position, "identifier " + id + " is not valid");
    }
    
    /**
     * Create AST node representing variable type it represents.
     * 
     * @param id to check its variable type
     * @param position location of this position
     * @return an AST node representing this new variable
     */
    public Node gettable(String id, ISourcePosition position) {
        if (id.equals("self")) {
            return new SelfNode(position);
        } else if (id.equals("nil")) {
        	return new NilNode(position);
        } else if (id.equals("true")) {
        	return new TrueNode(position);
        } else if (id.equals("false")) {
        	return new FalseNode(position);
        } else if (id.equals("__FILE__")) {
            return new StrNode(position, ByteList.create(position.getFile()));
        } else if (id.equals("__LINE__")) {
            return new FixnumNode(position, position.getEndLine()+1);
        } 
          
        return gettable2(id, position);
    }
    
    public AssignableNode assignable(Token lhs, Node value) {
        checkExpression(value);

        String id = (String) lhs.getValue();

        if ("self".equals(id)) {
            throw new SyntaxException(lhs.getPosition(), "Can't change the value of self");
        } else if ("nil".equals(id)) {
            throw new SyntaxException(lhs.getPosition(), "Can't assign to nil");
        } else if ("true".equals(id)) {
            throw new SyntaxException(lhs.getPosition(), "Can't assign to true");
        } else if ("false".equals(id)) {
            throw new SyntaxException(lhs.getPosition(), "Can't assign to false");
        } else if ("__FILE__".equals(id)) {
            throw new SyntaxException(lhs.getPosition(), "Can't assign to __FILE__");
        } else if ("__LINE__".equals(id)) {
            throw new SyntaxException(lhs.getPosition(), "Can't assign to __LINE__");
        } else {
            switch (IdUtil.getVarType(id)) {
            case IdUtil.LOCAL_VAR:
                return currentScope.assign(value != null ? union(lhs, value) : lhs.getPosition(), id, value);
            case IdUtil.CONSTANT:
                if (isInDef() || isInSingle()) {
                    throw new SyntaxException(lhs.getPosition(), "dynamic constant assignment");
                }
                return new ConstDeclNode(lhs.getPosition(), id, null, value);
            case IdUtil.INSTANCE_VAR:
                return new InstAsgnNode(lhs.getPosition(), id, value);
            case IdUtil.CLASS_VAR:
                if (isInDef() || isInSingle()) {
                    return new ClassVarAsgnNode(lhs.getPosition(), id, value);
                }
                return new ClassVarDeclNode(lhs.getPosition(), id, value);
            case IdUtil.GLOBAL_VAR:
                return new GlobalAsgnNode(lhs.getPosition(), id, value);
            }
        }

        throw new SyntaxException(lhs.getPosition(), "identifier " + id + " is not valid");
    }

    /**
     *  Wraps node with NEWLINE node.
     *
     *@param node
     *@return a NewlineNode or null if node is null.
     */
    public Node newline_node(Node node, ISourcePosition position) {
        if (node == null) return null;
        
        return node instanceof NewlineNode ? node : new NewlineNode(position, node); 
    }
    
    public ISourcePosition union(ISourcePositionHolder first, ISourcePositionHolder second) {
        while (first instanceof NewlineNode) {
            first = ((NewlineNode) first).getNextNode();
        }

        while (second instanceof NewlineNode) {
            second = ((NewlineNode) second).getNextNode();
        }
        
        if (second == null)	return first.getPosition();
        if (first == null) return second.getPosition();
        
        return first.getPosition().union(second.getPosition());
    }
    
    public ISourcePosition union(ISourcePosition first, ISourcePosition second) {
		if (first.getStartOffset() < second.getStartOffset()) return first.union(second); 

		return second.union(first);
	}
    
    public Node addRootNode(Node topOfAST, ISourcePosition position) {
        // I am not sure we need to get AST to set AST and the appendToBlock could maybe get removed.
        // For sure once we do two pass parsing we should since this is mostly just optimzation.
        RootNode root = new RootNode(topOfAST != null ? topOfAST.getPosition() : position, result.getScope(),
                appendToBlock(result.getAST(), topOfAST));

        return root;

    }
    
    public Node appendToBlock(Node head, Node tail) {
        if (tail == null) return head;
        if (head == null) return tail;
        
        //Mirko asks: This was added, and it breaks a lof of my code, is it really needed? 
        //while (head instanceof NewlineNode) {
        //    head = ((NewlineNode) head).getNextNode();
        //}

        if (!(head instanceof BlockNode)) {
            head = new BlockNode(head.getPosition()).add(head);
        }

        if (warnings.isVerbose() && isBreakStatement(((ListNode) head).getLast())) {
            warnings.warning(tail.getPosition(), "Statement not reached.");
        }

        // Assumption: tail is never a list node
        ((ListNode) head).addAll(tail);
        head.setPosition(union(head, tail));
        return head;
    }

    public Node getOperatorCallNode(Node firstNode, String operator) {
        checkExpression(firstNode);

        return new CallNode(firstNode.getPosition(), firstNode, operator, null);
    }
    
    public Node getOperatorCallNode(Node firstNode, String operator, Node secondNode) {
        return getOperatorCallNode(firstNode, operator, secondNode, null);
    }

    public Node getOperatorCallNode(Node firstNode, String operator, Node secondNode, ISourcePosition defaultPosition) {
        if (defaultPosition != null) {
            firstNode = checkForNilNode(firstNode, defaultPosition);
        	secondNode = checkForNilNode(secondNode, defaultPosition);
        }
        
        checkExpression(firstNode);
        checkExpression(secondNode);
        
        return new CallNode(union(firstNode.getPosition(), secondNode.getPosition()), firstNode, operator, new ArrayNode(secondNode.getPosition()).add(secondNode));
    }

    public Node getMatchNode(Node firstNode, Node secondNode) {
        if (firstNode instanceof DRegexpNode || firstNode instanceof RegexpNode) {
            return new Match2Node(firstNode.getPosition(), firstNode, secondNode);
        } else if (secondNode instanceof DRegexpNode || secondNode instanceof RegexpNode) {
            return new Match3Node(firstNode.getPosition(), secondNode, firstNode);
        } 

        return getOperatorCallNode(firstNode, "=~", secondNode);
    }

    /**
     * Define an array set condition so we can return lhs
     * 
     * @param receiver array being set
     * @param index node which should evalute to index of array set
     * @return an AttrAssignNode
     */
    public Node aryset(Node receiver, Node index) {
        checkExpression(receiver);

        return new AttrAssignNode(receiver.getPosition(), receiver, "[]=", index);
    }

    /**
     * Define an attribute set condition so we can return lhs
     * 
     * @param receiver object which contains attribute
     * @param name of the attribute being set
     * @return an AttrAssignNode
     */
    public Node attrset(Node receiver, String name) {
        checkExpression(receiver);

        return new AttrAssignNode(receiver.getPosition(), receiver, name + "=", null);
    }

    public void backrefAssignError(Node node) {
        if (node instanceof NthRefNode) {
            throw new SyntaxException(node.getPosition(), "Can't set variable $" + ((NthRefNode) node).getMatchNumber() + '.');
        } else if (node instanceof BackRefNode) {
            throw new SyntaxException(node.getPosition(), "Can't set variable $" + ((BackRefNode) node).getType() + '.');
        }
    }

    public Node arg_add(ISourcePosition position, Node node1, Node node2) {
        if (node1 == null) return new ArrayNode(node2 == null ? position : node2.getPosition(), node2);
        if (node1 instanceof ArrayNode) return ((ArrayNode) node1).add(node2);
        
        return new ArgsPushNode(position, node1, node2);
    }
    
	/**
	 * @fixme position
	 **/
    public Node node_assign(Node lhs, Node rhs) {
        if (lhs == null) return null;

        Node newNode = lhs;

        checkExpression(rhs);
        if (lhs instanceof AssignableNode) {
    	    ((AssignableNode) lhs).setValueNode(rhs);
    	    lhs.setPosition(union(lhs, rhs));
        } else if (lhs instanceof IArgumentNode) {
            IArgumentNode invokableNode = (IArgumentNode) lhs;
            
            invokableNode.setArgsNode(arg_add(lhs.getPosition(), invokableNode.getArgsNode(), rhs));
        }
        
        return newNode;
    }
    
    public Node ret_args(Node node, ISourcePosition position) {
        if (node != null) {
            if (node instanceof BlockPassNode) {
                throw new SyntaxException(position, "Dynamic constant assignment.");
            } else if (node instanceof ArrayNode && ((ArrayNode)node).size() == 1) {
                node = ((ArrayNode)node).get(0);
            } else if (node instanceof SplatNode) {
                node = new SValueNode(position, node);
            }
        }
        
        return node;
    }

    /**
     * Is the supplied node a break/control statement?
     * 
     * @param node to be checked
     * @return true if a control node, false otherwise
     */
    public boolean isBreakStatement(Node node) {
        breakLoop: do {
            if (node == null) return false;

            switch (node.nodeId) {
            case NodeTypes.NEWLINENODE:
                node = ((NewlineNode) node).getNextNode();
                continue breakLoop;
            case NodeTypes.BREAKNODE: case NodeTypes.NEXTNODE: case NodeTypes.REDONODE:
            case NodeTypes.RETRYNODE: case NodeTypes.RETURNNODE:
                return true;
            default:
                return false;
            }
        } while (true);                    
    }

    /**
     * Does this node represent an expression?
     * @param node to be checked
     * @return true if an expression, false otherwise
     */
    public void checkExpression(Node node) {
        if (!isExpression(node)) {
            warnings.warning(node.getPosition(), "void value expression");
        }
    }
    
    private boolean isExpression(Node node) {
        expressionLoop: do {
            if (node == null) return true;
            
            switch (node.nodeId) {
            case NodeTypes.BEGINNODE:
                node = ((BeginNode) node).getBodyNode();
                continue expressionLoop;
            case NodeTypes.BLOCKNODE:
                node = ((BlockNode) node).getLast();
                continue expressionLoop;
            case NodeTypes.BREAKNODE:
                node = ((BreakNode) node).getValueNode();
                continue expressionLoop;
            case NodeTypes.CLASSNODE: case NodeTypes.DEFNNODE: case NodeTypes.DEFSNODE:
            case NodeTypes.MODULENODE: case NodeTypes.NEXTNODE: case NodeTypes.REDONODE:
            case NodeTypes.RETRYNODE: case NodeTypes.RETURNNODE: case NodeTypes.UNTILNODE:
            case NodeTypes.WHILENODE:
                return false;
            case NodeTypes.IFNODE:
                return isExpression(((IfNode) node).getThenBody()) &&
                  isExpression(((IfNode) node).getElseBody());
            case NodeTypes.NEWLINENODE:
                node = ((NewlineNode) node).getNextNode();
                continue expressionLoop;
            default: // Node
                return true;
            }
        } while (true);
    }
    
    /**
     * Is this a literal in the sense that MRI has a NODE_LIT for.  This is different than
     * ILiteralNode.  We should pick a different name since ILiteralNode is something we created
     * which is similiar but used for a slightly different condition (can I do singleton things).
     * 
     * @param node to be tested
     * @return true if it is a literal
     */
    public boolean isLiteral(Node node) {
        return node != null && (node instanceof FixnumNode || node instanceof BignumNode || 
                node instanceof FloatNode || node instanceof SymbolNode || 
                (node instanceof RegexpNode && 
                        (((RegexpNode) node).getOptions() & ~ReOptions.RE_OPTION_ONCE) == 0));
    }

    private void handleUselessWarn(Node node, String useless) {
        warnings.warn(node.getPosition(), "Useless use of " + useless + " in void context.");
    }

    /**
     * Check to see if current node is an useless statement.  If useless a warning if printed.
     * 
     * @param node to be checked.
     */
    public void checkUselessStatement(Node node) {
        if (!warnings.isVerbose()) return;
        
        uselessLoop: do {
            if (node == null) return;
            
            switch (node.nodeId) {
            case NodeTypes.NEWLINENODE:
                node = ((NewlineNode) node).getNextNode();
                continue uselessLoop;
            case NodeTypes.CALLNODE: {
                String name = ((CallNode) node).getName().intern();
                
                if (name == "+" || name == "-" || name == "*" || name == "/" || name == "%" || 
                    name == "**" || name == "+@" || name == "-@" || name == "|" || name == "^" || 
                    name == "&" || name == "<=>" || name == ">" || name == ">=" || name == "<" || 
                    name == "<=" || name == "==" || name == "!=") {
                    handleUselessWarn(node, name);
                }
                return;
            }
            case NodeTypes.BACKREFNODE: case NodeTypes.DVARNODE: case NodeTypes.GLOBALVARNODE:
            case NodeTypes.LOCALVARNODE: case NodeTypes.NTHREFNODE: case NodeTypes.CLASSVARNODE:
            case NodeTypes.INSTVARNODE:
                handleUselessWarn(node, "a variable"); return;
            // FIXME: Temporarily disabling because this fires way too much running Rails tests. JRUBY-518
            /*case NodeTypes.CONSTNODE:
                handleUselessWarn(node, "a constant"); return;*/
            case NodeTypes.BIGNUMNODE: case NodeTypes.DREGEXPNODE: case NodeTypes.DSTRNODE:
            case NodeTypes.FIXNUMNODE: case NodeTypes.FLOATNODE: case NodeTypes.REGEXPNODE:
            case NodeTypes.STRNODE: case NodeTypes.SYMBOLNODE:
                handleUselessWarn(node, "a literal"); return;
            // FIXME: Temporarily disabling because this fires way too much running Rails tests. JRUBY-518
            /*case NodeTypes.CLASSNODE: case NodeTypes.COLON2NODE:
                handleUselessWarn(node, "::"); return;*/
            case NodeTypes.DOTNODE:
                handleUselessWarn(node, ((DotNode) node).isExclusive() ? "..." : ".."); return;
            case NodeTypes.DEFINEDNODE:
                handleUselessWarn(node, "defined?"); return;
            case NodeTypes.FALSENODE:
                handleUselessWarn(node, "false"); return;
            case NodeTypes.NILNODE: 
                handleUselessWarn(node, "nil"); return;
            // FIXME: Temporarily disabling because this fires way too much running Rails tests. JRUBY-518
            /*case NodeTypes.SELFNODE:
                handleUselessWarn(node, "self"); return;*/
            case NodeTypes.TRUENODE:
                handleUselessWarn(node, "true"); return;
            default: return;
            }
        } while (true);
    }

    /**
     * Check all nodes but the last one in a BlockNode for useless (void context) statements.
     * 
     * @param blockNode to be checked.
     */
    public void checkUselessStatements(BlockNode blockNode) {
        if (warnings.isVerbose()) {
            Node lastNode = blockNode.getLast();

            for (int i = 0; i < blockNode.size(); i++) {
                Node currentNode = blockNode.get(i);
        		
                if (lastNode != currentNode ) {
                    checkUselessStatement(currentNode);
                }
            }
        }
    }

	/**
	 * @fixme error handling
	 **/
    private boolean checkAssignmentInCondition(Node node) {
        if (node instanceof MultipleAsgnNode) {
            throw new SyntaxException(node.getPosition(), "Multiple assignment in conditional.");
        } else if (node instanceof LocalAsgnNode || node instanceof DAsgnNode || node instanceof GlobalAsgnNode || node instanceof InstAsgnNode) {
            Node valueNode = ((AssignableNode) node).getValueNode();
            if (valueNode instanceof ILiteralNode || valueNode instanceof NilNode || valueNode instanceof TrueNode || valueNode instanceof FalseNode) {
                warnings.warn(node.getPosition(), "Found '=' in conditional, should be '=='.");
            }
            return true;
        } 

        return false;
    }

    private Node cond0(Node node) {
        checkAssignmentInCondition(node);

        if (node instanceof DRegexpNode) {
            ISourcePosition position = node.getPosition();

            return new Match2Node(position, node, new GlobalVarNode(position, "$_"));
        } else if (node instanceof DotNode) {
            int slot = currentScope.getLocalScope().addVariable("");
            return new FlipNode(node.getPosition(),
                    getFlipConditionNode(((DotNode) node).getBeginNode()),
                    getFlipConditionNode(((DotNode) node).getEndNode()),
                    ((DotNode) node).isExclusive(), slot);
        } else if (node instanceof RegexpNode) {
            return new MatchNode(node.getPosition(), node);
        } else if (node instanceof StrNode) {
            ISourcePosition position = node.getPosition();

            return new MatchNode(position, new RegexpNode(position, (ByteList) ((StrNode) node).getValue().clone(), 0));
        } 

        return node;
    }

    public Node getConditionNode(Node node) {
        if (node == null) return null;

        if (node instanceof NewlineNode) {
            return new NewlineNode(node.getPosition(), cond0(((NewlineNode) node).getNextNode()));
        } 

        return cond0(node);
    }

    private Node getFlipConditionNode(Node node) {
        node = getConditionNode(node);

        if (node instanceof NewlineNode) return ((NewlineNode) node).getNextNode();
        
        if (node instanceof FixnumNode) {
            return getOperatorCallNode(node, "==", new GlobalVarNode(node.getPosition(), "$."));
        } 

        return node;
    }

    public AndNode newAndNode(Node left, Node right) {
        checkExpression(left);
        
        return new AndNode(union(left, right), left, right);
    }

    public OrNode newOrNode(Node left, Node right) {
        checkExpression(left);
        
        return new OrNode(union(left, right), left, right);
    }

    public Node getReturnArgsNode(Node node) {
        if (node instanceof ArrayNode && ((ArrayNode) node).size() == 1) { 
            return ((ListNode) node).get(0);
        } else if (node instanceof BlockPassNode) {
            throw new SyntaxException(node.getPosition(), "Block argument should not be given.");
        }
        return node;
    }

    public Node new_call(Node receiver, Token name, Node args, Node iter) {
        if (args == null) {
            return new CallNode(union(receiver, name), receiver,(String) name.getValue(), null, iter);
        }

        if (args instanceof BlockPassNode) {
            // Block and block pass passed in at same time....uh oh
            if (iter != null) {
                throw new SyntaxException(iter.getPosition(), "Both block arg and actual block given.");
            }
                
            return new CallNode(union(receiver, args), receiver, (String) name.getValue(), 
                    ((BlockPassNode) args).getArgsNode(), args);
        }
            
        return new CallNode(union(receiver, args), receiver,(String) name.getValue(), args, iter);
    }

    public Node new_fcall(Token operation, Node args, Node iter) {
        String name = (String) operation.getValue();
        
        if (args == null) return new FCallNode(operation.getPosition(), name, args, iter);

        if (args instanceof BlockPassNode) {
            if (iter != null) {
                throw new SyntaxException(iter.getPosition(), "Both block arg and actual block given.");
            }
            return new FCallNode(union(operation, args), name, ((BlockPassNode) args).getArgsNode(), args);
        }
        
        return new FCallNode(union(operation, args), name, args, iter);
    }

    public Node new_super(Node args, Token operation) {
        if (args != null && args instanceof BlockPassNode) {
            return new SuperNode(union(operation, args), ((BlockPassNode) args).getArgsNode(), args);
        }
        return new SuperNode(operation.getPosition(), args);
    }

    /**
    *  Description of the RubyMethod
    */
    public void initTopLocalVariables() {
        DynamicScope scope = configuration.getScope(); 
        currentScope = scope.getStaticScope(); 
        
        result.setScope(scope);
    }

    /** Getter for property inSingle.
     * @return Value of property inSingle.
     */
    public boolean isInSingle() {
        return inSingleton != 0;
    }

    /** Setter for property inSingle.
     * @param inSingle New value of property inSingle.
     */
    public void setInSingle(int inSingle) {
        this.inSingleton = inSingle;
    }

    public boolean isInDef() {
        return inDefinition;
    }

    public void setInDef(boolean inDef) {
        this.inDefinition = inDef;
    }

    /** Getter for property inSingle.
     * @return Value of property inSingle.
     */
    public int getInSingle() {
        return inSingleton;
    }

    /**
     * Gets the result.
     * @return Returns a RubyParserResult
     */
    public RubyParserResult getResult() {
        return result;
    }

    /**
     * Sets the result.
     * @param result The result to set
     */
    public void setResult(RubyParserResult result) {
        this.result = result;
    }

    /**
     * Sets the configuration.
     * @param configuration The configuration to set
     */
    public void setConfiguration(RubyParserConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setWarnings(IRubyWarnings warnings) {
        this.warnings = warnings;
    }
    
    public Node literal_concat(ISourcePosition position, Node head, Node tail) { 
        if (head == null) return tail;
        if (tail == null) return head;
        
        if (head instanceof EvStrNode) {
            head = new DStrNode(union(head.getPosition(), position)).add(head);
        } 

        if (tail instanceof StrNode) {
            if (head instanceof StrNode) {
        	    return new StrNode(union(head, tail), (StrNode) head, (StrNode) tail);
            } 
            head.setPosition(union(head, tail));
            return ((ListNode) head).add(tail);
        	
        } else if (tail instanceof DStrNode) {
            if (head instanceof StrNode){
                ((DStrNode)tail).prepend(head);
                return tail;
            } 

            return ((ListNode) head).addAll(tail);
        } 

        // tail must be EvStrNode at this point 
        if (head instanceof StrNode) {
        	
            //Do not add an empty string node
            if(((StrNode) head).getValue().length() == 0) {
                head = new DStrNode(head.getPosition());
            } else {
                // All first element StrNode's do not include syntacical sugar.
                head.getPosition().adjustStartOffset(-1);
                head = new DStrNode(head.getPosition()).add(head);
            }
        }
        return ((DStrNode) head).add(tail);
    }
    
    public Node newEvStrNode(ISourcePosition position, Node node) {
        Node head = node;
        while (true) {
            if (node == null) break;
            
            if (node instanceof StrNode || node instanceof DStrNode || node instanceof EvStrNode) {
                return node;
            }
                
            if (!(node instanceof NewlineNode)) break;
                
            node = ((NewlineNode) node).getNextNode();
        }
        
        return new EvStrNode(position, head);
    }
    
    public Node new_yield(ISourcePosition position, Node node) {
        boolean state = true;
        
        if (node != null) {
            if (node instanceof BlockPassNode) {
                throw new SyntaxException(node.getPosition(), "Block argument should not be given.");
            }
            
            if (node instanceof ArrayNode && ((ArrayNode)node).size() == 1) {
                node = ((ArrayNode)node).get(0);
                state = false;
            }
            
            if (node != null && node instanceof SplatNode) {
                state = true;
            }
        } else {
            state = false;
        }

        return new YieldNode(position, node, state);
    }
    
    public Node negateInteger(Node integerNode) {
        if (integerNode instanceof FixnumNode) {
            FixnumNode fixnumNode = (FixnumNode) integerNode;
            
            fixnumNode.setValue(-fixnumNode.getValue());
            return fixnumNode;
        } else if (integerNode instanceof BignumNode) {
            BignumNode bignumNode = (BignumNode) integerNode;
            
            bignumNode.setValue(bignumNode.getValue().negate());
        }
        
        return integerNode;
    }
    
    public FloatNode negateFloat(FloatNode floatNode) {
        floatNode.setValue(-floatNode.getValue());
        
        return floatNode;
    }
    
    public ISourcePosition createEmptyArgsNodePosition(ISourcePosition pos) {
        return new IDESourcePosition(pos.getFile(), pos.getStartLine(), pos.getEndLine(), pos.getEndOffset() - 1, pos.getEndOffset() - 1);
    }
    
    public Node unwrapNewlineNode(Node node) {
    	if(node instanceof NewlineNode) {
    		return ((NewlineNode) node).getNextNode();
    	}
    	return node;
    }
    
    private Node checkForNilNode(Node node, ISourcePosition defaultPosition) {
        return (node == null) ? new NilNode(defaultPosition) : node; 
    }

    public ArgumentNode getRestArgNode(Token token) {
        int index = ((Integer) token.getValue()).intValue();
        if(index < 0) {
            return null;
        }
        String name = getCurrentScope().getLocalScope().getVariables()[index];
        ISourcePosition position = new IDESourcePosition(token.getPosition().getFile(), token.getPosition().getStartLine(), token.getPosition().getEndLine(), token.getPosition().getStartOffset(), token.getPosition().getEndOffset() + name.length());
        return new ArgumentNode(position, name);
    }
}
