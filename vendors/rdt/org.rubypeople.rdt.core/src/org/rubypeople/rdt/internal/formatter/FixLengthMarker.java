package org.rubypeople.rdt.internal.formatter;


public class FixLengthMarker extends AbstractBlockMarker {

	int posInLine = -1 ;

	public FixLengthMarker(String aKeyword, int aPos) {
		super(aKeyword, aPos);
	}
	
	protected int getPosInLine(IndentationState state) {
		if (posInLine == -1) {
			posInLine = this.calculatePosInLine(state) ;	
		}
		return posInLine ;
	}
	
	private int calculatePosInLine(IndentationState state) {
		int i = this.getPos() ;
		// TODO: optimize
		while (!state.getUnformattedText().substring(i,i+1).equals("\n")) {
			i -= 1 ;
			if (i == 0) {
				break ;	
			}
		}
		while (state.getUnformattedText().charAt(i+1) == ' ' || state.getUnformattedText().charAt(i+1) == '\t') {
			i += 1 ;	
		} 
		return this.getPos() - i ;
	}


	protected void indentBeforePrint(IndentationState state) {
		state.setFixIndentation(this.getPosInLine(state)) ;
	}


	protected void indentAfterPrint(IndentationState state) {
	}

}
