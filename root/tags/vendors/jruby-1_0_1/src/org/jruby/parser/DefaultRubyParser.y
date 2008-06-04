%{
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
 * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
 * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
 * Copyright (C) 2001-2004 Stefan Matthias Aust <sma@3plus4.de>
 * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
 * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
 * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
 * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
 * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
 * Copyright (C) 2007 Mirko Stocker <me@misto.ch>
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

import java.io.IOException;

import org.jruby.ast.AliasNode;
import org.jruby.ast.ArgsNode;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.ArrayNode;
import org.jruby.ast.AssignableNode;
import org.jruby.ast.BackRefNode;
import org.jruby.ast.BeginNode;
import org.jruby.ast.BlockAcceptingNode;
import org.jruby.ast.BlockArgNode;
import org.jruby.ast.BlockNode;
import org.jruby.ast.BlockPassNode;
import org.jruby.ast.BreakNode;
import org.jruby.ast.CallNode;
import org.jruby.ast.CaseNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.Colon2Node;
import org.jruby.ast.Colon3Node;
import org.jruby.ast.ConstDeclNode;
import org.jruby.ast.DRegexpNode;
import org.jruby.ast.DStrNode;
import org.jruby.ast.DSymbolNode;
import org.jruby.ast.DXStrNode;
import org.jruby.ast.DefinedNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.DotNode;
import org.jruby.ast.EnsureNode;
import org.jruby.ast.EvStrNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.FloatNode;
import org.jruby.ast.ForNode;
import org.jruby.ast.GlobalVarNode;
import org.jruby.ast.HashNode;
import org.jruby.ast.IfNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.IterNode;
import org.jruby.ast.ListNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.MultipleAsgnNode;
import org.jruby.ast.NewlineNode;
import org.jruby.ast.NextNode;
import org.jruby.ast.NilNode;
import org.jruby.ast.Node;
import org.jruby.ast.NotNode;
import org.jruby.ast.OpAsgnAndNode;
import org.jruby.ast.OpAsgnNode;
import org.jruby.ast.OpAsgnOrNode;
import org.jruby.ast.OpElementAsgnNode;
import org.jruby.ast.PostExeNode;
import org.jruby.ast.RedoNode;
import org.jruby.ast.RegexpNode;
import org.jruby.ast.RescueBodyNode;
import org.jruby.ast.RescueNode;
import org.jruby.ast.RetryNode;
import org.jruby.ast.ReturnNode;
import org.jruby.ast.SClassNode;
import org.jruby.ast.SValueNode;
import org.jruby.ast.SelfNode;
import org.jruby.ast.SplatNode;
import org.jruby.ast.StarNode;
import org.jruby.ast.StrNode;
import org.jruby.ast.SymbolNode;
import org.jruby.ast.ToAryNode;
import org.jruby.ast.UndefNode;
import org.jruby.ast.UntilNode;
import org.jruby.ast.VAliasNode;
import org.jruby.ast.WhenNode;
import org.jruby.ast.WhileNode;
import org.jruby.ast.XStrNode;
import org.jruby.ast.YieldNode;
import org.jruby.ast.ZArrayNode;
import org.jruby.ast.ZSuperNode;
import org.jruby.ast.ZeroArgNode;
import org.jruby.ast.types.ILiteralNode;
import org.jruby.ast.types.INameNode;
import org.jruby.common.IRubyWarnings;
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.lexer.yacc.ISourcePositionHolder;
import org.jruby.lexer.yacc.LexState;
import org.jruby.lexer.yacc.LexerSource;
import org.jruby.lexer.yacc.RubyYaccLexer;
import org.jruby.lexer.yacc.StrTerm;
import org.jruby.lexer.yacc.SyntaxException;
import org.jruby.lexer.yacc.Token;
import org.jruby.runtime.Visibility;
import org.jruby.util.IdUtil;
import org.jruby.util.ByteList;

public class DefaultRubyParser {
    private ParserSupport support;
    private RubyYaccLexer lexer;
    private IRubyWarnings warnings;

    public DefaultRubyParser() {
        support = new ParserSupport();
        lexer = new RubyYaccLexer();
        lexer.setParserSupport(support);
    }

    public void setWarnings(IRubyWarnings warnings) {
        this.warnings = warnings;

        support.setWarnings(warnings);
        lexer.setWarnings(warnings);
    }
%}

%token <Token> kCLASS kMODULE kDEF kUNDEF kBEGIN kRESCUE kENSURE kEND kIF
  kUNLESS kTHEN kELSIF kELSE kCASE kWHEN kWHILE kUNTIL kFOR kBREAK kNEXT
  kREDO kRETRY kIN kDO kDO_COND kDO_BLOCK kRETURN kYIELD kSUPER kSELF kNIL
  kTRUE kFALSE kAND kOR kNOT kIF_MOD kUNLESS_MOD kWHILE_MOD kUNTIL_MOD
  kRESCUE_MOD kALIAS kDEFINED klBEGIN klEND k__LINE__ k__FILE__

%token <Token>  tIDENTIFIER tFID tGVAR tIVAR tCONSTANT tCVAR 
%token <Node> tNTH_REF tBACK_REF tSTRING_CONTENT tINTEGER 
%token <FloatNode> tFLOAT
%token <RegexpNode> tREGEXP_END

%type <Node>  singleton strings string string1 xstring regexp
%type <Node>  string_contents xstring_contents string_content method_call
%type <Node>  words qwords word literal numeric dsym cpath command_call
%type <Node>  compstmt bodystmt stmts stmt expr arg primary command 
%type <Node>  expr_value primary_value opt_else cases if_tail exc_var 
%type <Node>  call_args call_args2 open_args opt_ensure paren_args superclass
%type <Node>  command_args var_ref opt_paren_args block_call block_command
%type <Node>  f_arglist f_args f_opt undef_list string_dvar backref 
%type <Node>  mrhs mlhs_item mlhs_node arg_value case_body exc_list aref_args 
%type <Node>  block_var opt_block_var lhs none
%type <ListNode> qword_list word_list f_arg f_optarg 
%type <ListNode> args when_args mlhs_head assocs assoc assoc_list 
%type <BlockPassNode> opt_block_arg block_arg none_block_pass
%type <BlockArgNode> opt_f_block_arg f_block_arg 
%type <IterNode> brace_block do_block cmd_brace_block 
%type <MultipleAsgnNode> mlhs mlhs_basic mlhs_entry
%type <RescueBodyNode> opt_rescue
%type <AssignableNode> var_lhs
%type <Token> variable 
%type <Token>  fitem sym symbol operation operation2 operation3 cname fname op
%type <Token>  f_norm_arg f_rest_arg dot_or_colon restarg_mark blkarg_mark
%token <Token> tUPLUS         /* unary+ */
%token <Token> tUMINUS        /* unary- */
%token <Token> tUMINUS_NUM    /* unary- */
%token <Token> tPOW           /* ** */
%token <Token> tCMP           /* <=> */
%token <Token> tEQ            /* == */
%token <Token> tEQQ           /* === */
%token <Token> tNEQ           /* != */
%token <Token> tGEQ           /* >= */
%token <Token> tLEQ           /* <= */
%token <Token> tANDOP tOROP   /* && and || */
%token <Token> tMATCH tNMATCH /* =~ and !~ */
%token <Token>  tDOT           /* Is just '.' in ruby and not a token */
%token <Token> tDOT2 tDOT3    /* .. and ... */
%token <Token> tAREF tASET    /* [] and []= */
%token <Token> tLSHFT tRSHFT  /* << and >> */
%token <Token> tCOLON2        /* :: */
%token <Token> tCOLON3        /* :: at EXPR_BEG */
%token <Token> tOP_ASGN       /* +=, -=  etc. */
%token <Token> tASSOC         /* => */
%token <Token> tLPAREN        /* ( */
%token <Token> tLPAREN2        /* ( Is just '(' in ruby and not a token */
%token <Token> tRPAREN        /* ) */
%token <Token> tLPAREN_ARG    /* ( */
%token <Token> tLBRACK        /* [ */
%token <Token> tRBRACK        /* ] */
%token <Token> tLBRACE        /* { */
%token <Token> tLBRACE_ARG    /* { */
%token <Token> tSTAR          /* * */
%token <Token> tSTAR2         /* *  Is just '*' in ruby and not a token */
%token <Token> tAMPER         /* & */
%token <Token> tAMPER2        /* &  Is just '&' in ruby and not a token */
%token <Token> tTILDE         /* ` is just '`' in ruby and not a token */
%token <Token> tPERCENT       /* % is just '%' in ruby and not a token */
%token <Token> tDIVIDE        /* / is just '/' in ruby and not a token */
%token <Token> tPLUS          /* + is just '+' in ruby and not a token */
%token <Token> tMINUS         /* - is just '-' in ruby and not a token */
%token <Token> tLT            /* < is just '<' in ruby and not a token */
%token <Token> tGT            /* > is just '>' in ruby and not a token */
%token <Token> tPIPE          /* | is just '|' in ruby and not a token */
%token <Token> tBANG          /* ! is just '!' in ruby and not a token */
%token <Token> tCARET         /* ^ is just '^' in ruby and not a token */
%token <Token> tLCURLY        /* { is just '{' in ruby and not a token */
%token <Token> tRCURLY        /* } is just '}' in ruby and not a token */
%token <Token> tBACK_REF2     /* { is just '`' in ruby and not a token */
%token <Token> tSYMBEG tSTRING_BEG tXSTRING_BEG tREGEXP_BEG tWORDS_BEG tQWORDS_BEG
%token <Token> tSTRING_DBEG tSTRING_DVAR tSTRING_END


/*
 *    precedence table
 */
%nonassoc tLOWEST
%nonassoc tLBRACE_ARG

%nonassoc  kIF_MOD kUNLESS_MOD kWHILE_MOD kUNTIL_MOD 
%left  kOR kAND
%right kNOT
%nonassoc kDEFINED
%right '=' tOP_ASGN
%left kRESCUE_MOD
%right '?' ':'
%nonassoc tDOT2 tDOT3
%left  tOROP
%left  tANDOP
%nonassoc  tCMP tEQ tEQQ tNEQ tMATCH tNMATCH
%left  tGT tGEQ tLT tLEQ
%left  tPIPE tCARET
%left  tAMPER2
%left  tLSHFT tRSHFT
%left  tPLUS tMINUS
%left  tSTAR2 tDIVIDE tPERCENT
%right tUMINUS_NUM tUMINUS
%right tPOW
%right tBANG tTILDE tUPLUS

%token <Integer> tLAST_TOKEN

%%
program       : {
                  lexer.setState(LexState.EXPR_BEG);
                  support.initTopLocalVariables();
              } compstmt {
                  if ($2 != null) {
                      /* last expression should not be void */
                      if ($2 instanceof BlockNode) {
                          support.checkUselessStatement($<BlockNode>2.getLast());
                      } else {
                          support.checkUselessStatement($2);
                      }
                  }
                  support.getResult().setAST(support.addRootNode($2, getPosition($2)));
              }

bodystmt      : compstmt opt_rescue opt_else opt_ensure {
                  Node node = $1;

		  if ($2 != null) {
		      node = new RescueNode(getPosition($1, true), $1, $2, $3);
		  } else if ($3 != null) {
		      warnings.warn(getPosition($1), "else without rescue is useless");
                      node = support.appendToBlock($1, $3);
		  }
		  if ($4 != null) {
		      node = new EnsureNode(getPosition($1), node, $4);
		  }

	          $$ = node;
              }

compstmt      : stmts opt_terms {
                  if ($1 instanceof BlockNode) {
                      support.checkUselessStatements($<BlockNode>1);
		  }
                  $$ = $1;
              }

stmts         : none
              | stmt {
                  $$ = support.newline_node($1, getPosition($1, true));
              }
              | stmts terms stmt {
	          $$ = support.appendToBlock($1, support.newline_node($3, getPosition($3, true)));
              }
              | error stmt {
                  $$ = $2;
              }

stmt          : kALIAS fitem {
                  lexer.setState(LexState.EXPR_FNAME);
              } fitem {
                  $$ = new AliasNode(support.union($1, $4), (String) $2.getValue(), (String) $4.getValue());
              }
              | kALIAS tGVAR tGVAR {
                  $$ = new VAliasNode(getPosition($1), (String) $2.getValue(), (String) $3.getValue());
              }
              | kALIAS tGVAR tBACK_REF {
                  $$ = new VAliasNode(getPosition($1), (String) $2.getValue(), "$" + $<BackRefNode>3.getType()); // XXX
              }
              | kALIAS tGVAR tNTH_REF {
                  yyerror("can't make alias for the number variables");
              }
              | kUNDEF undef_list {
                  $$ = $2;
              }
              | stmt kIF_MOD expr_value {
                  $$ = new IfNode(support.union($1, $3), support.getConditionNode($3), $1, null);
              }
              | stmt kUNLESS_MOD expr_value {
                  $$ = new IfNode(support.union($1, $3), support.getConditionNode($3), null, $1);
              }
              | stmt kWHILE_MOD expr_value {
                  if ($1 != null && $1 instanceof BeginNode) {
                      $$ = new WhileNode(getPosition($1), support.getConditionNode($3), $<BeginNode>1.getBodyNode(), false);
                  } else {
                      $$ = new WhileNode(getPosition($1), support.getConditionNode($3), $1, true);
                  }
              }
              | stmt kUNTIL_MOD expr_value {
                  if ($1 != null && $1 instanceof BeginNode) {
                      $$ = new UntilNode(getPosition($1), support.getConditionNode($3), $<BeginNode>1.getBodyNode(), false);
                  } else {
                      $$ = new UntilNode(getPosition($1), support.getConditionNode($3), $1, true);
                  }
              }
              | stmt kRESCUE_MOD stmt {
	          $$ = new RescueNode(getPosition($1), $1, new RescueBodyNode(getPosition($1), null,$3, null), null);
              }
              | klBEGIN {
                  if (support.isInDef() || support.isInSingle()) {
                      yyerror("BEGIN in method");
                  }
		  support.pushLocalScope();
              } tLCURLY compstmt tRCURLY {
                  support.getResult().addBeginNode(support.getCurrentScope(), $4);
                  support.popCurrentScope();
                  $$ = null; //XXX 0;
              }
              | klEND tLCURLY compstmt tRCURLY {
                  if (support.isInDef() || support.isInSingle()) {
                      yyerror("END in method; use at_exit");
                  }

                  $$ = new PostExeNode(getPosition($1), $3);
              }
              | lhs '=' command_call {
                  support.checkExpression($3);
                  $$ = support.node_assign($1, $3);
              }
              | mlhs '=' command_call {
                  support.checkExpression($3);
		  if ($1.getHeadNode() != null) {
		      $1.setValueNode(new ToAryNode(getPosition($1), $3));
		  } else {
		      $1.setValueNode(new ArrayNode(getPosition($1), $3));
		  }
		  $$ = $1;
              }
              | var_lhs tOP_ASGN command_call {
 	          support.checkExpression($3);

	          String name = $<INameNode>1.getName();
		  String asgnOp = (String) $2.getValue();
		  if (asgnOp.equals("||")) {
	              $1.setValueNode($3);
	              $$ = new OpAsgnOrNode(support.union($1, $3), support.gettable2(name, $1.getPosition()), $1);
		  } else if (asgnOp.equals("&&")) {
	              $1.setValueNode($3);
                      $$ = new OpAsgnAndNode(support.union($1, $3), support.gettable2(name, $1.getPosition()), $1);
		  } else {
                      $1.setValueNode(support.getOperatorCallNode(support.gettable2(name, $1.getPosition()), asgnOp, $3));
                      $1.setPosition(support.union($1, $3));
		      $$ = $1;
		  }
	      }
              | primary_value '[' aref_args tRBRACK tOP_ASGN command_call {
                  $$ = new OpElementAsgnNode(getPosition($1), $1, (String) $5.getValue(), $3, $6);

              }
              | primary_value tDOT tIDENTIFIER tOP_ASGN command_call {
                  $$ = new OpAsgnNode(getPosition($1), $1, $5, (String) $3.getValue(), (String) $4.getValue());
              }
              | primary_value tDOT tCONSTANT tOP_ASGN command_call {
                  $$ = new OpAsgnNode(getPosition($1), $1, $5, (String) $3.getValue(), (String) $4.getValue());
              }
              | primary_value tCOLON2 tIDENTIFIER tOP_ASGN command_call {
                  $$ = new OpAsgnNode(getPosition($1), $1, $5, (String) $3.getValue(), (String) $4.getValue());
              }
              | backref tOP_ASGN command_call {
                  support.backrefAssignError($1);
              }
              | lhs '=' mrhs {
                  $$ = support.node_assign($1, new SValueNode(getPosition($1), $3));
              }
 	      | mlhs '=' arg_value {
                  if ($1.getHeadNode() != null) {
		      $1.setValueNode(new ToAryNode(getPosition($1), $3));
		  } else {
		      $1.setValueNode(new ArrayNode(getPosition($1), $3));
		  }
		  $$ = $1;
	      }
	      | mlhs '=' mrhs {
                  $<AssignableNode>1.setValueNode($3);
		  $$ = $1;
                  $1.setPosition(support.union($1, $3));
	      }
              | expr 

expr          : command_call 
              | expr kAND expr {
                  $$ = support.newAndNode($1, $3);
              }
              | expr kOR expr {
                  $$ = support.newOrNode($1, $3);
              }
              | kNOT expr {
                  $$ = new NotNode(support.union($1, $2), support.getConditionNode($2));
              }
              | tBANG command_call {
                  $$ = new NotNode(support.union($1, $2), support.getConditionNode($2));
              }
              | arg

expr_value    : expr {
                  support.checkExpression($1);
	      }

command_call  : command
              | block_command
              | kRETURN call_args {
                  $$ = new ReturnNode(support.union($1, $2), support.ret_args($2, getPosition($1)));
              }
              | kBREAK call_args {
                  $$ = new BreakNode(support.union($1, $2), support.ret_args($2, getPosition($1)));
              }
              | kNEXT call_args {
                  $$ = new NextNode(support.union($1, $2), support.ret_args($2, getPosition($1)));
              }

block_command : block_call
              | block_call tDOT operation2 command_args {
                  $$ = support.new_call($1, $3, $4, null);
              }
              | block_call tCOLON2 operation2 command_args {
                  $$ = support.new_call($1, $3, $4, null);
              }

cmd_brace_block	: tLBRACE_ARG {
                    support.pushBlockScope();
		} opt_block_var compstmt tRCURLY {
                    $$ = new IterNode(getPosition($1), $3, support.getCurrentScope(), $4);
                    support.popCurrentScope();
		}

command       : operation command_args  %prec tLOWEST {
                  $$ = support.new_fcall($1, $2, null);
              }
 	      | operation command_args cmd_brace_block {
                  $$ = support.new_fcall($1, $2, $3); 
              }
	      | primary_value tDOT operation2 command_args %prec tLOWEST {
                  $$ = support.new_call($1, $3, $4, null);
              }
 	      | primary_value tDOT operation2 command_args cmd_brace_block {
                  $$ = support.new_call($1, $3, $4, $5); 
	      }
              | primary_value tCOLON2 operation2 command_args %prec tLOWEST {
                  $$ = support.new_call($1, $3, $4, null);
              }
 	      | primary_value tCOLON2 operation2 command_args cmd_brace_block {
                  $$ = support.new_call($1, $3, $4, $5); 
	      }
              | kSUPER command_args {
		  $$ = support.new_super($2, $1); // .setPosFrom($2);
	      }
              | kYIELD command_args {
                  $$ = support.new_yield(getPosition($1), $2);
	      }

mlhs          : mlhs_basic
              | tLPAREN mlhs_entry tRPAREN {
                  $$ = $2;
	      }

mlhs_entry    : mlhs_basic
              | tLPAREN mlhs_entry tRPAREN {
                  $$ = new MultipleAsgnNode(getPosition($1), new ArrayNode(getPosition($1), $2), null);
              }

mlhs_basic    : mlhs_head {
                  $$ = new MultipleAsgnNode(getPosition($1), $1, null);
              }
              | mlhs_head mlhs_item {
//mirko: check
                  $$ = new MultipleAsgnNode(support.union($<Node>1, $<Node>2), $1.add($2), null);
                  $<Node>1.setPosition(support.union($<Node>1, $<Node>2));
              }
              | mlhs_head tSTAR mlhs_node {
                  $$ = new MultipleAsgnNode(getPosition($1), $1, $3);
              }
              | mlhs_head tSTAR {
                  $$ = new MultipleAsgnNode(getPosition($1), $1, new StarNode(getPosition(null)));
              }
              | tSTAR mlhs_node {
                  $$ = new MultipleAsgnNode(getPosition($1), null, $2);
              }
              | tSTAR {
                  $$ = new MultipleAsgnNode(getPosition($1), null, new StarNode(getPosition(null)));
              }

mlhs_item     : mlhs_node 
              | tLPAREN mlhs_entry tRPAREN {
                  $$ = $2;
              }

mlhs_head     : mlhs_item ',' {
                  $$ = new ArrayNode($1.getPosition(), $1);
              }
              | mlhs_head mlhs_item ',' {
                  $$ = $1.add($2);
              }

mlhs_node     : variable {
                  $$ = support.assignable($1, null);
              }
              | primary_value '[' aref_args tRBRACK {
                  $$ = support.aryset($1, $3);
              }
              | primary_value tDOT tIDENTIFIER {
                  $$ = support.attrset($1, (String) $3.getValue());
              }
              | primary_value tCOLON2 tIDENTIFIER {
                  $$ = support.attrset($1, (String) $3.getValue());
              }
              | primary_value tDOT tCONSTANT {
                  $$ = support.attrset($1, (String) $3.getValue());
              }
 	      | primary_value tCOLON2 tCONSTANT {
                  if (support.isInDef() || support.isInSingle()) {
		      yyerror("dynamic constant assignment");
		  }

		  ISourcePosition position = support.union($1, $3);

                  $$ = new ConstDeclNode(position, null, new Colon2Node(position, $1, (String) $3.getValue()), null);
	      }
 	      | tCOLON3 tCONSTANT {
                  if (support.isInDef() || support.isInSingle()) {
		      yyerror("dynamic constant assignment");
		  }

                  ISourcePosition position = support.union($1, $2);

                  $$ = new ConstDeclNode(position, null, new Colon3Node(position, (String) $2.getValue()), null);
	      }
              | backref {
	          support.backrefAssignError($1);
              }

lhs           : variable {
                  $$ = support.assignable($1, null);
              }
              | primary_value '[' aref_args tRBRACK {
                  $$ = support.aryset($1, $3);
              }
              | primary_value tDOT tIDENTIFIER {
                  $$ = support.attrset($1, (String) $3.getValue());
              }
              | primary_value tCOLON2 tIDENTIFIER {
                  $$ = support.attrset($1, (String) $3.getValue());
 	      }
              | primary_value tDOT tCONSTANT {
                  $$ = support.attrset($1, (String) $3.getValue());
              }
   	      | primary_value tCOLON2 tCONSTANT {
                  if (support.isInDef() || support.isInSingle()) {
		      yyerror("dynamic constant assignment");
		  }
			
		  ISourcePosition position = support.union($1, $3);

                  $$ = new ConstDeclNode(position, null, new Colon2Node(position, $1, (String) $3.getValue()), null);
              }
	      | tCOLON3 tCONSTANT {
                  if (support.isInDef() || support.isInSingle()) {
		      yyerror("dynamic constant assignment");
		  }

                  ISourcePosition position = support.union($1, $2);

                  $$ = new ConstDeclNode(position, null, new Colon3Node(position, (String) $2.getValue()), null);
	      }
              | backref {
                   support.backrefAssignError($1);
	      }

cname         : tIDENTIFIER {
                  yyerror("class/module name must be CONSTANT");
              }
              | tCONSTANT

cpath	      : tCOLON3 cname {
                  $$ = new Colon3Node(support.union($1, $2), (String) $2.getValue());
	      }
	      | cname {
                  $$ = new Colon2Node($1.getPosition(), null, (String) $1.getValue());
 	      }
	      | primary_value tCOLON2 cname {
                  $$ = new Colon2Node(support.union($1, $3), $1, (String) $3.getValue());
	      }

// Token:fname - A function name [!null]
fname         : tIDENTIFIER | tCONSTANT | tFID
              | op {
                  lexer.setState(LexState.EXPR_END);
                  $$ = $1;
              }
// FIXME: reswords is really Keyword which is not a Token...This should bomb
              | reswords {
                  lexer.setState(LexState.EXPR_END);
                  $$ = $<>1;
              }

fitem         : fname | symbol

undef_list    : fitem {
                  $$ = new UndefNode(getPosition($1), (String) $1.getValue());
              }
              | undef_list ',' {
                  lexer.setState(LexState.EXPR_FNAME);
	      } fitem {
                  $$ = support.appendToBlock($1, new UndefNode(getPosition($1), (String) $4.getValue()));
              }

// Token:op - inline operations [!null]
op            : tPIPE | tCARET | tAMPER2 | tCMP | tEQ | tEQQ | tMATCH | tGT
              | tGEQ | tLT | tLEQ | tLSHFT | tRSHFT | tPLUS  | tMINUS | tSTAR2
              | tSTAR | tDIVIDE | tPERCENT | tPOW | tTILDE | tUPLUS | tUMINUS
              | tAREF | tASET | tBACK_REF2

// Keyword:reswords - reserved words [!null]
reswords	: k__LINE__ | k__FILE__  | klBEGIN | klEND
		| kALIAS | kAND | kBEGIN | kBREAK | kCASE | kCLASS | kDEF
		| kDEFINED | kDO | kELSE | kELSIF | kEND | kENSURE | kFALSE
		| kFOR | kIN | kMODULE | kNEXT | kNIL | kNOT
		| kOR | kREDO | kRESCUE | kRETRY | kRETURN | kSELF | kSUPER
		| kTHEN | kTRUE | kUNDEF | kWHEN | kYIELD
		| kIF_MOD | kUNLESS_MOD | kWHILE_MOD | kUNTIL_MOD | kRESCUE_MOD

arg           : lhs '=' arg {
                  $$ = support.node_assign($1, $3);
		  // FIXME: Consider fixing node_assign itself rather than single case
		  $<Node>$.setPosition(support.union($1, $3));
              }
	      | lhs '=' arg kRESCUE_MOD arg {
                  ISourcePosition position = support.union($4, $5);
                  $$ = support.node_assign($1, new RescueNode(position, $3, new RescueBodyNode(position, null, $5, null), null));
	      }
	      | var_lhs tOP_ASGN arg {
		  support.checkExpression($3);
	          String name = $<INameNode>1.getName();
		  String asgnOp = (String) $2.getValue();

		  if (asgnOp.equals("||")) {
	              $1.setValueNode($3);
	              $$ = new OpAsgnOrNode(support.union($1, $3), support.gettable2(name, $1.getPosition()), $1);
		  } else if (asgnOp.equals("&&")) {
	              $1.setValueNode($3);
                      $$ = new OpAsgnAndNode(support.union($1, $3), support.gettable2(name, $1.getPosition()), $1);
		  } else {
		      $1.setValueNode(support.getOperatorCallNode(support.gettable2(name, $1.getPosition()), asgnOp, $3));
                      $1.setPosition(support.union($1, $3));
		      $$ = $1;
		  }
              }
              | primary_value '[' aref_args tRBRACK tOP_ASGN arg {
                  $$ = new OpElementAsgnNode(getPosition($1), $1, (String) $5.getValue(), $3, $6);
              }
              | primary_value tDOT tIDENTIFIER tOP_ASGN arg {
                  $$ = new OpAsgnNode(getPosition($1), $1, $5, (String) $3.getValue(), (String) $4.getValue());
              }
              | primary_value tDOT tCONSTANT tOP_ASGN arg {
                  $$ = new OpAsgnNode(getPosition($1), $1, $5, (String) $3.getValue(), (String) $4.getValue());
              }
              | primary_value tCOLON2 tIDENTIFIER tOP_ASGN arg {
                  $$ = new OpAsgnNode(getPosition($1), $1, $5, (String) $3.getValue(), (String) $4.getValue());
              }
	      | primary_value tCOLON2 tCONSTANT tOP_ASGN arg {
	          yyerror("constant re-assignment");
	      }
	      | tCOLON3 tCONSTANT tOP_ASGN arg {
		  yyerror("constant re-assignment");
	      }
              | backref tOP_ASGN arg {
                  support.backrefAssignError($1);
              }
              | arg tDOT2 arg {
		  support.checkExpression($1);
		  support.checkExpression($3);
                  $$ = new DotNode(support.union($1, $3), $1, $3, false);
              }
              | arg tDOT3 arg {
		  support.checkExpression($1);
		  support.checkExpression($3);
                  $$ = new DotNode(support.union($1, $3), $1, $3, true);
              }
              | arg tPLUS arg {
                  $$ = support.getOperatorCallNode($1, "+", $3, getPosition(null));
              }
              | arg tMINUS arg {
                  $$ = support.getOperatorCallNode($1, "-", $3, getPosition(null));
              }
              | arg tSTAR2 arg {
                  $$ = support.getOperatorCallNode($1, "*", $3, getPosition(null));
              }
              | arg tDIVIDE arg {
                  $$ = support.getOperatorCallNode($1, "/", $3, getPosition(null));
              }
              | arg tPERCENT arg {
                  $$ = support.getOperatorCallNode($1, "%", $3, getPosition(null));
              }
              | arg tPOW arg {
		  $$ = support.getOperatorCallNode($1, "**", $3, getPosition(null));
              }
	      | tUMINUS_NUM tINTEGER tPOW arg {
                  $$ = support.getOperatorCallNode(support.getOperatorCallNode($2, "**", $4, getPosition(null)), "-@");
              }
	      | tUMINUS_NUM tFLOAT tPOW arg {
                  $$ = support.getOperatorCallNode(support.getOperatorCallNode($2, "**", $4, getPosition(null)), "-@");
              }
              | tUPLUS arg {
                  if (support.isLiteral($2)) {
		      $$ = $2;
		  } else {
                      $$ = support.getOperatorCallNode($2, "+@");
		  }
              }
	      | tUMINUS arg {
                  $$ = support.getOperatorCallNode($2, "-@");
	      }
              | arg tPIPE arg {
                  $$ = support.getOperatorCallNode($1, "|", $3, getPosition(null));
              }
              | arg tCARET arg {
                  $$ = support.getOperatorCallNode($1, "^", $3, getPosition(null));
              }
              | arg tAMPER2 arg {
                  $$ = support.getOperatorCallNode($1, "&", $3, getPosition(null));
              }
              | arg tCMP arg {
                  $$ = support.getOperatorCallNode($1, "<=>", $3, getPosition(null));
              }
              | arg tGT arg {
                  $$ = support.getOperatorCallNode($1, ">", $3, getPosition(null));
              }
              | arg tGEQ arg {
                  $$ = support.getOperatorCallNode($1, ">=", $3, getPosition(null));
              }
              | arg tLT arg {
                  $$ = support.getOperatorCallNode($1, "<", $3, getPosition(null));
              }
              | arg tLEQ arg {
                  $$ = support.getOperatorCallNode($1, "<=", $3, getPosition(null));
              }
              | arg tEQ arg {
                  $$ = support.getOperatorCallNode($1, "==", $3, getPosition(null));
              }
              | arg tEQQ arg {
                  $$ = support.getOperatorCallNode($1, "===", $3, getPosition(null));
              }
              | arg tNEQ arg {
                  $$ = new NotNode(support.union($1, $3), support.getOperatorCallNode($1, "==", $3, getPosition(null)));
              }
              | arg tMATCH arg {
                  $$ = support.getMatchNode($1, $3);
              }
              | arg tNMATCH arg {
                  $$ = new NotNode(support.union($1, $3), support.getMatchNode($1, $3));
              }
              | tBANG arg {
                  $$ = new NotNode(support.union($1, $2), support.getConditionNode($2));
              }
              | tTILDE arg {
                  $$ = support.getOperatorCallNode($2, "~");
              }
              | arg tLSHFT arg {
                  $$ = support.getOperatorCallNode($1, "<<", $3, getPosition(null));
              }
              | arg tRSHFT arg {
                  $$ = support.getOperatorCallNode($1, ">>", $3, getPosition(null));
              }
              | arg tANDOP arg {
                  $$ = support.newAndNode($1, $3);
              }
              | arg tOROP arg {
                  $$ = support.newOrNode($1, $3);
              }
              | kDEFINED opt_nl arg {
                  $$ = new DefinedNode(getPosition($1), $3);
              }
              | arg '?' arg ':' arg {
                  $$ = new IfNode(getPosition($1), support.getConditionNode($1), $3, $5);
              }
              | primary {
                  $$ = $1;
              }

arg_value     : arg {
	          support.checkExpression($1);
	          $$ = $1;   
	      }

aref_args     : none
              | command opt_nl {
                  warnings.warn(getPosition($1), "parenthesize argument(s) for future version");
                  $$ = new ArrayNode(getPosition($1), $1);
              }
              | args trailer {
                  $$ = $1;
              }
              | args ',' tSTAR arg opt_nl {
                  support.checkExpression($4);
                  $$ = support.arg_concat(getPosition($1), $1, $4);
              }
              | assocs trailer {
                  $$ = new ArrayNode(getPosition($1), new HashNode(getPosition(null), $1));
              }
              | tSTAR arg opt_nl {
                  support.checkExpression($2);
		  $$ = new NewlineNode(getPosition($1), new SplatNode(getPosition($1), $2));
              }

paren_args    : tLPAREN2 none tRPAREN {
                  $$ = new ArrayNode(support.union($1, $3));
              }
              | tLPAREN2 call_args opt_nl tRPAREN {
                  $$ = $2;
		  $<Node>$.setPosition(support.union($1, $4));
              }
              | tLPAREN2 block_call opt_nl tRPAREN {
                  warnings.warn(getPosition($1), "parenthesize argument(s) for future version");
                  $$ = new ArrayNode(getPosition($1), $2);
              }
              | tLPAREN2 args ',' block_call opt_nl tRPAREN {
                  warnings.warn(getPosition($1), "parenthesize argument(s) for future version");
                  $$ = $2.add($4);
              }

opt_paren_args: none | paren_args 

// Node:call_args - Arguments for a function call
call_args     : command {
                  warnings.warn($1.getPosition(), "parenthesize argument(s) for future version");
                  $$ = new ArrayNode(getPosition($1), $1);
              }
              | args opt_block_arg {
                  $$ = support.arg_blk_pass($1, $2);
              }
              | args ',' tSTAR arg_value opt_block_arg {
                  $$ = support.arg_concat(getPosition($1), $1, $4);
                  $$ = support.arg_blk_pass($<Node>$, $5);
              }
              | assocs opt_block_arg {
                  $$ = new ArrayNode(getPosition($1), new HashNode(getPosition(null), $1));
                  $$ = support.arg_blk_pass((Node)$$, $2);
              }
              | assocs ',' tSTAR arg_value opt_block_arg {
                  $$ = support.arg_concat(getPosition($1), new ArrayNode(getPosition($1), new HashNode(getPosition(null), $1)), $4);
                  $$ = support.arg_blk_pass((Node)$$, $5);
              }
              | args ',' assocs opt_block_arg {
                  $$ = $1.add(new HashNode(getPosition(null), $3));
                  $$ = support.arg_blk_pass((Node)$$, $4);
              }
              | args ',' assocs ',' tSTAR arg opt_block_arg {
                  support.checkExpression($6);
		  $$ = support.arg_concat(getPosition($1), $1.add(new HashNode(getPosition(null), $3)), $6);
                  $$ = support.arg_blk_pass((Node)$$, $7);
              }
              | tSTAR arg_value opt_block_arg {
                  $$ = support.arg_blk_pass(new SplatNode(getPosition($1), $2), $3);
              }
              | block_arg {}

call_args2    : arg_value ',' args opt_block_arg {
                  $$ = support.arg_blk_pass(new ArrayNode(getPosition($1), $1).addAll($3), $4);
	      }
	      | arg_value ',' block_arg {
                  $$ = support.arg_blk_pass(new ArrayNode(getPosition($1), $1), $3);
              }
              | arg_value ',' tSTAR arg_value opt_block_arg {
                  $$ = support.arg_concat(getPosition($1), new ArrayNode(getPosition($1), $1), $4);
                  $$ = support.arg_blk_pass((Node)$$, $5);
	      }
	      | arg_value ',' args ',' tSTAR arg_value opt_block_arg {
                  $$ = support.arg_concat(getPosition($1), new ArrayNode(getPosition($1), $1).addAll(new HashNode(getPosition(null), $3)), $6);
                  $$ = support.arg_blk_pass((Node)$$, $7);
	      }
	      | assocs opt_block_arg {
                  $$ = new ArrayNode(getPosition($1), new HashNode(getPosition(null), $1));
                  $$ = support.arg_blk_pass((Node)$$, $2);
	      }
	      | assocs ',' tSTAR arg_value opt_block_arg {
                  $$ = support.arg_concat(getPosition($1), new ArrayNode(getPosition($1), new HashNode(getPosition(null), $1)), $4);
                  $$ = support.arg_blk_pass((Node)$$, $5);
	      }
	      | arg_value ',' assocs opt_block_arg {
                  $$ = new ArrayNode(getPosition($1), $1).add(new HashNode(getPosition(null), $3));
                  $$ = support.arg_blk_pass((Node)$$, $4);
	      }
	      | arg_value ',' args ',' assocs opt_block_arg {
                  $$ = new ArrayNode(getPosition($1), $1).addAll($3).add(new HashNode(getPosition(null), $5));
                  $$ = support.arg_blk_pass((Node)$$, $6);
	      }
	      | arg_value ',' assocs ',' tSTAR arg_value opt_block_arg {
                  $$ = support.arg_concat(getPosition($1), new ArrayNode(getPosition($1), $1).add(new HashNode(getPosition(null), $3)), $6);
                  $$ = support.arg_blk_pass((Node)$$, $7);
	      }
	      | arg_value ',' args ',' assocs ',' tSTAR arg_value opt_block_arg {
                  $$ = support.arg_concat(getPosition($1), new ArrayNode(getPosition($1), $1).addAll($3).add(new HashNode(getPosition(null), $5)), $8);
                  $$ = support.arg_blk_pass((Node)$$, $9);
	      }
	      | tSTAR arg_value opt_block_arg {
                  $$ = support.arg_blk_pass(new SplatNode(getPosition($1), $2), $3);
	      }
	      | block_arg {}

command_args  : /* none */ { 
	          $$ = new Long(lexer.getCmdArgumentState().begin());
	      } open_args {
                  lexer.getCmdArgumentState().reset($<Long>1.longValue());
                  $$ = $2;
              }

 open_args    : call_args
	      | tLPAREN_ARG  {                    
		  lexer.setState(LexState.EXPR_ENDARG);
	      } tRPAREN {
                  warnings.warn(getPosition($1), "don't put space before argument parentheses");
	          $$ = null;
	      }
	      | tLPAREN_ARG call_args2 {
		  lexer.setState(LexState.EXPR_ENDARG);
	      } tRPAREN {
                  warnings.warn(getPosition($1), "don't put space before argument parentheses");
		  $$ = $2;
	      }

block_arg     : tAMPER arg_value {
                  support.checkExpression($2);
                  $$ = new BlockPassNode(support.union($1, $2), $2);
              }

opt_block_arg : ',' block_arg {
                  $$ = $2;
              }
              | none_block_pass

args          : arg_value {
                  $$ = new ArrayNode(getPosition2($1), $1);
              }
              | args ',' arg_value {
                  $$ = $1.add($3);
              }

mrhs          : args ',' arg_value {
		  $$ = $1.add($3);
              }
 	      | args ',' tSTAR arg_value {
                  $$ = support.arg_concat(getPosition($1), $1, $4);
	      }
              | tSTAR arg_value {  
                  $$ = new SplatNode(getPosition($1), $2);
	      }

primary       : literal
              | strings
              | xstring 
              | regexp
              | words
              | qwords
	      | var_ref
	      | backref
	      | tFID {
                  $$ = new FCallNode($1.getPosition(), (String) $1.getValue(), null);
	      }
              | kBEGIN bodystmt kEND {
                  $$ = new BeginNode(support.union($1, $3), $2);
	      }
              | tLPAREN_ARG expr { 
                  lexer.setState(LexState.EXPR_ENDARG); 
              } opt_nl tRPAREN {
		  warnings.warning(getPosition($1), "(...) interpreted as grouped expression");
                  $$ = $2;
	      }
              | tLPAREN compstmt tRPAREN {
		  $$ = $2;
              }
              | primary_value tCOLON2 tCONSTANT {
                  $$ = new Colon2Node(support.union($1, $3), $1, (String) $3.getValue());
              }
              | tCOLON3 tCONSTANT {
                  $$ = new Colon3Node(support.union($1, $2), (String) $2.getValue());
              }
              | primary_value '[' aref_args tRBRACK {
                  if ($1 instanceof SelfNode) {
                      $$ = new FCallNode(getPosition($1), "[]", $3);
                  } else {
                      $$ = new CallNode(getPosition($1), $1, "[]", $3);
                  }
              }
              | tLBRACK aref_args tRBRACK {
                  ISourcePosition position = support.union($1, $3);
                  if ($2 == null) {
                      $$ = new ZArrayNode(position); /* zero length array */
                  } else {
                      $$ = $2;
                      $<ISourcePositionHolder>$.setPosition(position);
                  }
              }
              | tLBRACE assoc_list tRCURLY {
                  $$ = new HashNode(support.union($1, $3), $2);
              }
              | kRETURN {
		  $$ = new ReturnNode($1.getPosition(), null);
              }
              | kYIELD tLPAREN2 call_args tRPAREN {
                  $$ = support.new_yield(support.union($1, $4), $3);
              }
              | kYIELD tLPAREN2 tRPAREN {
                  $$ = new YieldNode(support.union($1, $3), null, false);
              }
              | kYIELD {
                  $$ = new YieldNode($1.getPosition(), null, false);
              }
              | kDEFINED opt_nl tLPAREN2 expr tRPAREN {
                  $$ = new DefinedNode(getPosition($1), $4);
              }
              | operation brace_block {
                  $$ = new FCallNode(support.union($1, $2), (String) $1.getValue(), null, $2);
              }
              | method_call
              | method_call brace_block {
	          if ($1 != null && 
                      $<BlockAcceptingNode>1.getIterNode() instanceof BlockPassNode) {
                      throw new SyntaxException(getPosition($1), "Both block arg and actual block given.");
		  }
		  $<BlockAcceptingNode>1.setIterNode($2);
		  $<Node>1.setPosition(support.union($1, $2));
              }
              | kIF expr_value then compstmt if_tail kEND {
                  $$ = new IfNode(support.union($1, $6), support.getConditionNode($2), $4, $5);
              }
              | kUNLESS expr_value then compstmt opt_else kEND {
                  $$ = new IfNode(support.union($1, $6), support.getConditionNode($2), $5, $4);
              }
              | kWHILE { 
                  lexer.getConditionState().begin();
	      } expr_value do {
		  lexer.getConditionState().end();
	      } compstmt kEND {
                  $$ = new WhileNode(support.union($1, $7), support.getConditionNode($3), $6);
              }
              | kUNTIL {
                  lexer.getConditionState().begin();
              } expr_value do {
                  lexer.getConditionState().end();
              } compstmt kEND {
                  $$ = new UntilNode(getPosition($1), support.getConditionNode($3), $6);
              }
              | kCASE expr_value opt_terms case_body kEND {
                  $$ = new CaseNode(support.union($1, $5), $2, $4);
              }
              | kCASE opt_terms case_body kEND {
                  $$ = new CaseNode(support.union($1, $4), null, $3);
              }
              | kCASE opt_terms kELSE compstmt kEND {
		  $$ = $4;
              }
              | kFOR block_var kIN {
                  lexer.getConditionState().begin();
              } expr_value do {
                  lexer.getConditionState().end();
              } compstmt kEND {
                  $$ = new ForNode(support.union($1, $9), $2, $8, $5);
              }
              | kCLASS cpath superclass {
                  if (support.isInDef() || support.isInSingle()) {
                      yyerror("class definition in method body");
                  }
		  support.pushLocalScope();
              } bodystmt kEND {
                  $$ = new ClassNode(support.union($1, $6), $<Colon3Node>2, support.getCurrentScope(), $5, $3);
                  support.popCurrentScope();
              }
              | kCLASS tLSHFT expr {
                  $$ = new Boolean(support.isInDef());
                  support.setInDef(false);
              } term {
                  $$ = new Integer(support.getInSingle());
                  support.setInSingle(0);
		  support.pushLocalScope();
              } bodystmt kEND {
                  $$ = new SClassNode(support.union($1, $8), $3, support.getCurrentScope(), $7);
                  support.popCurrentScope();
                  support.setInDef($<Boolean>4.booleanValue());
                  support.setInSingle($<Integer>6.intValue());
              }
              | kMODULE cpath {
                  if (support.isInDef() || support.isInSingle()) { 
                      yyerror("module definition in method body");
                  }
		  support.pushLocalScope();
              } bodystmt kEND {
                  $$ = new ModuleNode(support.union($1, $5), $<Colon3Node>2, support.getCurrentScope(), $4);
                  support.popCurrentScope();
              }
	      | kDEF fname {
                  support.setInDef(true);
		  support.pushLocalScope();
              } f_arglist bodystmt kEND {
                    /* NOEX_PRIVATE for toplevel */
                  $$ = new DefnNode(support.union($1, $6), new ArgumentNode($2.getPosition(), (String) $2.getValue()), $<ArgsNode>4, support.getCurrentScope(), $5, Visibility.PRIVATE);
                  support.popCurrentScope();
                  support.setInDef(false);
              }
              | kDEF singleton dot_or_colon {
                  lexer.setState(LexState.EXPR_FNAME);
              } fname {
                  support.setInSingle(support.getInSingle() + 1);
		  support.pushLocalScope();
                  lexer.setState(LexState.EXPR_END); /* force for args */
              } f_arglist bodystmt kEND {
                  $$ = new DefsNode(support.union($1, $9), $2, new ArgumentNode($5.getPosition(), (String) $5.getValue()), $<ArgsNode>7, support.getCurrentScope(), $8);
                  support.popCurrentScope();
                  support.setInSingle(support.getInSingle() - 1);
              }
              | kBREAK {
                  $$ = new BreakNode($1.getPosition());
              }
              | kNEXT {
                  $$ = new NextNode($1.getPosition());
              }
              | kREDO {
                  $$ = new RedoNode($1.getPosition());
              }
              | kRETRY {
                  $$ = new RetryNode($1.getPosition());
              }

primary_value : primary {
                  support.checkExpression($1);
		  $$ = $1;
	      }
 
then          : term
              | ":"
              | kTHEN
              | term kTHEN

do            : term
              | ":"
              | kDO_COND

if_tail       : opt_else 
              | kELSIF expr_value then compstmt if_tail {
//mirko: support.union($<ISourcePositionHolder>1.getPosition(), getPosition($<ISourcePositionHolder>1)) ?
                  $$ = new IfNode(getPosition($1), support.getConditionNode($2), $4, $5);
              }

opt_else      : none 
              | kELSE compstmt {
                  $$ = $2;
              }

block_var     : lhs
              | mlhs {}

opt_block_var : none
              | tPIPE /* none */ tPIPE {
                  $$ = new ZeroArgNode(support.union($1, $2));
              }
              | tOROP {
                  $$ = new ZeroArgNode($1.getPosition());
	      }
              | tPIPE block_var tPIPE {
                  $$ = $2;

		  // Include pipes on multiple arg type
                  if ($2 instanceof MultipleAsgnNode) {
		      $2.setPosition(support.union($1, $3));
		  } 
              }

do_block      : kDO_BLOCK {
                  support.pushBlockScope();
	      } opt_block_var compstmt kEND {
                  $$ = new IterNode(support.union($1, $5), $3, support.getCurrentScope(), $4);
                  support.popCurrentScope();
              }

block_call    : command do_block {
	          if ($1 != null && 
                      $<BlockAcceptingNode>1.getIterNode() instanceof BlockPassNode) {
                      throw new SyntaxException(getPosition($1), "Both block arg and actual block given.");
                  }
		  $<BlockAcceptingNode>1.setIterNode($2);
		  $<Node>1.setPosition(support.union($1, $2));
              }
              | block_call tDOT operation2 opt_paren_args {
                  $$ = support.new_call($1, $3, $4, null);
              }
              | block_call tCOLON2 operation2 opt_paren_args {
                  $$ = support.new_call($1, $3, $4, null);
              }

method_call   : operation paren_args {
                  $$ = support.new_fcall($1, $2, null);
              }
              | primary_value tDOT operation2 opt_paren_args {
                  $$ = support.new_call($1, $3, $4, null);
              }
              | primary_value tCOLON2 operation2 paren_args {
                  $$ = support.new_call($1, $3, $4, null);
              }
              | primary_value tCOLON2 operation3 {
                  $$ = support.new_call($1, $3, null, null);
              }
              | kSUPER paren_args {
                  $$ = support.new_super($2, $1);
              }
              | kSUPER {
                  $$ = new ZSuperNode($1.getPosition());
              }

// IterNode:brace_block - block invocation argument (foo >{...}< | foo >do end<) [!null]
brace_block   : tLCURLY {
                  support.pushBlockScope();
	      } opt_block_var compstmt tRCURLY {
                  $$ = new IterNode(support.union($1, $5), $3, support.getCurrentScope(), $4);
                  support.popCurrentScope();
              }
              | kDO {
                  support.pushBlockScope();
	      } opt_block_var compstmt kEND {
                  $$ = new IterNode(support.union($1, $5), $3, support.getCurrentScope(), $4);
                  $<ISourcePositionHolder>0.setPosition(support.union($<ISourcePositionHolder>0, $<ISourcePositionHolder>$));
                  support.popCurrentScope();
              }

case_body     : kWHEN when_args then compstmt cases {
                  $$ = new WhenNode(support.union($1, support.unwrapNewlineNode($4)), $2, $4, $5);
              }

when_args     : args
              | args ',' tSTAR arg_value {
                  $$ = $1.add(new WhenNode(getPosition($1), $4, null, null));
              }
              | tSTAR arg_value {
                  $$ = new ArrayNode(getPosition($1), new WhenNode(getPosition($1), $2, null, null));
              }

cases         : opt_else | case_body


opt_rescue    : kRESCUE exc_list exc_var then compstmt opt_rescue {
                  Node node;
                  if ($3 != null) {
                     node = support.appendToBlock(support.node_assign($3, new GlobalVarNode(getPosition($1), "$!")), $5);
                     if($5 != null) {
                        node.setPosition(support.unwrapNewlineNode($5).getPosition());
                     }
		  } else {
		     node = $5;
                  }
                  $$ = new RescueBodyNode(getPosition($1, true), $2, node, $6);
	      }
              | {$$ = null;}

exc_list      : arg_value {
                  $$ = new ArrayNode($1.getPosition(), $1);
	      }
              | mrhs
	      | none

exc_var       : tASSOC lhs {
                  $$ = $2;
              }
              | none

opt_ensure    : kENSURE compstmt {
                  if ($2 != null) {
                      $$ = $2;
                  } else {
                      $$ = new NilNode(getPosition(null));
                  }
              }
              | none

literal       : numeric
              | symbol {
                  $$ = new SymbolNode($1.getPosition(), (String) $1.getValue());
              }
              | dsym

strings       : string {
                  if ($1 instanceof EvStrNode) {
                      $$ = new DStrNode(getPosition($1)).add($1);
                  } else {
                      $$ = $1;
                  }
	      } 

string        : string1
              | string string1 {
                  $$ = support.literal_concat(getPosition($1), $1, $2);
              }

string1       : tSTRING_BEG string_contents tSTRING_END {
                  $$ = $2;
                  $<ISourcePositionHolder>$.setPosition(support.union($1, $3));
		  int extraLength = ((String) $1.getValue()).length() - 1;

                  // We may need to subtract addition offset off of first 
		  // string fragment (we optimistically take one off in
		  // ParserSupport.literal_concat).  Check token length
		  // and subtract as neeeded.
		  if (($2 instanceof DStrNode) && extraLength > 0) {
		     Node strNode = ((DStrNode)$2).get(0);
		     assert strNode != null;
		     strNode.getPosition().adjustStartOffset(-extraLength);
		  }
              }

xstring	      : tXSTRING_BEG xstring_contents tSTRING_END {
                  ISourcePosition position = support.union($1, $3);

		  if ($2 == null) {
		      $$ = new XStrNode(position, null);
		  } else if ($2 instanceof StrNode) {
                      $$ = new XStrNode(position, (ByteList) $<StrNode>2.getValue().clone());
		  } else if ($2 instanceof DStrNode) {
                      $$ = new DXStrNode(position, $<DStrNode>2);

                      $<Node>$.setPosition(position);
                  } else {
                      $$ = new DXStrNode(position).add($2);
		  }
              }

regexp	      : tREGEXP_BEG xstring_contents tREGEXP_END {
		  int options = $3.getOptions();
		  Node node = $2;

		  if (node == null) {
                      $$ = new RegexpNode(getPosition($1), ByteList.create(""), options & ~ReOptions.RE_OPTION_ONCE);
		  } else if (node instanceof StrNode) {
                      $$ = new RegexpNode($2.getPosition(), (ByteList) ((StrNode) node).getValue().clone(), options & ~ReOptions.RE_OPTION_ONCE);
		  } else if (node instanceof DStrNode) {
                      $$ = new DRegexpNode(getPosition($1), (DStrNode) node, options, (options & ReOptions.RE_OPTION_ONCE) != 0);
		  } else {
		      $$ = new DRegexpNode(getPosition($1), options, (options & ReOptions.RE_OPTION_ONCE) != 0).add(node);
                  }
	       }

words	       : tWORDS_BEG ' ' tSTRING_END {
                   $$ = new ZArrayNode(support.union($1, $3));
	       }
	       | tWORDS_BEG word_list tSTRING_END {
		   $$ = $2;
                   $<ISourcePositionHolder>$.setPosition(support.union($1, $3));
	       }

word_list      : /* none */ {
                   $$ = new ArrayNode(getPosition(null));
	       }
	       | word_list word ' ' {
                   $$ = $1.add($2 instanceof EvStrNode ? new DStrNode(getPosition($1)).add($2) : $2);
	       }

word	       : string_content
	       | word string_content {
                   $$ = support.literal_concat(getPosition($1), $1, $2);
	       }

qwords	       : tQWORDS_BEG ' ' tSTRING_END {
                   $$ = new ZArrayNode(support.union($1, $3));
	       }
	       | tQWORDS_BEG qword_list tSTRING_END {
		   $$ = $2;
                   $<ISourcePositionHolder>$.setPosition(support.union($1, $3));
	       }

qword_list     : /* none */ {
                   $$ = new ArrayNode(getPosition(null));
	       }
	       | qword_list tSTRING_CONTENT ' ' {
                   $$ = $1.add($2);
	       }

string_contents: /* none */ {
                   $$ = new StrNode($<Token>0.getPosition(), ByteList.create(""));
	       }
	       | string_contents string_content {
                   $$ = support.literal_concat(getPosition($1), $1, $2);
	       }

xstring_contents: /* none */ {
		   $$ = null;
	       }
	       | xstring_contents string_content {
                   $$ = support.literal_concat(getPosition($1), $1, $2);
	       }

string_content : tSTRING_CONTENT {
                   $$ = $1;
               }
	       | tSTRING_DVAR {
                   $$ = lexer.getStrTerm();
		   lexer.setStrTerm(null);
		   lexer.setState(LexState.EXPR_BEG);
	       } string_dvar {
		   lexer.setStrTerm($<StrTerm>2);
	           $$ = new EvStrNode(support.union($1, $3), $3);
	       }
	       | tSTRING_DBEG {
		   $$ = lexer.getStrTerm();
		   lexer.setStrTerm(null);
		   lexer.setState(LexState.EXPR_BEG);
	       } compstmt tRCURLY {
		   lexer.setStrTerm($<StrTerm>2);

		   $$ = support.newEvStrNode(support.union($1, $4), $3);
	       }

string_dvar    : tGVAR {
                   $$ = new GlobalVarNode($1.getPosition(), (String) $1.getValue());
               }
	       | tIVAR {
                   $$ = new InstVarNode($1.getPosition(), (String) $1.getValue());
               }
	       | tCVAR {
                   $$ = new ClassVarNode($1.getPosition(), (String) $1.getValue());
               }
	       | backref


symbol         : tSYMBEG sym {
                   lexer.setState(LexState.EXPR_END);
                   $$ = $2;
		   $<ISourcePositionHolder>$.setPosition(support.union($1, $2));
               }

sym            : fname | tIVAR | tGVAR | tCVAR

dsym	       : tSYMBEG xstring_contents tSTRING_END {
                   lexer.setState(LexState.EXPR_END);

		   // DStrNode: :"some text #{some expression}"
                   // StrNode: :"some text"
		   // EvStrNode :"#{some expression}"
                   if ($2 == null) {
                       yyerror("empty symbol literal");
                   }

		   if ($2 instanceof DStrNode) {
		       $$ = new DSymbolNode(support.union($1, $3), $<DStrNode>2);
		   } else {
                       ISourcePosition position = support.union($2, $3);

                       // We substract one since tsymbeg is longer than one
		       // and we cannot union it directly so we assume quote
                       // is one character long and subtract for it.
		       position.adjustStartOffset(-1);
                       $2.setPosition(position);
		       
		       $$ = new DSymbolNode(support.union($1, $3));
                       $<DSymbolNode>$.add($2);
                   }
	       }

// Node:numeric - numeric value [!null]
numeric        : tINTEGER | tFLOAT {
                   $$ = $1;
               }
	       | tUMINUS_NUM tINTEGER	       %prec tLOWEST {
                   $$ = support.negateInteger($2);
	       }
	       | tUMINUS_NUM tFLOAT	       %prec tLOWEST {
                   $$ = support.negateFloat($2);
	       }

// Token:variable - name (special and normal onces)
variable       : tIDENTIFIER | tIVAR | tGVAR | tCONSTANT | tCVAR
               | kNIL { 
		   $$ = new Token("nil", $1.getPosition());
               }
               | kSELF {
		   $$ = new Token("self", $1.getPosition());
               }
               | kTRUE { 
		   $$ = new Token("true", $1.getPosition());
               }
               | kFALSE {
		   $$ = new Token("false", $1.getPosition());
               }
               | k__FILE__ {
		   $$ = new Token("__FILE__", $1.getPosition());
               }
               | k__LINE__ {
		   $$ = new Token("__LINE__", $1.getPosition());
               }

var_ref        : variable {
		   $$ = support.gettable((String) $1.getValue(), $1.getPosition());
               }

var_lhs	       : variable {
                   $$ = support.assignable($1, null);
               }

backref        : tNTH_REF | tBACK_REF

superclass     : term {
                   $$ = null;
               }
               | tLT {
                   lexer.setState(LexState.EXPR_BEG);
               } expr_value term {
                   $$ = $3;
               }
               | error term {
                   yyerrok();
                   $$ = null;
               }

// f_arglist: Function Argument list for definitions
f_arglist      : tLPAREN2 f_args opt_nl tRPAREN {
                   $$ = $2;
                   $<ISourcePositionHolder>$.setPosition(support.union($1, $4));
                   lexer.setState(LexState.EXPR_BEG);
               }
               | f_args term {
                   $$ = $1;
               }

f_args         : f_arg ',' f_optarg ',' f_rest_arg opt_f_block_arg {
                   $$ = new ArgsNode(support.union($1, $6), $1, $3, ((Integer) $5.getValue()).intValue(), support.getRestArgNode($5), $6);
               }
               | f_arg ',' f_optarg opt_f_block_arg {
                   $$ = new ArgsNode(getPosition($1), $1, $3, -1, null, $4);
               }
               | f_arg ',' f_rest_arg opt_f_block_arg {
                   $$ = new ArgsNode(support.union($1, $4), $1, null, ((Integer) $3.getValue()).intValue(), support.getRestArgNode($3), $4);
               }
               | f_arg opt_f_block_arg {
                   $$ = new ArgsNode($<ISourcePositionHolder>1.getPosition(), $1, null, -1, null, $2);
               }
               | f_optarg ',' f_rest_arg opt_f_block_arg {
                   $$ = new ArgsNode(getPosition($1), null, $1, ((Integer) $3.getValue()).intValue(), support.getRestArgNode($3), $4);
               }
               | f_optarg opt_f_block_arg {
                   $$ = new ArgsNode(getPosition($1), null, $1, -1, null, $2);
               }
               | f_rest_arg opt_f_block_arg {
                   $$ = new ArgsNode(getPosition($1), null, null, ((Integer) $1.getValue()).intValue(), support.getRestArgNode($1), $2);
               }
               | f_block_arg {
                   $$ = new ArgsNode(getPosition($1), null, null, -1, null, $1);
               }
               | /* none */ {
                   $$ = new ArgsNode(support.createEmptyArgsNodePosition(getPosition(null)), null, null, -1, null, null);
               }

f_norm_arg     : tCONSTANT {
                   yyerror("formal argument cannot be a constant");
               }
               | tIVAR {
                   yyerror("formal argument cannot be an instance variable");
               }
               | tCVAR {
                   yyerror("formal argument cannot be a class variable");
               }
               | tIDENTIFIER {
                   String identifier = (String) $1.getValue();
                   if (IdUtil.getVarType(identifier) != IdUtil.LOCAL_VAR) {
                       yyerror("formal argument must be local variable");
                   } else if (support.getCurrentScope().getLocalScope().isDefined(identifier) >= 0) {
                       yyerror("duplicate argument name");
                   }

		   support.getCurrentScope().getLocalScope().addVariable(identifier);
                   $$ = $1;
               }

f_arg          : f_norm_arg {
                    $$ = new ListNode($<ISourcePositionHolder>1.getPosition());
                    ((ListNode) $$).add(new ArgumentNode($<ISourcePositionHolder>1.getPosition(), (String) $1.getValue()));
               }
               | f_arg ',' f_norm_arg {
                   $1.add(new ArgumentNode($<ISourcePositionHolder>3.getPosition(), (String) $3.getValue()));
                   $1.setPosition(support.union($1, $3));
		   $$ = $1;
               }

f_opt          : tIDENTIFIER '=' arg_value {
                   String identifier = (String) $1.getValue();

                   if (IdUtil.getVarType(identifier) != IdUtil.LOCAL_VAR) {
                       yyerror("formal argument must be local variable");
                   } else if (support.getCurrentScope().getLocalScope().isDefined(identifier) >= 0) {
                       yyerror("duplicate optional argument name");
                   }
		   support.getCurrentScope().getLocalScope().addVariable(identifier);
                   $$ = support.assignable($1, $3);
              }

f_optarg      : f_opt {
                  $$ = new BlockNode(getPosition($1)).add($1);
              }
              | f_optarg ',' f_opt {
                  $$ = support.appendToBlock($1, $3);
              }

restarg_mark  : tSTAR2 | tSTAR

f_rest_arg    : restarg_mark tIDENTIFIER {
                  String identifier = (String) $2.getValue();

                  if (IdUtil.getVarType(identifier) != IdUtil.LOCAL_VAR) {
                      yyerror("rest argument must be local variable");
                   } else if (support.getCurrentScope().getLocalScope().isDefined(identifier) >= 0) {
                      yyerror("duplicate rest argument name");
                  }
		  $1.setValue(new Integer(support.getCurrentScope().getLocalScope().addVariable(identifier)));
                  $$ = $1;
              }
              | restarg_mark {
                  $1.setValue(new Integer(-2));
                  $$ = $1;
              }

blkarg_mark   : tAMPER2 | tAMPER

f_block_arg   : blkarg_mark tIDENTIFIER {
                  String identifier = (String) $2.getValue();

                  if (IdUtil.getVarType(identifier) != IdUtil.LOCAL_VAR) {
                      yyerror("block argument must be local variable");
		  } else if (support.getCurrentScope().getLocalScope().isDefined(identifier) >= 0) {
                      yyerror("duplicate block argument name");
                  }
                  $$ = new BlockArgNode(support.union($1, $2), support.getCurrentScope().getLocalScope().addVariable(identifier), identifier);
              }

opt_f_block_arg: ',' f_block_arg {
                  $$ = $2;
              }
              | /* none */ {
	          $$ = null;
	      }

singleton     : var_ref {
                  if (!($1 instanceof SelfNode)) {
		      support.checkExpression($1);
		  }
		  $$ = $1;
              }
              | tLPAREN2 {
                  lexer.setState(LexState.EXPR_BEG);
              } expr opt_nl tRPAREN {
                  if ($3 instanceof ILiteralNode) {
                      yyerror("Can't define single method for literals.");
                  }
		  support.checkExpression($3);
                  $$ = $3;
              }

// ListNode:assoc_list - list of hash values pairs, like assocs but also
//   will accept ordinary list-style (e.g. a,b,c,d or a=>b,c=>d) [?null]
assoc_list    : none { // [!null]
                  $$ = new ArrayNode(getPosition(null));
              }
              | assocs trailer { // [!null]
                  $$ = $1;
              }
              | args trailer {
                  if ($1.size() % 2 != 0) {
                      yyerror("Odd number list for Hash.");
                  }
                  $$ = $1;
              }

// ListNode:assocs - list of hash value pairs (e.g. a => b, c => d) [!null]
assocs        : assoc // [!null]
              | assocs ',' assoc { // [!null]
                  $$ = $1.addAll($3);
              }

// ListNode:assoc - A single hash value pair (e.g. a => b) [!null]
assoc         : arg_value tASSOC arg_value { // [!null]
                  $$ = new ArrayNode(support.union($1, $3), $1).add($3);
              }

operation     : tIDENTIFIER | tCONSTANT | tFID
operation2    : tIDENTIFIER | tCONSTANT | tFID | op
operation3    : tIDENTIFIER | tFID | op
dot_or_colon  : tDOT | tCOLON2
opt_terms     : /* none */ | terms
opt_nl        : /* none */ | '\n'
trailer       : /* none */ | '\n' | ','

term          : ';' {
                  yyerrok();
              }
              | '\n'

terms         : term
              | terms ';' {
                  yyerrok();
              }

none          : /* none */ {
                  $$ = null;
              }

none_block_pass: /* none */ {  
                  $$ = null;
	      }

%%

    /** The parse method use an lexer stream and parse it to an AST node 
     * structure
     */
    public RubyParserResult parse(RubyParserConfiguration configuration, LexerSource source) {
        support.reset();
        support.setConfiguration(configuration);
        support.setResult(new RubyParserResult());
        
        lexer.reset();
        lexer.setSource(source);
        try {
	    //yyparse(lexer, new jay.yydebug.yyAnim("JRuby", 9));
	    //yyparse(lexer, new jay.yydebug.yyDebugAdapter());
	    yyparse(lexer, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (yyException e) {
            e.printStackTrace();
        }
        
        return support.getResult();
    }

    // +++
    // Helper Methods
    
    void yyerrok() {}

    /**
     * Since we can recieve positions at times we know can be null we
     * need an extra safety net here.
     */
    private ISourcePosition getPosition2(ISourcePositionHolder pos) {
        return pos == null ? lexer.getPosition(null, false) : pos.getPosition();
    }

    private ISourcePosition getPosition(ISourcePositionHolder start) {
        return getPosition(start, false);
    }

    private ISourcePosition getPosition(ISourcePositionHolder start, boolean inclusive) {
        if (start != null) {
	    return lexer.getPosition(start.getPosition(), inclusive);
	} 
	
	return lexer.getPosition(null, inclusive);
    }
}
