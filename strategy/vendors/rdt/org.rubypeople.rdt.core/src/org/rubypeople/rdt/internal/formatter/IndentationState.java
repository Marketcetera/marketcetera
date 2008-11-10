package org.rubypeople.rdt.internal.formatter;

import java.util.Map;

import org.rubypeople.rdt.core.formatter.Indents;

public class IndentationState {
	private String lastIndentationBasedOnLevel = "";
	private int indentationLevel ;
	private int offset ;
	private int pos ;
	
	//	indentation for parameter over multiple lines, like
	// method(param1
	// .......param2)
	private int fixIndentation ; 
	
	private String unformattedText ;

	
	public IndentationState(String unformattedText, int offset, int initialIndentLevel) {
		this.unformattedText = unformattedText ;
		this.offset = offset ;
		indentationLevel = initialIndentLevel ;		
		pos = 0 ;
		resetFixIndentation() ;		
	}
	
	public void decIndentationLevel() {
		indentationLevel -= 1 ;
		resetFixIndentation() ;		
	}
	
	
	public void incIndentationLevel() {
		indentationLevel += 1 ;	
		resetFixIndentation() ;
	}
	
	public void incPos(int increment) {
		pos += increment ;
	}
	
	public void resetFixIndentation() {
		fixIndentation = -1 ; 	
	}


	public int getIndentation() {
		return fixIndentation;
	}


	public int getIndentationLevel() {
		return indentationLevel;
	}


	public int getOffset() {
		return offset;
	}


	public int getPos() {
		return pos;
	}


	public String getUnformattedText() {
		return unformattedText;
	}


	public void setFixIndentation(int indentation) {
		this.fixIndentation = indentation;
	}


	public void setIndentationLevel(int indentationLevel) {
		this.indentationLevel = indentationLevel;
		this.resetFixIndentation() ;
	}


	public void setOffset(int offset) {
		this.offset = offset;
		this.resetFixIndentation() ;
	}


	public void setPos(int pos) {
		this.pos = pos;
	}
	
	protected String getIndentationString(Map options) {
		StringBuffer sb = new StringBuffer() ;
//        for (int i = 0; i < this.getOffset(); i++) {
//            sb.append(" ");
//        }
		if (this.getIndentation() != -1) {
			sb.append(lastIndentationBasedOnLevel) ;		
			sb.append(Indents.createFixIndentString(this.getIndentation(), options));
		}
		else {
			lastIndentationBasedOnLevel = Indents.createIndentString(this.getIndentationLevel(), options);
			sb.append(lastIndentationBasedOnLevel) ;
		}
		return sb.toString() ;
	}
	

}
