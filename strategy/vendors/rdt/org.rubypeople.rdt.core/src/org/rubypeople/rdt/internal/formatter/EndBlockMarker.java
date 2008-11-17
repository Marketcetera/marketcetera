package org.rubypeople.rdt.internal.formatter;


public class EndBlockMarker extends AbstractBlockMarker {

	public EndBlockMarker(String aKeyword, int aLine) {
		super(aKeyword, aLine);
	}

	protected void indentAfterPrint(IndentationState state) {

	}


	protected void indentBeforePrint(IndentationState state) {
		state.decIndentationLevel();
	}

}
