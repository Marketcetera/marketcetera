package org.marketcetera.quickfix.cficode;

class EquityCFICode extends CFICode {

	public EquityCFICode() {
		super(CATEGORY_EQUITY+"XXXXX");
	}

	public EquityCFICode(CharSequence codes) {
		super(codes);
	}

	@Override
	public boolean isValid() {
		// not yet implemented
		return false;
	}
	
}