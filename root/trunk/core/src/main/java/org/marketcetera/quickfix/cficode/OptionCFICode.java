package org.marketcetera.quickfix.cficode;

public class OptionCFICode extends CFICode {
	private static final int TYPE_POSITION = 1;
	private static final int EXERCISE_POSITION = 2;
	private static final int UNDERLYING_POSITION = 3;
	private static final int DELIVERY_POSITION = 4;
	private static final int STANDARD_POSITION = 5;

	public static final char EXERCISE_AMERICAN = 'A';
	public static final char EXERCISE_EUROPEAN = 'E';

	public static final char TYPE_PUT = 'P';
	public static final char TYPE_CALL = 'C';
	
	public static final char UNDERLYING_STOCK = 'S';
	public static final char UNDERLYING_INDEX = 'I';
	public static final char UNDERLYING_DEBT = 'D';
	public static final char UNDERLYING_CURRENCY = 'C';
	public static final char UNDERLYING_OPTION = 'O';
	public static final char UNDERLYING_FUTURE = 'F';
	public static final char UNDERLYING_COMMODITY = 'T';
	public static final char UNDERLYING_SWAP = 'W';
	public static final char UNDERLYING_BASKET = 'B';
	public static final char UNDERLYING_OTHER = 'M';
	
	public static final char DELIVERY_CASH = 'C';
	public static final char DELIVERY_PHYSICAL = 'P';

	public static final char STANDARD_STANDARD= 'S';
	public static final char STANDARD_NON_STANDARD= 'N';
	
	/**
	 * 
	 * @param sequence
	 * @throws IllegalArgumentException if sequence does not begin with O (capital o)
	 */
	public OptionCFICode(CharSequence sequence){
		super(sequence);
		if (CATEGORY_OPTION != sequence.charAt(0)){
			throw new IllegalArgumentException("Char 0"); //$NON-NLS-1$
		}
		setType(sequence.charAt(TYPE_POSITION));
		setExercise(sequence.charAt(EXERCISE_POSITION));
		setUnderlying(sequence.charAt(UNDERLYING_POSITION));
		setDelivery(sequence.charAt(DELIVERY_POSITION));
		setStandard(sequence.charAt(STANDARD_POSITION));
	}
	
	public char getExercise() {
		return charAt(EXERCISE_POSITION);
	}

	public void setExercise(char exercise) {
		if (isValidExercise(exercise)){
			setChar(EXERCISE_POSITION, exercise);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public boolean isValidExercise(char exercise){
		switch(exercise){
		case EXERCISE_AMERICAN:
		case EXERCISE_EUROPEAN:
		case UNKNOWN_UNUSED:
			return true;
		default:
			return false;
		}
	}
	
	public char getType() {
		return charAt(TYPE_POSITION);
	}

	public void setType(char type) {
		if (isValidType(type)){
			setChar(TYPE_POSITION, type);
		} else {
			throw new IllegalArgumentException();
		}
	}
	public boolean isValidType(char type) {
		switch(type){
		case TYPE_CALL:
		case TYPE_PUT:
		case UNKNOWN_UNUSED:
			return true;
		default:
			return false;
		}
	}

	public char getUnderlying() {
		return charAt(UNDERLYING_POSITION);
	}

	public void setUnderlying(char underlying) {
		if (isValidUnderlying(underlying)){
			setChar(UNDERLYING_POSITION, underlying);
		} else {
			throw new IllegalArgumentException();
		}
	}
	public boolean isValidUnderlying(char underlying) {
		switch(underlying){
		case UNDERLYING_STOCK:
		case UNDERLYING_INDEX:
		case UNDERLYING_DEBT:
		case UNDERLYING_CURRENCY:
		case UNDERLYING_OPTION:
		case UNDERLYING_FUTURE:
		case UNDERLYING_COMMODITY:
		case UNDERLYING_SWAP:
		case UNDERLYING_BASKET:
		case UNDERLYING_OTHER:
		case UNKNOWN_UNUSED:
			return true;
		default:
			return false;
		}
	}

	public char getDelivery() {
		return charAt(DELIVERY_POSITION);
	}


	public void setDelivery(char delivery) {
		if (isValidDelivery(delivery)){
			setChar(DELIVERY_POSITION, delivery);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public boolean isValidDelivery(char delivery) {
		switch(delivery){
		case DELIVERY_CASH:
		case DELIVERY_PHYSICAL:
		case UNKNOWN_UNUSED:
			return true;
		default:
			return false;
		}
	}

	public char getStandard() {
		return charAt(STANDARD_POSITION);
	}

	public void setStandard(char standard) {
		if (isValidStandard(standard)){
			setChar(STANDARD_POSITION, standard);
		} else {
			throw new IllegalArgumentException();
		}
	}
	public boolean isValidStandard(char standard) {
		switch(standard){
		case STANDARD_STANDARD:
		case STANDARD_NON_STANDARD:
		case UNKNOWN_UNUSED:
			return true;
		default:
			return false;
		}
	}

	@Override
	public String toString(){
		return ""+CATEGORY_OPTION+getType()+getExercise()+getUnderlying()+getDelivery()+getStandard(); //$NON-NLS-1$
	}

	@Override
	public boolean isValid() {
		// in this particular case, it is never possible to get this
		// object into an inconsistent state,
		// so we just return true
		return true;
	}
	
	public static boolean isOptionCFICode(CharSequence seq){
		return ((seq.length()==6)&& seq.charAt(0)==CFICode.CATEGORY_OPTION);
	}

}
