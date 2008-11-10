package org.rubypeople.rdt.internal.formatter;

import java.util.Map;

public class NoFormattingMarker  extends AbstractBlockMarker {


	public NoFormattingMarker(String aKeyword, int aPos) {
		super(aKeyword, aPos);
		// TODO Auto-generated constructor stub
	}

	public boolean isFormatting() 
	{
		return false ;	
	}

	protected void indentAfterPrint(IndentationState state) {


	}


	protected void indentBeforePrint(IndentationState state) {


	}
	
	public void appendIndentedLine(StringBuffer sb, IndentationState state, String originalLine, String strippedLine, Map options) {
			sb.append(originalLine) ;
	}	

}
