package org.marketcetera.quickfix.cficode;

public class OtherCFICode extends CFICode {

	
	public OtherCFICode() {
		super(CATEGORY_OTHER+"XXXXX");
	}

	public OtherCFICode(CharSequence codes) {
		super(codes);
	}

	@Override
	public boolean isValid() {
		// not yet implemented
		return false;
	}
	
}
