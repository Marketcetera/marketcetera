package org.marketcetera.quickfix.cficode;

class DebtCFICode extends CFICode {
	
	public DebtCFICode() {
		super(CATEGORY_DEBT+"XXXXX");
	}

	public DebtCFICode(CharSequence codes) {
		super(codes);
	}

	@Override
	public boolean isValid() {
		// not yet implemented
		return false;
	}
	
}