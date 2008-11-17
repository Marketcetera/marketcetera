package org.rubypeople.rdt.internal.formatter;


public class BeginBlockMarker extends AbstractBlockMarker {

	public BeginBlockMarker(String aKeyword, int aLine) {
		super(aKeyword, aLine);
	}

	protected void indentAfterPrint(IndentationState state) {
		if (!(state.getPos() > this.getPos())) {
			state.incIndentationLevel() ;
		}
	}


	protected void indentBeforePrint(IndentationState state) {
		if (state.getPos() > this.getPos() ) {
			state.incIndentationLevel() ;
		}
	}

}
