package org.rubypeople.rdt.internal.formatter;


public class NeutralMarker extends AbstractBlockMarker {

	public NeutralMarker(String aKeyword, int aLine) {
		super(aKeyword, aLine);
	}

	protected void indentAfterPrint(IndentationState state) {

	}


	protected void indentBeforePrint(IndentationState state) {
		state.resetFixIndentation() ;
	}

}
