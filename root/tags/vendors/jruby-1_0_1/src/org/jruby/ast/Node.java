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
 * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
 * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
 * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
 * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
 * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
package org.jruby.ast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jruby.ast.visitor.NodeVisitor;
import org.jruby.evaluator.Instruction;
import org.jruby.evaluator.InstructionBundle;
import org.jruby.evaluator.InstructionContext;
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.lexer.yacc.ISourcePositionHolder;
import org.jruby.lexer.yacc.IDESourcePosition;

/**
 *
 * @author  jpetersen
 */
public abstract class Node implements ISourcePositionHolder, InstructionContext, Serializable {
    static final long serialVersionUID = -5962822607672530224L;
    // We define an actual list to get around bug in java integration (1387115)
    static final List EMPTY_LIST = new ArrayList();
    
    public final int nodeId;
    
    public InstructionBundle instruction;

    private ISourcePosition position;
    private ArrayList comments;

    public Node(ISourcePosition position, int id) {
        this.position = position;
        this.nodeId = id;
    }

    /**
     * Location of this node within the source
     */
    public ISourcePosition getPosition() {
        return position;
    }

    public void setPosition(ISourcePosition position) {
        this.position = position;
    }
    
    public abstract Instruction accept(NodeVisitor visitor);
    public abstract List childNodes();

    static void addNode(Node node, List list) {
        if (node != null)
            list.add(node);
    }

    //TODO: Change to variable parameter list method once we have Java 1.5
    protected static List createList(Node node) {
        List list = new ArrayList();
        Node.addNode(node, list);
        return list;
    }

    protected  static List createList(Node node1, Node node2) {
        List list = createList(node1);
        Node.addNode(node2, list);
        return list;
    }

    protected  static List createList(Node node1, Node node2, Node node3) {
        List list = createList(node1, node2);
        Node.addNode(node3, list);
        return list;
    }

    protected  static List createList(Node node1, Node node2, Node node3, Node node4) {
        List list = createList(node1, node2, node3);
        Node.addNode(node4, list);
        return list;
    }    
    
    public String toString() {
        return getNodeName() + "[]";
    }

    protected String getNodeName() {
        String name = getClass().getName();
        int i = name.lastIndexOf('.');
        String nodeType = name.substring(i + 1);
        return nodeType;
    }
    
    public void addComment(CommentNode comment) {
        if(comments == null) {
            comments = new ArrayList();
        }
        comments.add(comment);
    }
    
    public void addComments(Collection comments) {
        if(this.comments == null) {
            this.comments = new ArrayList();
        }
        this.comments.addAll(comments);
    }
    
    public Collection getComments() {
        if(comments == null) {
            return EMPTY_LIST;
        }
        return comments;
    }
    
    public boolean hasComments() {
        return comments != null && !comments.isEmpty();
    }
    
    public ISourcePosition getPositionIncludingComments() {
        if(position == null || !hasComments()) {
            return position;
        }
        
        String fileName = position.getFile();
        int startOffset = position.getStartOffset();
        int endOffset = position.getEndOffset();
        int startLine = position.getStartLine();
        int endLine = position.getEndLine();
        
        // Since this is only used for IDEs this is safe code, but there is an obvious abstraction issue here.
        ISourcePosition commentIncludingPos = new IDESourcePosition(fileName, startLine, endLine, startOffset, endOffset);
        
        Iterator commentItr = comments.iterator();
        while(commentItr.hasNext()) {
            ISourcePosition commentPos = ((CommentNode)commentItr.next()).getPosition();
            commentIncludingPos = IDESourcePosition.combinePosition(commentIncludingPos, commentPos);
        }       

        return commentIncludingPos;
    }

}
