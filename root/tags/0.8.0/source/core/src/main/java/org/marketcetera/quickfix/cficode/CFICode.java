package org.marketcetera.quickfix.cficode;


public abstract class CFICode implements CharSequence{
	public static final char UNKNOWN_UNUSED = 'X';
	public static final char CATEGORY_POSITION = 0;
	public static final char GROUP_POSITION = 0;
	
	public static final char CATEGORY_EQUITY = 'E';
	public static final char CATEGORY_DEBT = 'D';
	public static final char CATEGORY_ENTITLEMENT = 'R';
	public static final char CATEGORY_OPTION = 'O';
	public static final char CATEGORY_FUTURE = 'F';
	public static final char CATEGORY_OTHER = 'M';

	private char [] chars = new char[6];

	/**
	 * 
	 * @param seq
	 * @throws StringIndexOutOfBoundsException if seq is shorter than 6 chars
	 */
	public CFICode(CharSequence seq){
		for (int i = 0; i < 6; i++){
			chars[i] = seq.charAt(i);
		}
	}
	
	public char charAt(int index) {
		return chars [index];
	}
	
	public void setChar(int index, char theChar){
		chars[index]=theChar;
	}

	public int length() {
		return chars.length;
	}

	public CharSequence subSequence(int start, int end) {
		return new String(chars, start, end-start);
	}

	public char getAttribute(int attributeIndex){
		return chars[attributeIndex+2];
	}

	public abstract boolean isValid();
	
	public static CFICode newCFICode(CharSequence codes){
		switch (codes.charAt(0)){
		case CATEGORY_DEBT:
			return new DebtCFICode(codes);
		case CATEGORY_EQUITY:
			return new EquityCFICode(codes);
		case CATEGORY_ENTITLEMENT:
			return new EntitlementCFICode(codes);
		case CATEGORY_FUTURE:
			return new FutureCFICode(codes);
		case CATEGORY_OPTION:
			return new OptionCFICode(codes);
		case CATEGORY_OTHER:
			return new OtherCFICode(codes);
			
		default:
			throw new IllegalArgumentException();
		}
	}

	public char getCategory()
	{
		return chars[CATEGORY_POSITION];
	}
	
	@Override
	public String toString(){
		return new String(chars);
	}

}





